package com.bank.controller;

import com.bank.model.Customer;
import com.bank.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                       @RequestParam(required = false) String username,
                       Model model) {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
            !SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        
        // display last username
        if (error != null && username != null) {
            model.addAttribute("lastAttempt", "Last attempt: " + username);
        }

        return "login";
    }

    @GetMapping("/register")
    public String register() {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
            !SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerCustomer(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        String validPattern = "^[_\\-\\.0-9a-z]{1,127}$";

        if (!username.matches(validPattern) || !password.matches(validPattern) || !confirmPassword.matches(validPattern)) {
            redirectAttributes.addFlashAttribute("error", "invalid_input");
            return "redirect:/register";
        }

        // Validate password confirmation
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "invalid_input");
            return "redirect:/register";
        }

        try {
            customerService.registerNewCustomer(username, password);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        }
        catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    // removed username to prevent IDOR
    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        Customer customer = customerService.findByUsername(principal.getName());

        if (customer == null) {
            model.addAttribute("error", "User not found");
            return "error"; // or redirect to login page
        }

        model.addAttribute("balance", customer.getBalance());
        model.addAttribute("username", customer.getUsername());
        return "dashboard"; // will render dashboard.html
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam String amountStr, Principal principal, RedirectAttributes redirectAttributes) {
        if (!amountStr.matches("^(0|[1-9][0-9]*)\\.[0-9]{2}$")) {
            redirectAttributes.addFlashAttribute("error", "invalid_input");
            return "redirect:/dashboard";
        }

        BigDecimal amount = new BigDecimal(amountStr);
        if (amount.compareTo(BigDecimal.ZERO) < 0 || amount.compareTo(new BigDecimal("4294967295.99")) > 0) {
            redirectAttributes.addFlashAttribute("error", "invalid_input");
            return "redirect:/dashboard";
        }

        Customer customer = customerService.findByUsername(principal.getName());
        customer.setBalance(customer.getBalance().add(amount));
        customerService.save(customer);
        redirectAttributes.addFlashAttribute("success", "Deposit successful!");
        return "redirect:/dashboard";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam String amountStr, Principal principal, RedirectAttributes redirectAttributes) {
        if (!amountStr.matches("^(0|[1-9][0-9]*)\\.[0-9]{2}$")) {
            redirectAttributes.addFlashAttribute("error", "invalid_input");
            return "redirect:/dashboard";
        }

        BigDecimal amount = new BigDecimal(amountStr);

        // Validate bounds: 0.00 to 4294967295.99
        BigDecimal min = new BigDecimal("0.00");
        BigDecimal max = new BigDecimal("4294967295.99");
        if (amount.compareTo(min) < 0 || amount.compareTo(max) > 0) {
            redirectAttributes.addFlashAttribute("error", "invalid_input");
            return "redirect:/dashboard";
        }

        Customer customer = customerService.findByUsername(principal.getName());

        // Check for sufficient funds
        if (customer.getBalance().compareTo(amount) < 0) {
            redirectAttributes.addFlashAttribute("error", "Insufficient funds.");
            return "redirect:/dashboard";
        }

        customer.setBalance(customer.getBalance().subtract(amount));
        customerService.save(customer);
        redirectAttributes.addFlashAttribute("success", "Withdrawal successful!");
        return "redirect:/dashboard";
    }
} 
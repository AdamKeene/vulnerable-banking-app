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
    public String login() {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
            !SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
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
        
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/register";
        }

        try {
            customerService.registerNewCustomer(username, password);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        Customer customer = customerService.findByUsername(principal.getName());
        model.addAttribute("balance", customer.getBalance());
        model.addAttribute("username", customer.getUsername());
        return "dashboard"; // will render dashboard.html
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam BigDecimal amount, Principal principal, RedirectAttributes redirectAttributes) {
        Customer customer = customerService.findByUsername(principal.getName());
        customer.setBalance(customer.getBalance().add(amount));
        customerService.save(customer);
        redirectAttributes.addFlashAttribute("success", "Deposit successful!");
        return "redirect:/dashboard";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam BigDecimal amount, Principal principal, RedirectAttributes redirectAttributes) {
        Customer customer = customerService.findByUsername(principal.getName());

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
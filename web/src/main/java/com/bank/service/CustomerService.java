package com.bank.service;

import com.bank.model.Customer;

import com.bank.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.regex.Pattern;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class CustomerService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // 1-127 characters, only letters, numbers, underscores, hyphens, and dots
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[_\\-\\.0-9a-z]{1,127}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[_\\-\\.0-9a-z]{1,127}$");

    public boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }
    public boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
            .username(customer.getUsername())
            .password(customer.getPassword())
            .roles("USER")
            .build();
    }

    // changed to PreparedStatement to prevent SQL injection
    public Customer findByUsername(String username) {
        Customer customer = null;
        String url = "jdbc:h2:file:./data/bankdb";
        String user = "sa";
        String pass = "";

        String sql = "SELECT * FROM customers WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    customer = new Customer();
                    customer.setUsername(rs.getString("username"));
                    customer.setPassword(rs.getString("password"));
                    customer.setBalance(rs.getBigDecimal("balance"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customer;
    }

    public Customer registerNewCustomer(String username, String password) {
        if (!isValidUsername(username)) {
            throw new RuntimeException("Invalid username");
        }
        if (!isValidPassword(password)) {
            throw new RuntimeException("Invalid password");
        }
        
        if (customerRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword(passwordEncoder.encode(password));
        customer.setBalance(new BigDecimal("404.00")); // make sure that user has an initial balance
        
        return customerRepository.save(customer);
    }

    public Customer authenticateCustomer(String username, String password) {
        Customer customer = customerRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, customer.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return customer;
    }

    public void save(Customer customer) {
        customerRepository.save(customer);
    }
} 
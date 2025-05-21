package com.bank.model;

import jakarta.persistence.*; // if we need databases but this is the java to SQL API notations
import java.math.BigDecimal;

@Entity
@Table(name = "customers") // name of table in database
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 127) // put restrictions on making new users
    private String username;

    @Column(nullable = false, length = 127)
    private String password;

    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal balance;

    public Customer() {}

    public Customer(String username, String password, BigDecimal balance) {
        this.username = username;
        this.password = password;
        this.balance = balance; // default value be 404 dollars?
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}

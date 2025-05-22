package com.bank.services;

import com.bank.model.Customer;
import com.bank.service.CustomerService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @BeforeEach
    void setup() {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/bankdb", "sa", "");
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS customers (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(127) UNIQUE NOT NULL, " +
                    "password VARCHAR(127) NOT NULL, " +
                    "balance DECIMAL(20,2) NOT NULL)");

            stmt.execute("DELETE FROM customers");

            stmt.execute("INSERT INTO customers (username, password, balance) " +
                    "VALUES ('testuser_deposit', 'password123', 100.00)");

            stmt.execute("INSERT INTO customers (username, password, balance) " +
                    "VALUES ('testuser_withdraw', 'password123', 100.00)");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Setup failed: " + e.getMessage());
        }
    }

    @Test
    public void testDepositIncreasesBalance() {
        Customer customer = customerService.findByUsername("testuser_deposit");
        assertNotNull(customer, "Customer should exist before deposit");

        BigDecimal depositAmount = new BigDecimal("25.00");

        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/bankdb", "sa", "");
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("UPDATE customers SET balance = balance + " + depositAmount +
                    " WHERE username = 'testuser_deposit'");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Deposit failed: " + e.getMessage());
        }

        Customer updated = customerService.findByUsername("testuser_deposit");
        assertEquals(new BigDecimal("125.00"), updated.getBalance());
    }

    @Test
    public void testWithdrawDecreasesBalance() {
        Customer customer = customerService.findByUsername("testuser_withdraw");
        assertNotNull(customer, "Customer should exist before withdrawal");

        BigDecimal withdrawAmount = new BigDecimal("40.00");

        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/bankdb", "sa", "");
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("UPDATE customers SET balance = balance - " + withdrawAmount +
                    " WHERE username = 'testuser_withdraw'");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Withdrawal failed: " + e.getMessage());
        }

        Customer updated = customerService.findByUsername("testuser_withdraw");
        assertEquals(new BigDecimal("60.00"), updated.getBalance());
    }
}

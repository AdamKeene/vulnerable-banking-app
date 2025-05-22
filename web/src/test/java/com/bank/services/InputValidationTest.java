package com.bank.services;

import com.bank.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class InputValidationTest {

    private CustomerService customerService;

    @BeforeEach
    void setup() {
        customerService = new CustomerService();
    }

    @Test
    public void testValidUsername() {
        assertTrue(customerService.isValidUsername("user_123"));
        assertTrue(customerService.isValidUsername("a.b-c"));
        assertFalse(customerService.isValidUsername("Invalid@User"));
        assertFalse(customerService.isValidUsername(""));
    }

    @Test
    public void testValidPassword() {
        assertTrue(customerService.isValidPassword("pass_123"));
        assertTrue(customerService.isValidPassword("a.b-c"));
        assertFalse(customerService.isValidPassword("Invalid#Pass"));
        assertFalse(customerService.isValidPassword(null));
    }

    @Test
    public void testValidNumericInputFormat() {
        Pattern pattern = Pattern.compile("(0|[1-9][0-9]*)");
        assertTrue(pattern.matcher("0").matches());
        assertTrue(pattern.matcher("42").matches());
        assertFalse(pattern.matcher("052").matches());
        assertFalse(pattern.matcher("0x2a").matches());
    }

    @Test
    public void testValidCurrencyAmount() {
        Pattern currencyPattern = Pattern.compile("\\d+\\.[0-9]{2}");
        assertTrue(currencyPattern.matcher("123.00").matches());
        assertTrue(currencyPattern.matcher("0.01").matches());
        assertFalse(currencyPattern.matcher("123").matches());
        assertFalse(currencyPattern.matcher("123.1").matches());
        assertFalse(currencyPattern.matcher("123.001").matches());
    }

    @Test
    void testInvalidNumericFormats() {
        Pattern numericPattern = Pattern.compile("^(0|[1-9][0-9]*)$");
        assertFalse(numericPattern.matcher("052").matches()); // leading zero
        assertFalse(numericPattern.matcher("0x2a").matches()); // hex
        assertTrue(numericPattern.matcher("42").matches());   // valid
    }

    @Test
    void testCurrencyFormatValidation() {
        assertTrue("12.34".matches("\\d+\\.\\d{2}"));
        assertFalse("12.345".matches("\\d+\\.\\d{2}")); // too many fractional digits
        assertFalse("12.".matches("\\d+\\.\\d{2}")); // missing cents
        assertFalse(".99".matches("\\d+\\.\\d{2}")); // missing whole number
    }
}
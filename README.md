# Bank Web App – Class Project

## Project Overview

This is a self-contained web or Android application that simulates a simple online banking system. It enables users to:

- Register a new bank account with an initial balance.
- Log in securely with a username and password.
- View current account balance.
- Deposit or withdraw funds from the account.

The goal of the project is to demonstrate understanding of full-stack development and secure user interactions.

---

## Core Requirements

### 1. User Registration
- Users must be able to create an account with:
    - Username
    - Password (stored securely, e.g., hashed)
    - Initial balance

### 2. User Login
- Authenticate users with registered credentials.
- Show an error message for failed login attempts.

### 3. Account Balance
- Logged-in users should be able to view their current balance.
- The UI should clearly display this information.

### 4. Transactions
- Allow users to:
    - **Deposit** funds (increase balance)
    - **Withdraw** funds (decrease balance)
- Ensure that withdrawals do not result in negative balances.

---

## Technical Notes

- We are developing it as  a **web app** (Spring Boot).
- N-Layer architecture is used to handle logic and testing at different layers
- Use a database to store user and transaction data (H2).
- Focus on usability, error handling, and secure coding practices.


---

## Educational Objectives

Through this project, you will demonstrate:

- Secure user registration and authentication
- Reading and writing user financial data
- Backend and frontend integration
- Building and testing a secure, interactive web application

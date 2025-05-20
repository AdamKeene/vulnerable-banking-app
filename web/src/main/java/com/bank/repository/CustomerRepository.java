package com.bank.repository;

import com.bank.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Custom query method
    Optional<Customer> findByUsername(String username);

    // Optionally, if you want to check existence
    boolean existsByUsername(String username);
}

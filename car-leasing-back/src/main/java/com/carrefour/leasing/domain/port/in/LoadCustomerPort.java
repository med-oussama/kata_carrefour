package com.carrefour.leasing.domain.port.in;

import com.carrefour.leasing.domain.model.Customer;

import java.util.Optional;

public interface LoadCustomerPort {
    Optional<Customer> loadById(Long id);
}

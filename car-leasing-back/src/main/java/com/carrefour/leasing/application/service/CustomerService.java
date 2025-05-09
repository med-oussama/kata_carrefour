package com.carrefour.leasing.application.service;

import com.carrefour.leasing.domain.model.Customer;
import com.carrefour.leasing.domain.port.in.RegisterCustomerUseCase;
import com.carrefour.leasing.domain.port.out.SaveCustomerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService implements RegisterCustomerUseCase {

    private final SaveCustomerPort saveCustomerPort;

    private final PasswordEncoder passwordEncoder;

    public CustomerService(SaveCustomerPort saveCustomerPort, PasswordEncoder passwordEncoder) {
        this.saveCustomerPort = saveCustomerPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Customer register(Customer customer) {
        String hashedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(hashedPassword);
        return saveCustomerPort.save(customer);
    }
}

package com.carrefour.leasing.adapter.out.persistence;


import com.carrefour.leasing.domain.model.Customer;
import com.carrefour.leasing.domain.port.in.LoadCustomerPort;
import com.carrefour.leasing.domain.port.out.SaveCustomerPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerPersistenceAdapter implements LoadCustomerPort, SaveCustomerPort {

    private final CustomerJpaRepository repository;

    public CustomerPersistenceAdapter(CustomerJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Customer> loadById(Long id) {
        return repository.findById(id)
                .map(entity -> new Customer(entity.getId(), entity.getEmail(), entity.getName(), null));
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(customer.getId());
        entity.setEmail(customer.getEmail());
        entity.setName(customer.getName());
        entity.setPassword(customer.getPassword());
        CustomerEntity saved = repository.save(entity);
        return new Customer(saved.getId(), saved.getEmail(), saved.getName(), saved.getPassword());
    }
}

package com.carrefour.leasing.adapter.security;

import com.carrefour.leasing.adapter.out.persistence.CustomerEntity;
import com.carrefour.leasing.adapter.out.persistence.CustomerJpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

// This class implements UserDetailsService to load user-specific data for spring security.
@Service
public class CustomerDetailsService implements UserDetailsService {

    private final CustomerJpaRepository customerRepository;

    public CustomerDetailsService(CustomerJpaRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CustomerEntity customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new User(
                customer.getEmail(),
                customer.getPassword(),
                Collections.emptyList()
        );
    }
}

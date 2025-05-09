package com.carrefour.leasing.adapter.in.web;

import com.carrefour.leasing.domain.model.Customer;
import com.carrefour.leasing.domain.port.in.RegisterCustomerUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final RegisterCustomerUseCase registerCustomerUseCase;


    @PostMapping("/register")
    public ResponseEntity<Customer> register(@RequestBody Customer customer) {
        Customer created = registerCustomerUseCase.register(customer);
        return ResponseEntity.ok(created);
    }

}

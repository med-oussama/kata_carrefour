package com.carrefour.leasing.domain.port.in;

import com.carrefour.leasing.domain.model.Customer;
import org.springframework.stereotype.Component;

@Component
public interface RegisterCustomerUseCase {
    Customer register(Customer customer);
}

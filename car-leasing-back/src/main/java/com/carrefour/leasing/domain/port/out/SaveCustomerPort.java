package com.carrefour.leasing.domain.port.out;

import com.carrefour.leasing.domain.model.Customer;

public interface SaveCustomerPort {
    Customer save(Customer customer);
}

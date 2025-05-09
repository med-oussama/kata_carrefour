package com.carrefour.leasing.domain.port.in;

import com.carrefour.leasing.domain.model.Car;

public interface GetLeasedCarByCustomerUseCase {
    Car getLeasedCarByCustomer(Long customerId);
}

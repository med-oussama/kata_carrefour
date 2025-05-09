package com.carrefour.leasing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Car {
    private final Long id;
    private final String model;
    private CarStatus status;
    private Long customerId;
}

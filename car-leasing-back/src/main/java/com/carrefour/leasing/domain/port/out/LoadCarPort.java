package com.carrefour.leasing.domain.port.out;

import com.carrefour.leasing.domain.model.Car;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface LoadCarPort {
    Optional<Car> loadById(Long id);

    Optional<Car> findByCustomerId(Long customerId);
}

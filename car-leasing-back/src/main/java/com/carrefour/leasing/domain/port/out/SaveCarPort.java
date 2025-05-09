package com.carrefour.leasing.domain.port.out;

import com.carrefour.leasing.domain.model.Car;
import org.springframework.stereotype.Component;

@Component
public interface SaveCarPort {
    void save(Car car);
}

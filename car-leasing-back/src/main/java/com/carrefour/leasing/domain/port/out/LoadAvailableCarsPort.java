package com.carrefour.leasing.domain.port.out;

import com.carrefour.leasing.domain.model.Car;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface LoadAvailableCarsPort {
    List<Car> loadAvailableCars();
}

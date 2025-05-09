package com.carrefour.leasing.domain.port.in;

import com.carrefour.leasing.domain.model.Car;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GetAvailableCarsUseCase {
    List<Car> getAvailableCars();
}

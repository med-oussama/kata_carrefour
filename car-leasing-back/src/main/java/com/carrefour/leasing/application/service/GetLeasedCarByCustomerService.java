package com.carrefour.leasing.application.service;

import com.carrefour.leasing.adapter.out.persistence.CarJpaRepository;
import com.carrefour.leasing.domain.model.Car;
import com.carrefour.leasing.domain.model.CarStatus;
import com.carrefour.leasing.domain.port.in.GetLeasedCarByCustomerUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetLeasedCarByCustomerService implements GetLeasedCarByCustomerUseCase {

    private final CarJpaRepository carRepository;


    @Override
    public Car getLeasedCarByCustomer(Long customerId) {
        return carRepository.findByCustomerId(customerId)
                .map(carEntity -> new Car(
                        carEntity.getId(),
                        carEntity.getModel(),
                        carEntity.getStatus() == null ? null : CarStatus.valueOf(carEntity.getStatus()),
                        carEntity.getCustomer().getId()
                ))
                .orElseThrow(() -> new IllegalArgumentException("Customer has no leased car."));
    }
}

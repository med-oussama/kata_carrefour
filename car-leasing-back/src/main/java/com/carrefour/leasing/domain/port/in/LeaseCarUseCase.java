package com.carrefour.leasing.domain.port.in;

public interface LeaseCarUseCase {
    void leaseCar(Long carId, Long customerId);
}

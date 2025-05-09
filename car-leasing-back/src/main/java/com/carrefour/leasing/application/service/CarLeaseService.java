package com.carrefour.leasing.application.service;

import com.carrefour.leasing.adapter.out.persistence.CarJpaRepository;
import com.carrefour.leasing.domain.model.Car;
import com.carrefour.leasing.domain.model.CarStatus;
import com.carrefour.leasing.domain.model.Customer;
import com.carrefour.leasing.domain.port.in.GetAvailableCarsUseCase;
import com.carrefour.leasing.domain.port.in.LeaseCarUseCase;
import com.carrefour.leasing.domain.port.in.LoadCustomerPort;
import com.carrefour.leasing.domain.port.in.ReturnCarUseCase;
import com.carrefour.leasing.domain.port.out.LoadAvailableCarsPort;
import com.carrefour.leasing.domain.port.out.LoadCarPort;
import com.carrefour.leasing.domain.port.out.SaveCarPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarLeaseService implements LeaseCarUseCase, ReturnCarUseCase, GetAvailableCarsUseCase {

    private final LoadCarPort loadCarPort;
    private final SaveCarPort saveCarPort;
    private final LoadCustomerPort loadCustomerPort;
    private final LoadAvailableCarsPort loadAvailableCarsPort;
    private final CarJpaRepository carRepository;


    @Override
    public List<Car> getAvailableCars() {
        return loadAvailableCarsPort.loadAvailableCars();
    }

    @Override
    public void leaseCar(Long carId, Long customerId) {
        // Check if the customer already has a leased car
        boolean hasLeasedCar = loadCarPort.findByCustomerId(customerId)
                .filter(car -> car.getStatus() == CarStatus.LEASED)
                .isPresent();
        if (hasLeasedCar) {
            // Throw an exception if the customer already has a leased car
            throw new IllegalStateException("Customer already has a leased car");
        }

        // Load the car by its ID
        Car car = loadCarPort.loadById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
        // Check if the car is available for leasing
        if (car.getStatus() != CarStatus.AVAILABLE) {
            // Throw an exception if the car is not available
            throw new IllegalStateException("Car not available");
        }

        // Load the customer by their ID
        Customer customer = loadCustomerPort.loadById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        // Update the car's status to LEASED and associate it with the customer
        car.setStatus(CarStatus.LEASED);
        car.setCustomerId(customer.getId());

        // Save the updated car information
        saveCarPort.save(car);
    }

    @Override
    public void returnCar(Long carId) {
        // Load the car by its ID
        Car car = loadCarPort.loadById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found")); // Throw an exception if the car is not found

        // Check if the car is currently leased
        if (car.getStatus() != CarStatus.LEASED) {
            // Throw an exception if the car is not rented
            throw new IllegalStateException("Car is not rented");
        }

        // Update the car's status to AVAILABLE
        car.setStatus(CarStatus.AVAILABLE);

        // Remove the association with the customer
        car.setCustomerId(null);

        // Save the updated car information
        saveCarPort.save(car);
    }
}

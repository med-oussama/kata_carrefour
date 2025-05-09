package com.carrefour.leasing.adapter.out.persistence;

import com.carrefour.leasing.domain.model.Car;
import com.carrefour.leasing.domain.model.CarStatus;
import com.carrefour.leasing.domain.port.out.LoadAvailableCarsPort;
import com.carrefour.leasing.domain.port.out.LoadCarPort;
import com.carrefour.leasing.domain.port.out.SaveCarPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CarPersistenceAdapter implements LoadCarPort, SaveCarPort, LoadAvailableCarsPort {

    private final CarJpaRepository repository;

    public CarPersistenceAdapter(CarJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Car> loadById(Long id) {
        return repository.findById(id)
                .map(entity -> new Car(
                        entity.getId(),
                        entity.getModel(),
                        CarStatus.valueOf(entity.getStatus()),
                        entity.getCustomer() != null ? entity.getCustomer().getId() : null
                ));
    }

    @Override
    public Optional<Car> findByCustomerId(Long customerId) {
        return repository.findByCustomerId(customerId)
                .map(carEntity -> new Car(
                        carEntity.getId(),
                        carEntity.getModel(),
                        CarStatus.valueOf(carEntity.getStatus()),
                        carEntity.getCustomer() != null ? carEntity.getCustomer().getId() : null
                ));
    }

    @Override
    public List<Car> loadAvailableCars() {
        return repository.findByStatus(CarStatus.AVAILABLE.toString())
                .stream()
                .map(entity -> new Car(
                        entity.getId(),
                        entity.getModel(),
                        CarStatus.valueOf(entity.getStatus()),
                        entity.getCustomer() != null ? entity.getCustomer().getId() : null
                ))
                .toList();
    }

    @Override
    public void save(Car car) {
        CarEntity entity = new CarEntity();
        entity.setId(car.getId());
        entity.setModel(car.getModel());
        entity.setStatus(car.getStatus().name());
        if (car.getCustomerId() != null) {
            CustomerEntity customerEntity = new CustomerEntity();
            customerEntity.setId(car.getCustomerId());
            entity.setCustomer(customerEntity);
        } else {
            entity.setCustomer(null);
        }
        repository.save(entity);
    }
}

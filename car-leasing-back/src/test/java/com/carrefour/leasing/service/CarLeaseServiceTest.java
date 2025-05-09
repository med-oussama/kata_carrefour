package com.carrefour.leasing.service;


import com.carrefour.leasing.application.service.CarLeaseService;
import com.carrefour.leasing.domain.model.Car;
import com.carrefour.leasing.domain.model.CarStatus;
import com.carrefour.leasing.domain.model.Customer;
import com.carrefour.leasing.domain.port.in.LoadCustomerPort;
import com.carrefour.leasing.domain.port.out.LoadAvailableCarsPort;
import com.carrefour.leasing.domain.port.out.LoadCarPort;
import com.carrefour.leasing.domain.port.out.SaveCarPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarLeaseServiceTest {

    // Mock the port for loading available cars
    @Mock
    private LoadAvailableCarsPort loadAvailableCarsPort;

    // Mock the port for loading a specific car
    @Mock
    private LoadCarPort loadCarPort;

    // Mock the port for loading a customer
    @Mock
    private LoadCustomerPort loadCustomerPort;

    // Mock the port for saving a car
    @Mock
    private SaveCarPort saveCarPort;

    // Inject the mocks into the CarLeaseService
    @InjectMocks
    private CarLeaseService carLeaseService;

    @Test
    void shouldReturnListOfAvailableCars() {
        // Create mock data for available cars
        Car car1 = new Car(1L, "Clio", CarStatus.AVAILABLE, null);
        Car car2 = new Car(2L, "Civic", CarStatus.AVAILABLE, null);

        // Mock the behavior of the loadAvailableCarsPort
        when(loadAvailableCarsPort.loadAvailableCars()).thenReturn(List.of(car1, car2));

        // Call the method to test
        List<Car> availableCars = carLeaseService.getAvailableCars();

        // Verify the results
        assertEquals(2, availableCars.size());
        assertTrue(availableCars.contains(car1));
        assertTrue(availableCars.contains(car2));
        verify(loadAvailableCarsPort).loadAvailableCars();
    }

    @Test
    void shouldReturnEmptyListIfNoCarsAreAvailable() {
        // Mock the behavior to return an empty list
        when(loadAvailableCarsPort.loadAvailableCars()).thenReturn(List.of());

        // Call the method to test
        List<Car> availableCars = carLeaseService.getAvailableCars();

        // Verify the results
        assertTrue(availableCars.isEmpty());
        verify(loadAvailableCarsPort).loadAvailableCars();
    }

    @Test
    void shouldThrowExceptionIfCustomerNotFound() {
        // Create a mock car
        Car car = new Car(1L, "Clio", CarStatus.AVAILABLE, null);

        // Mock the behavior for loading a car and a non-existent customer
        when(loadCarPort.loadById(1L)).thenReturn(Optional.of(car));
        when(loadCustomerPort.loadById(1L)).thenReturn(Optional.empty());

        // Verify that an exception is thrown
        assertThrows(IllegalArgumentException.class, () -> carLeaseService.leaseCar(1L, 1L));
    }

    @Test
    void shouldThrowExceptionIfCarNotFound() {
        // Mock the behavior for a non-existent car
        when(loadCarPort.loadById(1L)).thenReturn(Optional.empty());

        // Verify that an exception is thrown
        assertThrows(IllegalArgumentException.class, () -> carLeaseService.leaseCar(1L, 1L));
    }

    @Test
    void shouldReturnCarSuccessfully() {
        // Create a mock leased car
        Car car = new Car(1L, "Clio", CarStatus.LEASED, 1L);

        // Mock the behavior for loading the car
        when(loadCarPort.loadById(1L)).thenReturn(Optional.of(car));

        // Call the method to test
        carLeaseService.returnCar(1L);

        // Verify the car's status and customer ID
        assertEquals(CarStatus.AVAILABLE, car.getStatus());
        assertNull(car.getCustomerId());
        verify(saveCarPort).save(car);
    }

    @Test
    void shouldLeaseCarSuccessfully() {
        // Create a mock available car and a customer
        Car car = new Car(1L, "Clio", CarStatus.AVAILABLE, null);
        Customer customer = new Customer(1L, "John", "Doe", null);

        // Mock the behavior for loading the car and customer
        when(loadCarPort.loadById(1L)).thenReturn(Optional.of(car));
        when(loadCustomerPort.loadById(1L)).thenReturn(Optional.of(customer));

        // Call the method to test
        carLeaseService.leaseCar(1L, 1L);

        // Verify the car's status and customer ID
        assertEquals(CarStatus.LEASED, car.getStatus());
        assertEquals(1L, car.getCustomerId());
        verify(saveCarPort).save(car);
    }

    @Test
    void shouldThrowExceptionIfCarNotRentedWhenReturning() {
        // Create a mock available car
        Car car = new Car(1L, "Clio", CarStatus.AVAILABLE, null);

        // Mock the behavior for loading the car
        when(loadCarPort.loadById(1L)).thenReturn(Optional.of(car));

        // Verify that an exception is thrown
        assertThrows(IllegalStateException.class, () -> carLeaseService.returnCar(1L));
    }
}

package com.carrefour.leasing.adapter.in.web;

import com.carrefour.leasing.domain.model.Car;
import com.carrefour.leasing.domain.port.in.GetAvailableCarsUseCase;
import com.carrefour.leasing.domain.port.in.GetLeasedCarByCustomerUseCase;
import com.carrefour.leasing.domain.port.in.LeaseCarUseCase;
import com.carrefour.leasing.domain.port.in.ReturnCarUseCase;
import com.carrefour.leasing.domain.port.out.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {

    private final LeaseCarUseCase leaseCarUseCase;
    private final ReturnCarUseCase returnCarUseCase;
    private final GetAvailableCarsUseCase getAvailableCarsUseCase;
    private final AuthenticationService authenticationService;
    private final GetLeasedCarByCustomerUseCase getLeasedCarUseCase;


    @GetMapping("/available")
    public ResponseEntity<CollectionModel<EntityModel<Car>>> getAvailableCars() {
        // Get the currently authenticated customer's ID via the security adapter
        Long customerId = authenticationService.getAuthenticatedCustomerId();

        // Map each available car to an EntityModel with a HATEOAS "lease" link
        List<EntityModel<Car>> cars = getAvailableCarsUseCase.getAvailableCars().stream()
                .map(car -> EntityModel.of(
                        car,
                        // Add a HATEOAS link that allows the authenticated customer to lease this car
                        linkTo(methodOn(CarController.class).leaseCar(car.getId(), customerId)).withRel("lease")
                ))
                .toList();

        // Return the list of cars as a HATEOAS CollectionModel, including a self-link to this endpoint
        return ResponseEntity.ok(CollectionModel.of(
                cars,
                linkTo(methodOn(CarController.class).getAvailableCars()).withSelfRel()
        ));
    }

    @PostMapping("/{carId}/lease/{customerId}")
    public ResponseEntity<Void> leaseCar(@PathVariable Long carId, @PathVariable Long customerId) {
        leaseCarUseCase.leaseCar(carId, customerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/leased-car")
    public ResponseEntity<EntityModel<Car>> getLeasedCar() {
        Long customerId = authenticationService.getAuthenticatedCustomerId();

        Car leasedCar = getLeasedCarUseCase.getLeasedCarByCustomer(customerId);

        EntityModel<Car> model = EntityModel.of(
                leasedCar,
                linkTo(methodOn(CarController.class).returnCar(leasedCar.getId())).withRel("return").withType("POST"),
                linkTo(methodOn(CarController.class).getLeasedCar()).withSelfRel()
        );
        return ResponseEntity.ok(model);
    }


    @PostMapping("/{id}/return")
    public ResponseEntity<Void> returnCar(@PathVariable Long id) {
        returnCarUseCase.returnCar(id);
        return ResponseEntity.ok().build();
    }

}

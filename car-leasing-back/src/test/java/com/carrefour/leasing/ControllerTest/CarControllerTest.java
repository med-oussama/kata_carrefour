package com.carrefour.leasing.ControllerTest;

import com.carrefour.leasing.adapter.in.web.CarController;
import com.carrefour.leasing.domain.model.Car;
import com.carrefour.leasing.domain.model.CarStatus;
import com.carrefour.leasing.domain.port.in.GetAvailableCarsUseCase;
import com.carrefour.leasing.domain.port.in.GetLeasedCarByCustomerUseCase;
import com.carrefour.leasing.domain.port.in.LeaseCarUseCase;
import com.carrefour.leasing.domain.port.in.ReturnCarUseCase;
import com.carrefour.leasing.domain.port.out.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CarControllerTest {

    // Mock dependencies for the controller
    @Mock
    private GetLeasedCarByCustomerUseCase getLeasedCarUseCase;

    @Mock
    private LeaseCarUseCase leaseCarUseCase;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private GetAvailableCarsUseCase getAvailableCarsUseCase;

    @Mock
    private ReturnCarUseCase returnCarUseCase;

    // Inject mocks into the CarController
    @InjectMocks
    private CarController carController;

    // MockMvc to simulate HTTP requests
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc with the CarController
        this.mockMvc = MockMvcBuilders.standaloneSetup(carController).build();
    }

    @Test
    void shouldReturnAvailableCarsWithLeaseLinks() throws Exception {
        // Mock data for available cars
        Long customerId = 3L;
        Car car1 = new Car(1L, "Renault Clio", CarStatus.AVAILABLE, null);
        Car car2 = new Car(3L, "Citroën C3", CarStatus.AVAILABLE, null);

        // Mock behavior of dependencies
        when(authenticationService.getAuthenticatedCustomerId()).thenReturn(customerId);
        when(getAvailableCarsUseCase.getAvailableCars()).thenReturn(List.of(car1, car2));

        // Perform GET request and verify the response
        mockMvc.perform(get("/cars/available"))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.content[0].id").value("1")) // Verify car1 details
                .andExpect(jsonPath("$.content[0].model").value("Renault Clio"))
                .andExpect(jsonPath("$.content[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$.content[0].links[?(@.rel == 'lease')].href").value("http://localhost/cars/1/lease/3"))
                .andExpect(jsonPath("$.content[1].id").value("3")) // Verify car2 details
                .andExpect(jsonPath("$.content[1].model").value("Citroën C3"))
                .andExpect(jsonPath("$.content[1].status").value("AVAILABLE"))
                .andExpect(jsonPath("$.content[1].links[?(@.rel == 'lease')].href").value("http://localhost/cars/3/lease/3"))
                .andExpect(jsonPath("$.links[?(@.rel == 'self')].href").value("http://localhost/cars/available"));

        // Verify interactions with mocked dependencies
        verify(authenticationService).getAuthenticatedCustomerId();
        verify(getAvailableCarsUseCase).getAvailableCars();
    }

    @Test
    void shouldReturnLeasedCarForAuthenticatedCustomer() throws Exception {
        Long customerId = 5L;
        Car leasedCar = new Car(1L, "Renault Clio", CarStatus.LEASED, customerId);

        when(authenticationService.getAuthenticatedCustomerId()).thenReturn(customerId);
        when(getLeasedCarUseCase.getLeasedCarByCustomer(customerId)).thenReturn(leasedCar);

        mockMvc.perform(get("/cars/leased-car"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.model").value("Renault Clio"))
                .andExpect(jsonPath("$.status").value("LEASED"))
                .andExpect(jsonPath("$.customerId").value(5))
                .andExpect(jsonPath("$.links[?(@.rel == 'return')].href").value(hasItem("http://localhost/cars/1/return")))
                .andExpect(jsonPath("$.links[?(@.rel == 'self')].href").value(hasItem("http://localhost/cars/leased-car")));

        verify(authenticationService).getAuthenticatedCustomerId();
        verify(getLeasedCarUseCase).getLeasedCarByCustomer(customerId);
    }


    @Test
    void shouldLeaseCarThroughControllerSuccessfully() throws Exception {
        // Mock behavior of leaseCarUseCase
        doNothing().when(leaseCarUseCase).leaseCar(1L, 1L);

        // Perform POST request to lease a car
        mockMvc.perform(post("/cars/1/lease/1"))
                .andExpect(status().isOk()); // Expect HTTP 200 OK

        // Verify interaction with leaseCarUseCase
        verify(leaseCarUseCase).leaseCar(1L, 1L);
    }

    @Test
    void shouldReturnCarThroughControllerSuccessfully() throws Exception {
        // Mock behavior of returnCarUseCase
        doNothing().when(returnCarUseCase).returnCar(1L);

        // Perform POST request to return a car
        mockMvc.perform(post("/cars/1/return"))
                .andExpect(status().isOk()); // Expect HTTP 200 OK

        // Verify interaction with returnCarUseCase
        verify(returnCarUseCase).returnCar(1L);
    }

    @Test
    void shouldReturnNotFoundForInvalidCarIdInLease() throws Exception {
        // Perform POST request with an invalid car ID
        mockMvc.perform(post("/cars//lease/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expect HTTP 404 Not Found
    }

    @Test
    void shouldReturnBadRequestForInvalidCarIdInLease() throws Exception {
        // Perform POST request with an invalid car ID format
        mockMvc.perform(post("/cars/1/lease/AB")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Expect HTTP 400 Bad Request
    }
}

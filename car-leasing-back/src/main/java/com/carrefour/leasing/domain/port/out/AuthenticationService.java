package com.carrefour.leasing.domain.port.out;

import org.springframework.stereotype.Component;

@Component
public interface AuthenticationService {
    Long getAuthenticatedCustomerId();
}

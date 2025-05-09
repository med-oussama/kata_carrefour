package com.carrefour.leasing.adapter.security;


import com.carrefour.leasing.adapter.out.persistence.CustomerJpaRepository;
import com.carrefour.leasing.domain.port.out.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationServiceAdapter implements AuthenticationService {

    private final CustomerJpaRepository customerRepository;

    @Override
    public Long getAuthenticatedCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            return customerRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Customer not found with firstName: " + username))
                    .getId();
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }
}

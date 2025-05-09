package com.carrefour.leasing.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface CarJpaRepository extends JpaRepository<CarEntity, Long> {
    Collection<CarEntity> findByStatus(String status);
    Optional<CarEntity> findByCustomerId(Long customerId);
}

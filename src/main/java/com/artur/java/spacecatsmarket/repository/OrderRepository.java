package com.artur.java.spacecatsmarket.repository;

import com.artur.java.spacecatsmarket.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByNumber(String number);
}

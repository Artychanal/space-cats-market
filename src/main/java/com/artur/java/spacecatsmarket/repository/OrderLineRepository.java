package com.artur.java.spacecatsmarket.repository;

import com.artur.java.spacecatsmarket.persistence.entity.OrderLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineRepository extends JpaRepository<OrderLineEntity, Long> {
}

package com.artur.java.spacecatsmarket.repository;

import com.artur.java.spacecatsmarket.domain.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {
}

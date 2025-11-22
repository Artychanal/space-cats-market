package com.artur.java.spacecatsmarket.repository;

import com.artur.java.spacecatsmarket.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCodeIgnoreCase(String code);
}

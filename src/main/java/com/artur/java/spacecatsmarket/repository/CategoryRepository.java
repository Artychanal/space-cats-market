package com.artur.java.spacecatsmarket.repository;

import com.artur.java.spacecatsmarket.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByCodeIgnoreCase(String code);
}

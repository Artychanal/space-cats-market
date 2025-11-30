package com.artur.java.spacecatsmarket.repository;

import com.artur.java.spacecatsmarket.persistence.entity.CategoryEntity;
import com.artur.java.spacecatsmarket.persistence.entity.ProductEntity;
import com.artur.java.spacecatsmarket.repository.projection.ProductSalesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsByNameIgnoreCaseAndCategory(String name, CategoryEntity category);
    boolean existsByNameIgnoreCaseAndCategoryAndIdNot(String name, CategoryEntity category, Long id);

    @Query(value = """
            select new com.artur.java.spacecatsmarket.repository.projection.ProductSalesProjection(
                p.id, p.name, sum(l.qty))
            from OrderLineEntity l
            join l.product p
            group by p.id, p.name
            order by sum(l.qty) desc
            """,
            countQuery = """
            select count(distinct p.id)
            from OrderLineEntity l
            join l.product p
            """)
    Page<ProductSalesProjection> findTopSellingProducts(Pageable pageable);
}

package com.artur.java.spacecatsmarket.repository;

import com.artur.java.spacecatsmarket.domain.Category;
import com.artur.java.spacecatsmarket.domain.Product;
import com.artur.java.spacecatsmarket.repository.projection.ProductSalesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByNameIgnoreCaseAndCategory(String name, Category category);
    boolean existsByNameIgnoreCaseAndCategoryAndIdNot(String name, Category category, Long id);

    @Query(value = """
            select new com.artur.java.spacecatsmarket.repository.projection.ProductSalesProjection(
                p.id, p.name, sum(l.qty))
            from OrderLine l
            join l.product p
            group by p.id, p.name
            order by sum(l.qty) desc
            """,
            countQuery = """
            select count(distinct p.id)
            from OrderLine l
            join l.product p
            """)
    Page<ProductSalesProjection> findTopSellingProducts(Pageable pageable);
}

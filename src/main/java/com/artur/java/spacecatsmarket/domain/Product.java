package com.artur.java.spacecatsmarket.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_product_name_category",
                        columnNames = {"name", "category_id"})
        },
        indexes = {
                @Index(name = "idx_product_name", columnList = "name")
        })
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "product_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_category"))
    private Category category;
}

package com.artur.java.spacecatsmarket.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_lines",
        uniqueConstraints = @UniqueConstraint(name = "uk_order_line_order_product",
                columnNames = {"order_id", "product_id"}))
public class OrderLineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_line_seq")
    @SequenceGenerator(name = "order_line_seq", sequenceName = "order_line_seq", allocationSize = 50)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_line_order"))
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_line_product"))
    private ProductEntity product;

    @Column(nullable = false)
    private int qty;

    @Column(name = "price_at_purchase", nullable = false, precision = 15, scale = 2)
    private BigDecimal priceAtPurchase;
}

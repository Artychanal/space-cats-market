package com.artur.java.spacecatsmarket.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_order_number", columnNames = "order_number"),
                @UniqueConstraint(name = "uk_order_customer_email", columnNames = "customer_email")
        })
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq", sequenceName = "order_seq", allocationSize = 50)
    private Long id;

    @NaturalId
    @Column(name = "order_number", nullable = false, length = 64, updatable = false)
    private String number;

    @Column(name = "customer_email", nullable = false, length = 255)
    private String customerEmail;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @Builder.Default
    private List<OrderLineEntity> lines = new ArrayList<>();
}

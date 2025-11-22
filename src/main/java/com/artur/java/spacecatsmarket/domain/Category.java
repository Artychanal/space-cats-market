package com.artur.java.spacecatsmarket.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categories",
        uniqueConstraints = @UniqueConstraint(name = "uk_category_code", columnNames = "code"))
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(name = "category_seq", sequenceName = "category_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 64)
    private String code;

    @Column(nullable = false, length = 128)
    private String title;

    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}

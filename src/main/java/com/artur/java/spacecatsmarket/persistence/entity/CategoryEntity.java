package com.artur.java.spacecatsmarket.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories",
        uniqueConstraints = @UniqueConstraint(name = "uk_category_code", columnNames = "code"))
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(name = "category_seq", sequenceName = "category_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false, length = 64)
    private String code;

    @Column(nullable = false, length = 128)
    private String title;

    @OneToMany(mappedBy = "category")
    private List<ProductEntity> products = new ArrayList<>();
}

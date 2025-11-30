package com.artur.java.spacecatsmarket.mapper;

import com.artur.java.spacecatsmarket.domain.Product;
import com.artur.java.spacecatsmarket.persistence.entity.CategoryEntity;
import com.artur.java.spacecatsmarket.persistence.entity.ProductEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = CommonMappers.class)
public interface ProductEntityMapper {

    @Mapping(target = "category", source = "category")
    @Mapping(target = "id", source = "domain.id")
    ProductEntity toEntity(Product domain, CategoryEntity category);

    @Mapping(target = "categoryCode", source = "category.code")
    Product toDomain(ProductEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    void merge(@MappingTarget ProductEntity target, ProductEntity source);
}

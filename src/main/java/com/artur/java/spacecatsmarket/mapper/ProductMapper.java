package com.artur.java.spacecatsmarket.mapper;

import com.artur.java.spacecatsmarket.domain.Product;
import com.artur.java.spacecatsmarket.dto.*;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(config = CommonMappers.class)
public interface ProductMapper {

    @Mapping(target="id", expression = "java(java.util.UUID.randomUUID())")
    Product toProduct(ProductRequestDto dto);

    ProductResponseDto toProductDto(Product entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Product target, ProductUpdateDto dto);

    default Product withId(Product p, UUID id){ p.setId(id); return p; }
}

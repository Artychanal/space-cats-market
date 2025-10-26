package com.artur.java.spacecatsmarket.mapper;

import com.artur.java.spacecatsmarket.domain.Product;
import com.artur.java.spacecatsmarket.dto.*;
import org.mapstruct.*;

@Mapper(config = CommonMappers.class)
public interface ProductMapper {

    @Mapping(target="id", expression = "java(java.util.UUID.randomUUID())")
    Product toProduct(ProductRequestDto dto);

    ProductResponseDto toProductDto(Product entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void merge(@MappingTarget Product target, ProductUpdateDto dto);
}

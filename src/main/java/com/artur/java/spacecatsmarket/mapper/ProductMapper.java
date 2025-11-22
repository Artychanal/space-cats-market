package com.artur.java.spacecatsmarket.mapper;

import com.artur.java.spacecatsmarket.domain.Product;
import com.artur.java.spacecatsmarket.dto.*;
import org.mapstruct.*;

@Mapper(config = CommonMappers.class)
public interface ProductMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "id", ignore = true)
    Product toProduct(ProductRequestDto dto);

    @Mapping(target = "categoryCode", source = "category.code")
    ProductResponseDto toProductDto(Product entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    void merge(@MappingTarget Product target, ProductUpdateDto dto);
}

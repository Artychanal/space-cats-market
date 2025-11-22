package com.artur.java.spacecatsmarket.mapper;

import com.artur.java.spacecatsmarket.domain.Category;
import com.artur.java.spacecatsmarket.dto.CategoryRequestDto;
import com.artur.java.spacecatsmarket.dto.CategoryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CommonMappers.class)
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryRequestDto dto);
    CategoryResponseDto toDto(Category category);
}

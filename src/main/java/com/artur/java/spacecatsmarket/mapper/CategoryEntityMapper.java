package com.artur.java.spacecatsmarket.mapper;

import com.artur.java.spacecatsmarket.domain.Category;
import com.artur.java.spacecatsmarket.persistence.entity.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(config = CommonMappers.class)
public interface CategoryEntityMapper {
    @org.mapstruct.Mapping(target = "products", ignore = true)
    CategoryEntity toEntity(Category domain);

    Category toDomain(CategoryEntity entity);
}

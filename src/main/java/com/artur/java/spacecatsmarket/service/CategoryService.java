package com.artur.java.spacecatsmarket.service;

import com.artur.java.spacecatsmarket.dto.CategoryRequestDto;
import com.artur.java.spacecatsmarket.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto request);
    List<CategoryResponseDto> getAll();
}

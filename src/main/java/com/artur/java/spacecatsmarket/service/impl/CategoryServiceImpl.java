package com.artur.java.spacecatsmarket.service.impl;

import com.artur.java.spacecatsmarket.dto.CategoryRequestDto;
import com.artur.java.spacecatsmarket.dto.CategoryResponseDto;
import com.artur.java.spacecatsmarket.mapper.CategoryEntityMapper;
import com.artur.java.spacecatsmarket.mapper.CategoryMapper;
import com.artur.java.spacecatsmarket.repository.CategoryRepository;
import com.artur.java.spacecatsmarket.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryEntityMapper categoryEntityMapper;

    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto request) {
        var domain = categoryMapper.toDomain(request);
        var entity = categoryEntityMapper.toEntity(domain);
        var saved = categoryRepository.save(entity);
        return categoryMapper.toDto(categoryEntityMapper.toDomain(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryEntityMapper::toDomain)
                .map(categoryMapper::toDto)
                .toList();
    }
}

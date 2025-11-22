package com.artur.java.spacecatsmarket.service.impl;

import com.artur.java.spacecatsmarket.dto.CategoryRequestDto;
import com.artur.java.spacecatsmarket.dto.CategoryResponseDto;
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
    private final CategoryMapper mapper;

    @Override
    public CategoryResponseDto create(CategoryRequestDto request) {
        var entity = mapper.toEntity(request);
        var saved = categoryRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}

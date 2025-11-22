package com.artur.java.spacecatsmarket.web;

import com.artur.java.spacecatsmarket.dto.CategoryRequestDto;
import com.artur.java.spacecatsmarket.dto.CategoryResponseDto;
import com.artur.java.spacecatsmarket.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CategoryResponseDto> create(@Valid @RequestBody CategoryRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }
}

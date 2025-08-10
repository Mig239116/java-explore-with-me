package ru.practicum.mainsvc.category.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainsvc.category.dto.CategoriesListDto;
import ru.practicum.mainsvc.category.dto.CategoryDto;
import ru.practicum.mainsvc.category.service.CategoryService;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping(path = "/categories")
public class CategoryPublicController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryPublicController(@Qualifier("categoryServiceImpl") CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CategoriesListDto getCategories(
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.debug("GET/categories - getting all categories");
        return new CategoriesListDto(new ArrayList<>(categoryService.getCategories(from, size)));
    }

    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategory(@PathVariable Long categoryId) {
        log.debug("GET/categories/id getting category {}", categoryId);
        return categoryService.getCategory(categoryId);
    }
}

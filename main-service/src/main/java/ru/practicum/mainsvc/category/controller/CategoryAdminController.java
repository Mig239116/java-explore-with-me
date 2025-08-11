package ru.practicum.mainsvc.category.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainsvc.category.dto.CategoryDto;
import ru.practicum.mainsvc.category.dto.NewCategoryDto;
import ru.practicum.mainsvc.category.service.CategoryService;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryAdminController(@Qualifier("categoryServiceImpl") CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto categoryDto) {
        log.debug("POST/admin/categories posting new category {}", categoryDto.getName());
        CategoryDto categoryDtoStored = categoryService.addCategory(categoryDto);
        log.debug("POST/admin/categories posting new category {} with id {}",
                categoryDtoStored.getName(),
                categoryDtoStored.getId()
        );
        return categoryDtoStored;
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long categoryId) {
        log.debug("DELETE/admin/categories/id deleting category {}", categoryId);
        categoryService.deleteCategory(categoryId);
    }

    @PatchMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(
            @PathVariable Long categoryId,
            @RequestBody @Valid NewCategoryDto newCategoryDto
    ) {
        log.debug("PATCH admin/categories/id updating category {} with id {}",
                newCategoryDto.getName(),
                categoryId);
        return categoryService.updateCategory(categoryId, newCategoryDto);
    }
}

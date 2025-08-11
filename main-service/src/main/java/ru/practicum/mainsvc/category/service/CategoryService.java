package ru.practicum.mainsvc.category.service;


import ru.practicum.mainsvc.category.dto.CategoryDto;
import ru.practicum.mainsvc.category.dto.NewCategoryDto;

import java.util.Collection;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto categoryDto);

    void deleteCategory(Long categoryId);

    CategoryDto updateCategory(Long categoryId, NewCategoryDto newCategoryDto);

    Collection<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(Long categoryId);
}

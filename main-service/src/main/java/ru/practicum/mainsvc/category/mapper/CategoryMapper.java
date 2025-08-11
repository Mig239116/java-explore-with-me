package ru.practicum.mainsvc.category.mapper;

import ru.practicum.mainsvc.category.dto.CategoryDto;
import ru.practicum.mainsvc.category.dto.NewCategoryDto;
import ru.practicum.mainsvc.category.model.Category;

public class CategoryMapper {

    public static Category toCategory(NewCategoryDto newCategoryDto) {
        Category category = new Category();
        category.setName(newCategoryDto.getName());
        return category;
    }

    public static CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}

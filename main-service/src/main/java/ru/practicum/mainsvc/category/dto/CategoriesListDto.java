package ru.practicum.mainsvc.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoriesListDto {
    private List<CategoryDto> categories;
}

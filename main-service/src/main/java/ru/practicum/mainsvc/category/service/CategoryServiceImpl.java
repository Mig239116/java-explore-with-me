package ru.practicum.mainsvc.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainsvc.category.dto.CategoryDto;
import ru.practicum.mainsvc.category.dto.NewCategoryDto;
import ru.practicum.mainsvc.category.mapper.CategoryMapper;
import ru.practicum.mainsvc.category.model.Category;
import ru.practicum.mainsvc.category.repository.CategoryRepository;
import ru.practicum.mainsvc.errors.ConflictException;
import ru.practicum.mainsvc.errors.NotFoundException;
import ru.practicum.mainsvc.event.repository.EventRepository;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        validateName(newCategoryDto.getName());
        Category category = CategoryMapper.toCategory(newCategoryDto);
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        validateNotFound(categoryId);
        validateIfEventsExists(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long categoryId, NewCategoryDto categoryDto) {
        Category category = validateNotFound(categoryId);
        if (!Objects.equals(category.getName(), categoryDto.getName())) {
            validateName(categoryDto.getName());
        }
        category.setName(categoryDto.getName());
        return CategoryMapper.toDto(category);
    }

    @Override
    public Collection<CategoryDto> getCategories(int from, int size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by("id").ascending());
        Collection<Category> categories = categoryRepository.findAllBy(page).getContent();
        return categories.stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Long categoryId) {
        Category category = validateNotFound(categoryId);
        return CategoryMapper.toDto(category);
    }

    private void validateName(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new ConflictException("Not unique category");
        }
    }

    private void validateIfEventsExists(Long id) {
        if (eventRepository.existsByCategoryId(id)) {
            throw new ConflictException("Can not delete category with events");
        }
    }

    private Category validateNotFound(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> {
                    NotFoundException e = new NotFoundException("Category " + id + " not found");
                    log.error(e.getMessage());
                    return e;
                }
        );
    }
}

package ru.practicum.mainsvc.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainsvc.category.model.Category;



public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findAllBy(Pageable pageable);

    boolean existsByName(String name);
}

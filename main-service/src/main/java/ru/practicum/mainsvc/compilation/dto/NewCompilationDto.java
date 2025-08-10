package ru.practicum.mainsvc.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    @Size(min = 1, max = 50, message = "Title length must be between 1 and 50 characters")
    @NotBlank(message = "Title cannot be blank")
    private String title;

    private Boolean pinned = false;

    private List<Long> events;
}

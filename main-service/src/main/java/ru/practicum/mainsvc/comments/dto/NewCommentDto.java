package ru.practicum.mainsvc.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentDto {

    @NotBlank
    @Size(min = 1, max = 1000)
    private String text;

    @NotNull
    @Positive
    private Long eventId;
}

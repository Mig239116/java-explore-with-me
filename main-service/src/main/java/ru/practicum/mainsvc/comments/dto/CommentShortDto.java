package ru.practicum.mainsvc.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainsvc.user.dto.UserShortDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentShortDto {
    private Long id;
    private String text;
    private UserShortDto author;
}

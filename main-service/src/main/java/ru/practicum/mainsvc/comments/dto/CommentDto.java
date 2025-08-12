package ru.practicum.mainsvc.comments.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainsvc.comments.model.CommentState;
import ru.practicum.mainsvc.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;

    private String text;

    private Long eventId;

    private UserShortDto author;

    private CommentState state;

    private LocalDateTime created;

    private LocalDateTime changed;

    private String rejectionReason;
}

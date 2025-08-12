package ru.practicum.mainsvc.comments.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainsvc.comments.model.CommentAction;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateAdminRequest {

    private CommentAction action;

    @Size(max = 1000)
    private String rejectionReason;
}

package ru.practicum.mainsvc.comments.service;

import org.springframework.cglib.core.Local;
import ru.practicum.mainsvc.comments.dto.CommentDto;
import ru.practicum.mainsvc.comments.dto.CommentUpdateAdminRequest;
import ru.practicum.mainsvc.comments.dto.NewCommentDto;
import ru.practicum.mainsvc.comments.dto.UpdateCommentDto;
import ru.practicum.mainsvc.comments.model.CommentState;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface CommentService {

    CommentDto addComment(NewCommentDto newCommentDto, Long userId);

    CommentDto updateComment(
            UpdateCommentDto updateCommentDto,
            Long userId,
            Long commentId
    );

    void deleteComment(
            Long userId,
            Long commentId
    );

    CommentDto getComment(
            Long userId,
            Long commentId
    );

    CommentDto updateComment(CommentUpdateAdminRequest commentDto, Long commentId);

    Collection<CommentDto> getComments(
            List<Long> users,
            List<CommentState> states,
            List<Long> events,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    );

    Collection<CommentDto> getComments(
            List<CommentState> states,
            Long userId,
            int from,
            int size
    );
}

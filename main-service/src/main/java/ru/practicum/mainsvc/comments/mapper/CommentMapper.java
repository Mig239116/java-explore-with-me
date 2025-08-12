package ru.practicum.mainsvc.comments.mapper;

import ru.practicum.mainsvc.comments.dto.CommentDto;
import ru.practicum.mainsvc.comments.dto.CommentShortDto;
import ru.practicum.mainsvc.comments.dto.NewCommentDto;
import ru.practicum.mainsvc.comments.model.Comment;
import ru.practicum.mainsvc.comments.model.CommentState;
import ru.practicum.mainsvc.event.model.Event;
import ru.practicum.mainsvc.user.mapper.UserMapper;
import ru.practicum.mainsvc.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toComment(NewCommentDto newCommentDto) {
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setState(CommentState.PENDING);
        comment.setCreated(LocalDateTime.now());
        comment.setChanged(LocalDateTime.now());
        comment.setRejectionReason(null);
        comment.setAuthor(new User());
        comment.setEvent(new Event());
        return comment;
    }


    public static CommentDto toFullDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getEvent().getId(),
                UserMapper.toShortDto(comment.getAuthor()),
                comment.getState(),
                comment.getCreated(),
                comment.getChanged(),
                comment.getRejectionReason()
        );
    }

    public static CommentShortDto toShortDto(Comment comment) {
        return new CommentShortDto(
                comment.getId(),
                comment.getText(),
               UserMapper.toShortDto(comment.getAuthor())
        );
    }
}

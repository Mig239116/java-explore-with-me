package ru.practicum.mainsvc.comments.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainsvc.comments.dto.CommentDto;
import ru.practicum.mainsvc.comments.dto.NewCommentDto;
import ru.practicum.mainsvc.comments.dto.UpdateCommentDto;
import ru.practicum.mainsvc.comments.model.CommentState;
import ru.practicum.mainsvc.comments.service.CommentService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/comments")
@Validated
public class CommentsPrivateController {
    private final CommentService commentService;

    @Autowired
    public CommentsPrivateController(@Qualifier("commentServiceImpl") CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(
            @RequestBody @Valid NewCommentDto newCommentDto,
            @PathVariable Long userId
    ) {
        log.debug("POST users/id/events/id/comments - posting a comment");
        CommentDto commentDto = commentService.addComment(newCommentDto, userId);
        log.debug(
                "POST users/id/events/id/comments - comment posted with id {} by user {} for event {}",
                commentDto.getId(),
                commentDto.getEventId(),
                commentDto.getAuthor().getId()
        );
        return commentDto;
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@RequestBody @Valid UpdateCommentDto updateCommentDto,
                                    @PathVariable Long userId,
                                    @PathVariable Long commentId
    ) {
        log.debug("PATCH users/id/events/id/comments/id - updating a comment {}", commentId);
        return commentService.updateComment(updateCommentDto, userId, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        log.debug("DELETE users/id/events/id/comments/id - deleting a comment {}", commentId);
        commentService.deleteComment(userId, commentId);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getComment(
            @PathVariable Long userId,
            @PathVariable Long commentId
    ) {
        log.debug("GET users/id/events/id/comments/id getting comment {}", commentId);
        return commentService.getComment(userId, commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentDto> getComments(
            @RequestParam(required = false) List<String> states,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.debug("GET users/id/comments - getting comments with user criterias");
        return commentService.getComments(
                parseStates(states),
                userId,
                from,
                size
        );
    }

    private List<CommentState> parseStates(List<String> states) {
        if (states == null) return null;
        return states.stream()
                .map(CommentState::valueOf)
                .collect(Collectors.toList());
    }
}

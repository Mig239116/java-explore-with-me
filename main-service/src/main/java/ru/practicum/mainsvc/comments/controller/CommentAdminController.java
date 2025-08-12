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
import ru.practicum.mainsvc.comments.dto.CommentUpdateAdminRequest;
import ru.practicum.mainsvc.comments.model.CommentState;
import ru.practicum.mainsvc.comments.service.CommentService;
import ru.practicum.mainsvc.errors.BadRequestException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/admin/comments")
@Validated
public class CommentAdminController {
    private final CommentService commentService;

    @Autowired
    public CommentAdminController(@Qualifier("commentServiceImpl") CommentService commentService) {
        this.commentService = commentService;
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(
            @RequestBody @Valid CommentUpdateAdminRequest commentDto,
            @PathVariable Long commentId
    ) {
        log.debug("PATCH admin/comments/id - moderating comment {}", commentDto);
        return commentService.updateComment(commentDto, commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentDto> getComments(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> events,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.debug("GET admin/comments - getting comments with admin criterias");
        if (rangeEnd != null && parseDateTime(rangeEnd).isBefore(parseDateTime(rangeStart))) {
            throw new BadRequestException("The end date is before start");
        }
        return commentService.getComments(
                users,
                parseStates(states),
                events,
                parseDateTime(rangeStart),
                parseDateTime(rangeEnd),
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

    private LocalDateTime parseDateTime(String dateTime) {
        if (dateTime == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid date format. Expected format: yyyy-MM-dd HH:mm:ss");
        }
    }
}

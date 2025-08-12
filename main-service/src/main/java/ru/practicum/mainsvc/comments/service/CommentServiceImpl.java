package ru.practicum.mainsvc.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainsvc.comments.dto.CommentDto;
import ru.practicum.mainsvc.comments.dto.CommentUpdateAdminRequest;
import ru.practicum.mainsvc.comments.dto.NewCommentDto;
import ru.practicum.mainsvc.comments.dto.UpdateCommentDto;
import ru.practicum.mainsvc.comments.mapper.CommentMapper;
import ru.practicum.mainsvc.comments.model.Comment;
import ru.practicum.mainsvc.comments.model.CommentState;
import ru.practicum.mainsvc.comments.repository.CommentRepository;
import ru.practicum.mainsvc.errors.ConflictException;
import ru.practicum.mainsvc.errors.NoAuthorizationException;
import ru.practicum.mainsvc.errors.NotFoundException;
import ru.practicum.mainsvc.event.model.Event;
import ru.practicum.mainsvc.event.repository.EventRepository;
import ru.practicum.mainsvc.user.model.User;
import ru.practicum.mainsvc.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentDto addComment(NewCommentDto newCommentDto, Long userId) {
        User author = getUserOrThrow(userId);
        Event event = getEventOrThrow(newCommentDto.getEventId());
        Comment comment = CommentMapper.toComment(newCommentDto);
        comment.setEvent(event);
        comment.setAuthor(author);
        return CommentMapper.toFullDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto updateComment(UpdateCommentDto updateCommentDto, Long userId, Long commentId) {
        User user = getUserOrThrow(userId);
        Comment comment = getCommentOrThrow(commentId);
        validateAuthor(user.getId(), comment.getAuthor().getId());
        comment.setState(CommentState.PENDING);
        comment.setText(updateCommentDto.getText());
        comment.setChanged(LocalDateTime.now());
        return CommentMapper.toFullDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        User user = getUserOrThrow(userId);
        Comment comment = getCommentOrThrow(commentId);
        validateAuthor(user.getId(), comment.getAuthor().getId());
        commentRepository.delete(comment);
    }

    @Override
    public CommentDto getComment(Long userId, Long commentId) {
        User user = getUserOrThrow(userId);
        Comment comment = getCommentOrThrow(commentId);
        validateAuthor(user.getId(), comment.getAuthor().getId());
        return CommentMapper.toFullDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(CommentUpdateAdminRequest commentDto, Long commentId) {
        Comment comment = getCommentOrThrow(commentId);
        if (commentDto.getAction() != null) {
            switch (commentDto.getAction()) {
                case PUBLISH_COMMENT:
                    if (comment.getState() != CommentState.PENDING) {
                        throw new ConflictException("Can not publish comment in current state");
                    }
                    comment.setState(CommentState.PUBLISHED);
                    break;
                case REJECT_COMMENT:
                    if (comment.getState() == CommentState.PUBLISHED) {
                        throw new ConflictException("Can not reject published comment");
                    }
                    comment.setState(CommentState.REJECTED);
                    comment.setRejectionReason(commentDto.getRejectionReason());
                    break;
                default:
                    throw new ConflictException("Invalid state for admin");
            }
        }
        return CommentMapper.toFullDto(comment);
    }

    @Override
    public Collection<CommentDto> getComments(
            List<Long> users,
            List<CommentState> states,
            List<Long> events,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    ) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return commentRepository.findAllByAdmin(users, states, events, rangeStart, rangeEnd, pageable).stream()
                .map(CommentMapper::toFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<CommentDto> getComments(
            List<CommentState> states,
            Long userId,
            int from,
            int size
    ) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return commentRepository.findAllByPrivate(states, userId, pageable).stream()
                .map(CommentMapper::toFullDto)
                .collect(Collectors.toList());
    }

    private void validateAuthor(Long userId, Long authorId) {
        if (userId != authorId) {
            throw new NoAuthorizationException("The user is not an author");
        }
    }

    private Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment " + commentId + " not found")
        );
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
    }

}

package ru.practicum.mainsvc.comments.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.mainsvc.comments.model.Comment;
import ru.practicum.mainsvc.comments.model.CommentState;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c " +
            "WHERE (:users IS NULL OR c.author.id IN :users) " +
            "AND (:states IS NULL OR c.state IN :states) " +
            "AND (:events IS NULL OR c.event.id IN :events) " +
            "AND (CAST(:rangeStart AS timestamp) IS NULL OR c.created >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS timestamp) IS NULL OR c.created <= :rangeEnd)")
    List<Comment> findAllByAdmin(@Param("users") List<Long> users,
                               @Param("states") List<CommentState> states,
                               @Param("events") List<Long> events,
                               @Param("rangeStart") LocalDateTime rangeStart,
                               @Param("rangeEnd") LocalDateTime rangeEnd,
                               Pageable pageable);

    @Query("SELECT c FROM Comment c " +
            "WHERE (c.author.id = :userId) " +
            "AND (:states IS NULL OR c.state IN :states)")
    List<Comment> findAllByPrivate(@Param("states") List<CommentState> states,
                                   @Param("userId") Long userId,
                                   Pageable pageable);

    List<Comment> findByEventIdInAndState(List<Long> eventIds, CommentState state);

    List<Comment> findByEventIdAndState(Long eventId, CommentState state);
}

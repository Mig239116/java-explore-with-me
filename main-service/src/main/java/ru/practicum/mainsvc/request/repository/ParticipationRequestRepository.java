package ru.practicum.mainsvc.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.mainsvc.request.model.ParticipationRequest;
import ru.practicum.mainsvc.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(Long userId);

    Optional<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long userId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByIdInAndEventId(List<Long> requestIds, Long eventId);

    @Query("SELECT COUNT(r) FROM ParticipationRequest r WHERE r.event.id = :eventId AND r.status = 'CONFIRMED'")
    Integer countConfirmedRequests(@Param("eventId") Long eventId);

    List<ParticipationRequest> findAllByEventInitiatorIdAndEventId(Long userId, Long eventId);

    List<ParticipationRequest> findAllByEventInitiatorIdAndEventIdAndStatus(Long userId, Long eventId, RequestStatus status);

    @Query("SELECT r FROM ParticipationRequest r " +
            "WHERE r.id IN :requestIds AND r.event.id = :eventId AND r.status = 'PENDING'")
    List<ParticipationRequest> findPendingRequestsByIdsAndEventId(
            @Param("requestIds") List<Long> requestIds,
            @Param("eventId") Long eventId);

    @Query("SELECT COUNT(r) FROM ParticipationRequest r WHERE r.event.id = :eventId AND r.status = :status")
    Integer countByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") RequestStatus status);

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    Optional<ParticipationRequest> findByIdAndRequesterId(Long id, Long requesterId);
}

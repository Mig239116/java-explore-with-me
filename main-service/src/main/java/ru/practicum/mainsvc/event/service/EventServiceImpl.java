package ru.practicum.mainsvc.event.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainsvc.category.model.Category;
import ru.practicum.mainsvc.category.repository.CategoryRepository;
import ru.practicum.mainsvc.common.stat.StatClientService;
import ru.practicum.mainsvc.errors.ConflictException;
import ru.practicum.mainsvc.errors.ForbiddenException;
import ru.practicum.mainsvc.errors.NotFoundException;
import ru.practicum.mainsvc.event.dto.*;
import ru.practicum.mainsvc.event.model.Event;
import ru.practicum.mainsvc.event.model.EventState;
import ru.practicum.mainsvc.event.mapper.EventMapper;
import ru.practicum.mainsvc.event.repository.EventRepository;
import ru.practicum.mainsvc.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainsvc.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainsvc.request.dto.ParticipationRequestDto;
import ru.practicum.mainsvc.request.mapper.ParticipationRequestMapper;
import ru.practicum.mainsvc.request.model.ParticipationRequest;
import ru.practicum.mainsvc.request.model.RequestStatus;
import ru.practicum.mainsvc.request.repository.ParticipationRequestRepository;
import ru.practicum.mainsvc.user.model.User;
import ru.practicum.mainsvc.user.repository.UserRepository;
import ru.practicum.statsdto.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository requestRepository;
    private final StatClientService statClientService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public EventFullDto addEvent(NewEventDto newEventDto, Long userId) {
        User initiator = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(newEventDto.getCategory());

        Event event = EventMapper.toEvent(newEventDto);
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        if (newEventDto.getPaid() == null) {
            event.setPaid(false);
        }
        if (newEventDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        if (newEventDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        Event savedEvent = eventRepository.save(event);
        return countableParametersFullDto(event);
    }

    @Override
    public Collection<EventShortDto> getEvents(Long userId) {
        getUserOrThrow(userId);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("eventDate").descending());
        return eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(this::countableParametersShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<EventFullDto> getEvents(List<Long> users, List<EventState> states,
                                              List<Long> categories, LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return eventRepository.findAllByAdmin(users, states, categories, rangeStart, rangeEnd, pageable).stream()
                .map(event -> {
                    EventFullDto dto = countableParametersFullDto(event);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Collection<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, int from, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> event = cq.from(Event.class);
        Pageable pageable = PageRequest.of(from / size, size,
                "VIEWS".equals(sort) ? Sort.by("views").descending() :
                        Sort.by("eventDate").ascending());
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(event.get("state"), EventState.PUBLISHED));
        if (text != null) {
            predicates.add(cb.or(
                    cb.like(cb.lower(event.get("annotation")), "%" + text.toLowerCase() + "%"),
                    cb.like(cb.lower(event.get("description")), "%" + text.toLowerCase() + "%")
            ));
        }
        if (categories != null && !categories.isEmpty()) {
            predicates.add(event.get("category").get("id").in(categories));
        }
        if (paid != null) {
            predicates.add(cb.equal(event.get("paid"), paid));
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        predicates.add(cb.greaterThanOrEqualTo(event.get("eventDate"), rangeStart));
        if (rangeEnd != null) {
            predicates.add(cb.lessThanOrEqualTo(event.get("eventDate"), rangeEnd));
        }
        cq.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Event> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        if (!onlyAvailable) {
            query.getResultList().stream()
                    .filter(evt -> {
                        if (evt.getParticipantLimit() == 0) {
                            return true;
                        }

                        long confirmedRequests = requestRepository.countByEventIdAndStatus(
                                evt.getId(),
                                RequestStatus.CONFIRMED
                        );

                        return confirmedRequests < evt.getParticipantLimit();
                    })
                    .map(EventMapper::toShortDto)
                    .collect(Collectors.toList());
        }
        return query.getResultList().stream()
                .map(this::countableParametersShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        getUserOrThrow(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        return countableParametersFullDto(event);
    }

    @Override
    public EventFullDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event not published");
        }
        return countableParametersFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(UpdateEventUserRequest requestDto, Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Cannot update published event");
        }

        updateEventFields(event, requestDto);

        if (requestDto.getStateAction() != null) {
            switch (requestDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new ConflictException("Invalid state action for user");
            }
        }

        Event updatedEvent = eventRepository.save(event);
        return countableParametersFullDto(updatedEvent);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(UpdateEventAdminRequest requestDto, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (requestDto.getStateAction() != null) {
            switch (requestDto.getStateAction()) {
                case PUBLISH_EVENT:
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException("Cannot publish event in current state");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Cannot reject published event");
                    }
                    event.setState(EventState.REJECTED);
                    break;
                default:
                    throw new ConflictException("Invalid state action for admin");
            }
        }

        updateEventFields(event, requestDto);
        Event updatedEvent = eventRepository.save(event);
        return countableParametersFullDto(updatedEvent);
    }

    @Override
    public Collection<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("User is not initiator of event");
        }

        return requestRepository.findAllByEventInitiatorIdAndEventId(userId, eventId).stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequests(EventRequestStatusUpdateRequest requestDto,
                                                              Long userId, Long eventId) {
        getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("User is not initiator of event");
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Event does not require request moderation");
        }

        List<ParticipationRequest> requests = requestRepository
                .findAllByIdInAndEventId(requestDto.getRequestIds(), eventId);

        if (requests.stream().anyMatch(r -> r.getStatus() != RequestStatus.PENDING)) {
            throw new ConflictException("Request must have status PENDING");
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        if (requestDto.getStatus() == RequestStatus.CONFIRMED) {
            Integer confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            if (confirmedCount >= event.getParticipantLimit()) {
                throw new ConflictException("Participant limit reached");
            }

            int availableSlots = event.getParticipantLimit() - (int) confirmedCount;
            int toConfirm = Math.min(availableSlots, requests.size());

            for (int i = 0; i < toConfirm; i++) {
                ParticipationRequest request = requests.get(i);
                request.setStatus(RequestStatus.CONFIRMED);
                result.getConfirmedRequests().add(ParticipationRequestMapper.toDto(request));
            }

            for (int i = toConfirm; i < requests.size(); i++) {
                ParticipationRequest request = requests.get(i);
                request.setStatus(RequestStatus.REJECTED);
                result.getRejectedRequests().add(ParticipationRequestMapper.toDto(request));
            }
        } else {
            requests.forEach(r -> r.setStatus(RequestStatus.REJECTED));
            result.setRejectedRequests(requests.stream()
                    .map(ParticipationRequestMapper::toDto)
                    .collect(Collectors.toList()));
        }

        requestRepository.saveAll(requests);
        return result;
    }

    private void updateEventFields(Event event, UpdateEventUserRequest requestDto) {
        if (requestDto.getAnnotation() != null) {
            event.setAnnotation(requestDto.getAnnotation());
        }
        if (requestDto.getCategory() != null) {
            event.setCategory(getCategoryOrThrow(requestDto.getCategory()));
        }
        if (requestDto.getDescription() != null) {
            event.setDescription(requestDto.getDescription());
        }
        if (requestDto.getEventDate() != null) {
            event.setEventDate(requestDto.getEventDate());
        }
        if (requestDto.getLocation() != null) {
            event.setLat(requestDto.getLocation().getLat());
            event.setLon(requestDto.getLocation().getLon());
        }
        if (requestDto.getPaid() != null) {
            event.setPaid(requestDto.getPaid());
        }
        if (requestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(requestDto.getParticipantLimit());
        }
        if (requestDto.getRequestModeration() != null) {
            event.setRequestModeration(requestDto.getRequestModeration());
        }
        if (requestDto.getTitle() != null) {
            event.setTitle(requestDto.getTitle());
        }
    }

    private void updateEventFields(Event event, UpdateEventAdminRequest requestDto) {
        if (requestDto.getAnnotation() != null) {
            event.setAnnotation(requestDto.getAnnotation());
        }
        if (requestDto.getCategory() != null) {
            event.setCategory(getCategoryOrThrow(requestDto.getCategory()));
        }
        if (requestDto.getDescription() != null) {
            event.setDescription(requestDto.getDescription());
        }
        if (requestDto.getEventDate() != null) {
            event.setEventDate(requestDto.getEventDate());
        }
        if (requestDto.getLocation() != null) {
            event.setLat(requestDto.getLocation().getLat());
            event.setLon(requestDto.getLocation().getLon());
        }
        if (requestDto.getPaid() != null) {
            event.setPaid(requestDto.getPaid());
        }
        if (requestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(requestDto.getParticipantLimit());
        }
        if (requestDto.getRequestModeration() != null) {
            event.setRequestModeration(requestDto.getRequestModeration());
        }
        if (requestDto.getTitle() != null) {
            event.setTitle(requestDto.getTitle());
        }
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    private EventFullDto countableParametersFullDto(Event event) {
        EventFullDto eventFullDto = EventMapper.toFullDto(event);
        eventFullDto.setConfirmedRequests(requestRepository.countConfirmedRequests(event.getId()));
        List<ViewStatsDto> viewsDto= statClientService.getEventStats(List.of(event.getId()),event.getCreatedOn(), LocalDateTime.now()).stream().toList();
        if (!viewsDto.isEmpty()) {
            eventFullDto.setViews(viewsDto.getFirst().getHits());
        }
        return eventFullDto;
    }

    private EventShortDto countableParametersShortDto(Event event) {
        EventShortDto eventShortDto = EventMapper.toShortDto(event);
        eventShortDto.setConfirmedRequests(requestRepository.countConfirmedRequests(event.getId()));
        List<ViewStatsDto> viewsDto= statClientService.getEventStats(List.of(event.getId()),event.getCreatedOn(), LocalDateTime.now()).stream().toList();
        if (!viewsDto.isEmpty()) {
            eventShortDto.setViews(viewsDto.getFirst().getHits());
        }
        return eventShortDto;
    }
}
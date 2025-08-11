package ru.practicum.mainsvc.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainsvc.compilation.dto.CompilationDto;
import ru.practicum.mainsvc.compilation.dto.NewCompilationDto;
import ru.practicum.mainsvc.compilation.dto.UpdateCompilationRequest;
import ru.practicum.mainsvc.compilation.mapper.CompilationMapper;
import ru.practicum.mainsvc.compilation.model.Compilation;
import ru.practicum.mainsvc.compilation.repository.CompilationRepository;
import ru.practicum.mainsvc.errors.NotFoundException;
import ru.practicum.mainsvc.event.dto.EventShortDto;
import ru.practicum.mainsvc.event.model.Event;
import ru.practicum.mainsvc.event.repository.EventRepository;
import ru.practicum.mainsvc.request.repository.ParticipationRequestRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository requestRepository;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(compilationDto);
        Set<Long> ids = compilationDto.getEvents() != null
                ? new HashSet<>(compilationDto.getEvents())
                : Collections.emptySet();
        compilation.setEvents(getEventsList(ids));
        Compilation compilationSaved = compilationRepository.save(compilation);
        CompilationDto compilationDtoSaved = CompilationMapper.toDto(compilationSaved);
        compilationDtoSaved.setEvents(getConfirmedRequests(compilationDtoSaved.getEvents().stream()
                .collect(Collectors.toSet())).stream()
                .toList());
        return compilationDtoSaved;
    }

    @Override
    @Transactional
    public void deleteCompilation(Long id) {
        validateNotFound(id);
        compilationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long id, UpdateCompilationRequest updateRequest) {
        Compilation compilation = validateNotFound(id);

        if (updateRequest.getTitle() != null) {
            compilation.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getPinned() != null) {
            compilation.setPinned(updateRequest.getPinned());
        }

        if (updateRequest.getEvents() != null) {
            Set<Long> eventIds = new HashSet<>(updateRequest.getEvents());
            compilation.setEvents(getEventsList(eventIds));
        }

        Compilation updatedCompilation = compilationRepository.save(compilation);
        CompilationDto compilationDto = CompilationMapper.toDto(updatedCompilation);
        compilationDto.setEvents(getConfirmedRequests(compilationDto.getEvents().stream()
                .collect(Collectors.toSet())).stream()
                .toList());
        return compilationDto;
    }

    @Override
    public Collection<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        Collection<Compilation> compilations;

        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, page).stream().toList();
        } else {
            compilations = compilationRepository.findAll(page).getContent();
        }

        return compilations.stream()
                .map(compilation -> {
                    CompilationDto dto = CompilationMapper.toDto(compilation);
                    dto.setEvents(getConfirmedRequests(dto.getEvents().stream()
                            .collect(Collectors.toSet())).stream()
                            .toList());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(Long id) {
        Compilation compilation = validateNotFound(id);
        CompilationDto compilationDto = CompilationMapper.toDto(compilation);
        compilationDto.setEvents(getConfirmedRequests(compilationDto.getEvents().stream()
                .collect(Collectors.toSet())).stream()
                .toList());
        return compilationDto;
    }

    private Compilation validateNotFound(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> {
                    NotFoundException e = new NotFoundException("Compilation " + id + " not found");
                    log.error(e.getMessage());
                    return e;
                });
    }

    private Set<Event> getEventsList(Set<Long> ids) {
        return new HashSet<>(eventRepository.findAllByIdIn(ids));
    }

    private Set<EventShortDto> getConfirmedRequests(Set<EventShortDto> events) {
        Set<EventShortDto> eventsWithCount = new HashSet<>();
        for (EventShortDto event : events) {
            event.setConfirmedRequests(requestRepository.countConfirmedRequests(event.getId()));
            eventsWithCount.add(event);
        }
        return eventsWithCount;
    }
}

package ru.practicum.mainsvc.compilation.mapper;

import ru.practicum.mainsvc.compilation.dto.CompilationDto;
import ru.practicum.mainsvc.compilation.dto.NewCompilationDto;
import ru.practicum.mainsvc.compilation.model.Compilation;
import ru.practicum.mainsvc.event.mapper.EventMapper;
import ru.practicum.mainsvc.event.model.Event;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(compilationDto.getPinned());
        compilation.setTitle(compilationDto.getTitle());
        compilation.setEvents(new HashSet<Event>());
        return compilation;
    }

    public static CompilationDto toDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.getPinned(),
                LocalDateTime.now(),
                compilation.getEvents().stream()
                        .map(EventMapper::toShortDto)
                        .collect(Collectors.toList())
        );
    }
}

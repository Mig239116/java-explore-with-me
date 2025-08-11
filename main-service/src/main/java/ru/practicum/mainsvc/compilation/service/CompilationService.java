package ru.practicum.mainsvc.compilation.service;

import ru.practicum.mainsvc.compilation.dto.CompilationDto;
import ru.practicum.mainsvc.compilation.dto.NewCompilationDto;
import ru.practicum.mainsvc.compilation.dto.UpdateCompilationRequest;

import java.util.Collection;

public interface CompilationService {

    CompilationDto addCompilation(NewCompilationDto compilationDto);

    void deleteCompilation(Long id);

    CompilationDto updateCompilation(Long id, UpdateCompilationRequest updateCompilationRequest);

    Collection<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilation(Long id);
}

package ru.practicum.mainsvc.compilation.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainsvc.compilation.dto.CompilationDto;
import ru.practicum.mainsvc.compilation.service.CompilationService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/compilations")
public class CompilationPublicController {
    private final CompilationService compilationService;

    @Autowired
    public CompilationPublicController(@Qualifier("compilationServiceImpl") CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping
    public Collection<CompilationDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.debug("GET /compilations - getting all compilations");
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        log.debug("GET /compilations/id - getting compilation {}", compId);
        return compilationService.getCompilation(compId);
    }

}

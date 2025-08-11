package ru.practicum.mainsvc.compilation.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainsvc.compilation.dto.CompilationDto;
import ru.practicum.mainsvc.compilation.dto.NewCompilationDto;
import ru.practicum.mainsvc.compilation.dto.UpdateCompilationRequest;
import ru.practicum.mainsvc.compilation.service.CompilationService;

@Slf4j
@RestController
@RequestMapping(path = "/admin/compilations")
public class CompilationAdminController {
    private final CompilationService compilationService;

    @Autowired
    public CompilationAdminController(@Qualifier("compilationServiceImpl") CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.debug("POST /compilations - creating new compilation");
        CompilationDto  compilationDtoStored = compilationService.addCompilation(newCompilationDto);
        log.debug("POST /compilations - compilation with id {} added", compilationDtoStored.getId());
        return compilationDtoStored;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.debug("DELETE /compilations/id - deleting compilation with id {}", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(
            @PathVariable Long compId,
            @RequestBody @Valid UpdateCompilationRequest updateRequest) {
        log.debug("PATCH /compilations/id - updating compilation with id {}", compId);
        return compilationService.updateCompilation(compId, updateRequest);
    }

}

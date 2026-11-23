package kigali.clinic.rw.controller;

import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.service.SpecializationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {

    private final SpecializationService specializationService;

    public SpecializationController(
            SpecializationService specializationService) {

        this.specializationService = specializationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Specialization createSpecialization(
            @RequestBody Specialization specialization) {

        return specializationService
                .createSpecialization(specialization);
    }
    @GetMapping("/unused")
    public List<Specialization> getUnusedSpecializations() {
        return specializationService.getUnusedSpecializations();
    }

    @GetMapping
    public List<Specialization> getAll() {

        return specializationService.getAll();
    }

    @PutMapping("/{id}")
    public Specialization updateSpecialization(
            @PathVariable UUID id,
            @RequestBody Specialization specialization) {

        return specializationService
                .updateSpecialization(id, specialization);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSpecialization(
            @PathVariable UUID id) {

        specializationService
                .deleteSpecialization(id);
    }
}
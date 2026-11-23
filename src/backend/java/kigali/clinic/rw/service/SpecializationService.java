package kigali.clinic.rw.service;

import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.SpecializationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    public SpecializationService(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
    }

    public Specialization createSpecialization(Specialization specialization) {
        return specializationRepository.save(specialization);
    }

    public List<Specialization> getAll() {
        return specializationRepository.findAll();
    }
    public List<Specialization> getUnusedSpecializations() {
        return specializationRepository.findUnusedSpecializations();
    }

    @Transactional
    public Specialization updateSpecialization(UUID id, Specialization updated) {
        Specialization existing = specializationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Specialization not found: " + id));
        existing.setName(updated.getName());

        return specializationRepository.save(existing);
    }

    public void deleteSpecialization(UUID id) {
        if (!specializationRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Specialization not found: " + id);
        }
        specializationRepository.deleteById(id);
    }
}
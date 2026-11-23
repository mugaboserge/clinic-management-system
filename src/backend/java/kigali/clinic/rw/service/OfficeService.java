package kigali.clinic.rw.service;

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.repository.OfficeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OfficeService {

    private final OfficeRepository officeRepository;

    public OfficeService(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }

    // Get all offices
    public List<Office> getAllOffices() {
        return officeRepository.findAll();
    }

    public List<Office> getFreeOffices() {
        return officeRepository.findFreeOffices();
    }

    //busiest offices //office itaboneka
    public Object[] getBusiestOffice() {

        List<Object[]> results = officeRepository.findBusiestOffice();

        if (results.isEmpty()) {
            return null;
        }

        return results.get(0);
    }

    // Get office by ID
    public Office getOfficeById(UUID id) {
        return officeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Office not found: " + id));
    }

    // Create office
    public Office createOffice(Office office) {
        return officeRepository.save(office);
    }

    // Update office
    public Office updateOffice(UUID id, Office office) {

        Office existing = getOfficeById(id);

        existing.setName(office.getName());
        existing.setOfficeNumber(office.getOfficeNumber());

        return officeRepository.save(existing);
    }

    // Delete office
    public void deleteOffice(UUID id) {

        Office existing = getOfficeById(id);

        officeRepository.delete(existing);
    }
}
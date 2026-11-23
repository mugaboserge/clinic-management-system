package kigali.clinic.rw.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.service.OfficeService;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/offices")
public class OfficeController {

    private final OfficeService officeService;

    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    // Get all offices
    @GetMapping
    public List<Office> getAllOffices() {
        return officeService.getAllOffices();
    }

    //  Get busiest office
    @GetMapping("/busiest")
    public Object getBusiestOffice() {

        Object[] result = officeService.getBusiestOffice();

        if (result == null) {
            return "No appointments yet";
        }

        return result;
    }
     // check free office (0ffice irimo ubusa)
    @GetMapping("/free")
    public List<Office> getFreeOffices() {
        return officeService.getFreeOffices();
    }
    // Get office by ID ( gukoresha office ID)
     @GetMapping("/{id}")
    public Office getOfficeById(@PathVariable UUID id) {
        return officeService.getOfficeById(id);
    }

    // Create office
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Office createOffice(@RequestBody Office office) {
        return officeService.createOffice(office);
    }

    // Update office
    @PutMapping("/{id}")
    public Office updateOffice(
            @PathVariable UUID id,
            @RequestBody Office office) {

        return officeService.updateOffice(id, office);
    }

    // Delete office
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOffice(@PathVariable UUID id) {
        officeService.deleteOffice(id);
    }
}
package kigali.clinic.rw.controller;
import java.time.LocalDate;
import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/by-specialization")
    public List<Doctor> getDoctorsBySpecialization(
            @RequestParam String name) {

        return doctorService.getDoctorsBySpecialization(name);
    }
    @GetMapping("/without-office")
    public List<Doctor> getDoctorsWithoutOffice() {
        return doctorService.getDoctorsWithoutOffice();
    }


    @GetMapping("/search")
    public List<Doctor> searchDoctorsByName(
            @RequestParam String name) {
        return doctorService.searchDoctorsByName(name);
    }

    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/{id}")
    public Doctor getDoctorById(@PathVariable UUID id) {
        return doctorService.getDoctorById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Doctor createDoctor(@RequestBody Doctor doctor) {
        return doctorService.createDoctor(doctor);
    }

    @PutMapping("/{id}")
    public Doctor updateDoctor(
            @PathVariable UUID id,
            @RequestBody Doctor doctor) {

        return doctorService.updateDoctor(id, doctor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDoctor(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
    }

// aha nuguhuza office nu doctotrs
    @PatchMapping("/{doctorId}/office")
    public ResponseEntity<String> moveDoctorToOffice(
            @PathVariable UUID doctorId,
            @RequestParam int officeNumber) {

        String message = doctorService.moveDoctorToOffice(
                doctorId, officeNumber);

        if (message.equals("The doctor with that id does not exist")
                || message.equals("The office with that office number does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }

        if (message.equals("That office is taken by other doctor")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }

        return ResponseEntity.ok(message);
    }

    @PutMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> addSpecialization(
            @PathVariable UUID doctorId,
            @PathVariable UUID specializationId) {

        String message = doctorService.addSpecialization(
                doctorId, specializationId);

        if (message.equals("The doctor with that id does not exist")
                || message.equals("That specialization does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }

        if (message.equals("The doctor already has that specialization")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }

        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> removeSpecialization(
            @PathVariable UUID doctorId,
            @PathVariable UUID specializationId) {

        String message = doctorService.removeSpecialization(
                doctorId, specializationId);

        if (message.equals("The doctor with that id does not exist")
                || message.equals("That specialization does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }

        if (message.equals("The doctor does not have that specialization")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }

        return ResponseEntity.ok(message);
    }

    // GET DOCTOR AGENDA

    @GetMapping("/{doctorId}/agenda")
    public List<Appointment> getDoctorAgenda(
            @PathVariable UUID doctorId,
            @RequestParam LocalDate date) {

        return doctorService.getDoctorAgenda(doctorId, date);
    }




}
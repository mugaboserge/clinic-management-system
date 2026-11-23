
package kigali.clinic.rw.controller;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.service.PatientService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // Get all patients
    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    // Find patients by last name
    @GetMapping("/by-last-name")
    public List<Patient> findPatientsByLastName(
            @RequestParam String lastName) {

        return patientService.findPatientsByLastName(lastName);
    }

    // Find patients of a doctor
    @GetMapping("/of-doctor/{doctorId}")
    public List<Patient> getPatientsOfDoctor(
            @PathVariable UUID doctorId) {

        return patientService.getPatientsOfDoctor(doctorId);
    }

    // Find frequent patients
    @GetMapping("/frequent")
    public List<Patient> getFrequentPatients(
            @RequestParam long min) {

        return patientService.getFrequentPatients(min);
    }

    //  KUREBA UMURWAYI UNYUZE KURI UPCAMMING APPOITMENTS

    @GetMapping("/{ssNumber}/upcoming")
    public List<Appointment> getUpcomingAppointments(
            @PathVariable String ssNumber) {

        return patientService.getUpcomingAppointments(ssNumber);
    }

    // Get patient by ID
    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable UUID id) {
        return patientService.getPatientById(id);
    }

    // Create patient
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient createPatient(@RequestBody Patient patient) {
        return patientService.createPatient(patient);
    }

    // Update patient
    @PutMapping("/{id}")
    public Patient updatePatient(
            @PathVariable UUID id,
            @RequestBody Patient patient) {

        return patientService.updatePatient(id, patient);
    }

    // Delete patient
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
    }
}

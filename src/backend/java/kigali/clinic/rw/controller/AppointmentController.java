
package kigali.clinic.rw.controller;
import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import kigali.clinic.rw.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // Get appointments by status
    @GetMapping("/by-status")
    public List<Appointment> getAppointmentsByStatus(
            @RequestParam AppointmentStatus status) {
        return appointmentService.getAppointmentsByStatus(status);
    }

    // Get appointments between dates
    @GetMapping("/between")
    public List<Appointment> getAppointmentsBetween(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        return appointmentService.getAppointmentsBetween(start, end);
    }

    // Appointment statistics
    @GetMapping("/stats/by-status")
    public List<Object[]> getAppointmentStatsByStatus() {
        return appointmentService.getAppointmentStatsByStatus();
    }

    // Cancel appointments for a doctor on a date
    @PatchMapping("/cancel-day")
    public ResponseEntity<Integer> cancelAppointmentsForDoctorOnDate(
            @RequestParam UUID doctorId,
            @RequestParam LocalDate date) {

        int cancelled = appointmentService
                .cancelAppointmentsForDoctorOnDate(doctorId, date);

        return ResponseEntity.ok(cancelled);
    }

    // Pagination
    @GetMapping("/page")
    public Page<Appointment> getAppointmentsPage(Pageable pageable) {
        return appointmentService.getAppointmentsPage(pageable);
    }

    // Get all appointments
    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    // Get appointment by ID
    @GetMapping("/{id}")
    public Appointment getAppointmentById(@PathVariable UUID id) {
        return appointmentService.getAppointmentById(id);
    }

    // Create appointment
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment createAppointment(
            @RequestBody Appointment appointment) {
        return appointmentService.createAppointment(appointment);
    }

    // Safe booking
    @PostMapping("/save")
    public ResponseEntity<String> saveAppointment(
            @RequestBody Appointment appointment) {

        String message = appointmentService.saveAppointment(appointment);

        if (message.equals("Appointment date is required")
                || message.equals("Appointment date cannot be in the past")) {
            return ResponseEntity.badRequest().body(message);
        }

        if (message.equals("Doctor is already booked on that date")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }

        if (message.equals("Appointment saved successfully")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        }

        return ResponseEntity.badRequest().body(message);
    }

    // Change appointment status
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> changeAppointmentStatus(
            @PathVariable UUID id,
            @RequestParam AppointmentStatus status) {

        String message = appointmentService
                .changeAppointmentStatus(id, status);

        if (message.equals("The appointment with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }

        if (message.startsWith("Cannot change status from ")
                || message.equals("A valid appointment status is required")) {
            return ResponseEntity.badRequest().body(message);
        }

        if (message.equals("Appointment status updated successfully")) {
            return ResponseEntity.ok(message);
        }

        return ResponseEntity.badRequest().body(message);
    }

    // Reschedule appointment
    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<String> rescheduleAppointment(
            @PathVariable UUID id,
            @RequestParam LocalDate newDate) {

        String message = appointmentService
                .rescheduleAppointment(id, newDate);

        if (message.equals("The appointment with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }

        if (message.equals("Doctor is already booked on that date")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }

        if (message.equals("Appointment rescheduled successfully")) {
            return ResponseEntity.ok(message);
        }

        return ResponseEntity.badRequest().body(message);
    }

    // Update appointment details
    @PutMapping("/{id}")
    public Appointment updateAppointment(
            @PathVariable UUID id,
            @RequestBody Appointment appointment) {
        return appointmentService.updateAppointment(id, appointment);
    }

    // Delete appointment
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable UUID id) {

        String message = appointmentService.deleteAppointment(id);

        if (message.equals("The appointment with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }

        if (message.equals(
                "That appointment has prescriptions, delete them first")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }

        if (message.equals("Appointment deleted successfully")) {
            return ResponseEntity.ok(message);
        }

        return ResponseEntity.badRequest().body(message);
    }
}

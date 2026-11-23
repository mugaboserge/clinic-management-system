
package kigali.clinic.rw.service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PrescriptionRepository prescriptionRepository) {
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    // Get all appointments
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // Get one appointment by ID
    public Appointment getAppointmentById(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "The appointment with that id does not exist"));
    }

    // Get appointments by status

    public List<Appointment> getAppointmentsByStatus(
            AppointmentStatus status) {
        return appointmentRepository
                .findByStatusOrderByAppointmentDateAsc(status);
    }

    // Get appointments between two dates

    public List<Appointment> getAppointmentsBetween(
            LocalDate startDate, LocalDate endDate) {
        return appointmentRepository
                .findByAppointmentDateBetweenOrderByAppointmentDateAsc(
                        startDate, endDate);
    }

    // Count appointments grouped by status
    public List<Object[]> getAppointmentStatsByStatus() {
        return appointmentRepository.countAppointmentsByStatus();
    }

    // Cancel appointments for a doctor on a specific date.
    // Completed appointments must not be cancelled.
    public int cancelAppointmentsForDoctorOnDate(
            UUID doctorId, LocalDate date) {
        return appointmentRepository.cancelAppointmentsForDoctorOnDate(
                doctorId,
                date,
                AppointmentStatus.CANCELLED,
                AppointmentStatus.COMPLETED);
    }

    // Pagination
    public Page<Appointment> getAppointmentsPage(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    // Basic creation method

    public Appointment createAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    // SavING APPOINTMENT IYI NI BUSINESS RULE
    public String saveAppointment(Appointment appointment) {

        if (appointment.getAppointmentDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Appointment date is required");
        }

        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Appointment date cannot be in the past");
        }

        if (appointment.getDoctor() == null
                || appointment.getPatient() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Doctor and patient are required");
        }

        boolean alreadyBooked =
                appointmentRepository
                        .existsByDoctorAndAppointmentDateAndStatusNot(
                                appointment.getDoctor(),
                                appointment.getAppointmentDate(),
                                AppointmentStatus.CANCELLED);

        if (alreadyBooked) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Doctor is already booked on that date");
        }

        // APPOINTMENT YOSE IGOMBA GUHERA KURI SCHEDULE AS A START POINT

        appointment.setId(null);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepository.save(appointment);

        return "Appointment saved successfully";
    }

    // GUHINDURA STATUS YA APPOITMENTS UKURIKIJE UKO ZIKURIKIRANA
    // MEANS GUHERA KURI SCHEDULE GUKOMEZA

    public String changeAppointmentStatus(
            UUID id, AppointmentStatus newStatus) {

        Appointment appointment = getAppointmentById(id);
        AppointmentStatus currentStatus = appointment.getStatus();

        boolean allowed =
                (currentStatus == AppointmentStatus.SCHEDULED
                        && (newStatus == AppointmentStatus.CONFIRMED
                        || newStatus == AppointmentStatus.CANCELLED))
                        ||
                        (currentStatus == AppointmentStatus.CONFIRMED
                                && (newStatus == AppointmentStatus.COMPLETED
                                || newStatus == AppointmentStatus.CANCELLED));

        if (!allowed) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot change status from "
                            + currentStatus + " to " + newStatus);
        }

        appointment.setStatus(newStatus);
        appointmentRepository.save(appointment);

        return "Appointment status updated successfully";
    }

    // GUHINDURA SCHEDURE YA APPOINTMENTS LIKE COMPLETE OR CANCEL
    public String rescheduleAppointment(
            UUID id, LocalDate newDate) {

        Appointment appointment = getAppointmentById(id);

        if (appointment.getStatus() == AppointmentStatus.COMPLETED
                || appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Completed or cancelled appointments cannot be rescheduled");
        }

        if (newDate == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "New appointment date is required");
        }

        if (newDate.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Appointment date cannot be in the past");
        }

        boolean alreadyBooked =
                appointmentRepository
                        .existsByDoctorAndAppointmentDateAndStatusNotAndIdNot(
                                appointment.getDoctor(),
                                newDate,
                                AppointmentStatus.CANCELLED,
                                id);

        if (alreadyBooked) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Doctor is already booked on that date");
        }

        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepository.save(appointment);

        return "Appointment rescheduled successfully";
    }

    // UPDATE APPOINTMENTS

    public Appointment updateAppointment(
            UUID id, Appointment updatedAppointment) {

        Appointment existing = getAppointmentById(id);

        if (updatedAppointment.getAppointmentDate() != null) {
            existing.setAppointmentDate(
                    updatedAppointment.getAppointmentDate());
        }

        if (updatedAppointment.getDoctor() != null) {
            existing.setDoctor(updatedAppointment.getDoctor());
        }

        if (updatedAppointment.getPatient() != null) {
            existing.setPatient(updatedAppointment.getPatient());
        }

        if (updatedAppointment.getStatus() != null) {
            existing.setStatus(updatedAppointment.getStatus());
        }

        return appointmentRepository.save(existing);
    }

    // GUSIBA APPOINTMENT MUGIHE IDAFITE PRESCRPITION

    public String deleteAppointment(UUID id) {

        Appointment appointment = getAppointmentById(id);

        if (prescriptionRepository.existsByAppointment_Id(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "That appointment has prescriptions, delete them first");
        }

        appointmentRepository.delete(appointment);

        return "Appointment deleted successfully";
    }
}

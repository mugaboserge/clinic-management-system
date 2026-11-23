
package kigali.clinic.rw.repository;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository
        extends JpaRepository<Appointment, UUID> {

    // FIND APPOINTMENT BY STATUS
    List<Appointment> findByStatusOrderByAppointmentDateAsc(
            AppointmentStatus status
    );

    // KUBARA APPOINTMENT WIFASHISHIJE STATUS ZIFITE (BY GROUPING STATUS)
    @Query("""
           SELECT a.status, COUNT(a)
           FROM Appointment a
           GROUP BY a.status
           """)
    List<Object[]> countAppointmentsByStatus();

    // FIND APPOINTMENT BY BETTWEN DATES

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(
            LocalDate startDate,
            LocalDate endDate
    );

    // Find a doctor's appointments on a date, excluding a status

    List<Appointment> findByDoctor_IdAndAppointmentDateAndStatusNot(
            UUID doctorId,
            LocalDate appointmentDate,
            AppointmentStatus status
    );

    // Check whether a doctor is booked on a date, excluding a status
    boolean existsByDoctorAndAppointmentDateAndStatusNot(
            Doctor doctor,
            LocalDate appointmentDate,
            AppointmentStatus status
    );

    // Check whether a doctor is booked by another appointment
    boolean existsByDoctorAndAppointmentDateAndStatusNotAndIdNot(
            Doctor doctor,
            LocalDate appointmentDate,
            AppointmentStatus status,
            UUID id
    );

    // Find upcoming appointments for a patient
    List<Appointment>
    findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
            String ssNumber,
            LocalDate appointmentDate,
            List<AppointmentStatus> statuses
    );

    // Cancel appointments for a doctor on a specified date,
    // excluding appointments already in the excluded status
    @Modifying
    @Transactional
    @Query("""
           UPDATE Appointment a
           SET a.status = :cancelledStatus
           WHERE a.doctor.id = :doctorId
             AND a.appointmentDate = :date
             AND a.status <> :excludedStatus
           """)
    int cancelAppointmentsForDoctorOnDate(
            @Param("doctorId") UUID doctorId,
            @Param("date") LocalDate date,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus,
            @Param("excludedStatus") AppointmentStatus excludedStatus
    );

    // Count appointments by one status
    long countByStatus(AppointmentStatus status);
    long countByAppointmentDate(LocalDate appointmentDate);
}



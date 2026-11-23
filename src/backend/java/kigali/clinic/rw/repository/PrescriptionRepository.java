
package kigali.clinic.rw.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.Prescription;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;




public interface PrescriptionRepository
        extends JpaRepository<Prescription, UUID> {

    List<Prescription> findByAppointment(Appointment appointment);
    boolean existsByAppointment_Id(UUID appointmentId);

    // FIND PPRESCRIPTION BY SS-NUMBER REBA HOSTORY Z'IMITI

    @Query("""
    SELECT p
    FROM Prescription p
    WHERE p.appointment.patient.ssNumber = :ssNumber
    ORDER BY p.issuedDate DESC
    """)
    List<Prescription> findPrescriptionHistoryByPatient(
            @Param("ssNumber") String ssNumber);
}



package kigali.clinic.rw.repository;

import kigali.clinic.rw.domain.Patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    // FIND PATIENT BY LAST NAMES
    List<Patient> findByLastNameIgnoreCaseOrderByFirstNameAsc(
            String lastName
    );

    // FIND PATIENTS BY SS-NUMBER
    Optional<Patient> findBySsNumber(String ssNumber);

    // FIND
    @Query("""
           SELECT DISTINCT a.patient
           FROM Appointment a
           WHERE a.doctor.id = :doctorId
           """)
    List<Patient> findPatientsOfDoctor(
            @Param("doctorId") UUID doctorId
    );

    // FIND
    @Query("""
           SELECT p
           FROM Patient p
           JOIN Appointment a ON a.patient = p
           GROUP BY p
           HAVING COUNT(a) >= :min
           ORDER BY COUNT(a) DESC
           """)
    List<Patient> findFrequentPatients(
            @Param("min") long min
    );
}

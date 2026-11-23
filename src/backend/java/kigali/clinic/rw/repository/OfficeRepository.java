package kigali.clinic.rw.repository;

import kigali.clinic.rw.domain.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfficeRepository extends JpaRepository<Office, UUID> {

    List<Office> findByName(String name);

    Optional<Office> findByOfficeNumber(int officeNumber);

    @Query("""
       SELECT o
       FROM Office o
       WHERE NOT EXISTS (
           SELECT d
           FROM Doctor d
           WHERE d.office = o
       )
       ORDER BY o.officeNumber ASC
       """)
    List<Office> findFreeOffices();

    // CHECK HIGH OR MANY APPOINTMENTS

    @Query("""
           SELECT o.name, o.officeNumber, COUNT(a)
           FROM Appointment a
           JOIN a.doctor d
           JOIN d.office o
           GROUP BY o.id, o.name, o.officeNumber
           ORDER BY COUNT(a) DESC
           """)
    List<Object[]> findBusiestOffice();
}
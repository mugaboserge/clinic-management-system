package kigali.clinic.rw.repository;

import kigali.clinic.rw.domain.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    @Query("""
    SELECT d FROM Doctor d
    WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%'))
       OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
    ORDER BY d.lastName ASC
    """)
    List<Doctor> searchDoctorsByName(
            @Param("name") String name);

    @Query("""
           SELECT d
           FROM Doctor d
           JOIN d.specializations s
           WHERE LOWER(s.name) = LOWER(:name)
           """)
    List<Doctor> findDoctorsBySpecialization(@Param("name") String name);

    boolean existsByOfficeOfficeNumberAndIdNot(
            int officeNumber, UUID doctorId);
 // CHECK WHETHER OFFICE IS NOT OCCUPIED

    @Query("""
       SELECT d
       FROM Doctor d
       WHERE d.office IS NULL
       ORDER BY d.lastName ASC
       """)
    List<Doctor> findDoctorsWithoutOffice();
}
package kigali.clinic.rw.service;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;
import kigali.clinic.rw.repository.SpecializationRepository;
import java.time.LocalDate;
import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.repository.AppointmentRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final OfficeRepository officeRepository;
    private final SpecializationRepository specializationRepository;
    private final AppointmentRepository appointmentRepository;
    public DoctorService(
            DoctorRepository doctorRepository,
            OfficeRepository officeRepository,
            SpecializationRepository specializationRepository,
            AppointmentRepository appointmentRepository) {

        this.doctorRepository = doctorRepository;
        this.officeRepository = officeRepository;
        this.specializationRepository = specializationRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }


    public List<Doctor> searchDoctorsByName(String name) {
        return doctorRepository.searchDoctorsByName(name);
    }

    //Find doctors by specialization
    public List<Doctor> getDoctorsBySpecialization(String name) {
        return doctorRepository.findDoctorsBySpecialization(name);
    }
// Find doctors without an office
    public List<Doctor> getDoctorsWithoutOffice() {
        return doctorRepository.findDoctorsWithoutOffice();
    }

    public Doctor getDoctorById(UUID id) {
        return doctorRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found: " + id));
    }

    public Doctor createDoctor(Doctor doctor) {

        // We want PostgreSQL to generate a new UUID.
        doctor.setId(null);

        if (doctor.getOffice() != null &&
                doctor.getOffice().getId() != null) {

            doctor.setOffice(
                    officeRepository.findById(
                            doctor.getOffice().getId()
                    ).orElseThrow(() ->
                            new RuntimeException("Office not found"))
            );
        }

        if (doctor.getSpecializations() != null &&
                !doctor.getSpecializations().isEmpty()) {

            List<Specialization> specializations = new ArrayList<>();

            for (Specialization specialization :
                    doctor.getSpecializations()) {

                Specialization existingSpecialization =
                        specializationRepository.findById(
                                specialization.getId()
                        ).orElseThrow(() ->
                                new RuntimeException(
                                        "Specialization not found: "
                                                + specialization.getId()
                                )
                        );

                specializations.add(existingSpecialization);
            }

            doctor.setSpecializations(specializations);
        }

        return doctorRepository.save(doctor);
    }

    public Doctor updateDoctor(UUID id, Doctor details) {

        Doctor doctor = getDoctorById(id);

        doctor.setFirstName(details.getFirstName());
        doctor.setLastName(details.getLastName());
        doctor.setDateOfBirth(details.getDateOfBirth());


         // Update Office

        if (details.getOffice() == null ||
                details.getOffice().getId() == null) {

            doctor.setOffice(null);

        } else {

            doctor.setOffice(
                    officeRepository.findById(
                            details.getOffice().getId()
                    ).orElseThrow(() ->
                            new RuntimeException("Office not found"))
            );
        }


          //Update Specializations

        if (details.getSpecializations() == null) {

            doctor.setSpecializations(new ArrayList<>());

        } else {

            List<Specialization> specializations =
                    new ArrayList<>();

            for (Specialization specialization :
                    details.getSpecializations()) {

                Specialization existingSpecialization =
                        specializationRepository.findById(
                                specialization.getId()
                        ).orElseThrow(() ->
                                new RuntimeException(
                                        "Specialization not found: "
                                                + specialization.getId()
                                )
                        );

                specializations.add(existingSpecialization);
            }

            doctor.setSpecializations(specializations);
        }

        return doctorRepository.save(doctor);
    }

    public String addSpecialization(UUID doctorId, UUID specializationId) {

        // 1. Find the doctor
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElse(null);

        if (doctor == null) {
            return "The doctor with that id does not exist";
        }

        // 2. Find the specialization
        Specialization specialization =
                specializationRepository.findById(specializationId)
                        .orElse(null);

        if (specialization == null) {
            return "That specialization does not exist";
        }

        // 3. Check whether the doctor already has it
        if (doctor.getSpecializations().contains(specialization)) {
            return "The doctor already has that specialization";
        }

        // 4. Add it without removing existing specializations
        doctor.getSpecializations().add(specialization);

        // 5. Save the owning entity
        doctorRepository.save(doctor);

        return "Specialization added to the doctor";
    }

    public String removeSpecialization(UUID doctorId, UUID specializationId) {

        // 1. Check whether the doctor exists
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElse(null);

        if (doctor == null) {
            return "The doctor with that id does not exist";
        }

        // 2. Check whether the specialization exists
        Specialization specialization =
                specializationRepository.findById(specializationId)
                        .orElse(null);

        if (specialization == null) {
            return "That specialization does not exist";
        }

        // 3. Check whether the doctor has this specialization
        if (!doctor.getSpecializations().contains(specialization)) {
            return "The doctor does not have that specialization";
        }

        // 4. Remove only the relationship
        doctor.getSpecializations().remove(specialization);

        // 5. Save the doctor
        doctorRepository.save(doctor);

        return "Specialization removed from the doctor";
    }
    // iyi ni methods i checking to office yafashwe(already occupies the requested office number.)

    public String moveDoctorToOffice(UUID doctorId, int officeNumber) {

        // 1. Check that the doctor exists
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElse(null);

        if (doctor == null) {
            return "The doctor with that id does not exist";
        }

        // 2. Check that the office exists
        var officeOptional = officeRepository.findByOfficeNumber(officeNumber);

        if (officeOptional.isEmpty()) {
            return "The office with that office number does not exist";
        }

        // 3. Check whether another doctor occupies the office
        if (doctorRepository.existsByOfficeOfficeNumberAndIdNot(
                officeNumber, doctorId)) {
            return "That office is taken by other doctor";
        }

        // 4. Assign the office to the doctor
        doctor.setOffice(officeOptional.get());
        doctorRepository.save(doctor);

        return "Office changed successfully";
    }

    public void deleteDoctor(UUID id) {

        Doctor doctor = getDoctorById(id);

        doctorRepository.delete(doctor);
    }

    // AGENDA DOCTORS

    public List<Appointment> getDoctorAgenda(UUID doctorId, LocalDate date) {

        if (!doctorRepository.existsById(doctorId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The doctor with that id does not exist");
        }

        return appointmentRepository
                .findByDoctor_IdAndAppointmentDateAndStatusNot(
                        doctorId,
                        date,
                        AppointmentStatus.CANCELLED);
    }
}
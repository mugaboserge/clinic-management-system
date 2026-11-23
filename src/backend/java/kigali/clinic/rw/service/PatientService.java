
package kigali.clinic.rw.service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public PatientService(
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository) {

        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // Get all patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // Find patients by last name
    public List<Patient> findPatientsByLastName(String lastName) {
        return patientRepository
                .findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    // Get patients who have appointments with a specific doctor
    public List<Patient> getPatientsOfDoctor(UUID doctorId) {

        if (!doctorRepository.existsById(doctorId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The doctor with that id does not exist"
            );
        }

        return patientRepository.findPatientsOfDoctor(doctorId);
    }

    // Get frequent patients
    public List<Patient> getFrequentPatients(long min) {
        return patientRepository.findFrequentPatients(min);
    }

    // Get a patient by ID
    public Patient getPatientById(UUID id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "The patient with that id does not exist"
                ));
    }

    // Create a patient
    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    // Update a patient
    public Patient updatePatient(UUID id, Patient updatedPatient) {

        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "The patient with that id does not exist"
                ));

        existingPatient.setFirstName(updatedPatient.getFirstName());
        existingPatient.setLastName(updatedPatient.getLastName());
        existingPatient.setSsNumber(updatedPatient.getSsNumber());

        return patientRepository.save(existingPatient);
    }

    // Delete a patient
    public void deletePatient(UUID id) {

        if (!patientRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The patient with that id does not exist"
            );
        }

        patientRepository.deleteById(id);
    }

    // Get future appointment (appointment zitaragerwaho)

    public List<Appointment> getUpcomingAppointments(String ssNumber) {

        patientRepository.findBySsNumber(ssNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "That patient with that ssnumber does not exist"
                ));

        return appointmentRepository
                .findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                        ssNumber,
                        LocalDate.now(),
                        List.of(
                                AppointmentStatus.SCHEDULED,
                                AppointmentStatus.CONFIRMED
                        )
                );
    }
}


package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            AppointmentRepository appointmentRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<Prescription> getPrescriptionHistoryByPatient(
            String ssNumber) {

        return prescriptionRepository
                .findPrescriptionHistoryByPatient(ssNumber);
    }


    public java.util.List<Prescription> getPrescriptionsByAppointment(
            UUID appointmentId) {

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElse(null);

        if (appointment == null) {
            return null;
        }

        return prescriptionRepository.findByAppointment(appointment);
    }

    @Transactional
    public String savePrescription(
            UUID appointmentId,
            Prescription prescription) {

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElse(null);

        if (appointment == null) {
            return "The appointment with that id does not exist";
        }

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            return "Prescriptions are only for COMPLETED appointments";
        }

        prescription.setId(null);
        prescription.setIssuedDate(LocalDate.now());
        prescription.setAppointment(appointment);

        prescriptionRepository.save(prescription);

        return "Prescription saved successfully";
    }

    public String updatePrescription(UUID id, Prescription updatedPrescription) {
        Prescription prescription = prescriptionRepository.findById(id).orElse(null);

        if (prescription == null) {
            return "The prescription with that id does not exist";
        }

        prescription.setMedicineName(updatedPrescription.getMedicineName());
        prescription.setDosage(updatedPrescription.getDosage());
        prescription.setDurationDays(updatedPrescription.getDurationDays());

        prescriptionRepository.save(prescription);

        return "Prescription updated successfully";
    }

    public String deletePrescription(UUID id) {
        Prescription prescription = prescriptionRepository.findById(id).orElse(null);

        if (prescription == null) {
            return "The prescription with that id does not exist";
        }

        prescriptionRepository.delete(prescription);

        return "Prescription deleted successfully";
    }

}


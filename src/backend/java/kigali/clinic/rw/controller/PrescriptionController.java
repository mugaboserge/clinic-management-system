
package kigali.clinic.rw.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.service.PrescriptionService;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(
            PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> savePrescription(
            @RequestParam UUID appointmentId,
            @RequestBody Prescription prescription) {

        String message = prescriptionService.savePrescription(
                appointmentId, prescription);

        if (message.equals(
                "The appointment with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(message);

        } else if (message.equals(
                "Prescriptions are only for COMPLETED appointments")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(message);

        } else {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(message);
        }
    }

    @GetMapping("/patient/{ssNumber}")
    public ResponseEntity<List<Prescription>> getPrescriptionHistory(
            @PathVariable String ssNumber) {

        List<Prescription> prescriptions =
                prescriptionService.getPrescriptionHistoryByPatient(
                        ssNumber);

        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getPrescriptionsByAppointment(
            @PathVariable UUID appointmentId) {

        List<Prescription> prescriptions =
                prescriptionService.getPrescriptionsByAppointment(
                        appointmentId);

        if (prescriptions == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("The appointment with that id does not exist");
        }

        return ResponseEntity.ok(prescriptions);
    }
 // put mapping
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updatePrescription(
            @PathVariable UUID id,
            @RequestBody Prescription prescription) {

        String message = prescriptionService.updatePrescription(id, prescription);

        if (message.equals("The prescription with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else {
            return ResponseEntity.ok(message);
        }
    }
 // delete

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePrescription(@PathVariable UUID id) {

        String message = prescriptionService.deletePrescription(id);

        if (message.equals("The prescription with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        } else {
            return ResponseEntity.ok(message);
        }
    }

}

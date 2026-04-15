package com.clinic.backend.service;

import com.clinic.backend.entity.Appointment;
import com.clinic.backend.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository repo;

    public Appointment book(Appointment appointment) {
        appointment.setStatus("BOOKED");
        return repo.save(appointment);
    }

    public List<Appointment> getByPatient(Long patientId) {
        return repo.findByPatientId(patientId);
    }

    public List<Appointment> getByDoctor(Long doctorId) {
        return repo.findByDoctorId(doctorId);
    }

    public Appointment complete(Long id) {
        Appointment appt = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        appt.setStatus("COMPLETED");

        return repo.save(appt);
    }

    // 🔥 ADD DIAGNOSIS + AUTO COMPLETE
    public Appointment addDiagnosis(Long id, String diagnosis, String prescription) {

        Appointment appt = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appt.setDiagnosis(diagnosis);
        appt.setPrescription(prescription);

        // 🔥 AUTO COMPLETE
        appt.setStatus("COMPLETED");

        return repo.save(appt);
    }

    // 🔥 REQUIRED FOR PDF
    public Appointment getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }
}
package com.clinic.backend.service;

import com.clinic.backend.entity.Patient;
import com.clinic.backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    @Autowired
    private PatientRepository repo;

    public Patient savePatient(Patient patient) {
        return repo.findByEmail(patient.getEmail())
                .orElseGet(() -> repo.save(patient));
    }

    public Patient getByEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }
    public Patient getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }
    public List<Patient> getAllPatients() {
        return repo.findAll();
    }

    public Patient verify(Long id) {
        Patient p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        p.setVerified(true);

        return repo.save(p);
    }

}
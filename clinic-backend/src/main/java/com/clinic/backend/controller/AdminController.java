package com.clinic.backend.controller;

import com.clinic.backend.entity.Patient;
import com.clinic.backend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PatientService service;

    @GetMapping("/patients")
    public List<Patient> allPatients() {
        return service.getAllPatients();
    }

    @PutMapping("/verify/{id}")
    public Patient verify(@PathVariable Long id) {
        return service.verify(id);
    }
}
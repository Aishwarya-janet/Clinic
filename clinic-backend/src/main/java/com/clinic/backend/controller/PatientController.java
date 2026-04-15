package com.clinic.backend.controller;

import com.clinic.backend.entity.Patient;
import com.clinic.backend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService service;

    // ✅ SAVE PATIENT
    @PostMapping("/save")
    public Patient save(@RequestBody Patient patient) {
        return service.savePatient(patient);
    }

    // ✅ GET MY PROFILE (FIXED)
    @GetMapping("/me")
    public Patient getMyProfile() {

        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return service.getByEmail(email);
    }

    // ✅ UPLOAD KYC (FINAL SAFE VERSION)
    @PostMapping("/upload")
    public String uploadKyc(
            @RequestParam("idProof") MultipartFile idProof,
            @RequestParam("selfie") MultipartFile selfie) throws IOException {

        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        System.out.println("EMAIL FROM TOKEN: " + email);

        Patient patient = service.getByEmail(email);

        if (patient == null) {
            throw new RuntimeException("PATIENT NOT FOUND - please call /patient/save first");
        }
        // 🔥 Absolute path (fix 500 error)
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads");

        // 🔥 Create folder if not exists
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 🔥 Unique file names
        String idProofName = System.currentTimeMillis() + "_" + idProof.getOriginalFilename();
        String selfieName = System.currentTimeMillis() + "_" + selfie.getOriginalFilename();

        Path idProofFile = uploadPath.resolve(idProofName);
        Path selfieFile = uploadPath.resolve(selfieName);

        // 🔥 Save files
        Files.copy(idProof.getInputStream(), idProofFile, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(selfie.getInputStream(), selfieFile, StandardCopyOption.REPLACE_EXISTING);

        // 🔥 Save paths in DB
        patient.setIdProofUrl("uploads/" + idProofName);
        patient.setSelfieUrl("uploads/" + selfieName);

        service.savePatient(patient);

        return "KYC Uploaded Successfully";
    }
}
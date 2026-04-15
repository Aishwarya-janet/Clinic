package com.clinic.backend.controller;

import com.clinic.backend.entity.Appointment;
import com.clinic.backend.entity.Patient;
import com.clinic.backend.service.AppointmentService;
import com.clinic.backend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// PDF IMPORTS
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService service;

    @Autowired
    private PatientService patientService;

    // ✅ BOOK
    @PostMapping("/book")
    public Appointment book(@RequestBody Appointment appointment) {

        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Patient patient = patientService.getByEmail(email);

        if (!patient.isVerified()) {
            throw new RuntimeException("KYC not verified");
        }

        appointment.setPatientId(patient.getId());

        return service.book(appointment);
    }

    // ✅ GET MY APPOINTMENTS
    @GetMapping("/my")
    public List<Appointment> myAppointments() {

        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Patient patient = patientService.getByEmail(email);

        return service.getByPatient(patient.getId());
    }

    // ✅ DOCTOR VIEW
    @GetMapping("/doctor/{doctorId}")
    public List<Appointment> getDoctorAppointments(@PathVariable Long doctorId) {
        return service.getByDoctor(doctorId);
    }

    // ✅ COMPLETE
    @PutMapping("/complete/{id}")
    public Appointment complete(@PathVariable Long id) {
        return service.complete(id);
    }

    // ✅ ADD DIAGNOSIS
    @PutMapping("/diagnosis/{id}")
    public Appointment addDiagnosis(
            @PathVariable Long id,
            @RequestBody Appointment request) {

        return service.addDiagnosis(
                id,
                request.getDiagnosis(),
                request.getPrescription()
        );
    }

    // 🔥 PDF DOWNLOAD
    // 🔥 PDF DOWNLOAD
    @GetMapping("/report/{id}")
    public void downloadReport(@PathVariable Long id,
                               HttpServletResponse response) throws Exception {

        // ✅ Get appointment
        Appointment appt = service.getById(id);

        // ✅ Get patient (FIX)
        Patient patient = patientService.getById(appt.getPatientId());

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=report.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // 🔥 TITLE
        document.add(new Paragraph("🏥 CLINIC REPORT")
                .setBold()
                .setFontSize(18));

        document.add(new Paragraph(" "));

        // 🔥 DETAILS
        document.add(new Paragraph("Patient Name: " + patient.getName()));
        document.add(new Paragraph("Date: " + appt.getAppointmentTime().toLocalDate()));
        document.add(new Paragraph("Status: " + appt.getStatus()));

        document.add(new Paragraph(" "));

        // 🔥 DIAGNOSIS
        document.add(new Paragraph("Diagnosis:").setBold());
        document.add(new Paragraph(
                appt.getDiagnosis() != null ? appt.getDiagnosis() : "N/A"
        ));

        document.add(new Paragraph(" "));

        // 🔥 PRESCRIPTION
        document.add(new Paragraph("Prescription:").setBold());
        document.add(new Paragraph(
                appt.getPrescription() != null ? appt.getPrescription() : "N/A"
        ));

        document.close();
    }
}
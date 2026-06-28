package com.journalinsight.backend.controller;

import com.journalinsight.backend.dto.PatientDto;
import com.journalinsight.backend.service.PatientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // GET /api/patients?professionalId=7875555678
    @GetMapping
    public List<PatientDto> getPatientsForProfessional(@RequestParam String professionalId) {
        return patientService.getPatientsForProfessional(professionalId);
    }
}

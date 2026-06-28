package com.journalinsight.backend.service;

import com.journalinsight.backend.dto.PatientDto;
import com.journalinsight.backend.model.*;
import com.journalinsight.backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvitationCodeService {

    private final ProfessionalRepository         professionalRepository;
    private final PatientRepository              patientRepository;
    private final PatientProfessionalRepository  patientProfessionalRepository;

    public InvitationCodeService(ProfessionalRepository professionalRepository,
                                 PatientRepository patientRepository,
                                 PatientProfessionalRepository patientProfessionalRepository) {
        this.professionalRepository       = professionalRepository;
        this.patientRepository            = patientRepository;
        this.patientProfessionalRepository = patientProfessionalRepository;
    }

    // ── Link ─────────────────────────────────────────────────────────────────────
    public String linkPatientToProfessional(String professionalPhone, String patientPhone) {
        Professional professional = professionalRepository.findById(professionalPhone)
                .orElseThrow(() -> new RuntimeException("Incorrect phone number. No professional found with that number."));

        Patient patient = patientRepository.findById(patientPhone)
                .orElseThrow(() -> new RuntimeException("Patient account not found."));

        PatientProfessionalId relationId = new PatientProfessionalId(patientPhone, professionalPhone);
        if (patientProfessionalRepository.existsById(relationId)) {
            throw new RuntimeException("You are already linked to this professional.");
        }

        PatientProfessional relation = new PatientProfessional();
        relation.setId(relationId);
        relation.setPatient(patient);
        relation.setProfessional(professional);
        patientProfessionalRepository.save(relation);

        String professionalName = professional.getAccount().getFirstName()
                + " " + professional.getAccount().getLastNamePaternal();
        return "Successfully linked with " + professionalName + ".";
    }

    // ── Unlink ────────────────────────────────────────────────────────────────────
    public void unlinkPatientFromProfessional(String patientPhone, String professionalPhone) {
        PatientProfessionalId id = new PatientProfessionalId(patientPhone, professionalPhone);
        if (!patientProfessionalRepository.existsById(id)) {
            throw new RuntimeException("No link found between this patient and professional.");
        }
        patientProfessionalRepository.deleteById(id);
    }

    // ── Get professionals for a patient ──────────────────────────────────────────
    public List<PatientDto> getProfessionalsForPatient(String patientPhone) {
        return patientProfessionalRepository.findByPatientId(patientPhone)
                .stream()
                .map(pp -> {
                    Professional professional = pp.getProfessional();
                    Account account = professional.getAccount();
                    return new PatientDto(
                            professional.getPhoneNumber(),
                            account.getFirstName(),
                            account.getLastNamePaternal()
                    );
                })
                .collect(Collectors.toList());
    }
}

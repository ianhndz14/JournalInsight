package com.journalinsight.backend.service;

import com.journalinsight.backend.dto.PatientDto;
import com.journalinsight.backend.model.Account;
import com.journalinsight.backend.model.Patient;
import com.journalinsight.backend.model.PatientProfessional;
import com.journalinsight.backend.repository.PatientProfessionalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientProfessionalRepository patientProfessionalRepository;

    public PatientService(PatientProfessionalRepository patientProfessionalRepository) {
        this.patientProfessionalRepository = patientProfessionalRepository;
    }

    public List<PatientDto> getPatientsForProfessional(String professionalId) {
        List<PatientProfessional> relations =
                patientProfessionalRepository.findByProfessionalId(professionalId);

        return relations.stream().map(pp -> {
            Patient patient = pp.getPatient();
            Account account = patient.getAccount();
            return new PatientDto(
                    patient.getPhoneNumber(),
                    account.getFirstName(),
                    account.getLastNamePaternal()
            );
        }).collect(Collectors.toList());
    }
}

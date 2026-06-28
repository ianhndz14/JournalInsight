package com.journalinsight.backend.service;

import com.journalinsight.backend.dto.AuthResponse;
import com.journalinsight.backend.dto.LoginRequest;
import com.journalinsight.backend.dto.RegisterRequest;
import com.journalinsight.backend.model.Account;
import com.journalinsight.backend.model.Patient;
import com.journalinsight.backend.model.Professional;
import com.journalinsight.backend.repository.AccountRepository;
import com.journalinsight.backend.repository.PatientRepository;
import com.journalinsight.backend.repository.ProfessionalRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AccountRepository accountRepository;
    private final PatientRepository patientRepository;
    private final ProfessionalRepository professionalRepository;

    public AuthService(AccountRepository accountRepository,
                       PatientRepository patientRepository,
                       ProfessionalRepository professionalRepository) {
        this.accountRepository      = accountRepository;
        this.patientRepository      = patientRepository;
        this.professionalRepository = professionalRepository;
    }

    public AuthResponse login(LoginRequest request) {
        Account account = accountRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new RuntimeException("No account found with that email."));

        if (!account.getPasswordHash().equals(request.getPassword())) {
            throw new RuntimeException("Incorrect password.");
        }

        String phoneNumber;

        if ("P".equals(account.getAccountType())) {
            phoneNumber = patientRepository.findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Patient record not found."))
                    .getPhoneNumber();
        } else {
            phoneNumber = professionalRepository.findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Professional record not found."))
                    .getPhoneNumber();
        }

        return new AuthResponse(phoneNumber, account.getAccountType(), account.getFirstName(), "Login successful.");
    }

    public AuthResponse register(RegisterRequest request) {
        if (accountRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new RuntimeException("An account with that email already exists.");
        }

        Account account = new Account();
        account.setFirstName(request.getFirstName());
        account.setLastNamePaternal(request.getLastNamePaternal());
        account.setEmail(request.getEmail().toLowerCase());
        account.setPasswordHash(request.getPassword());
        account.setAccountType(request.getAccountType());
        account.setLoginAttempts(0);

        Account savedAccount = accountRepository.save(account);

        String phoneNumber = request.getPhoneNumber();

        if ("P".equals(request.getAccountType())) {
            Patient patient = new Patient();
            patient.setPhoneNumber(phoneNumber);
            patient.setAccount(savedAccount);
            patientRepository.save(patient);
        } else {
            Professional professional = new Professional();
            professional.setPhoneNumber(phoneNumber);
            professional.setAccount(savedAccount);
            professional.setSpecialty(request.getSpecialty());
            professionalRepository.save(professional);
        }

        return new AuthResponse(phoneNumber, request.getAccountType(), request.getFirstName(), "Account created successfully.");
    }
}

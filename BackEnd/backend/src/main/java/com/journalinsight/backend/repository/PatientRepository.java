package com.journalinsight.backend.repository;

import com.journalinsight.backend.model.Account;
import com.journalinsight.backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, String> {

    Optional<Patient> findByAccount(Account account);
}

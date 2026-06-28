package com.journalinsight.backend.repository;

import com.journalinsight.backend.model.Account;
import com.journalinsight.backend.model.Professional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfessionalRepository extends JpaRepository<Professional, String> {

    Optional<Professional> findByAccount(Account account);
}

package com.journalinsight.backend.repository;

import com.journalinsight.backend.model.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    List<JournalEntry> findByPatientIdOrderByEntryDateDesc(String patientId);

    List<JournalEntry> findByPatientIdAndEntryDate(String patientId, LocalDate entryDate);

    @Query("SELECT DISTINCT je.entryDate FROM JournalEntry je WHERE je.patientId = :patientId ORDER BY je.entryDate DESC")
    List<LocalDate> findAvailableDatesByPatientId(@Param("patientId") String patientId);
}

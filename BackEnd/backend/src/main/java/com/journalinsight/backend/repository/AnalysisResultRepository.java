package com.journalinsight.backend.repository;

import com.journalinsight.backend.model.AnalysisResult;
import com.journalinsight.backend.model.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    Optional<AnalysisResult> findByJournalEntry(JournalEntry journalEntry);
}

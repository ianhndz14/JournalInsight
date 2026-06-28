package com.journalinsight.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_result", schema = "public")
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_result_id_pk")
    private Long analysisResultId;

    @OneToOne
    @JoinColumn(name = "journal_entry_id_fk1", nullable = false, unique = true)
    private JournalEntry journalEntry;

    @Column(name = "analysis_date")
    private LocalDateTime analysisDate;

    @Column(name = "general_classification", nullable = false, length = 50)
    private String generalClassification;

    public AnalysisResult() {
    }

    public Long getAnalysisResultId() {
        return analysisResultId;
    }

    public void setAnalysisResultId(Long analysisResultId) {
        this.analysisResultId = analysisResultId;
    }

    public JournalEntry getJournalEntry() {
        return journalEntry;
    }

    public void setJournalEntry(JournalEntry journalEntry) {
        this.journalEntry = journalEntry;
    }

    public LocalDateTime getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(LocalDateTime analysisDate) {
        this.analysisDate = analysisDate;
    }

    public String getGeneralClassification() {
        return generalClassification;
    }

    public void setGeneralClassification(String generalClassification) {
        this.generalClassification = generalClassification;
    }
}
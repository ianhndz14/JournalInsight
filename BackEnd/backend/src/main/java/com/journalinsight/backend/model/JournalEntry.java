package com.journalinsight.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "journal_entry", schema = "public")
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "journal_entry_id_pk")
    private Long journalEntryId;

    @Column(name = "patient_id_fk1", nullable = false, length = 15)
    private String patientId;

    @Column(name = "entry_text", nullable = false, columnDefinition = "TEXT")
    private String entryText;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    public JournalEntry() {
    }

    public Long getJournalEntryId() {
        return journalEntryId;
    }

    public void setJournalEntryId(Long journalEntryId) {
        this.journalEntryId = journalEntryId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getEntryText() {
        return entryText;
    }

    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }
}
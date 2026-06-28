package com.journalinsight.backend.dto;

import java.util.List;

public class JournalEntryWithAnalysisDto {

    private Long journalEntryId;
    private String patientId;
    private String entryDate;
    private String entryText;
    private String generalClassification;
    private List<String> detectedEmotions;

    public JournalEntryWithAnalysisDto() {}

    public JournalEntryWithAnalysisDto(Long journalEntryId,
                                       String patientId,
                                       String entryDate,
                                       String entryText,
                                       String generalClassification,
                                       List<String> detectedEmotions) {
        this.journalEntryId = journalEntryId;
        this.patientId = patientId;
        this.entryDate = entryDate;
        this.entryText = entryText;
        this.generalClassification = generalClassification;
        this.detectedEmotions = detectedEmotions;
    }

    public Long getJournalEntryId() { return journalEntryId; }
    public void setJournalEntryId(Long journalEntryId) { this.journalEntryId = journalEntryId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getEntryDate() { return entryDate; }
    public void setEntryDate(String entryDate) { this.entryDate = entryDate; }

    public String getEntryText() { return entryText; }
    public void setEntryText(String entryText) { this.entryText = entryText; }

    public String getGeneralClassification() { return generalClassification; }
    public void setGeneralClassification(String generalClassification) { this.generalClassification = generalClassification; }

    public List<String> getDetectedEmotions() { return detectedEmotions; }
    public void setDetectedEmotions(List<String> detectedEmotions) { this.detectedEmotions = detectedEmotions; }
}

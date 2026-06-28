package com.journalinsight.backend.dto;

public class CreateJournalEntryRequest {

    private String patientId;
    private String entryText;

    public CreateJournalEntryRequest() {
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
}
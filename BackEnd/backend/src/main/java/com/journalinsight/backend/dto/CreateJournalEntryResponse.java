package com.journalinsight.backend.dto;

import java.util.List;

public class CreateJournalEntryResponse {

    private Long journalEntryId;
    private String detectedEmotion;
    private List<String> detectedEmotions;
    private String message;

    public CreateJournalEntryResponse() {
    }

    public CreateJournalEntryResponse(Long journalEntryId,
                                      String detectedEmotion,
                                      List<String> detectedEmotions,
                                      String message) {
        this.journalEntryId = journalEntryId;
        this.detectedEmotion = detectedEmotion;
        this.detectedEmotions = detectedEmotions;
        this.message = message;
    }

    public Long getJournalEntryId() {
        return journalEntryId;
    }

    public void setJournalEntryId(Long journalEntryId) {
        this.journalEntryId = journalEntryId;
    }

    public String getDetectedEmotion() {
        return detectedEmotion;
    }

    public void setDetectedEmotion(String detectedEmotion) {
        this.detectedEmotion = detectedEmotion;
    }

    public List<String> getDetectedEmotions() {
        return detectedEmotions;
    }

    public void setDetectedEmotions(List<String> detectedEmotions) {
        this.detectedEmotions = detectedEmotions;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
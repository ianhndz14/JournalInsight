package com.journalinsight.backend.dto;

import java.util.List;

public class EmotionDetectionResult {

    private String generalClassification;
    private List<String> detectedEmotions;

    public EmotionDetectionResult() {
    }

    public EmotionDetectionResult(String generalClassification, List<String> detectedEmotions) {
        this.generalClassification = generalClassification;
        this.detectedEmotions = detectedEmotions;
    }

    public String getGeneralClassification() {
        return generalClassification;
    }

    public void setGeneralClassification(String generalClassification) {
        this.generalClassification = generalClassification;
    }

    public List<String> getDetectedEmotions() {
        return detectedEmotions;
    }

    public void setDetectedEmotions(List<String> detectedEmotions) {
        this.detectedEmotions = detectedEmotions;
    }
}
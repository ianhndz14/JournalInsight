package com.journalinsight.backend.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ResultEmotionId implements Serializable {

    private Long analysisResultId;
    private Long emotionCategoryId;

    public ResultEmotionId() {
    }

    public ResultEmotionId(Long analysisResultId, Long emotionCategoryId) {
        this.analysisResultId = analysisResultId;
        this.emotionCategoryId = emotionCategoryId;
    }

    public Long getAnalysisResultId() {
        return analysisResultId;
    }

    public void setAnalysisResultId(Long analysisResultId) {
        this.analysisResultId = analysisResultId;
    }

    public Long getEmotionCategoryId() {
        return emotionCategoryId;
    }

    public void setEmotionCategoryId(Long emotionCategoryId) {
        this.emotionCategoryId = emotionCategoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultEmotionId)) return false;
        ResultEmotionId that = (ResultEmotionId) o;
        return Objects.equals(analysisResultId, that.analysisResultId)
                && Objects.equals(emotionCategoryId, that.emotionCategoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(analysisResultId, emotionCategoryId);
    }
}
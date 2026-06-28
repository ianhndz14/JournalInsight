package com.journalinsight.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "result_emotion")
public class ResultEmotion {

    @EmbeddedId
    private ResultEmotionId id;

    @ManyToOne
    @MapsId("analysisResultId")
    @JoinColumn(name = "analysis_result_id_pk_fk1")
    private AnalysisResult analysisResult;

    @ManyToOne
    @MapsId("emotionCategoryId")
    @JoinColumn(name = "emotion_category_id_pk_fk2")
    private EmotionCategory emotionCategory;

    public ResultEmotion() {
    }

    public ResultEmotionId getId() {
        return id;
    }

    public void setId(ResultEmotionId id) {
        this.id = id;
    }

    public AnalysisResult getAnalysisResult() {
        return analysisResult;
    }

    public void setAnalysisResult(AnalysisResult analysisResult) {
        this.analysisResult = analysisResult;
    }

    public EmotionCategory getEmotionCategory() {
        return emotionCategory;
    }

    public void setEmotionCategory(EmotionCategory emotionCategory) {
        this.emotionCategory = emotionCategory;
    }
}
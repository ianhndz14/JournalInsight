package com.journalinsight.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "emotion_category")
public class EmotionCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emotion_category_id_pk")
    private Long emotionCategoryId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 200)
    private String description;

    public EmotionCategory() {
    }

    public Long getEmotionCategoryId() {
        return emotionCategoryId;
    }

    public void setEmotionCategoryId(Long emotionCategoryId) {
        this.emotionCategoryId = emotionCategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
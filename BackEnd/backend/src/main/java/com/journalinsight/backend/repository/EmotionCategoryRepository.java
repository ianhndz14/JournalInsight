package com.journalinsight.backend.repository;

import com.journalinsight.backend.model.EmotionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmotionCategoryRepository extends JpaRepository<EmotionCategory, Long> {
    Optional<EmotionCategory> findByName(String name);
}
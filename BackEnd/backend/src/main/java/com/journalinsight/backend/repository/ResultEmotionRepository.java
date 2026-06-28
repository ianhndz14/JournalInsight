package com.journalinsight.backend.repository;

import com.journalinsight.backend.model.AnalysisResult;
import com.journalinsight.backend.model.ResultEmotion;
import com.journalinsight.backend.model.ResultEmotionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultEmotionRepository extends JpaRepository<ResultEmotion, ResultEmotionId> {

    List<ResultEmotion> findByAnalysisResult(AnalysisResult analysisResult);
}

package com.journalinsight.backend.service;

import com.journalinsight.backend.dto.EmotionDetectionResult;
import com.journalinsight.backend.model.*;
import com.journalinsight.backend.repository.AnalysisResultRepository;
import com.journalinsight.backend.repository.EmotionCategoryRepository;
import com.journalinsight.backend.repository.ResultEmotionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AnalysisService {

    private final EmotionClassifierService emotionClassifierService;
    private final AnalysisResultRepository analysisResultRepository;
    private final EmotionCategoryRepository emotionCategoryRepository;
    private final ResultEmotionRepository resultEmotionRepository;

    public AnalysisService(EmotionClassifierService emotionClassifierService,
                           AnalysisResultRepository analysisResultRepository,
                           EmotionCategoryRepository emotionCategoryRepository,
                           ResultEmotionRepository resultEmotionRepository) {
        this.emotionClassifierService = emotionClassifierService;
        this.analysisResultRepository = analysisResultRepository;
        this.emotionCategoryRepository = emotionCategoryRepository;
        this.resultEmotionRepository = resultEmotionRepository;
    }

    public AnalysisResult analyzeAndSave(JournalEntry journalEntry) {
        EmotionDetectionResult detection = emotionClassifierService.classify(journalEntry.getEntryText());

        AnalysisResult result = new AnalysisResult();
        result.setJournalEntry(journalEntry);
        result.setAnalysisDate(LocalDateTime.now());
        result.setGeneralClassification(detection.getGeneralClassification());

        AnalysisResult savedResult = analysisResultRepository.save(result);

        for (String emotionName : detection.getDetectedEmotions()) {
            EmotionCategory emotionCategory = emotionCategoryRepository.findByName(emotionName)
                    .orElseThrow(() -> new RuntimeException("Emotion category not found: " + emotionName));

            ResultEmotion resultEmotion = new ResultEmotion();
            resultEmotion.setId(new ResultEmotionId(
                    savedResult.getAnalysisResultId(),
                    emotionCategory.getEmotionCategoryId()
            ));
            resultEmotion.setAnalysisResult(savedResult);
            resultEmotion.setEmotionCategory(emotionCategory);

            resultEmotionRepository.save(resultEmotion);
        }

        return savedResult;
    }
}
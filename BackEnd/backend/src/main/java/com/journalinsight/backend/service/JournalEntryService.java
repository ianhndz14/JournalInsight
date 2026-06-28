package com.journalinsight.backend.service;

import com.journalinsight.backend.dto.CreateJournalEntryRequest;
import com.journalinsight.backend.dto.CreateJournalEntryResponse;
import com.journalinsight.backend.dto.EmotionDetectionResult;
import com.journalinsight.backend.dto.JournalEntryWithAnalysisDto;
import com.journalinsight.backend.model.AnalysisResult;
import com.journalinsight.backend.model.JournalEntry;
import com.journalinsight.backend.model.ResultEmotion;
import com.journalinsight.backend.repository.AnalysisResultRepository;
import com.journalinsight.backend.repository.JournalEntryRepository;
import com.journalinsight.backend.repository.ResultEmotionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final ResultEmotionRepository resultEmotionRepository;
    private final AnalysisService analysisService;
    private final EmotionClassifierService emotionClassifierService;

    public JournalEntryService(JournalEntryRepository journalEntryRepository,
                               AnalysisResultRepository analysisResultRepository,
                               ResultEmotionRepository resultEmotionRepository,
                               AnalysisService analysisService,
                               EmotionClassifierService emotionClassifierService) {
        this.journalEntryRepository = journalEntryRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.resultEmotionRepository = resultEmotionRepository;
        this.analysisService = analysisService;
        this.emotionClassifierService = emotionClassifierService;
    }

    public CreateJournalEntryResponse createJournalEntry(CreateJournalEntryRequest request) {
        JournalEntry entry = new JournalEntry();
        entry.setPatientId(request.getPatientId());
        entry.setEntryText(request.getEntryText());
        entry.setEntryDate(LocalDate.now());

        JournalEntry savedEntry = journalEntryRepository.save(entry);

        EmotionDetectionResult detection = emotionClassifierService.classify(savedEntry.getEntryText());

        analysisService.analyzeAndSave(savedEntry);

        return new CreateJournalEntryResponse(
                savedEntry.getJournalEntryId(),
                detection.getGeneralClassification(),
                detection.getDetectedEmotions(),
                "Journal entry created and analyzed successfully"
        );
    }

    public List<JournalEntryWithAnalysisDto> getEntriesByPatient(String patientId) {
        List<JournalEntry> entries = journalEntryRepository.findByPatientIdOrderByEntryDateDesc(patientId);
        return entries.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<String> getAvailableDates(String patientId) {
        return journalEntryRepository.findAvailableDatesByPatientId(patientId)
                .stream()
                .map(LocalDate::toString)
                .collect(Collectors.toList());
    }

    public JournalEntryWithAnalysisDto getEntryById(Long id) {
        JournalEntry entry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found: " + id));
        return toDto(entry);
    }

    public List<JournalEntryWithAnalysisDto> getEntriesByPatientAndDate(String patientId, String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<JournalEntry> entries = journalEntryRepository.findByPatientIdAndEntryDate(patientId, localDate);
        return entries.stream().map(this::toDto).collect(Collectors.toList());
    }

    public void deleteEntry(Long id) {
        if (!journalEntryRepository.existsById(id)) {
            throw new RuntimeException("Entry not found: " + id);
        }
        journalEntryRepository.deleteById(id);
    }

    private JournalEntryWithAnalysisDto toDto(JournalEntry entry) {
        AnalysisResult analysis = analysisResultRepository.findByJournalEntry(entry).orElse(null);

        List<String> emotions = List.of();
        if (analysis != null) {
            emotions = resultEmotionRepository.findByAnalysisResult(analysis)
                    .stream()
                    .map(re -> re.getEmotionCategory().getName())
                    .collect(Collectors.toList());
        }

        return new JournalEntryWithAnalysisDto(
                entry.getJournalEntryId(),
                entry.getPatientId(),
                entry.getEntryDate().toString(),
                entry.getEntryText(),
                analysis != null ? analysis.getGeneralClassification() : null,
                emotions
        );
    }
}

package com.journalinsight.backend.controller;

import com.journalinsight.backend.dto.CreateJournalEntryRequest;
import com.journalinsight.backend.dto.CreateJournalEntryResponse;
import com.journalinsight.backend.dto.JournalEntryWithAnalysisDto;
import com.journalinsight.backend.service.JournalEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journal-entries")
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    public JournalEntryController(JournalEntryService journalEntryService) {
        this.journalEntryService = journalEntryService;
    }

    @PostMapping
    public CreateJournalEntryResponse createJournalEntry(@RequestBody CreateJournalEntryRequest request) {
        return journalEntryService.createJournalEntry(request);
    }

    // GET /api/journal-entries/dates?patientId=7875551234
    @GetMapping("/dates")
    public List<String> getAvailableDates(@RequestParam String patientId) {
        return journalEntryService.getAvailableDates(patientId);
    }

    // GET /api/journal-entries/123
    @GetMapping("/{id}")
    public JournalEntryWithAnalysisDto getEntryById(@PathVariable Long id) {
        return journalEntryService.getEntryById(id);
    }

    // GET /api/journal-entries?patientId=7875551234
    @GetMapping
    public List<JournalEntryWithAnalysisDto> getEntriesByPatient(@RequestParam String patientId) {
        return journalEntryService.getEntriesByPatient(patientId);
    }

    // GET /api/journal-entries/by-date?patientId=7875551234&date=2026-04-29
    @GetMapping("/by-date")
    public List<JournalEntryWithAnalysisDto> getEntriesByPatientAndDate(
            @RequestParam String patientId,
            @RequestParam String date) {
        return journalEntryService.getEntriesByPatientAndDate(patientId, date);
    }

    // DELETE /api/journal-entries/123
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        journalEntryService.deleteEntry(id);
        return ResponseEntity.noContent().build();
    }
}

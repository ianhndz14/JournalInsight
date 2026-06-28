package edu.uprb.journalinsight.data

import edu.uprb.journalinsight.model.AnalysisResult
import edu.uprb.journalinsight.model.JournalEntry
import edu.uprb.journalinsight.model.Patient
import java.time.LocalDate

object DummyData {

    val patients = listOf(
        Patient(1, "Ian Hernandez"),
        Patient(2, "Carlos Rivera")
    )

    val entries = listOf(
        JournalEntry(101, 1, "2026-02-20", "Today I felt very happy and motivated."),
        JournalEntry(102, 1, "2026-02-21", "I felt anxious in the afternoon but better at night."),
        JournalEntry(201, 2, "2024-05-02", "It has been a difficult day. I feel frustrated.")
    )

    val analysis = listOf(
        AnalysisResult(101, "POSITIVE", 0.86, "v1-keywords"),
        AnalysisResult(102, "MIXED", 0.62, "v1-keywords"),
        AnalysisResult(201, "NEGATIVE", 0.81, "v1-keywords")
    )

    fun analysisForEntry(entryId: Int): AnalysisResult? =
        analysis.firstOrNull { it.entryId == entryId }
}
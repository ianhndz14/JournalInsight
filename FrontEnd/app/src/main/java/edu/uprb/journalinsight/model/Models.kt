package edu.uprb.journalinsight.model

enum class Role { PATIENT, PROFESSIONAL }

data class Patient(
    val id: Int,
    val name: String
)

data class JournalEntry(
    val id: Int,
    val patientId: Int,
    val date: String,
    val text: String
)

data class AnalysisResult(
    val entryId: Int,
    val category: String,
    val confidence: Double,
    val modelVersion: String
)
package edu.uprb.journalinsight.network.dto

data class CreateEntryRequest(
    val patientId: String,
    val entryText: String
)

data class CreateEntryResponse(
    val journalEntryId: Long,
    val detectedEmotion: String,
    val detectedEmotions: List<String> = emptyList(),
    val message: String
)

data class JournalEntryDto(
    val journalEntryId: Long,
    val patientId: String,
    val entryDate: String,
    val entryText: String,
    val generalClassification: String?,
    val detectedEmotions: List<String>
)

data class RedeemCodeRequest(
    val professionalId: String,
    val patientId: String
)

data class PatientDto(
    val phoneNumber: String,
    val firstName: String,
    val lastNamePaternal: String
) {
    val fullName: String get() = "$firstName $lastNamePaternal"
    val initials: String get() = "${firstName.firstOrNull() ?: ""}${lastNamePaternal.firstOrNull() ?: ""}".uppercase()
}

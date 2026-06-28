package edu.uprb.journalinsight.data

import edu.uprb.journalinsight.network.ApiClient
import edu.uprb.journalinsight.network.dto.*
import org.json.JSONObject
import retrofit2.HttpException

class JournalRepository {
    private val api = ApiClient.journalService

    // ── Auth ──────────────────────────────────────────────────────────────────
    suspend fun login(email: String, password: String): AuthResponse =
        api.login(LoginRequest(email, password))

    suspend fun register(
        firstName: String,
        lastNamePaternal: String,
        email: String,
        password: String,
        phoneNumber: String,
        accountType: String,
        specialty: String? = null
    ): AuthResponse = api.register(
        RegisterRequest(firstName, lastNamePaternal, email, password, phoneNumber, accountType, specialty)
    )

    // ── Journal entries ───────────────────────────────────────────────────────
    suspend fun createEntry(text: String): CreateEntryResponse =
        api.createEntry(CreateEntryRequest(patientId = SessionManager.phoneNumber, entryText = text))

    suspend fun getEntryById(id: Long): JournalEntryDto =
        api.getEntryById(id)

    suspend fun getEntriesForPatient(): List<JournalEntryDto> =
        api.getEntriesByPatient(SessionManager.phoneNumber)

    suspend fun getEntriesForDate(patientId: String, date: String): List<JournalEntryDto> =
        api.getEntriesByDate(patientId, date)

    suspend fun getAvailableDates(patientId: String): List<String> =
        api.getAvailableDates(patientId)

    suspend fun deleteEntry(id: Long) {
        val response = api.deleteEntry(id)
        if (!response.isSuccessful) throw Exception("Delete failed (${response.code()})")
    }

    suspend fun getPatientsForProfessional(): List<PatientDto> =
        api.getPatientsForProfessional(SessionManager.phoneNumber)

    suspend fun getProfessionalsForPatient(): List<PatientDto> =
        api.getProfessionalsForPatient(SessionManager.phoneNumber)

    suspend fun redeemInvitationCode(professionalPhone: String): String {
        try {
            val result = api.redeemCode(
                RedeemCodeRequest(
                    professionalId = professionalPhone,
                    patientId      = SessionManager.phoneNumber
                )
            )
            return result["message"] ?: "Linked successfully."
        } catch (e: HttpException) {
            val errorMessage = try {
                val body = e.response()?.errorBody()?.string()
                JSONObject(body ?: "").getString("error")
            } catch (_: Exception) {
                "Incorrect phone number."
            }
            throw Exception(errorMessage)
        }
    }

    // Paciente desvincula a su profesional
    suspend fun unlinkPatientProfessional(professionalPhone: String): String {
        try {
            val result = api.unlinkPatientProfessional(
                patientId      = SessionManager.phoneNumber,
                professionalId = professionalPhone
            )
            return result["message"] ?: "Unlinked successfully."
        } catch (e: HttpException) {
            val errorMessage = try {
                val body = e.response()?.errorBody()?.string()
                JSONObject(body ?: "").getString("error")
            } catch (_: Exception) {
                "Could not unlink."
            }
            throw Exception(errorMessage)
        }
    }

    // Profesional desvincula a un paciente
    suspend fun unlinkProfessionalPatient(patientPhone: String): String {
        try {
            val result = api.unlinkPatientProfessional(
                patientId      = patientPhone,
                professionalId = SessionManager.phoneNumber
            )
            return result["message"] ?: "Unlinked successfully."
        } catch (e: HttpException) {
            val errorMessage = try {
                val body = e.response()?.errorBody()?.string()
                JSONObject(body ?: "").getString("error")
            } catch (_: Exception) {
                "Could not unlink."
            }
            throw Exception(errorMessage)
        }
    }
}

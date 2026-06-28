package edu.uprb.journalinsight.network

import edu.uprb.journalinsight.network.dto.AuthResponse
import edu.uprb.journalinsight.network.dto.CreateEntryRequest
import edu.uprb.journalinsight.network.dto.CreateEntryResponse
import edu.uprb.journalinsight.network.dto.JournalEntryDto
import edu.uprb.journalinsight.network.dto.LoginRequest
import edu.uprb.journalinsight.network.dto.PatientDto
import edu.uprb.journalinsight.network.dto.RedeemCodeRequest
import edu.uprb.journalinsight.network.dto.RegisterRequest
import retrofit2.Response
import retrofit2.http.*

interface JournalApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/journal-entries")
    suspend fun createEntry(@Body request: CreateEntryRequest): CreateEntryResponse

    @DELETE("api/journal-entries/{id}")
    suspend fun deleteEntry(@Path("id") id: Long): Response<Unit>

    @POST("api/invitations/redeem")
    suspend fun redeemCode(@Body request: RedeemCodeRequest): Map<String, String>

    @GET("api/invitations/professionals")
    suspend fun getProfessionalsForPatient(@Query("patientId") patientId: String): List<PatientDto>

    @DELETE("api/invitations/unlink")
    suspend fun unlinkPatientProfessional(
        @Query("patientId") patientId: String,
        @Query("professionalId") professionalId: String
    ): Map<String, String>

    @GET("api/patients")
    suspend fun getPatientsForProfessional(@Query("professionalId") professionalId: String): List<PatientDto>

    @GET("api/journal-entries/dates")
    suspend fun getAvailableDates(@Query("patientId") patientId: String): List<String>

    @GET("api/journal-entries/{id}")
    suspend fun getEntryById(@Path("id") id: Long): JournalEntryDto

    @GET("api/journal-entries")
    suspend fun getEntriesByPatient(@Query("patientId") patientId: String): List<JournalEntryDto>

    @GET("api/journal-entries/by-date")
    suspend fun getEntriesByDate(
        @Query("patientId") patientId: String,
        @Query("date") date: String
    ): List<JournalEntryDto>
}

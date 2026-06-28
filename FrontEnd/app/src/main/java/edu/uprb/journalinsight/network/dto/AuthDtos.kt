package edu.uprb.journalinsight.network.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val firstName: String,
    val lastNamePaternal: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val accountType: String,   // "P" = Patient, "R" = Professional
    val specialty: String? = null
)

data class AuthResponse(
    val phoneNumber: String,
    val accountType: String,
    val firstName: String,
    val message: String
)

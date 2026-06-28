package edu.uprb.journalinsight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.uprb.journalinsight.data.JournalRepository
import edu.uprb.journalinsight.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object SuccessPatient : AuthUiState()
    object SuccessProfessional : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {

    private val repository = JournalRepository()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter your email and password.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = repository.login(email, password)
                SessionManager.phoneNumber = response.phoneNumber
                SessionManager.accountType = response.accountType
                SessionManager.firstName   = response.firstName
                _uiState.value = if (response.accountType == "P")
                    AuthUiState.SuccessPatient
                else
                    AuthUiState.SuccessProfessional
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Incorrect email or password.")
            }
        }
    }

    fun register(
        firstName: String,
        lastNamePaternal: String,
        email: String,
        password: String,
        phoneNumber: String,
        accountType: String,
        specialty: String? = null
    ) {
        if (firstName.isBlank() || lastNamePaternal.isBlank() ||
            email.isBlank() || password.isBlank() || phoneNumber.isBlank()) {
            _uiState.value = AuthUiState.Error("Please fill in all required fields.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = repository.register(
                    firstName, lastNamePaternal, email, password, phoneNumber, accountType, specialty
                )
                SessionManager.phoneNumber = response.phoneNumber
                SessionManager.accountType = response.accountType
                SessionManager.firstName   = response.firstName
                _uiState.value = if (response.accountType == "P")
                    AuthUiState.SuccessPatient
                else
                    AuthUiState.SuccessProfessional
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(
                    e.message ?: "Could not create account. Email or phone may already be in use."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

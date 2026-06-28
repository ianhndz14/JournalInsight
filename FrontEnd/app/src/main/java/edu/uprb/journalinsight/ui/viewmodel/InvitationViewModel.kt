package edu.uprb.journalinsight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.uprb.journalinsight.data.JournalRepository
import edu.uprb.journalinsight.network.dto.PatientDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ── Shared action state (link / unlink result) ────────────────────────────────

sealed class LinkActionState {
    object Idle    : LinkActionState()
    object Loading : LinkActionState()
    data class Success(val message: String) : LinkActionState()
    data class Error(val message: String)   : LinkActionState()
}

// ── Patient side: manage linked professionals ─────────────────────────────────

class RedeemCodeViewModel : ViewModel() {

    private val repository = JournalRepository()

    private val _professionals = MutableStateFlow<List<PatientDto>>(emptyList())
    val professionals: StateFlow<List<PatientDto>> = _professionals

    private val _actionState = MutableStateFlow<LinkActionState>(LinkActionState.Idle)
    val actionState: StateFlow<LinkActionState> = _actionState

    init { loadProfessionals() }

    fun loadProfessionals() {
        viewModelScope.launch {
            try {
                _professionals.value = repository.getProfessionalsForPatient()
            } catch (_: Exception) {}
        }
    }

    fun link(professionalPhone: String) {
        if (professionalPhone.isBlank()) {
            _actionState.value = LinkActionState.Error("Please enter the professional's phone number.")
            return
        }
        viewModelScope.launch {
            _actionState.value = LinkActionState.Loading
            try {
                val message = repository.redeemInvitationCode(professionalPhone)
                loadProfessionals()
                _actionState.value = LinkActionState.Success(message)
            } catch (e: Exception) {
                _actionState.value = LinkActionState.Error(
                    e.message ?: "Professional not found or already linked."
                )
            }
        }
    }

    fun unlink(professionalPhone: String) {
        viewModelScope.launch {
            try {
                repository.unlinkPatientProfessional(professionalPhone)
                loadProfessionals()
            } catch (_: Exception) {}
        }
    }

    fun resetAction() { _actionState.value = LinkActionState.Idle }
}

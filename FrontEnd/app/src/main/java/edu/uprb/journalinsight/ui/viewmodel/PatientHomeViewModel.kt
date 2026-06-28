package edu.uprb.journalinsight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.uprb.journalinsight.data.JournalRepository
import edu.uprb.journalinsight.network.dto.JournalEntryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PatientHomeUiState {
    object Loading : PatientHomeUiState()
    data class Success(val entries: List<JournalEntryDto>) : PatientHomeUiState()
    data class Error(val message: String) : PatientHomeUiState()
}

class PatientHomeViewModel : ViewModel() {

    private val repository = JournalRepository()

    private val _uiState = MutableStateFlow<PatientHomeUiState>(PatientHomeUiState.Loading)
    val uiState: StateFlow<PatientHomeUiState> = _uiState

    // Sin init — la pantalla controla cuándo cargar
    fun loadEntries() {
        viewModelScope.launch {
            _uiState.value = PatientHomeUiState.Loading
            try {
                val entries = repository.getEntriesForPatient()
                _uiState.value = PatientHomeUiState.Success(entries)
            } catch (e: Exception) {
                _uiState.value = PatientHomeUiState.Error("Could not load entries. Is the backend running?")
            }
        }
    }
}

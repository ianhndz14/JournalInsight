package edu.uprb.journalinsight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.uprb.journalinsight.data.JournalRepository
import edu.uprb.journalinsight.network.dto.JournalEntryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfessionalDayUiState {
    object Loading : ProfessionalDayUiState()
    data class Success(val entries: List<JournalEntryDto>) : ProfessionalDayUiState()
    data class Error(val message: String) : ProfessionalDayUiState()
}

class ProfessionalDayViewModel : ViewModel() {

    private val repository = JournalRepository()

    private val _uiState = MutableStateFlow<ProfessionalDayUiState>(ProfessionalDayUiState.Loading)
    val uiState: StateFlow<ProfessionalDayUiState> = _uiState

    fun loadEntries(patientId: String, date: String) {
        viewModelScope.launch {
            _uiState.value = ProfessionalDayUiState.Loading
            try {
                val entries = repository.getEntriesForDate(patientId, date)
                _uiState.value = ProfessionalDayUiState.Success(entries)
            } catch (e: Exception) {
                _uiState.value = ProfessionalDayUiState.Error("Could not load entries. Is the backend running?")
            }
        }
    }
}

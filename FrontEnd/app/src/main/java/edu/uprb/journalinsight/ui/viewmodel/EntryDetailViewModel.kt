package edu.uprb.journalinsight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.uprb.journalinsight.data.JournalRepository
import edu.uprb.journalinsight.network.dto.JournalEntryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class EntryDetailUiState {
    object Loading : EntryDetailUiState()
    data class Success(val entry: JournalEntryDto) : EntryDetailUiState()
    data class Error(val message: String) : EntryDetailUiState()
    object Deleted : EntryDetailUiState()
}

class EntryDetailViewModel : ViewModel() {

    private val repository = JournalRepository()

    private val _uiState = MutableStateFlow<EntryDetailUiState>(EntryDetailUiState.Loading)
    val uiState: StateFlow<EntryDetailUiState> = _uiState

    fun loadEntry(id: Long) {
        viewModelScope.launch {
            _uiState.value = EntryDetailUiState.Loading
            try {
                val entry = repository.getEntryById(id)
                _uiState.value = EntryDetailUiState.Success(entry)
            } catch (e: Exception) {
                _uiState.value = EntryDetailUiState.Error("Entry not found.")
            }
        }
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteEntry(id)
                _uiState.value = EntryDetailUiState.Deleted
            } catch (e: Exception) {
                _uiState.value = EntryDetailUiState.Error("Could not delete entry.")
            }
        }
    }
}

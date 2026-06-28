package edu.uprb.journalinsight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.uprb.journalinsight.data.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class NewEntryUiState {
    object Idle : NewEntryUiState()
    object Loading : NewEntryUiState()
    data class Success(val emotion: String, val detectedEmotions: List<String> = emptyList()) : NewEntryUiState()
    data class Error(val message: String) : NewEntryUiState()
}

class NewEntryViewModel : ViewModel() {

    private val repository = JournalRepository()

    private val _uiState = MutableStateFlow<NewEntryUiState>(NewEntryUiState.Idle)
    val uiState: StateFlow<NewEntryUiState> = _uiState

    fun analyzeAndSave(text: String) {
        if (text.isBlank()) {
            _uiState.value = NewEntryUiState.Error("Please write something before analyzing.")
            return
        }
        viewModelScope.launch {
            _uiState.value = NewEntryUiState.Loading
            try {
                val response = repository.createEntry(text)
                _uiState.value = NewEntryUiState.Success(
                    emotion           = response.detectedEmotion,
                    detectedEmotions  = response.detectedEmotions
                )
            } catch (e: Exception) {
                _uiState.value = NewEntryUiState.Error("Could not reach the server. Is the backend running?")
            }
        }
    }
}

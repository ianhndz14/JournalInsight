package edu.uprb.journalinsight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.uprb.journalinsight.data.JournalRepository
import edu.uprb.journalinsight.network.dto.PatientDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfessionalHomeViewModel : ViewModel() {

    private val repository = JournalRepository()

    private val _patients = MutableStateFlow<List<PatientDto>>(emptyList())
    val patients: StateFlow<List<PatientDto>> = _patients

    private val _availableDates = MutableStateFlow<Set<String>>(emptySet())
    val availableDates: StateFlow<Set<String>> = _availableDates

    init {
        loadPatients()
    }

    fun loadPatients() {
        viewModelScope.launch {
            try {
                val list = repository.getPatientsForProfessional()
                _patients.value = list
                if (list.isNotEmpty()) {
                    loadAvailableDates(list.first().phoneNumber)
                }
            } catch (e: Exception) {
                _patients.value = emptyList()
            }
        }
    }

    fun loadAvailableDates(patientId: String) {
        viewModelScope.launch {
            _availableDates.value = emptySet()
            try {
                val dates = repository.getAvailableDates(patientId)
                _availableDates.value = dates.toSet()
            } catch (e: Exception) {
                _availableDates.value = emptySet()
            }
        }
    }

    fun unlinkPatient(patientPhone: String) {
        viewModelScope.launch {
            try {
                repository.unlinkProfessionalPatient(patientPhone)
                loadPatients()
            } catch (_: Exception) {}
        }
    }
}

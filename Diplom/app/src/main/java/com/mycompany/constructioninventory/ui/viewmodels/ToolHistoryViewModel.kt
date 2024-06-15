package com.mycompany.constructioninventory.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycompany.constructioninventory.data.RentLogRepository
import com.mycompany.constructioninventory.models.RentLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToolHistoryViewModel @Inject constructor(
    private val rentLogRepository: RentLogRepository
) : ViewModel() {

    private val _rentLogs = MutableStateFlow(RentLogState())
    val rentLogs = _rentLogs.asStateFlow()

    fun loadRentLogsForTool(toolId: String) {
        viewModelScope.launch {
            _rentLogs.value = _rentLogs.value.copy(isLoading = true)
            try {
                val logs = rentLogRepository.getRentLogsByTool(toolId)
                _rentLogs.value = _rentLogs.value.copy(
                    rentLogs = logs,
                    isLoading = false
                )
            } catch (e: Exception) {
                _rentLogs.value = _rentLogs.value.copy(
                    error = "Произошла ошибка при загрузке истории",
                    isLoading = false
                )
            }
        }
    }

    data class RentLogState(
        val rentLogs: List<RentLog> = emptyList(),
        val isLoading: Boolean = false,
        val error: String = ""
    )
}
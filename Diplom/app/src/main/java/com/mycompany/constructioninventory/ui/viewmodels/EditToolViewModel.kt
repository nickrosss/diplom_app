package com.mycompany.constructioninventory.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycompany.constructioninventory.data.ToolsRepository
import com.mycompany.constructioninventory.models.Tool
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditToolViewModel @Inject constructor(
    private val toolsRepository: ToolsRepository
) : ViewModel() {

    private val _tools = MutableStateFlow<List<Tool>>(emptyList())

    fun getToolById(toolId: String): Flow<Tool?> = flow {
        if (_tools.value.isEmpty()) {
            _tools.value = toolsRepository.getTools()
        }

        emit(_tools.value.find { it.id == toolId })
    }

    fun updateTool(tool: Tool, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val result = toolsRepository.updateTool(tool)
            result.onSuccess {
                _tools.value = _tools.value.map {
                    if (it.id == tool.id) tool else it
                }
                onSuccess()
            }.onFailure {
                onError()
            }
        }
    }
}
package com.mycompany.constructioninventory.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycompany.constructioninventory.data.ToolsRepository
import com.mycompany.constructioninventory.models.Tool
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddToolViewModel @Inject constructor(
    private val toolsRepository: ToolsRepository
) : ViewModel() {

    fun addTool(tool: Tool, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {

            // Создание нового инструмента с дополнительными полями
            val newTool = tool.copy()

            // Добавление инструмента в репозиторий
            val result = toolsRepository.addTool(newTool)
            result.onSuccess {
                onSuccess()
            }.onFailure {
                onError()
            }
        }
    }
}

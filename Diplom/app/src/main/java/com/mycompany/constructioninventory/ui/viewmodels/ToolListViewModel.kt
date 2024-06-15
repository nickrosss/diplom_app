package com.mycompany.constructioninventory.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycompany.constructioninventory.data.RentLogRepository
import com.mycompany.constructioninventory.data.ToolsRepository
import com.mycompany.constructioninventory.data.UserRepository
import com.mycompany.constructioninventory.models.RentLog
import com.mycompany.constructioninventory.models.RentLogAction
import com.mycompany.constructioninventory.models.Tool
import com.mycompany.constructioninventory.models.ToolStatus
import com.mycompany.constructioninventory.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ToolListViewModel @Inject constructor(
    private val toolsRepository: ToolsRepository,
    private val rentLogRepository: RentLogRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _toolsState = MutableStateFlow(ToolsState())
    val toolsState: StateFlow<ToolsState> = _toolsState.asStateFlow()

    private val _Tool = MutableStateFlow<Tool?>(null)
    val tool = _Tool.asStateFlow()

    private val _ToolsError = MutableStateFlow<String?>(null)
    val toolsError: StateFlow<String?> = _ToolsError.asStateFlow()

    init {
        loadTools()
    }

    private fun loadTools() {
        viewModelScope.launch {
            _toolsState.value = _toolsState.value.copy(isLoading = true)
            try {
                val tools = toolsRepository.getTools()
                _toolsState.value = _toolsState.value.copy(tools = tools, isLoading = false)
            } catch (e: Exception) {
                _toolsState.value = _toolsState.value.copy(
                    error = "Произошла ошибка при загрузке инструментов",
                    isLoading = false
                )
            }
        }
    }

    fun deleteTool(tool: Tool) {
        viewModelScope.launch {
            val result = toolsRepository.deleteTool(tool.id)

            result.onSuccess {
                // Если удаление прошло успешно, обновляем список инструментов
                loadTools()
            }.onFailure { error ->
                // Обработка ошибки удаления
                _toolsState.value =
                    _toolsState.value.copy(error = "Ошибка при удалении: ${error.message}")
            }
        }
    }

    fun takeTool(tool: Tool, currentUser: User) {
        viewModelScope.launch {
            try {
                // Проверяем доступность инструмента перед попыткой взять его
                if (tool.status == ToolStatus.AVAILABLE) {
                    val updatedTool =
                        tool.copy(status = ToolStatus.RENTED, currentUser = currentUser)
                    toolsRepository.updateTool(updatedTool) // Обновляем статус инструмента

                    // Создаем запись в журнале аренды
                    val rentLog = RentLog(
                        id = "", // ID будет сгенерировано в API
                        timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                        user = currentUser,
                        tool = updatedTool,
                        action = RentLogAction.CHECKOUT
                    )
                    rentLogRepository.createRentLog(rentLog)

                    // Обновляем список инструментов и сбрасываем данные сканирования
                    loadTools()
                    _Tool.value = null
                } else {
                    // Обрабатываем ситуацию, когда инструмент недоступен
                    _ToolsError.value = "Инструмент недоступен для взятия"
                }
            } catch (e: Exception) {
                _ToolsError.value = "Ошибка при взятии инструмента: ${e.message}"
                Log.e("ToolsViewModel", "Ошибка при взятии инструмента", e)
            }
        }
    }

    fun returnTool(tool: Tool) {
        viewModelScope.launch {
            try {
                // Проверяем, взят ли инструмент в аренду и кем
                if (tool.status == ToolStatus.RENTED && tool.currentUser != null) {
                    val updatedTool = tool.copy(status = ToolStatus.AVAILABLE, currentUser = null)
                    toolsRepository.updateTool(updatedTool) // Обновляем статус инструмента

                    // Обновляем запись в журнале аренды
                    // Вам нужно найти ID записи о выдаче этого инструмента этому пользователю
                    val rentLogId = findRentLogId(
                        tool.id,
                        tool.currentUser!!.id
                    ) // Замените на логику поиска ID
                    if (rentLogId != null) {
                        rentLogRepository.updateRentLog(rentLogId, RentLogAction.RETURN.name)
                    } else {
                        Log.e(
                            "ToolsViewModel",
                            "Не найден ID журнала аренды для возврата инструмента ${tool.id}"
                        )
                    }

                    // Обновляем список инструментов и сбрасываем данные сканирования
                    loadTools()
                    _Tool.value = null
                } else {
                    // Обрабатываем ситуацию, когда инструмент не был взят в аренду
                    _ToolsError.value = "Инструмент не был взят в аренду"
                }
            } catch (e: Exception) {
                _ToolsError.value = "Ошибка при возврате инструмента: ${e.message}"
                Log.e("ToolsViewModel", "Ошибка при возврате инструмента", e)
            }
        }
    }

    private suspend fun findRentLogId(toolId: String, userId: String): String? {
        return try {
            val rentLogs = rentLogRepository.getRentLogsByTool(toolId)
            // Находим последнюю запись о выдаче инструмента этому пользователю
            rentLogs.findLast {
                it.tool.id == toolId && it.user.id == userId && it.action == RentLogAction.CHECKOUT
            }?.id
        } catch (e: Exception) {
            Log.e("ToolsViewModel", "Ошибка при поиске ID журнала аренды", e)
            null
        }
    }

    fun showError(message: String) {
        viewModelScope.launch {
            _ToolsError.emit(message)
        }
    }

    suspend fun getUserById(userId: String): Result<User> = viewModelScope.async {
        try {
            val user = userRepository.getUser(userId)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }.await()

    data class ToolsState(
        val tools: List<Tool> = emptyList(),
        val isLoading: Boolean = false,
        val error: String = ""
    )
}
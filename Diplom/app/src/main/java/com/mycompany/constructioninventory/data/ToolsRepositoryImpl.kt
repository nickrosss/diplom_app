package com.mycompany.constructioninventory.data

import com.mycompany.constructioninventory.models.Tool
import com.mycompany.constructioninventory.network.ToolsApi
import javax.inject.Inject

class ToolsRepositoryImpl @Inject constructor(private val toolsApi: ToolsApi) : ToolsRepository {

    override suspend fun getTool(id: String): Tool? {
        return try {
            toolsApi.getTool(id)
        } catch (e: Exception) {
            // Логирование ошибки
            // e.printStackTrace()
            null
        }
    }

    override suspend fun getTools(): List<Tool> {
        return try {
            toolsApi.getTools()
        } catch (e: Exception) {
            // Логирование ошибки
            // e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun addTool(tool: Tool): Result<Tool> {
        return try {
            val response = toolsApi.addTool(tool)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Неизвестная ошибка сервера"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            // Логирование ошибки
            // e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun updateTool(tool: Tool): Result<Tool> {
        return try {
            val response = toolsApi.updateTool(tool.id, tool)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Неизвестная ошибка сервера"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            // Логирование ошибки
            // e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun deleteTool(toolId: String): Result<Unit> {
        return try {
            val response = toolsApi.deleteTool(toolId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Неизвестная ошибка сервера"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            // Логирование ошибки
            // e.printStackTrace()
            Result.failure(e)
        }
    }

    // Взятие инструмента
    override suspend fun takeTool(toolId: String, userId: String): Result<Tool> {
        return try {
            val response = toolsApi.takeTool(toolId, userId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Неизвестная ошибка сервера"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Возврат инструмента
    override suspend fun returnTool(toolId: String): Result<Tool> {
        return try {
            val tool = toolsApi.returnTool(toolId) // Получаем инструмент напрямую
            Result.success(tool) // Возвращаем успешный результат с полученным инструментом
        } catch (e: Exception) {
            // Обработка ошибки
            val errorMessage = e.message ?: "Неизвестная ошибка сервера"
            Result.failure(Exception(errorMessage))
        }
    }
}
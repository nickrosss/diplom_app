package com.mycompany.constructioninventory.data

import com.mycompany.constructioninventory.models.RentLog
import com.mycompany.constructioninventory.network.ToolsApi
import javax.inject.Inject

class RentLogRepositoryImpl @Inject constructor(private val toolsApi: ToolsApi) :
    RentLogRepository {
    // Получение всех записей журнала аренды.
    override suspend fun getRentLogs(): List<RentLog> {
        return try {
            toolsApi.getRentLogs()
        } catch (e: Exception) {
            // Обработка ошибки
            emptyList()
        }
    }

    // Получение записей журнала аренды по ID инструмента.
    override suspend fun getRentLogsByTool(toolId: String): List<RentLog> {
        return try {
            toolsApi.getRentLogsByTool(toolId)
        } catch (e: Exception) {
            // Обработка ошибки
            emptyList()
        }
    }

    // Получение записей журнала аренды по ID пользователя.
    override suspend fun getRentLogsByUser(userId: String): List<RentLog> {
        return try {
            toolsApi.getRentLogsByUser(userId)
        } catch (e: Exception) {
            // Обработка ошибки
            emptyList()
        }
    }

    // Создание новой записи в журнале аренды при выдаче инструмента.
    override suspend fun createRentLog(rentLog: RentLog): Result<RentLog> {
        return try {
            val response = toolsApi.createRentLog(rentLog)
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

    // Обновление записи в журнале аренды при возврате инструмента
    override suspend fun updateRentLog(rentLogId: String, action: String): Result<RentLog> {
        return try {
            val response = toolsApi.updateRentLog(rentLogId, action)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                // Обработка ошибки сервера
                val errorMessage = response.errorBody()?.string() ?: "Неизвестная ошибка сервера"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
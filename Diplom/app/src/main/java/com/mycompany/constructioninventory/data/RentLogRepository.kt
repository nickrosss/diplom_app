package com.mycompany.constructioninventory.data

import com.mycompany.constructioninventory.models.RentLog

interface RentLogRepository {
    suspend fun getRentLogs(): List<RentLog>
    suspend fun getRentLogsByTool(toolId: String): List<RentLog>
    suspend fun getRentLogsByUser(userId: String): List<RentLog>
    suspend fun createRentLog(rentLog: RentLog): Result<RentLog>
    suspend fun updateRentLog(rentLogId: String, action: String): Result<RentLog>
}
package com.mycompany.constructioninventory.data

import com.mycompany.constructioninventory.models.Tool

interface ToolsRepository {
    suspend fun getTool(id: String): Tool?
    suspend fun getTools(): List<Tool>
    suspend fun addTool(tool: Tool): Result<Tool>
    suspend fun updateTool(tool: Tool): Result<Tool>
    suspend fun deleteTool(toolId: String): Result<Unit>
    suspend fun takeTool(toolId: String, userId: String): Result<Tool>
    suspend fun returnTool(toolId: String): Result<Tool>
}
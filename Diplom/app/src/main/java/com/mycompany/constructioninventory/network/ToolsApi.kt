package com.mycompany.constructioninventory.network

import com.mycompany.constructioninventory.models.RentLog
import com.mycompany.constructioninventory.models.Tool
import com.mycompany.constructioninventory.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class RegisterUser(val user: RegisterUserData)
data class RegisterUserData(val username: String, val email: String, val password: String)

data class LoginUser(val user: LoginUserData)
data class LoginUserData(val email: String, val password: String)

interface ToolsApi {
    @POST("/api/users/")
    suspend fun register(@Body data: RegisterUser): User

    @POST("/api/users/login/")
    suspend fun login(@Body data: LoginUser): User

    @GET("/api/tools/")
    suspend fun getTools(): List<Tool>

    @GET("/api/tools/{id}/")
    suspend fun getTool(@Path("id") id: String): Tool

    @POST("/api/tools/")
    suspend fun addTool(@Body tool: Tool): Response<Tool>

    @PUT("/api/tools/{id}/")
    suspend fun updateTool(@Path("id") id: String, @Body tool: Tool): Response<Tool>

    @DELETE("/api/tools/{id}/")
    suspend fun deleteTool(@Path("id") id: String): Response<Unit>

    @GET("/api/rentlogs/")
    suspend fun getRentLogs(): List<RentLog>

    @GET("/api/rentlogs/tool/{toolId}/")
    suspend fun getRentLogsByTool(@Path("toolId") toolId: String): List<RentLog>

    @GET("/api/rentlogs/user/{userId}/")
    suspend fun getRentLogsByUser(@Path("userId") userId: String): List<RentLog>

    @POST("/api/rentlogs/")
    suspend fun createRentLog(@Body rentLog: RentLog): Response<RentLog>

    @PUT("/api/rentlogs/{id}/")
    suspend fun updateRentLog(@Path("id") id: String, @Body action: String): Response<RentLog>

    @GET("/api/users/{id}/")
    suspend fun getUser(@Path("id") userId: String): User

    @POST("/api/tools/{id}/take/")
    suspend fun takeTool(@Path("id") toolId: String, @Body userId: String): Response<Tool>

    @POST("/api/tools/{id}/return/")
    suspend fun returnTool(@Path("id") id: String): Tool
}
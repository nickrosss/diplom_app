package com.mycompany.constructioninventory.data

import com.mycompany.constructioninventory.models.User
import com.mycompany.constructioninventory.network.ToolsApi
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val toolsApi: ToolsApi) : UserRepository {

    override suspend fun getUser(userId: String): User {
        return toolsApi.getUser(userId)
    }
}
package com.mycompany.constructioninventory.data

import com.mycompany.constructioninventory.models.User
import com.mycompany.constructioninventory.network.LoginUser
import com.mycompany.constructioninventory.network.RegisterUser
import com.mycompany.constructioninventory.network.ToolsApi
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val toolsApi: ToolsApi) : LoginRepository {

    override suspend fun register(user: RegisterUser): User {
        return toolsApi.register(user)
    }

    override suspend fun login(user: LoginUser): User {
        return toolsApi.login(user)
    }
}
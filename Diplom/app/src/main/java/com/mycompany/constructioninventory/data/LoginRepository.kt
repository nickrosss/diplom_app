package com.mycompany.constructioninventory.data

import com.mycompany.constructioninventory.models.User
import com.mycompany.constructioninventory.network.LoginUser
import com.mycompany.constructioninventory.network.RegisterUser

interface LoginRepository {
    suspend fun register(user: RegisterUser): User
    suspend fun login(user: LoginUser): User
}
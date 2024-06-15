package com.mycompany.constructioninventory.data

import com.mycompany.constructioninventory.models.User

interface UserRepository {
    suspend fun getUser(userId: String): User
}
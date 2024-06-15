package com.mycompany.constructioninventory.models

data class User(

    val id: String,
    val username: String,
    val email: String,
    val token: String,
    val role: UserRole,
)
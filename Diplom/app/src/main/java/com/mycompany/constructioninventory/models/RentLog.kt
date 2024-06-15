package com.mycompany.constructioninventory.models

data class RentLog(
    val id: String,
    val timestamp: String,
    val user: User,
    val tool: Tool,
    val action: RentLogAction
)
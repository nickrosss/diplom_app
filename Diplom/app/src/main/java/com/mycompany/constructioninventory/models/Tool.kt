package com.mycompany.constructioninventory.models

data class Tool(
    val id: String,
    val name: String,
    val description: String,
    var status: ToolStatus = ToolStatus.AVAILABLE,
    var currentUser: User? = null
)

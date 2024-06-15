@file:OptIn(ExperimentalMaterial3Api::class)

package com.mycompany.constructioninventory.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mycompany.constructioninventory.models.Tool
import com.mycompany.constructioninventory.models.ToolStatus
import com.mycompany.constructioninventory.models.User
import com.mycompany.constructioninventory.ui.viewmodels.ToolListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolDetailsScreen(navController: NavController, toolId: String) {
    val viewModel: ToolListViewModel = hiltViewModel()
    val tool by viewModel.tool.collectAsState()
    val context = LocalContext.current
    var showErrorSnackbar by remember { mutableStateOf(false) }
    val toolError by viewModel.toolsError.collectAsState()
    val user = remember { mutableStateOf<User?>(null) }

    // Загружаем инструмент по toolId
    LaunchedEffect(toolId) {

        // Получаем ID пользователя из SharedPreferences
        val userId = getUserId(context)

        // Загружаем User с сервера
        if (userId.isNotBlank()) {
            viewModel.getUserById(userId)
                .onSuccess { loadedUser ->
                    user.value = loadedUser
                }.onFailure { error ->
                    Log.e("ToolDetailsScreen", "Ошибка загрузки пользователя: ${error.message}")
                    showErrorSnackbar = true
                    viewModel.showError("Ошибка загрузки пользователя")
                }
        } else {
            Log.e("ToolDetailsScreen", "ID пользователя не найден")
            showErrorSnackbar = true
            viewModel.showError("ID пользователя не найден")
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Детали инструмента") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        snackbarHost = {
            if (showErrorSnackbar) {
                Snackbar(
                    action = {
                        TextButton(onClick = { showErrorSnackbar = false }) {
                            Text("ОК")
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) { Text("Ошибка: $toolError") }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            tool?.let { tool ->
                ToolInfoCard(tool = tool)
                Spacer(modifier = Modifier.height(16.dp))

                    //val userId = getUserId(context) // Получаем ID пользователя из SharedPreferences

                if (tool.status == ToolStatus.AVAILABLE) {
                    Button(
                        onClick = {
                            val currentUser = user.value
                            if (currentUser != null) {
                                viewModel.takeTool(tool, currentUser) // Передаем объект User
                                showErrorSnackbar = viewModel.toolsError.value != null
                            } else {
                                // Обработайте случай, когда User не найден
                                showErrorSnackbar = true
                                viewModel.showError("Пользователь не найден")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Взять инструмент")
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.returnTool(tool)
                            showErrorSnackbar = viewModel.toolsError.value != null
                        },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Вернуть инструмент")
                    }
                }
            } ?: run {
                if (viewModel.toolsError.value != null) {
                    Text("Ошибка: ${viewModel.toolsError.value}")
                } else {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun ToolInfoCard(tool: Tool) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Название: ${tool.name}", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Описание: ${tool.description}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Статус: ${tool.status}")
        }
    }
}


// Функция для получения ID пользователя из SharedPreferences
fun getUserId(context: Context): String {
    val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
    return sharedPref.getString("user_id", "") ?: ""
}
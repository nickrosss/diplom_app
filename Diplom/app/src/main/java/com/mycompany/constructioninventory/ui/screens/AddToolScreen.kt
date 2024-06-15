@file:OptIn(ExperimentalMaterial3Api::class)

package com.mycompany.constructioninventory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mycompany.constructioninventory.models.Tool
import com.mycompany.constructioninventory.models.ToolStatus
import com.mycompany.constructioninventory.ui.viewmodels.AddToolViewModel

@Composable
fun AddToolScreen(navController: NavController) {
    val viewModel: AddToolViewModel = hiltViewModel()
    val toolName = remember { mutableStateOf("") }
    val toolDescription = remember { mutableStateOf("") }
    val showErrorSnackbar = remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            if (showErrorSnackbar.value) {
                Snackbar(
                    action = {
                        TextButton(onClick = { showErrorSnackbar.value = false }) {
                            Text("ОК")
                        }
                    }
                ) { Text("Ошибка добавления инструмента. Пожалуйста, попробуйте снова.") }
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Добавление нового инструмента") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = toolName.value,
                onValueChange = { toolName.value = it },
                label = { Text("Название инструмента") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = toolDescription.value,
                onValueChange = { toolDescription.value = it },
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.addTool(
                        tool = Tool(
                            id = "", // ID будет сгенерирован в ViewModel
                            name = toolName.value.trim(), // Удаляем лишние пробелы
                            description = toolDescription.value.trim(),
                            status = ToolStatus.AVAILABLE,
                        ),
                        onSuccess = {
                            navController.popBackStack()
                        },
                        onError = {
                            showErrorSnackbar.value = true // Показываем Snackbar при ошибке
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Добавить инструмент")
            }
        }
    }
}

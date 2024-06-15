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
import com.mycompany.constructioninventory.ui.viewmodels.EditToolViewModel

@Composable
fun EditToolScreen(navController: NavController, toolId: String) {
    val viewModel: EditToolViewModel = hiltViewModel()
    val toolState = viewModel.getToolById(toolId).collectAsState(initial = null)

    // Извлечение инструмента из состояния
    val tool = toolState.value

    // Состояния для полей ввода
    val toolName = remember { mutableStateOf(tool?.name ?: "") }
    val toolDescription = remember { mutableStateOf(tool?.description ?: "") }
    val selectedStatus = remember { mutableStateOf(tool?.status ?: ToolStatus.AVAILABLE) }

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
                ) { Text("Ошибка обновления инструмента. Пожалуйста, попробуйте снова.") }
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Редактирование инструмента") }
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

            if (tool != null) {
                // Поля ввода для редактирования
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

                Spacer(modifier = Modifier.height(16.dp))

                // Выбор статуса
                ToolStatusRadioGroup(
                    selectedStatus = selectedStatus.value,
                    onStatusSelected = { selectedStatus.value = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Кнопка сохранения изменений
                Button(
                    onClick = {
                        viewModel.updateTool(
                            Tool(
                                id = tool.id,
                                name = toolName.value.trim(),
                                description = toolDescription.value.trim(),
                                status = selectedStatus.value,
                            ),
                            onSuccess = {
                                navController.popBackStack() // Возвращаемся назад после сохранения
                            },
                            onError = {
                                showErrorSnackbar.value = true
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Сохранить изменения")
                }
            } else {
                // Показываем индикатор загрузки или сообщение об ошибке, если инструмент не найден
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun ToolStatusRadioGroup(selectedStatus: ToolStatus, onStatusSelected: (ToolStatus) -> Unit) {
    Column {
        Row {
            RadioButton(
                selected = selectedStatus == ToolStatus.AVAILABLE,
                onClick = { onStatusSelected(ToolStatus.AVAILABLE) }
            )
            Text("Доступен")
        }
        Row {
            RadioButton(
                selected = selectedStatus == ToolStatus.RENTED,
                onClick = { onStatusSelected(ToolStatus.RENTED) }
            )
            Text("В аренде")
        }
    }
}
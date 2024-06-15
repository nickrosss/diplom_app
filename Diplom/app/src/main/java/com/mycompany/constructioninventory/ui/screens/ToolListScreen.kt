package com.mycompany.constructioninventory.ui.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mycompany.constructioninventory.ui.viewmodels.ToolListViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolListScreen(navController: NavController, viewModel: ToolListViewModel = hiltViewModel()) {
    val toolsState by viewModel.toolsState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Список Инструментов") }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (toolsState.isLoading) {
                // Показываем индикатор загрузки
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            } else if (toolsState.error.isNotBlank()) {
                // Показываем сообщение об ошибке
                Text(
                    text = "Ошибка загрузки данных: ${toolsState.error}",
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            } else {
                // Отображаем список инструментов
                LazyColumn {
                    items(toolsState.tools) { tool ->
                        ToolCardScreen(
                            tool = tool,
                            onClick = {
                                navController.navigate("toolDetailsScreen/${tool.id}")
                            },
                            onHistoryClick = {
                                navController.navigate("toolHistory/${tool.id}")
                            },
                            onDeleteClick = { toolToDelete ->
                                viewModel.deleteTool(toolToDelete)
                            },
                            onEditClick = { toolToEdit ->
                                navController.navigate("editToolScreen/${toolToEdit.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}
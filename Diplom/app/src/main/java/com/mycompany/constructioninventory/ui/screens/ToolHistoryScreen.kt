@file:OptIn(ExperimentalMaterial3Api::class)

package com.mycompany.constructioninventory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mycompany.constructioninventory.models.RentLog
import com.mycompany.constructioninventory.ui.viewmodels.ToolHistoryViewModel

@Composable
fun ToolHistoryScreen(navController: NavController, toolId: String) {
    val viewModel: ToolHistoryViewModel = hiltViewModel()
    val rentLogs by viewModel.rentLogs.collectAsState()

    LaunchedEffect(toolId) {
        viewModel.loadRentLogsForTool(toolId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("История инструмента: $toolId") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (rentLogs.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            } else if (rentLogs.error.isNotBlank()) {
                Text(
                    text = "Ошибка загрузки данных: ${rentLogs.error}",
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            } else {
                RentLogList(rentLogs = rentLogs.rentLogs)
            }
        }
    }
}

@Composable
fun RentLogList(rentLogs: List<RentLog>) {
    LazyColumn {
        items(rentLogs) { rentLog ->
            RentLogItem(rentLog = rentLog)
        }
    }
}

@Composable
fun RentLogItem(rentLog: RentLog) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Действие: ${rentLog.action}")
            Text(text = "Пользователь: ${rentLog.user.username}")
            Text(text = "Дата: ${rentLog.timestamp}")
        }
    }
}
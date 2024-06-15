package com.mycompany.constructioninventory.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mycompany.constructioninventory.models.Tool
import com.mycompany.constructioninventory.ui.viewmodels.ToolListViewModel

@Composable
fun ToolCardScreen(
    tool: Tool,
    onClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onDeleteClick: (Tool) -> Unit,
    onEditClick: (Tool) -> Unit,
    viewModel: ToolListViewModel = hiltViewModel()
) {
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = tool.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Статус: ${tool.status}")
            }
            Row {
                IconButton(onClick = onHistoryClick) {
                    Icon(imageVector = Icons.Filled.History, contentDescription = "История")
                }
                IconButton(onClick = { onEditClick(tool) }) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Редактировать")
                }
                IconButton(onClick = { showDeleteConfirmationDialog = true }) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Удалить")
                }
            }
        }
    }
    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text("Подтверждение удаления") },
            text = { Text("Вы уверены, что хотите удалить инструмент '${tool.name}'?") },
            confirmButton = {
                Button(onClick = {
                    onDeleteClick(tool)
                    showDeleteConfirmationDialog = false
                }) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirmationDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}
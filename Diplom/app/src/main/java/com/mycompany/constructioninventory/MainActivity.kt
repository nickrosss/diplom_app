package com.mycompany.constructioninventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mycompany.constructioninventory.ui.screens.AddToolScreen
import com.mycompany.constructioninventory.ui.screens.EditToolScreen
import com.mycompany.constructioninventory.ui.screens.LoginScreen
import com.mycompany.constructioninventory.ui.screens.ToolDetailsScreen
import com.mycompany.constructioninventory.ui.screens.ToolHistoryScreen
import com.mycompany.constructioninventory.ui.screens.ToolListScreen
import com.mycompany.constructioninventory.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Navigation()
                }
            }
        }
    }
}

object Destinations {
    const val LOGIN = "LoginScreen"
    const val TOOL_LIST = "toolListScreen"
    const val ADD_TOOL = "addToolScreen"
    const val TOOL_HISTORY = "toolHistory/{toolId}"
    const val EDIT_TOOL = "editToolScreen/{toolId}"
    const val TOOL_DETAILS = "toolDetailsScreen/{toolId}"
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val items = listOf(
        Destinations.TOOL_LIST,
        Destinations.ADD_TOOL
    )

    // Состояние для видимости панели навигации
    val showBottomBar = rememberSaveable { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            // Отображаем панель навигации, только если showBottomBar = true
            if (showBottomBar.value) {
                NavigationBar {
                    val currentRoute = currentRoute(navController)
                    items.forEach { screen ->
                        NavigationItem(
                            icon = when (screen) {
                                Destinations.TOOL_LIST -> Icons.AutoMirrored.Filled.List
                                Destinations.ADD_TOOL -> Icons.Filled.Add
                                else -> Icons.Filled.Home // Или другой значок по умолчанию
                            },
                            label = when (screen) {
                                Destinations.TOOL_LIST -> "Список"
                                Destinations.ADD_TOOL -> "Добавить"
                                else -> "Домой"
                            },
                            isSelected = currentRoute == screen,
                            onItemClick = {
                                navController.navigate(screen) {
                                    // Избегаем повторного создания уже существующего экрана
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Destinations.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destinations.LOGIN) {
                showBottomBar.value = false
                LoginScreen(navController)
            }
            composable(Destinations.TOOL_LIST) {
                showBottomBar.value = true
                ToolListScreen(navController)
            }
            composable(Destinations.ADD_TOOL) {
                showBottomBar.value = true
                AddToolScreen(navController)
            }
            composable(Destinations.TOOL_HISTORY) { backStackEntry ->
                val toolId = backStackEntry.arguments?.getString("toolId") ?: ""
                ToolHistoryScreen(navController, toolId)
            }
            composable(Destinations.TOOL_DETAILS) { backStackEntry ->
                val toolId = backStackEntry.arguments?.getString("toolId") ?: ""
                ToolDetailsScreen(navController, toolId)
            }
            composable(Destinations.EDIT_TOOL) { backStackEntry ->
                val toolId = backStackEntry.arguments?.getString("toolId") ?: ""
                EditToolScreen(navController, toolId)
            }
        }
    }
}

// Вспомогательная функция для получения текущего маршрута
@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun NavigationItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onItemClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onItemClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.6f
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.6f
            )
        )
    }
}
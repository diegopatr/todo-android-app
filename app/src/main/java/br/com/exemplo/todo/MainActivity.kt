package br.com.exemplo.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.exemplo.todo.data.local.TaskDao
import br.com.exemplo.todo.data.model.Task
import br.com.exemplo.todo.data.repository.TaskFirebaseRepository
import br.com.exemplo.todo.data.repository.TaskRepository
import br.com.exemplo.todo.ui.components.BottomBar
import br.com.exemplo.todo.ui.screens.AddTaskScreen
import br.com.exemplo.todo.ui.screens.HomeScreen
import br.com.exemplo.todo.ui.screens.LoginScreen
import br.com.exemplo.todo.ui.screens.ProfileScreen
import br.com.exemplo.todo.ui.theme.TodoTheme
import br.com.exemplo.todo.viewmodel.TaskViewModel
import br.com.exemplo.todo.viewmodel.TaskViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = TaskFirebaseRepository() // Cria o repositório
        val taskViewModel: TaskViewModel by viewModels {
            TaskViewModelFactory(repository)
        }

        setContent {
            TodoTheme {
                TodoApp(taskViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(taskViewModel: TaskViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        },
        contentColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavigationHost(
            navController = navController,
            taskViewModel = taskViewModel, // Passa o ViewModel para a navegação
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun NavigationHost(
    navController: NavHostController,
    taskViewModel: TaskViewModel, // Adiciona o ViewModel
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = "home", modifier = modifier) {
        composable("home") { HomeScreen(taskViewModel, navController) } // Passa o ViewModel
        composable("profile") { ProfileScreen(onLogoutSuccess = { }) }
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("home")
            })
        }
        composable("addTask") {
            AddTaskScreen(
                taskViewModel,
                navController
            )
        } // Passa o ViewModel
    }
}

data class BottomNavigationItemData(val route: String, val label: String, val icon: ImageVector)
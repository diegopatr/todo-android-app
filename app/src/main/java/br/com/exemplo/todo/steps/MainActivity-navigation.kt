package br.com.exemplo.todo.step1_navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import br.com.exemplo.todo.ui.theme.TodoTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoTheme {
                TodoApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("To-Do App") },
                actions = {
                    IconButton(onClick = { /* Implementar busca */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("addTask") // Navegue para adicionar tarefa
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        contentColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavigationHost(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun NavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "home", modifier = modifier) {
        composable("home") { HomeScreen(navController) }
        composable("profile") { ProfileScreen() }
        composable("addTask") { AddTaskScreen(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var tasks by remember {
        mutableStateOf(
            listOf(
                "Comprar leite",
                "Estudar Compose",
                "Fazer exercício"
            )
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = { innerPadding ->
            if (tasks.isEmpty()) {
                // Exibe mensagem se não houver tarefas
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhuma tarefa encontrada!", textAlign = TextAlign.Center)
                }
            } else {
                // Lista de tarefas
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    tasks.forEachIndexed { index, task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = task,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            IconButton(onClick = {
                                // Remove a tarefa e mostra uma Snackbar
                                val removedTask = task
                                tasks = tasks.toMutableList().apply { removeAt(index) }
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Tarefa removida: $removedTask")
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Task")
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun AddTaskScreen(navController: NavController) {
    var newTask by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = newTask,
            onValueChange = { newTask = it },
            label = { Text("Nova Tarefa") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (newTask.isNotBlank()) {
                navController.navigate("home") // Voltar para a tela inicial
            }
        }) {
            Text("Adicionar Tarefa")
        }
    }
}

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Perfil do usuário em desenvolvimento...")
    }
}

@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        listOf(
            BottomNavigationItemData("home", "Início", Icons.Default.Home),
            BottomNavigationItemData("profile", "Perfil", Icons.Default.Person)
        ).forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodoAppPreview() {
    TodoTheme {
        TodoApp()
    }
}

data class BottomNavigationItemData(val route: String, val label: String, val icon: ImageVector)
package com.example.timeline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.timeline.data.local.AppDatabase
import com.example.timeline.data.remote.FirebaseService
import com.example.timeline.data.repository.TaskRepository
import com.example.timeline.data.sync.SyncManager
import com.example.timeline.ui.screens.*
import com.example.timeline.ui.theme.TaskTrackerTheme
import com.example.timeline.util.DataMode
import com.example.timeline.util.PreferenceManager
import com.example.timeline.util.ThemeMode
import com.example.timeline.viewmodel.AuthViewModel
import com.example.timeline.viewmodel.TaskViewModel
import com.example.timeline.viewmodel.TaskViewModelFactory
import androidx.compose.foundation.isSystemInDarkTheme

class MainActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // Inform user that notifications are disabled
        }
    }

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val signInTask = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = signInTask.getResult(com.google.android.gms.common.api.ApiException::class.java)!!
            val idToken = account.idToken ?: throw Exception("ID Token is null")
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            com.google.firebase.auth.FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnSuccessListener {
                    authViewModel.onGoogleSignInSuccess()
                    showSyncDialog = true
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var showSyncDialog by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(this)
        val firebaseService = FirebaseService()
        val preferenceManager = PreferenceManager(this)
        val driveService = com.example.timeline.data.remote.GoogleDriveService(this)
        val repository = TaskRepository(database.taskDao(), driveService)
        val factory = TaskViewModelFactory(application, repository, firebaseService, preferenceManager)
        
        SyncManager.scheduleSync(this)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        
        setContent {
            val viewModel: TaskViewModel = viewModel(factory = factory)
            authViewModel = viewModel(factory = factory)
            
            val themeMode by authViewModel.themeMode.collectAsStateWithLifecycle()
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            TaskTrackerTheme(darkTheme = darkTheme) {
                val dataMode by authViewModel.dataMode.collectAsStateWithLifecycle()
                val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
                
                if (dataMode == DataMode.UNSET) {
                    WelcomeScreen(
                        onContinueWithGoogle = { startGoogleSignIn() },
                        onUseOfflineMode = { authViewModel.setOfflineMode() }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        if (dataMode == DataMode.GOOGLE) {
                            viewModel.syncFromRemote()
                        }
                    }
                    
                    TaskTrackerApp(viewModel, authViewModel, onSignInClick = { startGoogleSignIn() })
                }

                if (showSyncDialog) {
                    AlertDialog(
                        onDismissRequest = { showSyncDialog = false },
                        title = { Text("Sync tasks?") },
                        text = { 
                            Text("Local tasks found. Would you like to sync them with your Google account?")
                        },
                        confirmButton = {
                            Button(onClick = {
                                authViewModel.syncWithDrive()
                                showSyncDialog = false
                            }) { Text("Sync & Continue") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showSyncDialog = false }) { Text("Cancel") }
                        }
                    )
                }
            }
        }
    }

    private fun startGoogleSignIn() {
        try {
            val resId = resources.getIdentifier("default_web_client_id", "string", packageName)
            val webClientId = if (resId != 0) getString(resId) else ""
            
            if (webClientId.isEmpty()) {
                android.util.Log.e("TimeLine", "Google Sign-In failed: default_web_client_id not found.")
                android.widget.Toast.makeText(this, "Configuration error.", android.widget.Toast.LENGTH_LONG).show()
                return
            }

            val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .requestScopes(com.google.android.gms.common.api.Scope("https://www.googleapis.com/auth/drive.appdata"))
                .build()
            val client = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this, gso)
            googleSignInLauncher.launch(client.signInIntent)
        } catch (e: Exception) {
            android.util.Log.e("TimeLine", "Google Sign-In error", e)
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Timeline : Screen("home", "Timeline", Icons.Rounded.Timeline)
    object Calendar : Screen("calendar", "Calendar", Icons.Rounded.CalendarMonth)
    object Upcoming : Screen("upcoming", "Upcoming", Icons.Rounded.Upcoming)
    object Completed : Screen("completed", "Completed", Icons.Rounded.CheckCircle)
    object Settings : Screen("settings", "Settings", Icons.Rounded.Settings)
}

@Composable
fun TaskTrackerApp(
    viewModel: TaskViewModel, 
    authViewModel: AuthViewModel,
    onSignInClick: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val navigationItems = listOf(
        Screen.Timeline,
        Screen.Calendar,
        Screen.Upcoming,
        Screen.Completed,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            if (currentDestination?.route in navigationItems.map { it.route }) {
                NavigationBar {
                    navigationItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
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
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    authViewModel = authViewModel,
                    onAddTaskClick = { navController.navigate("add_task") },
                    onTaskClick = { taskId: Int -> navController.navigate("task_detail/$taskId") },
                    onProfileClick = { navController.navigate("profile") }
                )
            }
            composable("calendar") {
                CalendarScreen(
                    viewModel = viewModel,
                    authViewModel = authViewModel,
                    onAddTaskClick = { navController.navigate("add_task") },
                    onTaskClick = { taskId: Int -> navController.navigate("task_detail/$taskId") },
                    onProfileClick = { navController.navigate("profile") }
                )
            }
            composable("upcoming") {
                UpcomingScreen(
                    viewModel = viewModel,
                    authViewModel = authViewModel,
                    onAddTaskClick = { navController.navigate("add_task") },
                    onTaskClick = { taskId: Int -> navController.navigate("task_detail/$taskId") },
                    onProfileClick = { navController.navigate("profile") }
                )
            }
            composable("profile") {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("completed") {
                CompletedScreen(
                    viewModel = viewModel,
                    onTaskClick = { taskId: Int -> navController.navigate("task_detail/$taskId") },
                    onProfileClick = { navController.navigate("profile") }
                )
            }
            composable("settings") {
                SettingsScreen(
                    authViewModel = authViewModel,
                    onSignInClick = onSignInClick,
                    onProfileClick = { navController.navigate("profile") }
                )
            }
            composable("add_task") {
                AddTaskScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "edit_task/{taskId}",
                arguments = listOf(navArgument("taskId") { type = NavType.IntType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable
                AddTaskScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    taskId = taskId
                )
            }
            composable(
                route = "task_detail/{taskId}",
                arguments = listOf(navArgument("taskId") { type = NavType.IntType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable
                TaskDetailScreen(
                    taskId = taskId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onEditClick = { id -> navController.navigate("edit_task/$id") }
                )
            }
        }
    }
}

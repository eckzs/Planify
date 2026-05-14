package com.app.planify

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.planify.components.PlBottomBar
import com.app.planify.constants.Routes
import com.app.planify.logic.utils.EmailLinkAuthHandler
import com.app.planify.logic.utils.EmailLinkState
import com.app.planify.screens.auth.AuthScreen
import com.app.planify.screens.auth.OnboardingScreen
import com.app.planify.screens.home.HomeScreen
import com.app.planify.screens.tasks.AddTaskScreen
import com.app.planify.screens.pomodoro.PomodoroScreen
// TODO: uncomment when feat/profile is merged
// import com.app.planify.screens.profile.ProfileScreen
import com.app.planify.screens.tasks.TasksScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val emailLinkState by EmailLinkAuthHandler.state.collectAsState()

    // TODO: add Routes.PROFILE when its feature is merged
    val bottomBarRoutes = setOf(Routes.HOME, Routes.TASKS, Routes.POMODORO)
    val showBottomBar = currentRoute in bottomBarRoutes

    LaunchedEffect(emailLinkState) {
        when (val state = emailLinkState) {
            is EmailLinkState.Success -> {
                val nextRoute = if (state.isNewUser) Routes.ONBOARDING else Routes.HOME
                navController.navigate(nextRoute) {
                    popUpTo(Routes.AUTH) { inclusive = true }
                }
                EmailLinkAuthHandler.clearState()
            }

            is EmailLinkState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                EmailLinkAuthHandler.clearState()
            }

            null -> Unit
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) PlBottomBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.AUTH,
            modifier = Modifier.padding(innerPadding)
        ) {

            // ── Auth ──────────────────────────────────────────────────────────

            composable(Routes.AUTH) {
                AuthScreen(
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.AUTH) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ── Main app ──────────────────────────────────────────────────────

            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToTasks    = { navController.navigate(Routes.TASKS) },
                    onNavigateToPomodoro = { navController.navigate(Routes.POMODORO) }
                )
            }

            composable(Routes.TASKS) {
                TasksScreen(
                    onNavigateToAdd = {
                        navController.navigate(Routes.ADD_TASK)
                    },
                    onNavigateToEdit = { taskId ->
                        navController.navigate(Routes.taskDetail(taskId))
                    }
                )
            }

            composable(Routes.ADD_TASK) {
                AddTaskScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Routes.TASK_DETAIL,
                arguments = listOf(navArgument("taskId") { type = NavType.StringType })
            ) { backStack ->
                val taskId = android.net.Uri.decode(
                    backStack.arguments?.getString("taskId") ?: ""
                )

                AddTaskScreen(
                    taskId = taskId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.POMODORO) {
                PomodoroScreen()
            }

            // TODO: temporary — enable when feat/profile is merged
            // composable(Routes.PROFILE) { ProfileScreen() }
        }
    }
}

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
import com.app.planify.screens.ai.AiChatScreen
import com.app.planify.screens.auth.AuthScreen
import com.app.planify.screens.auth.OnboardingScreen
import com.app.planify.screens.home.HomeScreen
import com.app.planify.screens.tasks.AddTaskScreen
import com.app.planify.screens.pomodoro.PomodoroScreen
import com.app.planify.screens.tasks.TasksScreen
import com.app.planify.screens.tasks.TasksViewModel
import com.google.firebase.auth.FirebaseAuth

import com.app.planify.screens.courses.CoursesScreen
import com.app.planify.screens.flashcards.AddFlashcardScreen
import com.app.planify.screens.flashcards.FlashcardsScreen
import com.app.planify.screens.flashcards.GenerateFlashcardsScreen
import com.app.planify.screens.profile.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val emailLinkState by EmailLinkAuthHandler.state.collectAsState()

    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDestination = if (currentUser != null) Routes.HOME else Routes.AUTH

    val bottomBarRoutes = setOf(Routes.HOME, Routes.TASKS, Routes.COURSES, Routes.AI_CHAT, Routes.PROFILE)
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
            startDestination = startDestination,
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

            composable(Routes.TASKS) { entry ->
                val taskChanged = entry.savedStateHandle.get<Boolean>("task_changed") == true
                val tasksViewModel: TasksViewModel = androidx.lifecycle.viewmodel.compose.viewModel(entry)

                LaunchedEffect(taskChanged) {
                    if (taskChanged) {
                        tasksViewModel.loadTasks()
                        entry.savedStateHandle["task_changed"] = false
                    }
                }

                TasksScreen(
                    viewModel = tasksViewModel,
                    onNavigateToAdd = {
                        navController.navigate(Routes.ADD_TASK)
                    },
                    onNavigateToEdit = { taskId ->
                        navController.navigate(Routes.taskDetail(taskId))
                    },
                    onNavigateToPomodoro = { taskId ->
                        navController.navigate(Routes.pomodoro(taskId))
                    }
                )
            }

            composable(Routes.ADD_TASK) {
                AddTaskScreen(
                    onNavigateBack = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle?.set("task_changed", true)
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
                        navController.previousBackStackEntry
                            ?.savedStateHandle?.set("task_changed", true)
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.POMODORO) {
                PomodoroScreen()
            }

            composable(Routes.POMODORO_WITH_TASK, arguments = listOf(navArgument("taskId") { type = NavType.StringType })) { backStack ->
                val taskId = android.net.Uri.decode(backStack.arguments?.getString("taskId") ?: "")
                PomodoroScreen(taskId = taskId)
            }

            // ── Study Toolkit ──────────────────────────────────────────────────

            composable(Routes.COURSES) {
                CoursesScreen(
                    onNavigateToCourseDetail = { courseId ->
                        navController.navigate(Routes.flashcards(courseId))
                    }
                )
            }

            composable(
                route = Routes.FLASHCARDS_STUDY,
                arguments = listOf(navArgument("courseId") { type = NavType.StringType })
            ) { backStack ->
                val courseId = android.net.Uri.decode(
                    backStack.arguments?.getString("courseId") ?: ""
                )
                FlashcardsScreen(
                    courseId = courseId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAdd = { navController.navigate(Routes.addFlashcard(courseId)) },
                    onNavigateToGenerate = { navController.navigate(Routes.generateFlashcards(courseId)) }
                )
            }

            composable(
                route = Routes.ADD_FLASHCARD,
                arguments = listOf(navArgument("courseId") { type = NavType.StringType })
            ) { backStack ->
                val courseId = android.net.Uri.decode(
                    backStack.arguments?.getString("courseId") ?: ""
                )
                AddFlashcardScreen(
                    courseId = courseId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.GENERATE_FLASHCARDS,
                arguments = listOf(navArgument("courseId") { type = NavType.StringType })
            ) { backStack ->
                val courseId = android.net.Uri.decode(
                    backStack.arguments?.getString("courseId") ?: ""
                )
                GenerateFlashcardsScreen(
                    courseId = courseId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ── AI ──────────────────────────────────────────────────────────

            composable(Routes.AI_CHAT) {
                AiChatScreen()
            }

            // ── Profile ──────────────────────────────────────────────────────

            composable(Routes.PROFILE) {
                ProfileScreen(
                    onNavigateToAuth = {
                        navController.navigate(Routes.AUTH) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

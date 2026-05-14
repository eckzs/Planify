package com.app.planify

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.planify.components.PlBottomBar
import com.app.planify.constants.Routes
import com.app.planify.screens.auth.AuthScreen
import com.app.planify.screens.auth.OnboardingScreen
import com.app.planify.screens.auth.OtpScreen
import com.app.planify.screens.home.HomeScreen
// TODO: uncomment when feat/pomodoro is merged
// import com.app.planify.screens.pomodoro.PomodoroScreen
// TODO: uncomment when feat/profile is merged
// import com.app.planify.screens.profile.ProfileScreen
import com.app.planify.screens.tasks.TasksScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // TODO: add Routes.POMODORO and Routes.PROFILE when their features are merged
    val bottomBarRoutes = setOf(Routes.HOME, Routes.TASKS)
    val showBottomBar = currentRoute in bottomBarRoutes

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
                    onNavigateToOtp = { email -> navController.navigate(Routes.otp(email)) },
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.AUTH) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Routes.OTP,
                arguments = listOf(navArgument("email") { type = NavType.StringType })
            ) { backStack ->
                val email = android.net.Uri.decode(
                    backStack.arguments?.getString("email") ?: ""
                )
                OtpScreen(
                    email = email,
                    onNavigateToOnboarding = {
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.AUTH) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.AUTH) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
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
                        // TODO: navController.navigate(Routes.ADD_TASK)
                    }
                )
            }

                // TODO: temporary — enable when feat/pomodoro is merged
            // composable(Routes.POMODORO) { PomodoroScreen() }

            // TODO: temporary — enable when feat/profile is merged
            // composable(Routes.PROFILE) { ProfileScreen() }
        }
    }
}

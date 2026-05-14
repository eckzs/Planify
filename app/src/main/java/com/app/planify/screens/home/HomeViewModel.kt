package com.app.planify.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    var userName by mutableStateOf("Estudiante")
        private set

    // TODO: reemplazar con TasksRepository.getTasks().count { !it.isDone }
    var pendingTasksCount by mutableStateOf(3)
        private set

    // TODO: obtener ultimas 2 tareas desde TasksRepository
    var recentTasks by mutableStateOf(
        listOf(
            "Estudiar parcial de cálculo",
            "Entregar laboratorio de física"
        )
    )
        private set

    init {
        loadGoogleUserName()
    }

    private fun loadGoogleUserName() {
        val fullName = firebaseAuth.currentUser?.displayName

        userName = fullName
            ?.trim()
            ?.split(" ")
            ?.firstOrNull()
            ?.takeIf { it.isNotBlank() }
            ?: "Estudiante"
    }
}

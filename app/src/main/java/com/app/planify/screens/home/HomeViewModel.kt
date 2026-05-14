package com.app.planify.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

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
        loadUserName()
    }

    private fun loadUserName() {
        val user = firebaseAuth.currentUser
        val userId = user?.uid

        if (userId == null) {
            userName = "Estudiante"
            return
        }

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val savedName = document.getString("name")
                userName = getFirstName(savedName ?: user.displayName)
            }
            .addOnFailureListener {
                userName = getFirstName(user.displayName)
            }
    }

    private fun getFirstName(fullName: String?): String {
        return fullName
            ?.trim()
            ?.split(" ")
            ?.firstOrNull()
            ?.takeIf { it.isNotBlank() }
            ?: "Estudiante"
    }
}

package com.app.planify.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.planify.api.services.TasksRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val tasksRepository = TasksRepository()

    var userName by mutableStateOf("Estudiante")
        private set

    var pendingTasksCount by mutableStateOf(0)
        private set

    var recentTasks by mutableStateOf(emptyList<String>())
        private set

    init {
        loadUserName()
        loadTasksSummary()
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

    private fun loadTasksSummary() {
        viewModelScope.launch {
            tasksRepository.getTasks()
                .onSuccess { tasks ->
                    pendingTasksCount = tasks.size
                    recentTasks = tasks
                        .take(2)
                        .map { it.title }
                }
                .onFailure {
                    pendingTasksCount = 0
                    recentTasks = emptyList()
                }
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

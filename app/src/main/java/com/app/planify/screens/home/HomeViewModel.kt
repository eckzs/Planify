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

import com.app.planify.api.services.PomodoroRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.ZoneId

data class DailyMetric(val date: String, val count: Int)

class HomeViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val tasksRepository = TasksRepository()
    private val pomodoroRepository = PomodoroRepository()
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    var userName by mutableStateOf("Estudiante")
        private set

    var pendingTasksCount by mutableStateOf(0)
        private set

    var recentTasks by mutableStateOf(emptyList<String>())
        private set

    var dailyPomodoros by mutableStateOf(emptyList<DailyMetric>())
        private set

    var dailyTasks by mutableStateOf(emptyList<DailyMetric>())
        private set

    init {
        loadUserName()
        loadData()
    }

    private fun loadUserName() {
        val user = firebaseAuth.currentUser
        val userId = user?.uid

        if (userId == null) {
            userName = "Estudiante"
            return
        }

        // Primero intentamos con el nombre de Firebase Auth si existe
        if (!user.displayName.isNullOrBlank()) {
            userName = getFirstName(user.displayName)
        }

        // Luego intentamos obtener el nombre personalizado de Firestore
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val savedName = document.getString("name")
                if (!savedName.isNullOrBlank()) {
                    userName = getFirstName(savedName)
                }
            }
    }

    fun loadData() {
        viewModelScope.launch {
            // Load Tasks
            tasksRepository.getTasks()
                .onSuccess { tasks ->
                    pendingTasksCount = tasks.count { !it.completed }
                    recentTasks = tasks.filter { !it.completed }.take(2).map { it.title }
                    
                    // Process daily tasks (last 7 days)
                    val last7Days = (0..6).map { LocalDate.now().minusDays(it.toLong()) }
                    dailyTasks = last7Days.map { date ->
                        val dateStr = date.format(dateFormatter)
                        val count = tasks.count { it.completed && it.date == dateStr }
                        DailyMetric(dateStr.take(5), count) // Solo día/mes
                    }.reversed()
                }

            // Load Pomodoros
            pomodoroRepository.getSessions()
                .onSuccess { sessions ->
                    val last7Days = (0..6).map { LocalDate.now().minusDays(it.toLong()) }
                    dailyPomodoros = last7Days.map { date ->
                        val count = sessions.count { session ->
                            val sessionDate = session.endedAt.toDate().toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate()
                            session.completed && sessionDate == date
                        }
                        DailyMetric(date.format(dateFormatter).take(5), count)
                    }.reversed()
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

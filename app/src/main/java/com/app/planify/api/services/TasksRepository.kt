package com.app.planify.api.services

import com.app.planify.api.models.Task
import com.app.planify.constants.TaskConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class TasksRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val tasksCollection = firestore.collection(TaskConstants.COLLECTION)

    suspend fun getTasks(): Result<List<Task>> = suspendCancellableCoroutine { continuation ->
        val userId = firebaseAuth.currentUser?.uid

        if (userId == null) {
            continuation.resume(Result.failure(Exception("No hay usuario autenticado")))
            return@suspendCancellableCoroutine
        }

        tasksCollection
            .whereEqualTo(TaskConstants.FIELD_USER_ID, userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val tasks = snapshot.documents.map { Task.fromDocument(it) }
                continuation.resume(Result.success(tasks))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
    }

    suspend fun getTask(taskId: String): Result<Task> = suspendCancellableCoroutine { continuation ->
        val userId = firebaseAuth.currentUser?.uid

        if (userId == null) {
            continuation.resume(Result.failure(Exception("No hay usuario autenticado")))
            return@suspendCancellableCoroutine
        }

        tasksCollection
            .document(taskId)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    continuation.resume(Result.failure(Exception("No se encontro la tarea")))
                    return@addOnSuccessListener
                }

                val task = Task.fromDocument(document)
                if (task.userId != userId) {
                    continuation.resume(Result.failure(Exception("No puedes editar esta tarea")))
                    return@addOnSuccessListener
                }

                continuation.resume(Result.success(task))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
    }

    suspend fun createTask(
        title: String,
        date: String,
        priority: String
    ): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val userId = firebaseAuth.currentUser?.uid

        if (userId == null) {
            continuation.resume(Result.failure(Exception("No hay usuario autenticado")))
            return@suspendCancellableCoroutine
        }

        val task = Task(
            title = title.trim(),
            date = date.trim(),
            priority = priority,
            userId = userId
        )

        tasksCollection
            .add(task.toMap())
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
    }

    suspend fun updateTask(
        taskId: String,
        title: String,
        date: String,
        priority: String
    ): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val data = mapOf(
            TaskConstants.FIELD_TITLE to title.trim(),
            TaskConstants.FIELD_DATE to date.trim(),
            TaskConstants.FIELD_PRIORITY to priority
        )

        tasksCollection
            .document(taskId)
            .update(data)
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
    }

    suspend fun deleteTask(taskId: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        tasksCollection
            .document(taskId)
            .delete()
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
    }
}

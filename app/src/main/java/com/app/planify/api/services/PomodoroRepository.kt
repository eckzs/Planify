package com.app.planify.api.services

import com.app.planify.api.models.ActivePomodoro
import com.app.planify.api.models.PomodoroSession
import com.app.planify.constants.PomodoroConstants
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Date
import kotlin.coroutines.resume

class PomodoroRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val pomodoroCollection = firestore.collection(PomodoroConstants.POMODORO_COLLECTION)

    suspend fun saveActivePomodoro(
        taskId: String,
        mode: String,
        cycleNumber: Int,
        startedAtMillis: Long,
        endsAtMillis: Long,
        paused: Boolean,
        completed: Boolean = false
    ): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val userId = firebaseAuth.currentUser?.uid

        if (userId == null) {
            continuation.resume(Result.failure(Exception("No hay usuario autenticado")))
            return@suspendCancellableCoroutine
        }

        val activePomodoro = ActivePomodoro(
            userId = userId,
            taskId = taskId,
            mode = mode,
            cycleNumber = cycleNumber,
            startedAt = Timestamp(Date(startedAtMillis)),
            endsAt = Timestamp(Date(endsAtMillis)),
            paused = paused,
            completed = completed
        )

        // Usamos el userId como ID del documento para que sea un único documento por usuario
        // Usamos SetOptions.merge() para no sobrescribir otros campos que puedan existir
        pomodoroCollection
            .document(userId)
            .set(activePomodoro.toMap(), SetOptions.merge())
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
    }

    suspend fun updatePaused(paused: Boolean): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            val userId = firebaseAuth.currentUser?.uid

            if (userId == null) {
                continuation.resume(Result.failure(Exception("No hay usuario autenticado")))
                return@suspendCancellableCoroutine
            }

            pomodoroCollection
                .document(userId)
                .update(PomodoroConstants.FIELD_PAUSED, paused)
                .addOnSuccessListener {
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }
        }

    suspend fun savePomodoroSession(
        taskId: String,
        mode: String,
        cycleNumber: Int,
        startedAtMillis: Long,
        endedAtMillis: Long,
        completed: Boolean
    ): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val userId = firebaseAuth.currentUser?.uid

        if (userId == null) {
            continuation.resume(Result.failure(Exception("No hay usuario autenticado")))
            return@suspendCancellableCoroutine
        }

        // Calcular duración en minutos como número decimal (Double)
        val durationMinutes = (endedAtMillis - startedAtMillis) / 60000.0
        val session = PomodoroSession(
            userId = userId,
            taskId = taskId,
            mode = mode,
            cycleNumber = cycleNumber,
            startedAt = Timestamp(Date(startedAtMillis)),
            endedAt = Timestamp(Date(endedAtMillis)),
            duration = durationMinutes,
            completed = completed
        )

        // Actualizamos el MISMO documento del usuario en lugar de crear uno nuevo
        // Esto consolida todos los campos en un solo documento en la colección 'pomodoro'
        pomodoroCollection
            .document(userId)
            .set(session.toMap(), SetOptions.merge())
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
    }
}

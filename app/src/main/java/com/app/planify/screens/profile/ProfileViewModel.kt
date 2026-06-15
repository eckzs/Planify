package com.app.planify.screens.profile

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.app.planify.constants.CourseConstants
import com.app.planify.constants.FlashcardConstants
import com.app.planify.constants.TaskConstants
import com.app.planify.logic.utils.AppPrefs
import com.app.planify.logic.utils.AppSettings
import com.app.planify.logic.utils.FontScale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ProfileStats(
    val courses: Int = 0,
    val flashcards: Int = 0,
    val tasksCompleted: Int = 0
)

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // ── Datos del perfil ──────────────────────────────────────────────────────

    var displayName by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var initials by mutableStateOf("?")
        private set
    var major by mutableStateOf<String?>(null)
        private set
    var university by mutableStateOf<String?>(null)
        private set

    // ── Datos de cuenta (Firebase Auth metadata) ──────────────────────────────

    var createdAt by mutableStateOf("")
        private set
    var authProvider by mutableStateOf("")
        private set

    // ── Estadísticas ──────────────────────────────────────────────────────────

    var stats by mutableStateOf(ProfileStats())
        private set

    // ── Estado de edición ─────────────────────────────────────────────────────

    var isEditing by mutableStateOf(false)
        private set
    var editName by mutableStateOf("")
        private set
    var editMajor by mutableStateOf("")
        private set
    var editUniversity by mutableStateOf("")
        private set
    var isSaving by mutableStateOf(false)
        private set
    var saveError by mutableStateOf<String?>(null)
        private set

    // ─────────────────────────────────────────────────────────────────────────

    init {
        loadUser()
        loadAccountInfo()
        loadStats()
    }

    private fun loadUser() {
        val user = auth.currentUser ?: return
        displayName = user.displayName ?: ""
        email = user.email ?: ""
        initials = buildInitials(displayName)

        firestore.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                doc.getString("name")?.takeIf { it.isNotBlank() }?.let {
                    displayName = it
                    initials = buildInitials(it)
                }
                major = doc.getString("major")?.takeIf { it.isNotBlank() }
                university = doc.getString("uniName")?.takeIf { it.isNotBlank() }
            }
    }

    private fun loadAccountInfo() {
        val user = auth.currentUser ?: return

        val creationMs = user.metadata?.creationTimestamp ?: 0L
        if (creationMs > 0L) {
            val sdf = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
            createdAt = sdf.format(Date(creationMs))
                .replaceFirstChar { it.uppercaseChar() }
        }

        val provider = user.providerData
            .firstOrNull { it.providerId != "firebase" }
            ?.providerId
        authProvider = when (provider) {
            "google.com" -> "Google"
            else -> "Email"
        }
    }

    private fun loadStats() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection(CourseConstants.COLLECTION)
            .whereEqualTo(CourseConstants.FIELD_USER_ID, userId)
            .get()
            .addOnSuccessListener { stats = stats.copy(courses = it.size()) }

        firestore.collection(FlashcardConstants.COLLECTION)
            .whereEqualTo(FlashcardConstants.FIELD_USER_ID, userId)
            .get()
            .addOnSuccessListener { stats = stats.copy(flashcards = it.size()) }

        firestore.collection(TaskConstants.COLLECTION)
            .whereEqualTo(TaskConstants.FIELD_USER_ID, userId)
            .whereEqualTo(TaskConstants.FIELD_COMPLETED, true)
            .get()
            .addOnSuccessListener { stats = stats.copy(tasksCompleted = it.size()) }
    }

    // ── Edición ───────────────────────────────────────────────────────────────

    fun startEdit() {
        editName = displayName
        editMajor = major ?: ""
        editUniversity = university ?: ""
        saveError = null
        isEditing = true
    }

    fun cancelEdit() {
        isEditing = false
        saveError = null
    }

    fun onEditNameChange(v: String) { editName = v }
    fun onEditMajorChange(v: String) { editMajor = v }
    fun onEditUniversityChange(v: String) { editUniversity = v }

    fun saveEdit() {
        if (editName.isBlank()) { saveError = "El nombre es obligatorio"; return }
        val userId = auth.currentUser?.uid ?: return
        isSaving = true
        saveError = null

        val updates = mapOf(
            "name" to editName.trim(),
            "major" to editMajor.trim().ifBlank { null },
            "uniName" to editUniversity.trim().ifBlank { null }
        )

        firestore.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener {
                displayName = editName.trim()
                major = editMajor.trim().ifBlank { null }
                university = editUniversity.trim().ifBlank { null }
                initials = buildInitials(displayName)
                isSaving = false
                isEditing = false
            }
            .addOnFailureListener {
                saveError = it.message ?: "Error al guardar"
                isSaving = false
            }
    }

    // ── Apariencia ────────────────────────────────────────────────────────────

    fun cycleDarkTheme(context: Context) {
        val next = when (AppSettings.isDarkTheme) {
            null  -> true
            true  -> false
            false -> null
        }
        AppPrefs.setDarkTheme(context, next)
    }

    fun setFontScale(context: Context, scale: FontScale) {
        AppPrefs.setFontScale(context, scale)
    }

    fun signOut(onNavigateToAuth: () -> Unit) {
        auth.signOut()
        onNavigateToAuth()
    }

    private fun buildInitials(name: String): String {
        val parts = name.trim().split(" ").filter { it.isNotBlank() }
        return when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            parts.size == 1 -> parts[0].first().uppercaseChar().toString()
            else -> "?"
        }
    }
}

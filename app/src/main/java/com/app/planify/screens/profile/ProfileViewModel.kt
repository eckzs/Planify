package com.app.planify.screens.profile

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.app.planify.logic.utils.AppPrefs
import com.app.planify.logic.utils.AppSettings
import com.app.planify.logic.utils.FontScale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

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

    init {
        loadUser()
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

    private fun buildInitials(name: String): String {
        val parts = name.trim().split(" ").filter { it.isNotBlank() }
        return when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            parts.size == 1 -> parts[0].first().uppercaseChar().toString()
            else -> "?"
        }
    }

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
}

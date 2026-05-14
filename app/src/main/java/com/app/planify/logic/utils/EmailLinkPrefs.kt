package com.app.planify.logic.utils

import android.content.Context

object EmailLinkPrefs {
    private const val PREFS_NAME = "email_link_auth"
    private const val KEY_EMAIL = "email_for_sign_in"

    fun saveEmail(context: Context, email: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun getEmail(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_EMAIL, null)
    }

    fun clearEmail(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_EMAIL)
            .apply()
    }
}

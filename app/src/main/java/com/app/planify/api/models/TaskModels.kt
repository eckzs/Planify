package com.app.planify.api.models

import com.app.planify.constants.TaskConstants
import com.google.firebase.firestore.DocumentSnapshot

data class Task(
    val id: String = "",
    val date: String = "",
    val priority: String = "",
    val title: String = "",
    val userId: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            TaskConstants.FIELD_DATE to date,
            TaskConstants.FIELD_PRIORITY to priority,
            TaskConstants.FIELD_TITLE to title,
            TaskConstants.FIELD_USER_ID to userId
        )
    }

    companion object {
        fun fromDocument(document: DocumentSnapshot): Task {
            return Task(
                id = document.id,
                date = document.getString(TaskConstants.FIELD_DATE).orEmpty(),
                priority = document.getString(TaskConstants.FIELD_PRIORITY).orEmpty(),
                title = document.getString(TaskConstants.FIELD_TITLE).orEmpty(),
                userId = document.getString(TaskConstants.FIELD_USER_ID).orEmpty()
            )
        }
    }
}

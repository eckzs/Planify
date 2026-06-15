package com.app.planify.api.models

import com.app.planify.constants.TaskConstants
import com.google.firebase.firestore.DocumentSnapshot

data class Task(
    val id: String = "",
    val date: String = "",
    val priority: String = "",
    val title: String = "",
    val userId: String = "",
    val completed: Boolean = false,
    val courseId: String? = null,
    val tags: List<String> = emptyList(),
    val evidenceUrl: String? = null,
    val notes: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            TaskConstants.FIELD_DATE to date,
            TaskConstants.FIELD_PRIORITY to priority,
            TaskConstants.FIELD_TITLE to title,
            TaskConstants.FIELD_USER_ID to userId,
            TaskConstants.FIELD_COMPLETED to completed,
            TaskConstants.FIELD_COURSE_ID to courseId,
            TaskConstants.FIELD_TAGS to tags,
            TaskConstants.FIELD_EVIDENCE_URL to evidenceUrl,
            TaskConstants.FIELD_NOTES to notes
        )
    }

    companion object {
        fun fromDocument(document: DocumentSnapshot): Task {
            @Suppress("UNCHECKED_CAST")
            return Task(
                id = document.id,
                date = document.getString(TaskConstants.FIELD_DATE).orEmpty(),
                priority = document.getString(TaskConstants.FIELD_PRIORITY).orEmpty(),
                title = document.getString(TaskConstants.FIELD_TITLE).orEmpty(),
                userId = document.getString(TaskConstants.FIELD_USER_ID).orEmpty(),
                completed = document.getBoolean(TaskConstants.FIELD_COMPLETED) ?: false,
                courseId = document.getString(TaskConstants.FIELD_COURSE_ID),
                tags = (document.get(TaskConstants.FIELD_TAGS) as? List<String>) ?: emptyList(),
                evidenceUrl = document.getString(TaskConstants.FIELD_EVIDENCE_URL),
                notes = document.getString(TaskConstants.FIELD_NOTES).orEmpty()
            )
        }
    }
}

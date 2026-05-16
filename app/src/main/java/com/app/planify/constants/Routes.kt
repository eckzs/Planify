package com.app.planify.constants

object Routes {
  // Auth flow
  const val AUTH = "auth"
  const val ONBOARDING = "onboarding"

  // Main app (bottom nav)
  const val HOME = "home"
  const val TASKS = "tasks"
  const val ADD_TASK = "tasks/add"
  const val TASK_DETAIL = "tasks/{taskId}"
  const val COURSES = "courses"
  const val FLASHCARDS_STUDY = "flashcards/{courseId}"
  const val ADD_FLASHCARD = "flashcards/{courseId}/add"
  const val POMODORO = "pomodoro"
  const val POMODORO_WITH_TASK = "pomodoro/{taskId}"
  const val PROFILE = "profile"

  fun taskDetail(taskId: String) = "tasks/${android.net.Uri.encode(taskId)}"
  fun pomodoro(taskId: String) = "pomodoro/${android.net.Uri.encode(taskId)}"
  fun flashcards(courseId: String) = "flashcards/${android.net.Uri.encode(courseId)}"
  fun addFlashcard(courseId: String) = "flashcards/${android.net.Uri.encode(courseId)}/add"
}

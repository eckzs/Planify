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
  const val POMODORO = "pomodoro"
  const val PROFILE = "profile"

  // TODO: uncomment when screens are ready
  fun taskDetail(taskId: String) = "tasks/${android.net.Uri.encode(taskId)}"
}

# Plan: Integración Gemini AI en Planify

Modelo: `gemini-2.0-flash` · SDK: `com.google.ai.client.generativeai` · Capa gratuita Google AI Studio

---

## Estado general

- [x] Fase 0 — Setup inicial
- [x] Fase 1 — AI Chat libre
- [x] Fase 2 — Tutor de flashcards
- [x] Fase 3 — Generador de flashcards desde texto

---

## Fase 0 — Setup inicial ✓

- [x] `app/build.gradle.kts` — `buildConfig = true`, `buildConfigField GEMINI_API_KEY`, dep `generativeai:0.9.0`
- [x] `local.properties` — `GEMINI_API_KEY=PEGA_TU_KEY_AQUI` (no se sube a git)
- [x] `api/client/GeminiClient.kt` — Singleton con system instruction en español

---

## Fase 1 — AI Chat libre ✓

- [x] `api/models/AiModels.kt` — `ChatMessage`, `GeneratedFlashcard`
- [x] `api/services/AiRepository.kt` — `sendMessage`, `explainFlashcard`, `generateFlashcards`
- [x] `screens/ai/AiChatState.kt` — `AiChatUiState` (Idle, Loading, Error)
- [x] `screens/ai/AiChatViewModel.kt`
- [x] `screens/ai/AiChatScreen.kt` — burbujas de chat, input, scroll automático
- [x] `constants/Routes.kt` — `AI_CHAT = "ai"`
- [x] `AppNavigation.kt` — ruta AI_CHAT + bottomBarRoutes actualizado
- [x] `components/PlBottomBar.kt` — ítem "AI" con icono AutoAwesome

---

## Fase 2 — Tutor de flashcards ✓

- [x] `api/services/AiRepository.kt` — `explainFlashcard(front, back)`
- [x] `screens/flashcards/FlashcardsViewModel.kt` — `showExplanation`, `isExplaining`, `explanationText`, `explanationError`, `explainCurrentCard()`, `dismissExplanation()`
- [x] `screens/flashcards/FlashcardCard.kt` — botón "¿Por qué es esto?" bajo los rating buttons
- [x] `screens/flashcards/FlashcardsScreen.kt` — `ModalBottomSheet` con explicación + botón AI en top bar

---

## Fase 3 — Generador de flashcards desde texto ✓

- [x] `api/models/AiModels.kt` — `GeneratedFlashcard`
- [x] `api/services/AiRepository.kt` — `generateFlashcards(topic, count)` con parsing JSON
- [x] `screens/flashcards/GenerateFlashcardsViewModel.kt`
- [x] `screens/flashcards/GenerateFlashcardsScreen.kt` — form de input, chips de cantidad, preview + guardar
- [x] `constants/Routes.kt` — `GENERATE_FLASHCARDS`, helper `generateFlashcards(courseId)`
- [x] `AppNavigation.kt` — ruta GENERATE_FLASHCARDS
- [x] `screens/flashcards/FlashcardsScreen.kt` — botón ✨ en top bar navega a GenerateFlashcards

---

## Pendiente (tú)

1. Entrar a [aistudio.google.com](https://aistudio.google.com) y crear una API key gratuita
2. Pegar la key en `local.properties` reemplazando `PEGA_TU_KEY_AQUI`
3. Hacer Sync Gradle en Android Studio
4. Compilar y probar

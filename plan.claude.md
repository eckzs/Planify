# plan.claude — Roadmap completo de Planify

> Referencia maestra para completar el proyecto.
> Cada fase tiene precondiciones, archivos exactos y cambios específicos.
> Actualizar este archivo marcando [ ] → [x] al completar cada ítem.

---

## Visión del producto

App de estudio para universitarios con cuatro pilares:
1. **Cursos** — hub central: agrupa tareas, flashcards y estadísticas por materia
2. **Tareas** — gestión diaria con fecha, prioridad y vinculación a curso
3. **Pomodoro** — timer con tracking de tiempo por tarea/curso
4. **Flashcards** — repaso espaciado (SM-2) con generación por IA

Métricas visibles al usuario: tiempo por curso, tareas completadas por día, tarjetas revisadas por tema.

---

## Estado actual (mayo 2026)

| Módulo | Estado |
|---|---|
| Auth (email OTP + Google) | ✅ Completo |
| Courses CRUD | ✅ Completo |
| Tasks CRUD (con courseId) | ✅ Completo |
| Flashcards con SRS (SM-2) | ✅ Completo |
| Pomodoro + sesiones | ✅ Completo |
| Home con charts 7 días | ✅ Completo |
| Navegación + bottom bar | ✅ Completo |
| CourseDetailScreen | ❌ Falta |
| flashcard_reviews tracking | ❌ Falta |
| Analytics por curso | ❌ Falta |
| ProfileScreen | ❌ Falta |
| AI — Generador de flashcards | ❌ Falta |
| AI — Chat asistente | ❌ Falta |

---

## Deuda técnica crítica (resolver antes de agregar features)

Los ViewModels de courses y flashcards no usan Hilt — construyen repositorios directamente.
Esto rompe el patrón del resto del proyecto.

Archivos afectados:
- `CoursesViewModel.kt` → agregar `@HiltViewModel` + `@Inject constructor(private val repo: CoursesRepository)`
- `FlashcardsViewModel.kt` → ídem
- `AddFlashcardViewModel.kt` → ídem
- `CoursesScreen.kt` → cambiar `viewModel()` por `hiltViewModel()`
- `FlashcardsScreen.kt` → ídem
- `AddFlashcardScreen.kt` → ídem

Los repositorios (`CoursesRepository`, `FlashcardsRepository`) necesitan anotación `@Inject constructor()`.

---

## Fase 0 — Refactor estructural

> Precondición: ninguna. Ejecutar antes de escribir código nuevo.
> Referencia detallada: `plan.std.md`

### 0.1 screens/tasks/
- [ ] Crear `TasksDateSelector.kt` ← mover `DateSelector`, `DateItem`
- [ ] Crear `TaskCard.kt` ← mover `TaskCard`, `TasksList`
- [ ] Crear `TasksState.kt` ← mover `sealed class TasksState`
- [ ] `TasksScreen.kt` queda con: `TasksScreen`, `TasksHeader`
- [ ] `TasksViewModel.kt` queda con: solo `TasksViewModel`

### 0.2 screens/home/
- [ ] Crear `HomeCards.kt` ← mover `PendingTasksCard`, `RecentTasksCard`, `QuickAccessRow`
- [ ] Crear `HomeChart.kt` ← mover `ChartCard`, `SimpleBarChart`
- [ ] Crear `DailyMetric.kt` ← mover `data class DailyMetric`
- [ ] `HomeScreen.kt` queda con: `HomeScreen`, `HomeHeader`
- [ ] `HomeViewModel.kt` queda con: solo `HomeViewModel`

### 0.3 screens/pomodoro/
- [ ] Crear `PomodoroFilter.kt` ← mover `enum PomodoroFilter`
- [ ] Crear `PomodoroTimer.kt` ← mover `CircularTimer`, `PomodoroControls`, `formatTime()`, `modeText()`
- [ ] Crear `PomodoroDashboard.kt` ← mover `FilterChip`, `DashboardTaskItem`
- [ ] `PomodoroScreen.kt` queda con: solo `PomodoroScreen`
- [ ] `PomodoroViewModel.kt` queda con: solo `PomodoroViewModel`

### 0.4 screens/courses/
- [ ] Crear `CourseCard.kt` ← mover `CourseCard`, `CoursesList`
- [ ] Crear `CourseDialogs.kt` ← mover `AddCourseDialog`, `ColorPicker`
- [ ] Crear `CoursesState.kt` ← mover `sealed class CoursesState`
- [ ] `CoursesScreen.kt` queda con: `CoursesScreen`, `EmptyCoursesState`
- [ ] `CoursesViewModel.kt` queda con: solo `CoursesViewModel`

### 0.5 screens/flashcards/
- [ ] Crear `logic/utils/SpacedRepetition.kt` ← mover `fun applySpacedRepetition(card, rating): Flashcard`
- [ ] Crear `FlashcardsState.kt` ← mover `sealed class FlashcardsState`
- [ ] Crear `FlashcardCard.kt` ← mover `CardSide`, `StudyContent`, `StudyProgress`
- [ ] Crear `FlashcardRating.kt` ← mover `RatingButtons`
- [ ] Crear `FlashcardEndStates.kt` ← mover `StudyFinishedState`, `NoDueCardsState`
- [ ] `FlashcardsScreen.kt` queda con: solo `FlashcardsScreen`
- [ ] `FlashcardsViewModel.kt` queda con: solo `FlashcardsViewModel` (usa `SpacedRepetition.kt`)

### 0.6 components/
- [ ] Crear `PlPriorityBadge.kt` ← mover `PlPriorityBadge` de `PlBadge.kt`
- [ ] Crear `BottomBarItem.kt` ← mover data class interna de `PlBottomBar.kt`

### 0.7 Deuda técnica Hilt
- [ ] Anotar `CoursesRepository` con `@Inject constructor()`
- [ ] Anotar `FlashcardsRepository` con `@Inject constructor()`
- [ ] Migrar `CoursesViewModel` a `@HiltViewModel` + `@Inject constructor`
- [ ] Migrar `FlashcardsViewModel` a `@HiltViewModel` + `@Inject constructor`
- [ ] Migrar `AddFlashcardViewModel` a `@HiltViewModel` + `@Inject constructor`
- [ ] Cambiar `viewModel()` por `hiltViewModel()` en `CoursesScreen`, `FlashcardsScreen`, `AddFlashcardScreen`

---

## Fase 1 — CourseDetailScreen

> Precondición: Fase 0 completa.
> La pantalla más importante: conecta cursos con tareas, flashcards y estadísticas.

### Flujo de navegación
```
CoursesScreen → CourseDetailScreen → FlashcardsScreen (estudio)
                                  → AddFlashcardScreen
                                  → AddTaskScreen (con courseId pre-cargado)
```

### 1.1 Ruta de navegación
- [ ] `constants/Routes.kt` → agregar:
  ```kotlin
  const val COURSE_DETAIL = "courses/{courseId}"
  fun courseDetail(courseId: String) = "courses/${Uri.encode(courseId)}"
  ```
- [ ] `AppNavigation.kt` → registrar composable para `Routes.COURSE_DETAIL`

### 1.2 Datos que necesita CourseDetailViewModel
- `getCourse(courseId)` → `CoursesRepository.getCourse(courseId)` — **agregar este método**
- `getTasksByCourse(courseId)` → `TasksRepository.getTasksByCourse(courseId)` — ya existe
- `getDueCardCount(courseId)` → `FlashcardsRepository` — **agregar método**
- `getTotalCardCount(courseId)` → `FlashcardsRepository` — **agregar método**

### 1.3 Cambios en repositorios
- [ ] `CoursesRepository.kt` → agregar:
  ```kotlin
  suspend fun getCourse(courseId: String): Result<Course>
  ```
- [ ] `FlashcardsRepository.kt` → agregar:
  ```kotlin
  suspend fun getCardCountByCourse(courseId: String): Result<Pair<Int, Int>>
  // devuelve Pair(totalCards, dueToday)
  ```

### 1.4 Archivos nuevos
- [ ] `screens/courses/CourseDetailScreen.kt`
  - Header con nombre + color del curso
  - Sección Tareas: lista de tareas del curso, % completado, botón [+ Tarea]
  - Sección Flashcards: N total | N para hoy, botones [Estudiar] [+ Tarjeta]
  - Sección Estadísticas: placeholder (se llena en Fase 3)
- [ ] `screens/courses/CourseDetailViewModel.kt`
  - `@HiltViewModel`
  - `StateFlow<CourseDetailState>`
  - Llama a los tres repositorios en paralelo (usar `async/await` o cargar en secuencia)
  - `sealed class CourseDetailState` en su propio archivo `CourseDetailState.kt`
- [ ] `screens/courses/CourseDetailState.kt`
  - `Loading`, `Success(course, tasks, totalCards, dueCards)`, `Error(message)`

### 1.5 Ajuste en CoursesScreen
- [ ] `CoursesScreen.kt` → cambiar navegación: en lugar de ir a flashcards, ir a `courseDetail(course.id)`
- [ ] `AppNavigation.kt` → actualizar la ruta del click de CourseCard

---

## Fase 2 — Tracking de revisiones de flashcards

> Precondición: Fase 1 completa.
> Sin esto no hay métricas de "tarjetas revisadas por día/curso".

### 2.1 Nueva entidad Firestore
Colección: `flashcard_reviews`
```
flashcard_reviews/{docId}
  userId:     string
  courseId:   string
  cardId:     string
  rating:     int   ← 1 | 3 | 4
  reviewedAt: Timestamp
```

### 2.2 Archivos a modificar/crear
- [ ] `api/models/FlashcardModels.kt` → agregar:
  ```kotlin
  data class FlashcardReview(
      val id: String = "",
      val userId: String = "",
      val courseId: String = "",
      val cardId: String = "",
      val rating: Int = 0,
      val reviewedAt: Timestamp = Timestamp.now()
  )
  ```
- [ ] `constants/FlashcardConstants.kt` → agregar:
  ```kotlin
  const val REVIEWS_COLLECTION = "flashcard_reviews"
  ```
  Y los campos: `userId`, `courseId`, `cardId`, `rating`, `reviewedAt`

- [ ] `api/services/FlashcardsRepository.kt` → agregar:
  ```kotlin
  suspend fun logReview(cardId: String, courseId: String, rating: Int): Result<Unit>
  suspend fun getReviewsByDateRange(courseId: String, from: Timestamp, to: Timestamp): Result<List<FlashcardReview>>
  suspend fun getReviewsToday(courseId: String): Result<Int>  // count
  ```

- [ ] `screens/flashcards/FlashcardsViewModel.kt` → en `onReviewResult()`, después de `flashcardsRepository.updateCard(updatedCard)`, llamar también a `flashcardsRepository.logReview(card.id, courseId, rating)`
  - Requiere guardar `courseId` en el ViewModel (recibirlo en `loadCards()` y almacenarlo)

---

## Fase 3 — Analytics por curso + Home mejorado

> Precondición: Fase 2 completa (necesita `flashcard_reviews`).

### 3.1 Tiempo Pomodoro por curso
`PomodoroRepository` ya guarda `sessions` con `taskId`. Las `tasks` tienen `courseId`.
El join se hace en el ViewModel (no en el repositorio).

- [ ] `PomodoroRepository.kt` → agregar:
  ```kotlin
  suspend fun getSessionsByTaskIds(taskIds: List<String>): Result<List<PomodoroSession>>
  ```

- [ ] `CourseDetailViewModel.kt` → agregar carga de tiempo:
  1. `getTasksByCourse(courseId)` → lista de taskIds
  2. `getSessionsByTaskIds(taskIds)` → sesiones
  3. Sumar `duration` de sesiones completadas → total en minutos

### 3.2 Sección Estadísticas en CourseDetail
- [ ] `CourseDetailState.kt` → agregar campos: `totalPomodoroMinutes`, `reviewsThisWeek`, `completedTasksThisMonth`
- [ ] `CourseDetailScreen.kt` → implementar sección Estadísticas:
  - "Tiempo invertido: X h Y min"
  - "Tarjetas esta semana: N"
  - "Tareas completadas este mes: N"
  - Mini bar chart de actividad de los últimos 7 días (reutilizar `SimpleBarChart` de Home)

### 3.3 Home mejorado
- [ ] `HomeViewModel.kt` → agregar:
  - `totalDueCardsToday: Int` — suma de cartas por revisar en todos los cursos
  - `pomodoroMinutesToday: Int` — tiempo total de sesiones de hoy
- [ ] `HomeScreen.kt` / `HomeCards.kt` → agregar tarjetas de resumen:
  - "Tarjetas a repasar hoy: N"
  - "Pomodoro hoy: X min"

---

## Fase 4 — ProfileScreen

> Precondición: ninguna (paralela a otras fases).
> Ruta ya existe en `Routes.PROFILE` y en el bottom bar.

### 4.1 Datos del perfil
Vienen de Firestore colección `users/{userId}`: `name`, `email`, `career`, `university`

- [ ] Verificar que `OnboardingViewModel` guarda `career` y `university` en Firestore
  - Si no: actualizar para que los persista
- [ ] `CoursesRepository.kt` o nuevo `UserRepository.kt` → agregar:
  ```kotlin
  suspend fun getProfile(): Result<UserProfile>
  ```

### 4.2 Archivos nuevos
- [ ] `api/models/UserModels.kt` → `data class UserProfile(name, email, career, university)`
- [ ] `api/services/UserRepository.kt` → `getProfile()`, `updateProfile()`
- [ ] `screens/profile/ProfileScreen.kt`
  - Avatar con inicial del nombre
  - Nombre, email (no editable), carrera, universidad
  - Botón "Cerrar sesión" → limpia token, navega a AUTH
- [ ] `screens/profile/ProfileViewModel.kt`
  - `@HiltViewModel`
  - `StateFlow<ProfileState>`
  - `fun signOut(onComplete: () -> Unit)`

---

## Fase 5 — AI Flashcard Generator

> Precondición: Fase 1 completa (CourseDetail ya existe para iniciar la navegación).

### 5.1 Arquitectura de integración IA
**Recomendación:** Firebase Function como proxy (el API key nunca sale del backend).
Alternativa rápida: llamada directa desde Android (API key en `BuildConfig` — aceptable en dev).

Endpoint esperado:
```
POST /generateFlashcards
Body: { "topic": "...", "count": 10 }
Response: [{ "front": "...", "back": "..." }, ...]
```

### 5.2 Ruta de navegación
- [ ] `constants/Routes.kt` → agregar:
  ```kotlin
  const val AI_FLASHCARD_GENERATOR = "flashcards/{courseId}/generate"
  fun aiFlashcardGenerator(courseId: String) = "flashcards/${Uri.encode(courseId)}/generate"
  ```
- [ ] `AppNavigation.kt` → registrar ruta
- [ ] `CourseDetailScreen.kt` → agregar botón [✦ Generar con IA] en sección Flashcards

### 5.3 Archivos nuevos
- [ ] `api/models/AIModels.kt`
  ```kotlin
  data class GenerateFlashcardsRequest(val topic: String, val count: Int = 10)
  data class GeneratedCard(val front: String, val back: String)
  ```
- [ ] `api/services/AIRepository.kt`
  ```kotlin
  suspend fun generateFlashcards(topic: String, count: Int): Result<List<GeneratedCard>>
  ```
- [ ] `screens/flashcards/AIFlashcardGeneratorScreen.kt`
  - Input: campo de texto "Tema o concepto"
  - Slider o dropdown: "Número de tarjetas (5/10/15)"
  - [Generar] → loading state
  - Lista de cards generadas con campos editables (frente/reverso)
  - [Eliminar card individual]
  - [Guardar todas] → llama a `FlashcardsRepository.createCard()` × N → navega de vuelta
- [ ] `screens/flashcards/AIFlashcardGeneratorViewModel.kt`
  - `@HiltViewModel`
  - `StateFlow<AIGeneratorState>`
  - `fun generate(topic, count, courseId)`
  - `fun editCard(index, front, back)`
  - `fun removeCard(index)`
  - `fun saveAll(courseId, onComplete)`
  - `sealed class AIGeneratorState`: `Idle`, `Loading`, `Generated(cards)`, `Saving`, `Error(message)`

### 5.4 Prompt para Claude API
```
Eres un asistente de estudio. Genera exactamente {count} flashcards de memoria 
para el tema: "{topic}".
Devuelve únicamente un JSON array con objetos {"front": "pregunta concisa", "back": "respuesta clara"}.
Sin texto adicional, solo el JSON.
```

---

## Fase 6 — AI Chat asistente por curso

> Precondición: Fase 5 (infraestructura AI ya establecida).

### 6.1 Contexto del chat
- Nombre del curso
- Los `front` de todas las flashcards del curso (base de conocimiento)
- Historial de la conversación (últimos N turnos)

### 6.2 Ruta
- [ ] `constants/Routes.kt` → agregar `COURSE_CHAT = "courses/{courseId}/chat"`
- [ ] `CourseDetailScreen.kt` → agregar botón [Chat IA] en el header o como FAB

### 6.3 Archivos nuevos
- [ ] `api/models/AIModels.kt` → agregar:
  ```kotlin
  data class ChatMessage(val role: String, val content: String)
  data class ChatRequest(val messages: List<ChatMessage>, val context: String)
  ```
- [ ] `api/services/AIRepository.kt` → agregar:
  ```kotlin
  suspend fun chat(messages: List<ChatMessage>, courseContext: String): Result<String>
  ```
- [ ] `screens/courses/CourseChatScreen.kt`
  - Lista de mensajes (LazyColumn, scroll al último)
  - Input + botón enviar
  - Mensaje de carga mientras espera respuesta
  - Burbujas de chat diferenciadas (usuario / IA)
- [ ] `screens/courses/CourseChatViewModel.kt`
  - Carga flashcard fronts del curso para armar el contexto
  - Mantiene historial de mensajes
  - `fun sendMessage(text)`

---

## Fase 7 — Técnica Feynman con IA (stretch)

> Precondición: Fases 5 y 6 completas.
> Complejidad alta — dejar para última iteración.

### Flujo propuesto
1. Usuario elige un tema/flashcard como punto de partida
2. Se le pide "Explica este concepto con tus propias palabras"
3. La IA evalúa la explicación: ¿qué entendió bien? ¿qué le falta?
4. Sugiere recursos o preguntas para los gaps detectados
5. Opcionalmente genera nuevas flashcards de los gaps

### Archivos estimados
- `screens/courses/FeynmanScreen.kt`
- `screens/courses/FeynmanViewModel.kt`
- Ruta: `courses/{courseId}/feynman`

---

## Orden de ejecución recomendado

```
Fase 0 (Refactor)
  ├── 0.1 tasks
  ├── 0.2 home
  ├── 0.3 pomodoro
  ├── 0.4 courses
  ├── 0.5 flashcards
  ├── 0.6 components
  └── 0.7 Hilt migration
       ↓
Fase 1 (CourseDetail) ← desbloquea el valor central del producto
       ↓
Fase 2 (flashcard_reviews) ← desbloquea métricas
       ↓
Fase 3 (Analytics) ← enriquece CourseDetail + Home
       ↓
Fase 4 (Profile) ← paralela a 3, independiente
       ↓
Fase 5 (AI Flashcards) ← feature diferenciadora
       ↓
Fase 6 (AI Chat) ← feature diferenciadora
       ↓
Fase 7 (Feynman) ← stretch goal
```

---

## Nuevos archivos del proyecto al completar todas las fases

```
screens/courses/
  CourseDetailScreen.kt
  CourseDetailViewModel.kt
  CourseDetailState.kt
  CourseChatScreen.kt           ← Fase 6
  CourseChatViewModel.kt        ← Fase 6
  FeynmanScreen.kt              ← Fase 7
  FeynmanViewModel.kt           ← Fase 7

screens/flashcards/
  AIFlashcardGeneratorScreen.kt
  AIFlashcardGeneratorViewModel.kt

screens/profile/
  ProfileScreen.kt
  ProfileViewModel.kt

api/models/
  UserModels.kt
  AIModels.kt

api/services/
  UserRepository.kt
  AIRepository.kt

logic/utils/
  SpacedRepetition.kt           ← movida desde FlashcardsViewModel

constants/
  Routes.kt                     ← actualizar con nuevas rutas
```

# plan.std — Reorganización estructural de Planify

> Objetivo: un composable/clase por archivo. Sin cambiar lógica de negocio.
> Nota: mover un composable `private` a otro archivo requiere quitar el modificador `private`
> (queda package-internal en Kotlin). Es el único cambio de código necesario.

---

## Estado actual vs. objetivo

| Archivo actual | Problema | Resultado deseado |
|---|---|---|
| `TasksScreen.kt` | 281 líneas, 6 composables | 3 archivos |
| `PomodoroScreen.kt` | 311 líneas, 5 composables + 2 funciones | 3 archivos |
| `HomeScreen.kt` | 280 líneas, 7 composables | 3 archivos |
| `TasksViewModel.kt` | ViewModel + sealed class `TasksState` | 2 archivos |
| `CoursesScreen.kt` | 192 líneas, 6 composables | 3 archivos |
| `CoursesViewModel.kt` | ViewModel + sealed class `CoursesState` | 2 archivos |
| `FlashcardsScreen.kt` | 304 líneas, 7 composables | 4 archivos |
| `FlashcardsViewModel.kt` | ViewModel + sealed class + algoritmo SRS | 3 archivos |
| `PlBadge.kt` | 2 composables no relacionados en un mismo archivo | 2 archivos |
| `PlBottomBar.kt` | Composable + data class mezclados | 2 archivos |

---

## Paso 1 — `screens/tasks/`

### Antes
```
tasks/
├── TasksScreen.kt       ← TasksScreen, TasksHeader, DateSelector, DateItem, TasksList, TaskCard
├── TasksViewModel.kt    ← TasksViewModel, TasksState (sealed class)
└── AddTaskScreen.kt     ← AddTaskScreen, PriorityDropdown
```

### Después
```
tasks/
├── TasksScreen.kt       ← solo TasksScreen + TasksHeader
├── TasksDateSelector.kt ← DateSelector, DateItem
├── TaskCard.kt          ← TaskCard, TasksList
├── TasksState.kt        ← sealed class TasksState
├── TasksViewModel.kt    ← solo TasksViewModel
└── AddTaskScreen.kt     ← AddTaskScreen, PriorityDropdown  (sin cambios)
```

### Movimientos concretos

| Qué mover | Desde | Hacia |
|---|---|---|
| `DateSelector` + `DateItem` | `TasksScreen.kt` | `TasksDateSelector.kt` (mismo package) |
| `TaskCard` + `TasksList` | `TasksScreen.kt` | `TaskCard.kt` (mismo package) |
| `sealed class TasksState` | `TasksViewModel.kt` | `TasksState.kt` (mismo package) |

---

## Paso 2 — `screens/pomodoro/`

### Antes
```
pomodoro/
├── PomodoroScreen.kt    ← PomodoroScreen, FilterChip, DashboardTaskItem,
│                           CircularTimer, PomodoroControls, formatTime(), modeText()
└── PomodoroViewModel.kt ← PomodoroViewModel, enum PomodoroFilter
```

### Después
```
pomodoro/
├── PomodoroScreen.kt    ← solo PomodoroScreen
├── PomodoroTimer.kt     ← CircularTimer, PomodoroControls, formatTime(), modeText()
├── PomodoroDashboard.kt ← FilterChip, DashboardTaskItem
├── PomodoroFilter.kt    ← enum PomodoroFilter
└── PomodoroViewModel.kt ← solo PomodoroViewModel
```

### Movimientos concretos

| Qué mover | Desde | Hacia |
|---|---|---|
| `CircularTimer`, `PomodoroControls`, `formatTime()`, `modeText()` | `PomodoroScreen.kt` | `PomodoroTimer.kt` |
| `FilterChip`, `DashboardTaskItem` | `PomodoroScreen.kt` | `PomodoroDashboard.kt` |
| `enum PomodoroFilter` | `PomodoroViewModel.kt` | `PomodoroFilter.kt` |

---

## Paso 3 — `screens/home/`

### Antes
```
home/
├── HomeScreen.kt     ← HomeScreen, HomeHeader, PendingTasksCard, RecentTasksCard,
│                        QuickAccessRow, ChartCard, SimpleBarChart
└── HomeViewModel.kt  ← HomeViewModel, data class DailyMetric
```

### Después
```
home/
├── HomeScreen.kt     ← solo HomeScreen + HomeHeader
├── HomeCards.kt      ← PendingTasksCard, RecentTasksCard, QuickAccessRow
├── HomeChart.kt      ← ChartCard, SimpleBarChart
├── DailyMetric.kt    ← data class DailyMetric
└── HomeViewModel.kt  ← solo HomeViewModel
```

### Movimientos concretos

| Qué mover | Desde | Hacia |
|---|---|---|
| `PendingTasksCard`, `RecentTasksCard`, `QuickAccessRow` | `HomeScreen.kt` | `HomeCards.kt` |
| `ChartCard`, `SimpleBarChart` | `HomeScreen.kt` | `HomeChart.kt` |
| `data class DailyMetric` | `HomeViewModel.kt` | `DailyMetric.kt` |

---

## Paso 4 — `screens/courses/`

### Antes
```
courses/
├── CoursesScreen.kt    ← CoursesScreen, CoursesList, CourseCard,
│                          EmptyCoursesState, AddCourseDialog, ColorPicker
└── CoursesViewModel.kt ← CoursesViewModel, sealed class CoursesState
```

### Después
```
courses/
├── CoursesScreen.kt    ← solo CoursesScreen + EmptyCoursesState (<15 líneas)
├── CourseCard.kt       ← CourseCard, CoursesList
├── CourseDialogs.kt    ← AddCourseDialog, ColorPicker
├── CoursesState.kt     ← sealed class CoursesState
└── CoursesViewModel.kt ← solo CoursesViewModel
```

### Movimientos concretos

| Qué mover | Desde | Hacia |
|---|---|---|
| `CourseCard` + `CoursesList` | `CoursesScreen.kt` | `CourseCard.kt` |
| `AddCourseDialog` + `ColorPicker` | `CoursesScreen.kt` | `CourseDialogs.kt` |
| `sealed class CoursesState` | `CoursesViewModel.kt` | `CoursesState.kt` |

---

## Paso 5 — `screens/flashcards/`

### Antes
```
flashcards/
├── FlashcardsScreen.kt    ← FlashcardsScreen, StudyProgress, StudyContent,
│                             RatingButtons, CardSide, StudyFinishedState, NoDueCardsState
├── FlashcardsViewModel.kt ← FlashcardsViewModel, sealed class FlashcardsState,
│                             fun applySpacedRepetition()
├── AddFlashcardScreen.kt  ← AddFlashcardScreen (92 líneas, 1 composable — OK)
└── AddFlashcardViewModel.kt ← AddFlashcardViewModel (57 líneas — OK)
```

### Después
```
flashcards/
├── FlashcardsScreen.kt    ← solo FlashcardsScreen
├── FlashcardCard.kt       ← CardSide, StudyContent, StudyProgress
├── FlashcardRating.kt     ← RatingButtons
├── FlashcardEndStates.kt  ← StudyFinishedState, NoDueCardsState
├── FlashcardsState.kt     ← sealed class FlashcardsState
├── FlashcardsViewModel.kt ← solo FlashcardsViewModel (llama a SpacedRepetition)
├── AddFlashcardScreen.kt  ← sin cambios
└── AddFlashcardViewModel.kt ← sin cambios
logic/utils/
└── SpacedRepetition.kt    ← fun applySpacedRepetition(card, rating): Flashcard
```

### Movimientos concretos

| Qué mover | Desde | Hacia |
|---|---|---|
| `CardSide`, `StudyContent`, `StudyProgress` | `FlashcardsScreen.kt` | `FlashcardCard.kt` |
| `RatingButtons` | `FlashcardsScreen.kt` | `FlashcardRating.kt` |
| `StudyFinishedState`, `NoDueCardsState` | `FlashcardsScreen.kt` | `FlashcardEndStates.kt` |
| `sealed class FlashcardsState` | `FlashcardsViewModel.kt` | `FlashcardsState.kt` |
| `fun applySpacedRepetition()` | `FlashcardsViewModel.kt` | `logic/utils/SpacedRepetition.kt` |

---

## Paso 6 — `components/`

### Antes
```
components/
├── PlBadge.kt       ← PlBadge (genérico) + PlPriorityBadge (específico de tasks)
└── PlBottomBar.kt   ← PlBottomBar + BottomBarItem (data class)
```

### Después
```
components/
├── PlBadge.kt           ← solo PlBadge
├── PlPriorityBadge.kt   ← solo PlPriorityBadge
├── PlBottomBar.kt       ← solo PlBottomBar
└── BottomBarItem.kt     ← data class BottomBarItem
```

### Movimientos concretos

| Qué mover | Desde | Hacia |
|---|---|---|
| `PlPriorityBadge` | `PlBadge.kt` | `PlPriorityBadge.kt` |
| `data class BottomBarItem` (o similar) | `PlBottomBar.kt` | `BottomBarItem.kt` |

---

## Lo que NO se mueve

| Archivo/carpeta | Razón |
|---|---|
| `ui/theme/Color.kt` + `PlColors.kt` | Responsabilidades distintas: primitivas de paleta vs. wrapper semántico sobre MaterialTheme |
| `ui/theme/Type.kt` + `PlTypography.kt` | Mismo patrón: definición de Typography vs. wrapper de acceso |
| `api/services/*Repository.kt` | Sin interfaces Retrofit separadas porque el backend es Firebase, no Supabase REST |
| `AuthScreen.kt` (PlLogo, PlDivider) | Composables privados de <60 líneas, dentro del límite razonable |
| `OtpScreen.kt` (OtpBoxes) | Ídem |
| `AddFlashcardScreen.kt` | 92 líneas, 1 composable — dentro del límite razonable |
| `AddFlashcardViewModel.kt` | 57 líneas — OK |
| `constants/` | Un objeto por archivo, ya correcto |
| `api/models/` | Un modelo por archivo, ya correcto |

---

## Orden de ejecución recomendado

1. `screens/tasks/` — mayor ganancia de legibilidad (6 composables → 3 archivos)
2. `screens/home/` — mismo motivo
3. `screens/pomodoro/` — incluye mover el enum antes que los composables
4. `screens/courses/` — añadido: nuevos archivos desde el desarrollo reciente
5. `screens/flashcards/` — el más complejo: mueve lógica SRS a `logic/utils/`
6. `components/PlBadge.kt` — cambio pequeño, bajo riesgo
7. `components/PlBottomBar.kt` — cambio pequeño, bajo riesgo

---

## Regla para decidir cuándo extraer en el futuro

- Un archivo supera **~80 líneas** → candidato a dividir
- Un archivo tiene **más de 2 composables top-level** → dividir por composable
- Un `sealed class` o `enum` es usado desde más de un archivo → moverlo a su propio archivo
- Lógica pura sin dependencias de UI o Android → mover a `logic/utils/`

# Arquitectura de Datos: Planify (Firebase Firestore)

Este documento detalla las colecciones y entidades de Firestore que conforman el ecosistema de Planify, explicando sus campos y relaciones para facilitar el desarrollo de nuevos flujos.

---

## 1. Colección: `users`
Almacena la información de perfil de los estudiantes.

| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| `name` | String | Nombre completo del usuario. |
| `email` | String | Correo electrónico principal. |
| `onboardingCompleted` | Boolean | Indica si el usuario terminó el flujo inicial. |

---

## 2. Colección: `courses`
Entidad central que agrupa tareas y métodos de estudio por materia o proyecto.

| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| `userId` | String | Referencia al ID del usuario propietario. |
| `name` | String | Nombre de la materia (ej: "Física 1"). |
| `color` | String | Código Hexadecimal para personalización de UI. |

---

## 3. Colección: `tasks`
Gestión de actividades con metadatos de estudio y evidencias.

| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| `userId` | String | Propietario de la tarea. |
| `courseId` | String (opcional) | **Relación:** ID del curso al que pertenece. |
| `title` | String | Descripción breve de la actividad. |
| `date` | String | Fecha programada (formato `dd/MM/yyyy`). |
| `priority` | String | "Alta", "Media" o "Baja". |
| `completed` | Boolean | Estado de la tarea. |
| `tags` | List\<String\> | Etiquetas (ej: #Parcial, #Laboratorio). |
| `evidenceUrl` | String (opcional) | Enlace a la evidencia subida. |
| `notes` | String | Detalles adicionales o instrucciones. |

---

## 4. Colección: `flashcards`
Sistema de memorización basado en Repetición Espaciada (SRS).

| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| `courseId` | String | **Relación Obligatoria:** Mazo al que pertenece. |
| `front` | String | Pregunta o concepto. |
| `back` | String | Respuesta o definición. |
| `nextReview` | Timestamp | Fecha programada para el próximo repaso. |
| `interval` | Int | Días entre repasos (según algoritmo SRS). |
| `easeFactor` | Double | Factor de facilidad (multiplicador de dificultad). |

---

## 5. Colección: `pomodoro`
Historial de sesiones de enfoque y métricas de productividad.

### Documento: `[userId]` (Documento único por usuario)
Contiene el estado de la sesión activa.

| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| `activeTaskId` | String | Tarea que se está trabajando actualmente. |
| `mode` | String | "focus", "break" o "longBreak". |
| `paused` | Boolean | Indica si el cronómetro está detenido. |

### Subcolección: `sessions`
Historial individual para analíticas y gráficas del Home.

| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| `taskId` | String | Tarea asociada a la sesión. |
| `duration` | Double | Duración real en minutos. |
| `completed` | Boolean | Indica si el ciclo se terminó sin interrupciones. |
| `endedAt` | Timestamp | Fecha y hora de finalización. |

---

## Relaciones Clave
1.  **Cursos como Ancla**: Un `courseId` vincula tanto una `task` como una `flashcard` a una materia específica, permitiendo filtrar toda la actividad del estudiante por asignatura.
2.  **Tareas y Pomodoro**: El `taskId` permite rastrear exactamente en qué se invirtió el tiempo de estudio.
3.  **Analíticas**: La subcolección `sessions` y el campo `completed` en `tasks` son la fuente de datos para las gráficas de progreso en la pantalla de inicio.

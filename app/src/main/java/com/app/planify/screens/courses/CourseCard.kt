package com.app.planify.screens.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.planify.api.models.Course
import com.app.planify.components.PlCard
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun CoursesList(
    courses: List<Course>,
    onCourseClick: (String) -> Unit,
    onEditCourse: (Course) -> Unit,
    onDeleteCourse: (Course) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(PlSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(PlSpacing.md)
    ) {
        items(courses) { course ->
            CourseCard(
                course = course,
                onClick = { onCourseClick(course.id) },
                onEdit = { onEditCourse(course) },
                onDelete = { onDeleteCourse(course) }
            )
        }
    }
}

@Composable
fun CourseCard(
    course: Course,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val courseColor = Color(android.graphics.Color.parseColor(course.color))

    PlCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(PlSpacing.sm)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(courseColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Style,
                    contentDescription = null,
                    tint = courseColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(PlSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.name,
                    style = PlTypography.titleMedium,
                    color = PlColors.TextMain,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ver flashcards",
                    style = PlTypography.labelMedium,
                    color = PlColors.TextHint
                )
            }
            var menuExpanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "Opciones",
                        tint = PlColors.TextHint
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Editar") },
                        leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                        onClick = {
                            menuExpanded = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Eliminar", color = PlColors.Error) },
                        leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = PlColors.Error) },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

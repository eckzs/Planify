package com.app.planify.screens.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    onCourseClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(PlSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(PlSpacing.md)
    ) {
        items(courses) { course ->
            CourseCard(course = course, onClick = { onCourseClick(course.id) })
        }
    }
}

@Composable
fun CourseCard(course: Course, onClick: () -> Unit) {
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
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(course.color)))
            )
            Spacer(Modifier.width(PlSpacing.md))
            Text(
                text = course.name,
                style = PlTypography.titleMedium,
                color = PlColors.TextMain,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

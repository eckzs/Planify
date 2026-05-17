package com.app.planify.screens.tasks

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DateSelector(
    selectedDate: LocalDate,
    dates: List<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    val initialIndex = remember(dates) {
        val index = dates.indexOf(LocalDate.now())
        if (index != -1) index else 0
    }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    LaunchedEffect(initialIndex) {
        if (initialIndex > 0) {
            listState.scrollToItem(initialIndex)
        }
    }

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = PlSpacing.lg),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = dates,
            key = { it.toString() }
        ) { date ->
            DateItem(
                date = date,
                isSelected = date == selectedDate,
                isToday = date == LocalDate.now(),
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
private fun DateItem(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val dayName = date.dayOfWeek
        .getDisplayName(TextStyle.NARROW, Locale("es"))
        .uppercase()
    val dayNumber = date.dayOfMonth.toString()

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) PlColors.Primary else PlColors.Surface,
        animationSpec = tween(200),
        label = "dateBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) PlColors.OnPrimary else PlColors.TextMain,
        animationSpec = tween(200),
        label = "dateText"
    )
    val labelColor by animateColorAsState(
        targetValue = if (isSelected) PlColors.OnPrimary.copy(alpha = 0.7f) else PlColors.TextHint,
        animationSpec = tween(200),
        label = "dateLabel"
    )

    Column(
        modifier = Modifier
            .width(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = dayName,
            style = PlTypography.labelSmall,
            color = labelColor,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = dayNumber,
            style = PlTypography.titleMedium,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
        if (isToday && !isSelected) {
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(PlColors.Primary)
            )
        } else {
            Spacer(Modifier.height(8.dp))
        }
    }
}

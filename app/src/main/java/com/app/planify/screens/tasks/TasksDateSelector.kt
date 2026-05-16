package com.app.planify.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
        horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)
    ) {
        items(
            items = dates,
            key = { it.toString() }
        ) { date ->
            DateItem(
                date = date,
                isSelected = date == selectedDate,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
fun DateItem(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es")).uppercase()
    val dayNumber = date.dayOfMonth.toString()

    Column(
        modifier = Modifier
            .width(60.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PlColors.Primary else PlColors.Surface)
            .clickable { onClick() }
            .padding(vertical = PlSpacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = dayName,
            style = PlTypography.labelSmall,
            color = if (isSelected) PlColors.OnPrimary else PlColors.TextHint,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(PlSpacing.xs))
        Text(
            text = dayNumber,
            style = PlTypography.titleMedium,
            color = if (isSelected) PlColors.OnPrimary else PlColors.TextMain,
            fontWeight = FontWeight.Bold
        )
    }
}

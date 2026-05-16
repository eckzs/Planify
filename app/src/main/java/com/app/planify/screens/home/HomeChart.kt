package com.app.planify.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.planify.components.PlCard
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun ChartCard(
    title: String,
    data: List<DailyMetric>,
    modifier: Modifier = Modifier
) {
    PlCard(modifier = modifier) {
        Column(modifier = Modifier.padding(PlSpacing.sm)) {
            Text(
                text = title,
                style = PlTypography.labelSmall,
                color = PlColors.TextHint,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(PlSpacing.sm))

            SimpleBarChart(
                data = data,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
        }
    }
}

@Composable
fun SimpleBarChart(
    data: List<DailyMetric>,
    modifier: Modifier = Modifier
) {
    val primaryColor = PlColors.Primary
    val textHintColor = PlColors.TextHint

    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas

        val maxCount = data.maxOf { it.count }.coerceAtLeast(1)
        val barWidth = size.width / (data.size * 1.5f)
        val spaceBetween = (size.width - (barWidth * data.size)) / (data.size + 1)

        data.forEachIndexed { index, metric ->
            val barHeight = (metric.count.toFloat() / maxCount.toFloat()) * (size.height * 0.7f)
            val x = spaceBetween + index * (barWidth + spaceBetween)
            val y = size.height - barHeight - 20.dp.toPx()

            // Draw Bar
            drawRect(
                color = primaryColor.copy(alpha = if (metric.count > 0) 1f else 0.1f),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )

            // Draw Date (Day/Month)
            drawContext.canvas.nativeCanvas.drawText(
                metric.date,
                x + barWidth / 2,
                size.height - 5.dp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 8.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )

            // Draw Count
            if (metric.count > 0) {
                drawContext.canvas.nativeCanvas.drawText(
                    metric.count.toString(),
                    x + barWidth / 2,
                    y - 5.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 8.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

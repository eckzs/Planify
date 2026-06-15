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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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

            SmoothLineChart(
                data = data,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }
    }
}

@Composable
fun SmoothLineChart(
    data: List<DailyMetric>,
    modifier: Modifier = Modifier
) {
    val lineColor = PlColors.Primary
    val hintColor = PlColors.TextHint

    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas

        val paddingTop = 16.dp.toPx()
        val paddingBottom = 24.dp.toPx()
        val chartHeight = size.height - paddingTop - paddingBottom
        val chartWidth = size.width

        val maxCount = data.maxOf { it.count }.coerceAtLeast(1)
        val points = data.mapIndexed { index, metric ->
            val x = if (data.size == 1) chartWidth / 2
                    else index * (chartWidth / (data.size - 1).toFloat())
            val y = paddingTop + chartHeight - (metric.count.toFloat() / maxCount * chartHeight)
            Offset(x, y)
        }

        // Gradient fill under the curve
        val fillPath = Path().apply {
            moveTo(points.first().x, paddingTop + chartHeight)
            lineTo(points.first().x, points.first().y)

            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val cx = (p0.x + p1.x) / 2f
                cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
            }

            lineTo(points.last().x, paddingTop + chartHeight)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    lineColor.copy(alpha = 0.25f),
                    lineColor.copy(alpha = 0.0f)
                ),
                startY = paddingTop,
                endY = paddingTop + chartHeight
            )
        )

        // Smooth line
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val cx = (p0.x + p1.x) / 2f
                cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
            }
        }

        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Dots at data points
        points.forEachIndexed { index, point ->
            if (data[index].count > 0) {
                drawCircle(
                    color = lineColor,
                    radius = 3.5.dp.toPx(),
                    center = point
                )
            }
        }

        // Date labels
        data.forEachIndexed { index, metric ->
            val x = if (data.size == 1) chartWidth / 2
                    else index * (chartWidth / (data.size - 1).toFloat())

            drawContext.canvas.nativeCanvas.drawText(
                metric.date,
                x,
                size.height - 4.dp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(
                        (hintColor.alpha * 255).toInt(),
                        (hintColor.red * 255).toInt(),
                        (hintColor.green * 255).toInt(),
                        (hintColor.blue * 255).toInt()
                    )
                    textSize = 9.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
            )
        }
    }
}

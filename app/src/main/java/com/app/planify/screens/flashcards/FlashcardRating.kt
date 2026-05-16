package com.app.planify.screens.flashcards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.planify.ui.theme.PlColors
import com.app.planify.ui.theme.PlSpacing
import com.app.planify.ui.theme.PlTypography

@Composable
fun RatingButtons(onResult: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PlSpacing.sm)
    ) {
        Button(
            onClick = { onResult(1) },
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PlColors.Error),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Otra vez", style = PlTypography.labelMedium, fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = { onResult(3) },
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PlColors.Container),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Difícil",
                style = PlTypography.labelMedium,
                color = PlColors.TextMain,
                fontWeight = FontWeight.Bold
            )
        }
        Button(
            onClick = { onResult(4) },
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PlColors.Primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Bien", style = PlTypography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SignalGreen
import com.example.ui.theme.SignalRed
import com.example.ui.theme.SignalYellow

@Composable
fun SignalStrengthIndicator(
    strength: Int, // 1 to 4
    modifier: Modifier = Modifier,
    barWidth: Dp = 2.5.dp,
    maxBarHeight: Dp = 10.dp
) {
    val activeColor = when {
        strength >= 4 -> SignalGreen
        strength >= 3 -> SignalYellow
        else -> SignalRed
    }
    val inactiveColor = Color.Gray.copy(alpha = 0.35f)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(1.5.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        for (i in 1..4) {
            val heightFraction = i / 4f
            val isFilled = i <= strength
            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(maxBarHeight * heightFraction)
                    .clip(RoundedCornerShape(1.dp))
                    .background(if (isFilled) activeColor else inactiveColor)
            )
        }
    }
}

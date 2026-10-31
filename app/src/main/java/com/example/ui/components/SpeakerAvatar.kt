package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.User
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.MutedMicRed
import com.example.ui.theme.SpeakerActiveGlow

@Composable
fun SpeakerAvatar(
    user: User,
    isSpeaking: Boolean,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 68.dp,
    showMuteBadge: Boolean = true,
    showSignalBadge: Boolean = true,
    showModeratorBadge: Boolean = true,
    amplitude: Float = 0f,
    onClick: (() -> Unit)? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
    ) {
        // Subtle glow ring around profile circle to know who is speaking (no heavy animated scaling)
        val ringColor = when {
            isSpeaking -> SpeakerActiveGlow
            user.isHost -> AmberGoldPrimary.copy(alpha = 0.65f)
            else -> Color(0xFF3E342B)
        }
        val ringWidth = if (isSpeaking) 2.5.dp else 1.5.dp

        val initials = user.name.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .uppercase()

        // Avatar Core
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .background(Color(user.avatarColorHex))
                .border(ringWidth, ringColor, CircleShape)
        ) {
            Text(
                text = initials.ifBlank { "CA" },
                fontSize = (avatarSize.value * 0.36f).sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Host / Moderator star badge (Top Left)
        if (showModeratorBadge && (user.isHost || user.isModerator)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(AmberGoldPrimary)
                    .border(1.dp, Color(0xFF1E1405), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Host / Moderator",
                    tint = Color(0xFF1E1405),
                    modifier = Modifier.size(11.dp)
                )
            }
        }

        // Raised Hand badge (Top Right)
        if (user.hasHandRaised) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(SpeakerActiveGlow)
                    .border(1.dp, Color.Black, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.PanTool,
                    contentDescription = "Hand Raised",
                    tint = Color.Black,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        // Microphone status pill (Bottom Right)
        if (showMuteBadge) {
            val micBg = if (user.isMuted) MutedMicRed else if (isSpeaking) AmberGoldPrimary else Color(0xFF2E2721)
            val micTint = if (user.isMuted) Color.White else if (isSpeaking) Color(0xFF1E1405) else Color.LightGray
            val icon = if (user.isMuted) Icons.Default.MicOff else Icons.Default.Mic

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(19.dp)
                    .clip(CircleShape)
                    .background(micBg)
                    .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = if (user.isMuted) "Muted" else "Speaking",
                    tint = micTint,
                    modifier = Modifier.size(11.dp)
                )
            }
        }

        // Real-time signal strength indicator badge (Bottom Left)
        if (showSignalBadge) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .clip(CircleShape)
                    .background(Color(0xFF1E1915).copy(alpha = 0.9f))
                    .padding(horizontal = 3.dp, vertical = 2.dp)
            ) {
                SignalStrengthIndicator(
                    strength = user.signalStrength,
                    barWidth = 2.dp,
                    maxBarHeight = 7.dp
                )
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioRoom
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.MutedMicRed
import com.example.ui.theme.OnDarkTextPrimary
import com.example.ui.theme.OnDarkTextSecondary
import com.example.ui.theme.SpeakerActiveGlow

@Composable
fun MinimizedRoomBar(
    room: AudioRoom,
    isMicrophoneMuted: Boolean,
    isUserSpeaking: Boolean,
    onExpand: () -> Unit,
    onToggleMute: () -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF26211C),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberGoldPrimary.copy(alpha = 0.4f)),
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(onClick = onExpand)
            .testTag("minimized_room_dock")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Pulsing live indicator and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(SpeakerActiveGlow.copy(alpha = if (isUserSpeaking) 0.5f else 0.2f))
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isUserSpeaking) SpeakerActiveGlow else AmberGoldPrimary)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = room.houseBadge, fontSize = 11.sp)
                        Text(
                            text = room.houseName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberGoldPrimary
                        )
                    }
                    Text(
                        text = room.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnDarkTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Quick controls: Mute & Leave
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isMicrophoneMuted) Color(0xFF332018) else AmberGoldPrimary)
                ) {
                    Icon(
                        imageVector = if (isMicrophoneMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute Toggle",
                        tint = if (isMicrophoneMuted) MutedMicRed else Color(0xFF1E1405),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onLeave,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF332018))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Leave",
                        tint = MutedMicRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

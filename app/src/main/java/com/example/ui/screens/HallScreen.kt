package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.model.HouseClub
import com.example.model.ScheduledStage
import com.example.model.User
import com.example.ui.components.SpeakerAvatar
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.MutedMicRed
import com.example.ui.theme.OnDarkTextPrimary
import com.example.ui.theme.OnDarkTextSecondary

@Composable
fun HallScreen(
    currentUser: User,
    liveRooms: List<AudioRoom>,
    scheduledStages: List<ScheduledStage>,
    houses: List<HouseClub>,
    onJoinRoom: (AudioRoom) -> Unit,
    onStartRoomClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onToggleReminder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Welcome & Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Live Hall",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Join spontaneous drop-in audio stages",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onStartRoomClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberGoldPrimary,
                                contentColor = Color(0xFF1E1405)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("hall_top_start_stage_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Start Stage", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        IconButton(
                            onClick = onScheduleClick,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .testTag("hall_schedule_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Scheduled Rooms",
                                tint = AmberGoldPrimary
                            )
                        }
                    }
                }
            }

            // Scheduled Stages Carousel (Upcoming)
            if (scheduledStages.isNotEmpty()) {
                item {
                    Text(
                        text = "UPCOMING SCHEDULED STAGES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGoldPrimary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(scheduledStages, key = { it.id }) { stage ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .width(260.dp)
                                    .clickable { onToggleReminder(stage.id) }
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "${stage.dateFormatted} • ${stage.timeFormatted}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AmberGoldPrimary
                                        )

                                        IconButton(
                                            onClick = { onToggleReminder(stage.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (stage.hasReminderSet) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                                contentDescription = "Toggle reminder",
                                                tint = if (stage.hasReminderSet) AmberGoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = stage.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "Hosted by ${stage.host.name} • ${stage.houseName}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Featured Houses / Clubs
            item {
                Text(
                    text = "ACTIVE HOUSES & CLUBS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGoldPrimary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(houses, key = { it.id }) { house ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                            modifier = Modifier.width(180.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(AmberGoldPrimary.copy(alpha = 0.15f))
                                ) {
                                    Text(text = house.badge, fontSize = 18.sp)
                                }

                                Column {
                                    Text(
                                        text = house.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${house.memberCount / 1000}k members",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Active Live Rooms Feed
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "HAPPENING NOW (${liveRooms.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGoldPrimary,
                        letterSpacing = 1.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MutedMicRed)
                        )
                        Text(
                            text = "LIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedMicRed
                        )
                    }
                }
            }

            items(liveRooms, key = { it.id }) { room ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onJoinRoom(room) }
                        .testTag("room_card_${room.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // House header and tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = room.houseBadge, fontSize = 13.sp)
                                Text(
                                    text = room.houseName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGoldPrimary
                                )
                            }

                            if (room.isRecording) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MutedMicRed.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "🔴 REC", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedMicRed)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Room Title
                        Text(
                            text = room.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Speaker Row preview
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Stacked avatars
                            Row(horizontalArrangement = Arrangement.spacedBy((-10).dp)) {
                                room.speakers.take(3).forEach { speaker ->
                                    SpeakerAvatar(
                                        user = speaker,
                                        isSpeaking = false,
                                        avatarSize = 36.dp,
                                        showMuteBadge = false,
                                        showSignalBadge = false,
                                        showModeratorBadge = false
                                    )
                                }
                            }

                            // Speaker names preview
                            Column(modifier = Modifier.weight(1f)) {
                                room.speakers.take(3).forEach { spk ->
                                    Text(
                                        text = "${spk.name} 💬",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Bottom metrics & Join button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "👥 ${room.memberCount}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "🎙️ ${room.speakers.size} on stage",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = { onJoinRoom(room) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AmberGoldPrimary,
                                    contentColor = Color(0xFF1E1405)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(text = "Join Stage", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button: Start a room
        FloatingActionButton(
            onClick = onStartRoomClick,
            containerColor = AmberGoldPrimary,
            contentColor = Color(0xFF1E1405),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .testTag("start_room_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Text(text = "Start a room", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

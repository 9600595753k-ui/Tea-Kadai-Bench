package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioRoom
import com.example.model.ChatSegment
import com.example.model.User
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveAudioStageView(
    room: AudioRoom,
    currentUser: User,
    isMicrophoneMuted: Boolean,
    isUserSpeaking: Boolean,
    activeSpeakerId: String?,
    micAmplitude: Float,
    isRecording: Boolean,
    stageReactions: List<String>,
    onToggleMute: () -> Unit,
    onRaiseHand: () -> Unit,
    onToggleRecording: () -> Unit,
    onOpenModeratorMenu: (User) -> Unit,
    onSendReaction: (String) -> Unit,
    onShareRoom: () -> Unit,
    onMinimize: () -> Unit,
    onLeaveQuietly: () -> Unit,
    modifier: Modifier = Modifier,
    // Integrated Chat sheet controls
    onOpenChatSegment: (ChatSegment) -> Unit
) {
    var showChatSheet by remember { mutableStateOf(false) }
    var showEmojiBar by remember { mutableStateOf(false) }

    val reactionEmojis = listOf("🔥", "👏", "❤️", "💡", "😂", "🎉", "✨", "💯")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1B1612),
                        Color(0xFF13110E),
                        Color(0xFF0F0D0C)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Stage Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onMinimize,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF26211C))
                        .testTag("minimize_room_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimize Room",
                        tint = OnDarkTextPrimary
                    )
                }

                // House Badge and Title summary
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF26211C))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(text = room.houseBadge, fontSize = 14.sp)
                    Text(
                        text = room.houseName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AmberGoldPrimary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Social Share Button
                    IconButton(
                        onClick = onShareRoom,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF26211C))
                            .testTag("share_room_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Invite Friends to Room",
                            tint = AmberGoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Leave Quietly
                    Button(
                        onClick = onLeaveQuietly,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF332018),
                            contentColor = MutedMicRed
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("leave_room_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Leave",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Leave quietly", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Room Title & Tags Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = room.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnDarkTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Recording status badge
                    val isRec = room.isRecording || isRecording
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isRec) MutedMicRed.copy(alpha = 0.2f) else Color(0xFF26211C))
                            .clickable { onToggleRecording() }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isRec) MutedMicRed else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isRec) "REC ON" else "Rec Off",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRec) MutedMicRed else Color.Gray
                        )
                    }

                    // Room Topic Tags
                    room.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF231E18))
                                .border(1.dp, Color(0xFF3B3126), RoundedCornerShape(8.dp))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(text = "#$tag", fontSize = 10.sp, color = AmberGoldPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "🎧 ${room.memberCount} listening",
                        fontSize = 11.sp,
                        color = OnDarkTextSecondary
                    )
                }

                // Live mic waveform visualizer bar (Low latency audio feedback)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E1914))
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = if (isMicrophoneMuted) "Mic Muted" else "HD Audio",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMicrophoneMuted) MutedMicRed else SignalGreen
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    for (bar in 1..24) {
                        val baseHeight = if (isMicrophoneMuted) 2f else {
                            val seed = ((bar * 7) % 11) / 10f
                            val factor = if (isUserSpeaking) (micAmplitude * 12f).coerceAtLeast(4f) else 3f
                            (seed * factor).coerceIn(2f, 14f)
                        }
                        Box(
                            modifier = Modifier
                                .width(2.5.dp)
                                .height(baseHeight.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(
                                    if (isMicrophoneMuted) Color.Gray.copy(alpha = 0.3f)
                                    else if (isUserSpeaking) SpeakerActiveGlow
                                    else AmberGoldPrimary.copy(alpha = 0.6f)
                                )
                        )
                    }
                }
            }

            Divider(
                color = Color(0xFF2E2721),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 8.dp)
            )

            val isCurrentUserOnStage = room.speakers.any { it.id == currentUser.id } || currentUser.isHost || currentUser.isModerator

            // Stage Grid: Speakers & Audience
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Section: Speakers on Stage
                item(span = { GridItemSpan(3) }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "STAGE SPEAKERS (${room.speakers.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberGoldPrimary,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = if (isCurrentUserOnStage) "Stage Controls Available" else "Stage in session",
                            fontSize = 10.sp,
                            color = if (isCurrentUserOnStage) AmberGoldPrimary else OnDarkTextSecondary
                        )
                    }
                }

                items(room.speakers, key = { "speaker_${it.id}" }) { speaker ->
                    val isTalking = if (speaker.id == "me") isUserSpeaking
                    else activeSpeakerId == speaker.id

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SpeakerAvatar(
                            user = speaker,
                            isSpeaking = isTalking,
                            avatarSize = 72.dp,
                            showMuteBadge = true,
                            showSignalBadge = true,
                            showModeratorBadge = true,
                            amplitude = if (speaker.id == "me") micAmplitude else 0f,
                            onClick = {
                                onOpenModeratorMenu(speaker)
                            }
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (speaker.id == "me") "You" else speaker.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isTalking) SpeakerActiveGlow else OnDarkTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = if (speaker.isHost) "Host" else if (speaker.isModerator) "Moderator" else speaker.handle,
                            fontSize = 10.sp,
                            color = if (speaker.isHost || speaker.isModerator) AmberGoldPrimary else OnDarkTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Section: Audience / Others in Room
                item(span = { GridItemSpan(3) }) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text(
                            text = "OTHERS IN THE ROOM (${room.audience.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnDarkTextSecondary,
                            letterSpacing = 1.sp
                        )
                    }
                }

                items(room.audience, key = { "aud_${it.id}" }) { listener ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SpeakerAvatar(
                            user = listener,
                            isSpeaking = false,
                            avatarSize = 58.dp,
                            showMuteBadge = false,
                            showSignalBadge = true,
                            showModeratorBadge = false,
                            onClick = {
                                onOpenModeratorMenu(listener)
                            }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (listener.id == "me") "You" else listener.name,
                            fontSize = 11.sp,
                            color = OnDarkTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )

                        if (listener.hasHandRaised) {
                            Text(
                                text = "✋ Hand Raised",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberGoldPrimary
                            )
                        }
                    }
                }
            }

            // Floating Emoji reaction bar (expandable)
            AnimatedVisibility(
                visible = showEmojiBar,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF26211C))
                        .border(1.dp, AmberGoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    reactionEmojis.forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 22.sp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    onSendReaction(emoji)
                                }
                                .padding(4.dp)
                        )
                    }
                }
            }

            // Bottom Stage Controls Bar
            Surface(
                color = Color(0xFF1B1612),
                tonalElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2721)),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Microphone Mute / Unmute Button
                    val micBg = if (isMicrophoneMuted) Color(0xFF332018) else AmberGoldPrimary
                    val micTint = if (isMicrophoneMuted) MutedMicRed else Color(0xFF1E1405)

                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(micBg)
                            .border(
                                2.dp,
                                if (!isMicrophoneMuted && isUserSpeaking) SpeakerActiveGlow else Color.Transparent,
                                CircleShape
                            )
                            .testTag("stage_mic_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isMicrophoneMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = if (isMicrophoneMuted) "Unmute" else "Mute",
                            tint = micTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Raise Hand button
                    val hasHandRaised = currentUser.hasHandRaised
                    IconButton(
                        onClick = onRaiseHand,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (hasHandRaised) SpeakerActiveGlow else Color(0xFF26211C))
                            .border(1.dp, if (hasHandRaised) Color.White else Color(0xFF3D3328), CircleShape)
                            .testTag("stage_raise_hand_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PanTool,
                            contentDescription = "Raise Hand",
                            tint = if (hasHandRaised) Color.Black else OnDarkTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Segmented Topic Chat Button
                    IconButton(
                        onClick = {
                            onOpenChatSegment(room.activeChatSegment)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF26211C))
                            .border(1.dp, Color(0xFF3D3328), CircleShape)
                            .testTag("stage_chat_toggle_button")
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = AmberGoldPrimary,
                                    contentColor = Color(0xFF1E1405)
                                ) {
                                    Text("4")
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Topic Chatboxes",
                                tint = OnDarkTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Emoji / Sticker Bursts Trigger
                    IconButton(
                        onClick = { showEmojiBar = !showEmojiBar },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (showEmojiBar) AmberGoldPrimary else Color(0xFF26211C))
                            .border(1.dp, Color(0xFF3D3328), CircleShape)
                            .testTag("stage_reactions_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mood,
                            contentDescription = "Reactions & Stickers",
                            tint = if (showEmojiBar) Color(0xFF1E1405) else AmberGoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // Live Floating Reaction Bursts across the stage
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            stageReactions.takeLast(4).forEach { emoji ->
                Text(
                    text = emoji,
                    fontSize = 32.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF1B1612).copy(alpha = 0.7f))
                        .padding(6.dp)
                )
            }
        }
    }
}

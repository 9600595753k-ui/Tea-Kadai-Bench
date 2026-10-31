package com.example.ui.components

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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.ChatSegment
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.MutedMicRed
import com.example.ui.theme.SpeakerActiveGlow
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedChatView(
    activeSegment: ChatSegment,
    onSegmentSelected: (ChatSegment) -> Unit,
    messages: List<ChatMessage>,
    onSendMessage: (String, String?, ChatSegment) -> Unit,
    onAddReaction: (String, String) -> Unit,
    activeSpeakerId: String?,
    modifier: Modifier = Modifier
) {
    var messageInput by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var showPhotoPickerSimulation by remember { mutableStateOf(false) }

    val quickEmojis = listOf("🔥", "👏", "💡", "❤️", "🎯", "👋", "✨")

    val filteredMessages = remember(messages, activeSegment, searchQuery) {
        messages.filter { it.segment == activeSegment }
            .filter {
                if (searchQuery.isBlank()) true
                else it.text.contains(searchQuery, ignoreCase = true) ||
                        it.sender.name.contains(searchQuery, ignoreCase = true)
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Chat Segment Tabs
        ScrollableTabRow(
            selectedTabIndex = activeSegment.ordinal,
            edgePadding = 12.dp,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface,
            indicator = {},
            divider = {}
        ) {
            ChatSegment.values().forEach { segment ->
                val isSelected = segment == activeSegment
                val count = messages.count { it.segment == segment }

                Tab(
                    selected = isSelected,
                    onClick = { onSegmentSelected(segment) },
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) AmberGoldPrimary else MaterialTheme.colorScheme.surface
                        )
                        .border(
                            1.dp,
                            if (isSelected) AmberGlow else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = segment.displayName,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF1E1405) else MaterialTheme.colorScheme.onSurface
                        )
                        if (count > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) Color(0xFF1E1405).copy(alpha = 0.2f)
                                        else AmberGoldPrimary.copy(alpha = 0.2f)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = count.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF1E1405) else AmberGoldPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Subheader with segment description and search box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = activeSegment.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Encrypted in room",
                    tint = AmberGoldPrimary,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "Encrypted",
                    fontSize = 10.sp,
                    color = AmberGoldPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Search bar within topic chat
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search in ${activeSegment.displayName}...", fontSize = 12.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search messages",
                    modifier = Modifier.size(16.dp)
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AmberGoldPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )
        )

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            reverseLayout = false
        ) {
            if (filteredMessages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) "No messages yet in ${activeSegment.displayName}. Be the first to share!"
                            else "No messages matched '$searchQuery'",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredMessages, key = { it.id }) { msg ->
                    val isSpeaker = activeSpeakerId == msg.sender.id || msg.sender.isSpeaking
                    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(msg.timestamp))

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSpeaker) SpeakerActiveGlow.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Header: Sender Avatar, Speaker/Mute Status Pill, and Timestamp
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Profile photo with near mute/speaking option
                                Box(contentAlignment = Alignment.Center) {
                                    val initials = msg.sender.name.take(2).uppercase()
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(msg.sender.avatarColorHex))
                                    ) {
                                        Text(
                                            text = initials,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = msg.sender.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        // Status badge near photo: Speaking glow or Mute pill
                                        val isMuted = msg.sender.isMuted
                                        val statusBg = when {
                                            isSpeaker -> AmberGoldPrimary.copy(alpha = 0.2f)
                                            isMuted -> MutedMicRed.copy(alpha = 0.15f)
                                            else -> Color.Gray.copy(alpha = 0.15f)
                                        }
                                        val statusColor = when {
                                            isSpeaker -> SpeakerActiveGlow
                                            isMuted -> MutedMicRed
                                            else -> Color.Gray
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(statusBg)
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                                contentDescription = if (isMuted) "Muted" else "Live Audio",
                                                tint = statusColor,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = if (isSpeaker) "Speaking" else if (isMuted) "Muted" else "Listener",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = statusColor
                                            )
                                        }
                                    }

                                    Text(
                                        text = msg.sender.handle,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = timeFormat,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Message text
                            Text(
                                text = msg.text,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Photo preview if attached (photo sharing only, no video)
                            if (msg.photoResName != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF2E261E), Color(0xFF42372B))
                                            )
                                        )
                                        .border(1.dp, AmberGoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddPhotoAlternate,
                                            contentDescription = "Shared Photo",
                                            tint = AmberGoldPrimary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Text(
                                            text = "📷 High-Res Shared Photo (No Video)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = AmberGlow
                                        )
                                    }
                                }
                            }

                            // Reactions Bar
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                msg.reactions.forEach { (emoji, count) ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .clickable { onAddReaction(msg.id, emoji) }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "$emoji $count", fontSize = 11.sp)
                                    }
                                }

                                // Quick react button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF2E261E))
                                        .clickable { onAddReaction(msg.id, "❤️") }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "+ ❤️", fontSize = 10.sp, color = AmberGoldPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Emoji reactions strip
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickEmojis) { emoji ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            messageInput += emoji
                        }
                ) {
                    Text(text = emoji, fontSize = 16.sp)
                }
            }
        }

        // Input controls (Text, Photo attach, Send)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp, top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Photo attach button (Photo sharing only, not video)
            IconButton(
                onClick = {
                    showPhotoPickerSimulation = true
                    onSendMessage("Shared photo diagram 📸", "attached_photo_reference", activeSegment)
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("attach_photo_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Share Photo (No Video)",
                    tint = AmberGoldPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            OutlinedTextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                placeholder = { Text("Post in ${activeSegment.displayName}...", fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("chat_input_field"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberGoldPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true
            )

            IconButton(
                onClick = {
                    if (messageInput.isNotBlank()) {
                        onSendMessage(messageInput.trim(), null, activeSegment)
                        messageInput = ""
                    }
                },
                enabled = messageInput.isNotBlank(),
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (messageInput.isNotBlank()) AmberGoldPrimary else Color.Gray.copy(alpha = 0.3f))
                    .testTag("send_chat_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (messageInput.isNotBlank()) Color(0xFF1E1405) else Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

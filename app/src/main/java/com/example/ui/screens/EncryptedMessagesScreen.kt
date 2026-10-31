package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DirectChatThread
import com.example.ui.components.SpeakerAvatar
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.AmberGlow
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncryptedMessagesScreen(
    threads: List<DirectChatThread>,
    selectedThread: DirectChatThread?,
    onOpenThread: (DirectChatThread) -> Unit,
    onCloseThread: () -> Unit,
    onSendMessage: (threadId: String, text: String, photoRes: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedThread != null) {
        // Active Chat Conversation View
        ThreadConversationView(
            thread = selectedThread,
            onBack = onCloseThread,
            onSendMessage = { text, photo ->
                onSendMessage(selectedThread.id, text, photo)
            },
            modifier = modifier
        )
    } else {
        // Threads List View
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Encrypted Messages",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "End-to-end encrypted 1-on-1 and group chats",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Security Shield Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AmberGoldPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "E2EE Secured",
                        tint = AmberGoldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "E2EE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGoldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // E2EE Privacy Notice Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberGoldPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = AmberGoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "All direct messages and group chats are zero-knowledge and stored locally on your device with cryptographic signatures.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                items(threads, key = { it.id }) { thread ->
                    val lastMsg = thread.messages.lastOrNull()
                    val title = if (thread.isGroup) thread.groupName ?: "Group Chat" else thread.participant.name

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenThread(thread) }
                            .testTag("chat_thread_${thread.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (thread.isGroup) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(AmberGoldPrimary)
                                ) {
                                    Text(text = "🎙️", fontSize = 20.sp)
                                }
                            } else {
                                SpeakerAvatar(
                                    user = thread.participant,
                                    isSpeaking = false,
                                    avatarSize = 48.dp,
                                    showMuteBadge = false,
                                    showSignalBadge = true,
                                    showModeratorBadge = false
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Encrypted",
                                        tint = AmberGoldPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = lastMsg?.text ?: "No messages yet",
                                    fontSize = 12.sp,
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
    }
}

@Composable
private fun ThreadConversationView(
    thread: DirectChatThread,
    onBack: () -> Unit,
    onSendMessage: (String, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var input by remember { mutableStateOf("") }
    val title = if (thread.isGroup) thread.groupName ?: "Group Chat" else thread.participant.name

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = AmberGoldPrimary,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "End-to-End Encrypted",
                        fontSize = 10.sp,
                        color = AmberGoldPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        // Message bubbles
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(thread.messages, key = { it.id }) { msg ->
                val isMe = msg.senderId == "me"
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(msg.timestamp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isMe) 14.dp else 2.dp,
                            bottomEnd = if (isMe) 2.dp else 14.dp
                        ),
                        color = if (isMe) AmberGoldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = msg.text,
                                fontSize = 13.sp,
                                color = if (isMe) Color(0xFF1E1405) else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = timeFormat,
                                fontSize = 9.sp,
                                color = if (isMe) Color(0xFF1E1405).copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        // Input row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    onSendMessage("📷 Shared Photo Asset (E2EE Protected)", "photo_ref")
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Share Photo",
                    tint = AmberGoldPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Encrypted message...", fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberGoldPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true
            )

            IconButton(
                onClick = {
                    if (input.isNotBlank()) {
                        onSendMessage(input.trim(), null)
                        input = ""
                    }
                },
                enabled = input.isNotBlank(),
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (input.isNotBlank()) AmberGoldPrimary else Color.Gray.copy(alpha = 0.3f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (input.isNotBlank()) Color(0xFF1E1405) else Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

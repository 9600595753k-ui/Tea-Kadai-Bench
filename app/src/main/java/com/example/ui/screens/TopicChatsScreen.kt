package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.AudioRoom
import com.example.model.ChatMessage
import com.example.model.ChatSegment
import com.example.model.User
import com.example.ui.components.SegmentedChatView

@Composable
fun TopicChatsScreen(
    activeRoom: AudioRoom?,
    allMessages: Map<String, List<ChatMessage>>,
    currentUser: User,
    activeSpeakerId: String?,
    onSendMessage: (String, String?, ChatSegment) -> Unit,
    onAddReaction: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSegment by remember { mutableStateOf(ChatSegment.GENERAL) }

    val currentRoomId = activeRoom?.id ?: "room_1"
    val messages = allMessages[currentRoomId] ?: emptyList()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        SegmentedChatView(
            activeSegment = selectedSegment,
            onSegmentSelected = { selectedSegment = it },
            messages = messages,
            onSendMessage = onSendMessage,
            onAddReaction = onAddReaction,
            activeSpeakerId = activeSpeakerId,
            modifier = Modifier.fillMaxSize()
        )
    }
}

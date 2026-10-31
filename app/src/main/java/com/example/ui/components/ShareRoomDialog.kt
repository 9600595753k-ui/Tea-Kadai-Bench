package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioRoom
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.OnDarkTextPrimary
import com.example.ui.theme.OnDarkTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareRoomDialog(
    room: AudioRoom,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val roomLink = "https://clubaudio.app/room/${room.id}"
    val shareText = "🎙️ Join my live audio conversation on ClubAudio: \"${room.title}\" hosted under ${room.houseName}! Listen in live: $roomLink"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1915),
        contentColor = OnDarkTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = AmberGoldPrimary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Invite Friends to Room",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OnDarkTextPrimary
            )

            Text(
                text = room.title,
                fontSize = 13.sp,
                color = AmberGoldPrimary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Social Sharing Quick Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SocialShareTarget(
                    icon = "💬",
                    name = "Messages / SMS",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Invite via SMS"))
                        onDismiss()
                    }
                )
                SocialShareTarget(
                    icon = "🟢",
                    name = "WhatsApp",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            setPackage("com.whatsapp")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val generalIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(generalIntent, "Share with"))
                        }
                        onDismiss()
                    }
                )
                SocialShareTarget(
                    icon = "✈️",
                    name = "Telegram",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share via Telegram"))
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Copy Link Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF26211C),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3E342B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = roomLink,
                        fontSize = 12.sp,
                        color = OnDarkTextSecondary,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("ClubAudio Room Link", roomLink)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Room link copied to clipboard!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberGoldPrimary,
                            contentColor = Color(0xFF1E1405)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("copy_room_link_button")
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SocialShareTarget(
    icon: String,
    name: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF2E2721))
                .border(1.dp, Color(0xFF453B32), CircleShape)
        ) {
            Text(text = icon, fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = name, fontSize = 11.sp, color = OnDarkTextSecondary)
    }
}

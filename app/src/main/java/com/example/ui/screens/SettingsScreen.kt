package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppNotificationPreferences
import com.example.model.NotificationLevel
import com.example.model.User
import com.example.ui.components.SpeakerAvatar
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.SignalGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentUser: User,
    preferences: AppNotificationPreferences,
    onToggleDarkMode: () -> Unit,
    onUpdateNotificationLevel: (NotificationLevel) -> Unit,
    onOpenPhoneVerification: () -> Unit,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Settings & Privacy",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Preferences, contact notification rules, and identity",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Profile & Mobile Number Registration Card
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        SpeakerAvatar(
                            user = currentUser,
                            isSpeaking = false,
                            avatarSize = 56.dp,
                            showMuteBadge = false,
                            showSignalBadge = true,
                            showModeratorBadge = true
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = currentUser.handle,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Mobile Number",
                                    tint = SignalGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = currentUser.phone,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SignalGreen
                                )
                            }
                        }

                        IconButton(
                            onClick = onOpenPhoneVerification,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit phone registration",
                                tint = AmberGoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Identity registered via mobile phone number for authenticated security. Only real members on stage.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Zero Data Collection Privacy Guarantee
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberGoldPrimary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AmberGoldPrimary.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = AmberGoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Zero Data Collection",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberGoldPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ClubAudio does not collect, track, or sell your audio logs, messages, or device telemetry. All encryption keys remain stored locally.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Display & Theme Settings (Dark Mode Toggle)
        item {
            Text(
                text = "APPEARANCE & DISPLAY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AmberGoldPrimary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = AmberGoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "Dark Mode Toggle",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Reduces eye strain during nighttime audio rooms",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = preferences.darkTheme,
                        onCheckedChange = { onToggleDarkMode() },
                        colors = SwitchDefaults.colors(checkedThumbColor = AmberGoldPrimary),
                        modifier = Modifier.testTag("dark_mode_toggle_switch")
                    )
                }
            }
        }

        // Notification Settings & Contact Group Preferences
        item {
            Text(
                text = "NOTIFICATION PREFERENCES FOR CONTACT GROUPS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AmberGoldPrimary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Customize alerts for your individual contact circles (Friends & Family tag):",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    NotificationLevel.values().forEach { level ->
                        val isSelected = preferences.contactGroupRule == level
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUpdateNotificationLevel(level) }
                                .padding(vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onUpdateNotificationLevel(level) },
                                colors = RadioButtonDefaults.colors(selectedColor = AmberGoldPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = level.label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (level) {
                                        NotificationLevel.ALL -> "Receive notifications whenever anyone you follow speaks"
                                        NotificationLevel.HIGHLIGHTS -> "Only get alerts for scheduled stages & big rooms"
                                        NotificationLevel.FRIENDS_FAMILY_ONLY -> "Priority alerts ONLY when Friends & Family go live"
                                        NotificationLevel.MUTED -> "Mute all push notifications"
                                    },
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedButton(
                onClick = onLogout,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDF5353)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("logout_button")
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out / Switch Mobile Number", fontWeight = FontWeight.Bold)
            }
        }
    }
}

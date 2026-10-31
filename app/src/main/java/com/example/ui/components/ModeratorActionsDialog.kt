package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ContactTag
import com.example.model.User
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorActionsDialog(
    targetUser: User,
    isTargetSpeaker: Boolean,
    isStageMember: Boolean, // True ONLY if current viewer is on stage (speaker/host/moderator)
    onDismiss: () -> Unit,
    onMuteSpeaker: (String) -> Unit,
    onUnmuteSpeaker: (String) -> Unit,
    onInviteToStage: (User) -> Unit,
    onDemoteToAudience: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onUpdateContactTag: (String, ContactTag) -> Unit
) {
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
            // Target user profile header
            SpeakerAvatar(
                user = targetUser,
                isSpeaking = false,
                avatarSize = 64.dp,
                showMuteBadge = true,
                showSignalBadge = true,
                showModeratorBadge = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = targetUser.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = OnDarkTextPrimary
            )

            Text(
                text = "${targetUser.handle} • ${targetUser.phone}",
                fontSize = 12.sp,
                color = OnDarkTextSecondary
            )

            if (targetUser.bio.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = targetUser.bio,
                    fontSize = 12.sp,
                    color = OnDarkTextSecondary,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFF2E2721))
            Spacer(modifier = Modifier.height(16.dp))

            // Only stage people have rights to pick others up to stage or moderate
            if (isStageMember) {
                Text(
                    text = "STAGE & MODERATOR CONTROLS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGoldPrimary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (isTargetSpeaker) {
                    // Leader Mute Button (When someone talks uncontrollably)
                    if (!targetUser.isMuted) {
                        Button(
                            onClick = { onMuteSpeaker(targetUser.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MutedMicRed.copy(alpha = 0.2f),
                                contentColor = MutedMicRed
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("moderator_mute_button")
                        ) {
                            Icon(imageVector = Icons.Default.MicOff, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Leader Mute (Stop Uncontrolled Speech)", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { onUnmuteSpeaker(targetUser.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberGoldPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("moderator_unmute_button")
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Allow to Unmute")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { onDemoteToAudience(targetUser.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OnDarkTextSecondary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Move to Audience")
                    }
                } else {
                    // Listener in audience -> Stage people have rights to pick them up to the stage
                    Button(
                        onClick = { onInviteToStage(targetUser) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberGoldPrimary,
                            contentColor = Color(0xFF1E1405)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("invite_to_stage_button")
                    ) {
                        Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pick up to Stage (Speaker)", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFF2E2721))
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Section: Social Follow & Friends and Family Tagging (Accessible to everyone)
            Text(
                text = "FRIENDS & FAMILY CIRCLE TAG",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AmberGoldPrimary,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ContactTag.values().forEach { tag ->
                    val isSelected = targetUser.contactTag == tag
                    FilterChip(
                        selected = isSelected,
                        onClick = { onUpdateContactTag(targetUser.id, tag) },
                        label = { Text(tag.label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberGoldPrimary,
                            selectedLabelColor = Color(0xFF1E1405)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Follow / Unfollow Toggle
            OutlinedButton(
                onClick = { onToggleFollow(targetUser.id) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (targetUser.isFollowing) OnDarkTextSecondary else AmberGoldPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (targetUser.isFollowing) Icons.Default.Check else Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (targetUser.isFollowing) "Following (Custom Alerts Active)" else "Follow User")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

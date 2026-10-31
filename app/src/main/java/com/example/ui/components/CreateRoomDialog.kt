package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HouseClub
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.OnDarkTextPrimary
import com.example.ui.theme.OnDarkTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoomDialog(
    houses: List<HouseClub>,
    onDismiss: () -> Unit,
    onCreateRoom: (title: String, houseName: String, tags: List<String>, isRecording: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedHouse by remember { mutableStateOf(houses.firstOrNull()?.name ?: "Acoustic Lounge") }
    var topicTag by remember { mutableStateOf("Tech") }
    var isRecordingEnabled by remember { mutableStateOf(false) }

    fun submitRoom() {
        val finalTitle = title.ifBlank { "Spontaneous Audio Lounge" }
        onCreateRoom(finalTitle, selectedHouse, listOf(topicTag, "Live"), isRecordingEnabled)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1915),
        contentColor = OnDarkTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Top Header with immediate Start Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = AmberGoldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Host a Live Stage",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnDarkTextPrimary
                    )
                }

                // Immediate header start button
                Button(
                    onClick = { submitRoom() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberGoldPrimary,
                        contentColor = Color(0xFF1E1405)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("top_start_stage_button")
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Start", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Room Title input with keyboard action to submit
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Stage Topic Name") },
                placeholder = { Text("e.g. Acoustic Audio & Tech Lounge") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = { submitRoom() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_room_title_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberGoldPrimary,
                    unfocusedBorderColor = Color(0xFF453B32)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Prominent Full-Width Start Button directly below input
            Button(
                onClick = { submitRoom() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberGoldPrimary,
                    contentColor = Color(0xFF1E1405)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("start_room_confirm_button")
            ) {
                Icon(imageVector = Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Start Stage Now (Go Live)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Select House/Club
            Text(
                text = "Host under Club / House (Optional)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AmberGoldPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                houses.take(3).forEach { house ->
                    val isSelected = selectedHouse == house.name
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedHouse = house.name },
                        label = { Text("${house.badge} ${house.name}", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberGoldPrimary,
                            selectedLabelColor = Color(0xFF1E1405)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Record stage toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Enable Audio Recording",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnDarkTextPrimary
                    )
                    Text(
                        text = "Allows participants to replay audio later",
                        fontSize = 11.sp,
                        color = OnDarkTextSecondary
                    )
                }

                Switch(
                    checked = isRecordingEnabled,
                    onCheckedChange = { isRecordingEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = AmberGoldPrimary)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

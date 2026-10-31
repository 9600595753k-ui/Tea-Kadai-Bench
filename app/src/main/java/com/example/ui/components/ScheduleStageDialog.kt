package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HouseClub
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.OnDarkTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleStageDialog(
    houses: List<HouseClub>,
    onDismiss: () -> Unit,
    onSchedule: (title: String, desc: String, houseName: String, time: String, date: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedHouse by remember { mutableStateOf(houses.firstOrNull()?.name ?: "Acoustic Lounge") }
    var date by remember { mutableStateOf("Tomorrow") }
    var time by remember { mutableStateOf("7:00 PM") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1915),
        contentColor = OnDarkTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = AmberGoldPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Schedule an Upcoming Stage",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnDarkTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Stage Title") },
                placeholder = { Text("e.g. AI & Music Production AMA") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("schedule_title_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description & Agenda") },
                placeholder = { Text("Topics we'll cover, guest speakers...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSchedule(title, description, selectedHouse, time, date)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberGoldPrimary,
                    contentColor = Color(0xFF1E1405)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_schedule_button")
            ) {
                Text(text = "Schedule Event & Notify Followers", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

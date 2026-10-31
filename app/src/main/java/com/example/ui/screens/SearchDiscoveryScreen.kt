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
import androidx.compose.material.icons.filled.*
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
import com.example.model.AudioRoom
import com.example.model.ContactTag
import com.example.model.HouseClub
import com.example.model.User
import com.example.ui.components.SpeakerAvatar
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.OnDarkTextPrimary
import com.example.ui.theme.OnDarkTextSecondary

enum class SearchTab(val label: String) {
    PEOPLE("People"),
    ROOMS("Rooms"),
    HOUSES("Houses & Clubs")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDiscoveryScreen(
    people: List<User>,
    rooms: List<AudioRoom>,
    houses: List<HouseClub>,
    onJoinRoom: (AudioRoom) -> Unit,
    onToggleFollow: (String) -> Unit,
    onUpdateContactTag: (String, ContactTag) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(SearchTab.PEOPLE) }
    var searchQuery by remember { mutableStateOf("") }
    var userToTag by remember { mutableStateOf<User?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Explore & Discover",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search ${selectedTab.label.lowercase()}...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AmberGoldPrimary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_discovery_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AmberGoldPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Segmented Tabs: Separately show People, Rooms, and Houses
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color.Transparent,
            contentColor = AmberGoldPrimary,
            indicator = {},
            divider = {}
        ) {
            SearchTab.values().forEach { tab ->
                val isSelected = tab == selectedTab
                Tab(
                    selected = isSelected,
                    onClick = { selectedTab = tab },
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) AmberGoldPrimary else MaterialTheme.colorScheme.surfaceVariant)
                        .padding(vertical = 8.dp)
                        .testTag("search_tab_${tab.name}")
                ) {
                    Text(
                        text = tab.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF1E1405) else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Content Area by Tab
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            when (selectedTab) {
                SearchTab.PEOPLE -> {
                    val filteredPeople = people.filter {
                        searchQuery.isBlank() ||
                                it.name.contains(searchQuery, ignoreCase = true) ||
                                it.handle.contains(searchQuery, ignoreCase = true) ||
                                it.bio.contains(searchQuery, ignoreCase = true)
                    }

                    if (filteredPeople.isEmpty()) {
                        item {
                            EmptySearchResult("No people found matching '$searchQuery'")
                        }
                    } else {
                        items(filteredPeople, key = { it.id }) { user ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    SpeakerAvatar(
                                        user = user,
                                        isSpeaking = false,
                                        avatarSize = 50.dp,
                                        showMuteBadge = false,
                                        showSignalBadge = true,
                                        showModeratorBadge = false
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = user.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            // Friends & Family tag chip
                                            if (user.contactTag != ContactTag.NONE) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(AmberGoldPrimary.copy(alpha = 0.15f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = user.contactTag.label,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = AmberGoldPrimary
                                                    )
                                                }
                                            }
                                        }

                                        Text(
                                            text = user.handle,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = user.bio,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Follow & Tag actions
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                onToggleFollow(user.id)
                                                // After following, prompt tag if not tagged
                                                if (!user.isFollowing && user.contactTag == ContactTag.NONE) {
                                                    userToTag = user
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (user.isFollowing) MaterialTheme.colorScheme.surface else AmberGoldPrimary,
                                                contentColor = if (user.isFollowing) MaterialTheme.colorScheme.onSurface else Color(0xFF1E1405)
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text(
                                                text = if (user.isFollowing) "Following" else "Follow",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        if (user.isFollowing) {
                                            Text(
                                                text = "Tag: ${user.contactTag.label}",
                                                fontSize = 10.sp,
                                                color = AmberGoldPrimary,
                                                modifier = Modifier.clickable { userToTag = user }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                SearchTab.ROOMS -> {
                    val filteredRooms = rooms.filter {
                        searchQuery.isBlank() ||
                                it.title.contains(searchQuery, ignoreCase = true) ||
                                it.houseName.contains(searchQuery, ignoreCase = true) ||
                                it.tags.any { tag -> tag.contains(searchQuery, ignoreCase = true) }
                    }

                    if (filteredRooms.isEmpty()) {
                        item {
                            EmptySearchResult("No active audio rooms found matching '$searchQuery'")
                        }
                    } else {
                        items(filteredRooms, key = { it.id }) { room ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onJoinRoom(room) }
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "${room.houseBadge} ${room.houseName}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AmberGoldPrimary
                                        )
                                        Text(
                                            text = "👥 ${room.memberCount} in room",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = room.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Speakers: ${room.speakers.joinToString(", ") { it.name }}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Button(
                                            onClick = { onJoinRoom(room) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = AmberGoldPrimary,
                                                contentColor = Color(0xFF1E1405)
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text(text = "Join", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                SearchTab.HOUSES -> {
                    val filteredHouses = houses.filter {
                        searchQuery.isBlank() ||
                                it.name.contains(searchQuery, ignoreCase = true) ||
                                it.description.contains(searchQuery, ignoreCase = true) ||
                                it.categories.any { cat -> cat.contains(searchQuery, ignoreCase = true) }
                    }

                    if (filteredHouses.isEmpty()) {
                        item {
                            EmptySearchResult("No houses or clubs found matching '$searchQuery'")
                        }
                    } else {
                        items(filteredHouses, key = { it.id }) { house ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(AmberGoldPrimary.copy(alpha = 0.15f))
                                        ) {
                                            Text(text = house.badge, fontSize = 22.sp)
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = house.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${house.memberCount} members • ${house.followerCount} followers",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Button(
                                            onClick = {},
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = AmberGoldPrimary,
                                                contentColor = Color(0xFF1E1405)
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text("Join House", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = house.description,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal to set Friends & Family tag after follow
    userToTag?.let { target ->
        AlertDialog(
            onDismissRequest = { userToTag = null },
            title = {
                Text(
                    text = "Assign Contact Tag to ${target.name}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Customize your notification rules: assign Friends & Family tag so you get high-priority alerts when they go live or start speaking.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ContactTag.values().forEach { tag ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateContactTag(target.id, tag)
                                    userToTag = null
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = target.contactTag == tag,
                                onClick = {
                                    onUpdateContactTag(target.id, tag)
                                    userToTag = null
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = tag.label, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { userToTag = null }) {
                    Text("Done", color = AmberGoldPrimary)
                }
            }
        )
    }
}

@Composable
private fun EmptySearchResult(message: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp)
    ) {
        Text(text = message, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

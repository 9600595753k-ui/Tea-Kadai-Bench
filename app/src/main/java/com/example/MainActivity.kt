package com.example

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ChatSegment
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppNavDestination
import com.example.viewmodel.ClubViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ClubViewModel = viewModel()
            val prefs by viewModel.notificationPrefs.collectAsState()

            MyApplicationTheme(darkTheme = prefs.darkTheme) {
                ClubAudioApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ClubAudioApp(viewModel: ClubViewModel) {
    // Record audio permission launcher for real-time mic wave processing
    val recordPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.audioEngine.startCapture()
        }
    }

    LaunchedEffect(Unit) {
        if (!viewModel.audioEngine.hasRecordPermission()) {
            recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // State collections
    val currentNav by viewModel.currentNav.collectAsState()
    val activeRoom by viewModel.activeRoom.collectAsState()
    val isRoomFloatingDock by viewModel.isRoomFloatingDock.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val liveRooms by viewModel.liveRooms.collectAsState()
    val scheduledStages by viewModel.scheduledStages.collectAsState()
    val houses by viewModel.houses.collectAsState()
    val people by viewModel.people.collectAsState()
    val roomMessages by viewModel.roomMessages.collectAsState()
    val directChats by viewModel.directChats.collectAsState()
    val selectedChatThread by viewModel.selectedChatThread.collectAsState()
    val stageReactions by viewModel.stageReactions.collectAsState()
    val notificationPrefs by viewModel.notificationPrefs.collectAsState()

    val micAmplitude by viewModel.micAmplitude.collectAsState()
    val isUserSpeaking by viewModel.isUserSpeaking.collectAsState()
    val activeSpeakerId by viewModel.activeSpeakerId.collectAsState()
    val isMicrophoneMuted by viewModel.isMicrophoneMuted.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()

    val showCreateRoomDialog by viewModel.showCreateRoomDialog.collectAsState()
    val showScheduleDialog by viewModel.showScheduleDialog.collectAsState()
    val selectedModeratorUser by viewModel.selectedModeratorUser.collectAsState()
    val showShareDialog by viewModel.showShareDialog.collectAsState()

    var showAuthPhoneDialog by remember { mutableStateOf(false) }

    // System Back Press handling
    BackHandler(enabled = activeRoom != null && !isRoomFloatingDock) {
        viewModel.minimizeRoom()
    }

    val isUserRegistered by viewModel.isUserRegistered.collectAsState()

    // If first-time or not logged in, display the dedicated Login / Registration screen
    if (!isUserRegistered) {
        AuthScreen(
            onAuthSuccess = { phone, name, handle, bio ->
                viewModel.registerPhone(phone, name, handle, bio)
            }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Only show bottom navigation when not in full-screen audio room
            if (activeRoom == null || isRoomFloatingDock) {
                Column {
                    // Floating minimized room pill if a room is active in background
                    activeRoom?.let { room ->
                        if (isRoomFloatingDock) {
                            MinimizedRoomBar(
                                room = room,
                                isMicrophoneMuted = isMicrophoneMuted,
                                isUserSpeaking = isUserSpeaking,
                                onExpand = { viewModel.expandRoom() },
                                onToggleMute = { viewModel.toggleMute() },
                                onLeave = { viewModel.leaveRoom() }
                            )
                        }
                    }

                    NavigationBar(
                        windowInsets = WindowInsets.navigationBars,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 6.dp
                    ) {
                        NavigationBarItem(
                            selected = currentNav == AppNavDestination.HALL,
                            onClick = { viewModel.selectNav(AppNavDestination.HALL) },
                            icon = { Icon(Icons.Default.Podcasts, contentDescription = "Live Hall") },
                            label = { Text("Hall", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF1E1405),
                                indicatorColor = AmberGoldPrimary
                            ),
                            modifier = Modifier.testTag("nav_hall")
                        )

                        NavigationBarItem(
                            selected = currentNav == AppNavDestination.CHATS,
                            onClick = { viewModel.selectNav(AppNavDestination.CHATS) },
                            icon = {
                                BadgedBox(badge = { Badge { Text("4") } }) {
                                    Icon(Icons.Default.Forum, contentDescription = "Topic Chats")
                                }
                            },
                            label = { Text("Topics", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF1E1405),
                                indicatorColor = AmberGoldPrimary
                            ),
                            modifier = Modifier.testTag("nav_chats")
                        )

                        NavigationBarItem(
                            selected = currentNav == AppNavDestination.SEARCH,
                            onClick = { viewModel.selectNav(AppNavDestination.SEARCH) },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Explore") },
                            label = { Text("Explore", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF1E1405),
                                indicatorColor = AmberGoldPrimary
                            ),
                            modifier = Modifier.testTag("nav_search")
                        )

                        NavigationBarItem(
                            selected = currentNav == AppNavDestination.MESSAGES,
                            onClick = { viewModel.selectNav(AppNavDestination.MESSAGES) },
                            icon = { Icon(Icons.Default.Shield, contentDescription = "Encrypted DMs") },
                            label = { Text("DMs", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF1E1405),
                                indicatorColor = AmberGoldPrimary
                            ),
                            modifier = Modifier.testTag("nav_messages")
                        )

                        NavigationBarItem(
                            selected = currentNav == AppNavDestination.SETTINGS,
                            onClick = { viewModel.selectNav(AppNavDestination.SETTINGS) },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                            label = { Text("Settings", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF1E1405),
                                indicatorColor = AmberGoldPrimary
                            ),
                            modifier = Modifier.testTag("nav_settings")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Navigation Screens
            when (currentNav) {
                AppNavDestination.HALL -> {
                    HallScreen(
                        currentUser = currentUser,
                        liveRooms = liveRooms,
                        scheduledStages = scheduledStages,
                        houses = houses,
                        onJoinRoom = { viewModel.joinRoom(it) },
                        onStartRoomClick = { viewModel.setCreateRoomDialogOpen(true) },
                        onScheduleClick = { viewModel.setScheduleDialogOpen(true) },
                        onToggleReminder = { viewModel.toggleReminder(it) }
                    )
                }

                AppNavDestination.CHATS -> {
                    TopicChatsScreen(
                        activeRoom = activeRoom,
                        allMessages = roomMessages,
                        currentUser = currentUser,
                        activeSpeakerId = activeSpeakerId,
                        onSendMessage = { text, photo, segment ->
                            viewModel.sendRoomMessage(text, photo, segment)
                        },
                        onAddReaction = { msgId, emoji ->
                            viewModel.addReactionToMessage(msgId, emoji)
                        }
                    )
                }

                AppNavDestination.SEARCH -> {
                    SearchDiscoveryScreen(
                        people = people,
                        rooms = liveRooms,
                        houses = houses,
                        onJoinRoom = { viewModel.joinRoom(it) },
                        onToggleFollow = { viewModel.toggleFollow(it) },
                        onUpdateContactTag = { id, tag -> viewModel.updateContactTag(id, tag) }
                    )
                }

                AppNavDestination.MESSAGES -> {
                    EncryptedMessagesScreen(
                        threads = directChats,
                        selectedThread = selectedChatThread,
                        onOpenThread = { viewModel.openChatThread(it) },
                        onCloseThread = { viewModel.closeChatThread() },
                        onSendMessage = { threadId, text, photo ->
                            viewModel.sendDirectMessage(threadId, text, photo)
                        }
                    )
                }

                AppNavDestination.SETTINGS -> {
                    SettingsScreen(
                        currentUser = currentUser,
                        preferences = notificationPrefs,
                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                        onUpdateNotificationLevel = { viewModel.updateNotificationLevel(it) },
                        onOpenPhoneVerification = { showAuthPhoneDialog = true },
                        onLogout = { viewModel.logout() }
                    )
                }
            }

            // Full Screen Live Audio Stage Overlay (when active and not minimized)
            AnimatedVisibility(
                visible = activeRoom != null && !isRoomFloatingDock,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                activeRoom?.let { room ->
                    LiveAudioStageView(
                        room = room,
                        currentUser = currentUser,
                        isMicrophoneMuted = isMicrophoneMuted,
                        isUserSpeaking = isUserSpeaking,
                        activeSpeakerId = activeSpeakerId,
                        micAmplitude = micAmplitude,
                        isRecording = isRecording,
                        stageReactions = stageReactions,
                        onToggleMute = { viewModel.toggleMute() },
                        onRaiseHand = { viewModel.raiseHand() },
                        onToggleRecording = { viewModel.toggleRoomRecording() },
                        onOpenModeratorMenu = { viewModel.openModeratorMenu(it) },
                        onSendReaction = { viewModel.sendStageReaction(it) },
                        onShareRoom = { viewModel.setShareDialogOpen(true) },
                        onMinimize = { viewModel.minimizeRoom() },
                        onLeaveQuietly = { viewModel.leaveRoom() },
                        onOpenChatSegment = { segment ->
                            viewModel.setChatSegment(segment)
                            viewModel.selectNav(AppNavDestination.CHATS)
                            viewModel.minimizeRoom()
                        }
                    )
                }
            }
        }
    }

    // Modal Dialogs
    if (showCreateRoomDialog) {
        CreateRoomDialog(
            houses = houses,
            onDismiss = { viewModel.setCreateRoomDialogOpen(false) },
            onCreateRoom = { title, house, tags, isRec ->
                viewModel.createRoom(title, house, tags, isRec)
            }
        )
    }

    if (showScheduleDialog) {
        ScheduleStageDialog(
            houses = houses,
            onDismiss = { viewModel.setScheduleDialogOpen(false) },
            onSchedule = { title, desc, house, time, date ->
                viewModel.scheduleStage(title, desc, house, time, date)
            }
        )
    }

    if (showShareDialog && activeRoom != null) {
        ShareRoomDialog(
            room = activeRoom!!,
            onDismiss = { viewModel.setShareDialogOpen(false) }
        )
    }

    selectedModeratorUser?.let { target ->
        val isSpeaker = activeRoom?.speakers?.any { it.id == target.id } == true
        val isStageMember = activeRoom?.speakers?.any { it.id == currentUser.id } == true || currentUser.isHost || currentUser.isModerator
        ModeratorActionsDialog(
            targetUser = target,
            isTargetSpeaker = isSpeaker,
            isStageMember = isStageMember,
            onDismiss = { viewModel.closeModeratorMenu() },
            onMuteSpeaker = { viewModel.moderatorMute(it) },
            onUnmuteSpeaker = { viewModel.moderatorUnmute(it) },
            onInviteToStage = { viewModel.inviteToStage(it) },
            onDemoteToAudience = { viewModel.demoteToAudience(it) },
            onToggleFollow = { viewModel.toggleFollow(it) },
            onUpdateContactTag = { id, tag -> viewModel.updateContactTag(id, tag) }
        )
    }

    if (showAuthPhoneDialog) {
        AuthPhoneDialog(
            initialPhone = currentUser.phone,
            initialName = currentUser.name,
            initialHandle = currentUser.handle,
            onDismiss = { showAuthPhoneDialog = false },
            onRegister = { phone, name, handle ->
                viewModel.registerPhone(phone, name, handle)
            }
        )
    }
}

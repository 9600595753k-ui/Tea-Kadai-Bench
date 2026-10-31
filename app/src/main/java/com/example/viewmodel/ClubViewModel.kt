package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioEngine
import com.example.data.ClubRepository
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppNavDestination(val label: String) {
    HALL("Live Hall"),
    CHATS("Topic Chats"),
    SEARCH("Explore"),
    MESSAGES("Encrypted DMs"),
    SETTINGS("Settings")
}

class ClubViewModel(application: Application) : AndroidViewModel(application) {

    val repository = ClubRepository(application)
    val audioEngine = AudioEngine(application)

    // Navigation
    private val _currentNav = MutableStateFlow(AppNavDestination.HALL)
    val currentNav: StateFlow<AppNavDestination> = _currentNav.asStateFlow()

    // Modals
    private val _showCreateRoomDialog = MutableStateFlow(false)
    val showCreateRoomDialog: StateFlow<Boolean> = _showCreateRoomDialog.asStateFlow()

    private val _showScheduleDialog = MutableStateFlow(false)
    val showScheduleDialog: StateFlow<Boolean> = _showScheduleDialog.asStateFlow()

    private val _selectedModeratorUser = MutableStateFlow<User?>(null)
    val selectedModeratorUser: StateFlow<User?> = _selectedModeratorUser.asStateFlow()

    private val _showShareDialog = MutableStateFlow(false)
    val showShareDialog: StateFlow<Boolean> = _showShareDialog.asStateFlow()

    private val _selectedChatThread = MutableStateFlow<DirectChatThread?>(null)
    val selectedChatThread: StateFlow<DirectChatThread?> = _selectedChatThread.asStateFlow()

    // Repositories StateFlow bindings
    val currentUser = repository.currentUser
    val isUserRegistered = repository.isUserRegistered
    val activeRoom = repository.activeRoom
    val isRoomFloatingDock = repository.isRoomFloatingDock
    val liveRooms = repository.liveRooms
    val scheduledStages = repository.scheduledStages
    val houses = repository.houses
    val people = repository.people
    val roomMessages = repository.roomMessages
    val directChats = repository.directChats
    val stageReactions = repository.stageReactions
    val notificationPrefs = repository.notificationPrefs

    // Audio Engine StateFlow bindings
    val micAmplitude = audioEngine.micAmplitude
    val isUserSpeaking = audioEngine.isUserSpeaking
    val activeSpeakerId = audioEngine.activeSpeakerId
    val isMicrophoneMuted = audioEngine.isMicrophoneMuted
    val isRecording = audioEngine.isRecording

    init {
        // Observe active room to spin up audio engine simulation and speaker activity
        viewModelScope.launch {
            activeRoom.collect { room ->
                if (room != null) {
                    val speakerIds = room.speakers.map { it.id }
                    audioEngine.startCapture()
                    audioEngine.startPeerSpeakerSimulation(speakerIds)
                } else {
                    audioEngine.stopCapture()
                }
            }
        }
    }

    fun selectNav(dest: AppNavDestination) {
        _currentNav.value = dest
    }

    fun joinRoom(room: AudioRoom) {
        repository.joinRoom(room)
    }

    fun leaveRoom() {
        repository.leaveCurrentRoom()
    }

    fun minimizeRoom() {
        repository.minimizeRoomToDock()
    }

    fun expandRoom() {
        repository.maximizeRoomFromDock()
    }

    fun toggleMute() {
        audioEngine.toggleMute()
    }

    fun raiseHand() {
        repository.raiseHand()
    }

    fun toggleRoomRecording() {
        repository.toggleRoomRecording()
        audioEngine.toggleRecording()
    }

    // Host & Moderator Actions
    fun openModeratorMenu(user: User) {
        _selectedModeratorUser.value = user
    }

    fun closeModeratorMenu() {
        _selectedModeratorUser.value = null
    }

    fun moderatorMute(userId: String) {
        repository.moderatorMuteSpeaker(userId)
        closeModeratorMenu()
    }

    fun moderatorUnmute(userId: String) {
        repository.moderatorUnmuteSpeaker(userId)
        closeModeratorMenu()
    }

    fun inviteToStage(user: User) {
        repository.inviteToStage(user)
        closeModeratorMenu()
    }

    fun demoteToAudience(userId: String) {
        repository.demoteToAudience(userId)
        closeModeratorMenu()
    }

    // Chatbox Segments
    fun setChatSegment(segment: ChatSegment) {
        repository.setChatSegment(segment)
    }

    fun sendRoomMessage(text: String, photoResName: String? = null, segment: ChatSegment) {
        repository.sendChatMessage(text, photoResName, segment)
    }

    fun addReactionToMessage(msgId: String, emoji: String) {
        repository.addReactionToMessage(msgId, emoji)
    }

    fun sendStageReaction(emoji: String) {
        repository.triggerStageReaction(emoji)
    }

    // Create / Schedule dialogs
    fun setCreateRoomDialogOpen(open: Boolean) {
        _showCreateRoomDialog.value = open
    }

    fun setScheduleDialogOpen(open: Boolean) {
        _showScheduleDialog.value = open
    }

    fun setShareDialogOpen(open: Boolean) {
        _showShareDialog.value = open
    }

    fun createRoom(title: String, houseName: String, tags: List<String>, isRecording: Boolean) {
        repository.createRoom(title, houseName, tags, isRecording)
        _showCreateRoomDialog.value = false
    }

    fun scheduleStage(title: String, desc: String, houseName: String, time: String, date: String) {
        repository.scheduleRoom(title, desc, houseName, time, date)
        _showScheduleDialog.value = false
    }

    fun toggleReminder(stageId: String) {
        repository.toggleScheduledReminder(stageId)
    }

    // Contacts
    fun toggleFollow(userId: String) {
        repository.toggleFollow(userId)
    }

    fun updateContactTag(userId: String, tag: ContactTag) {
        repository.updateContactTag(userId, tag)
    }

    // Direct Messages
    fun openChatThread(thread: DirectChatThread) {
        _selectedChatThread.value = thread
    }

    fun closeChatThread() {
        _selectedChatThread.value = null
    }

    fun sendDirectMessage(threadId: String, text: String, photoRes: String? = null) {
        repository.sendDirectMessage(threadId, text, photoRes)
    }

    // Settings
    fun toggleDarkMode() {
        val current = notificationPrefs.value
        repository.updateNotificationPreferences(current.copy(darkTheme = !current.darkTheme))
    }

    fun updateNotificationLevel(level: NotificationLevel) {
        val current = notificationPrefs.value
        repository.updateNotificationPreferences(current.copy(contactGroupRule = level))
    }

    fun registerPhone(phone: String, name: String, handle: String, bio: String = "") {
        repository.registerWithPhone(phone, name, handle, bio)
    }

    fun logout() {
        repository.logout()
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.stopCapture()
    }
}

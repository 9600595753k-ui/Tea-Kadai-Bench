package com.example.data

import android.content.Context
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class ClubRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("club_audio_auth_prefs", Context.MODE_PRIVATE)

    // Current Registered User
    private val _currentUser = MutableStateFlow(
        User(
            id = "me",
            name = prefs.getString("user_name", "Alex Vance") ?: "Alex Vance",
            handle = prefs.getString("user_handle", "@alexvance") ?: "@alexvance",
            phone = prefs.getString("user_phone", "+1 (555) 234-8901") ?: "+1 (555) 234-8901",
            bio = prefs.getString("user_bio", "Acoustic enthusiast & founder. Building high-fidelity live audio spaces.") ?: "Acoustic enthusiast & founder.",
            avatarColorHex = 0xFFE5A93C,
            signalStrength = 4,
            isModerator = true,
            isHost = true
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // First time launch: false (must login/register first), then persisted
    private val _isUserRegistered = MutableStateFlow(prefs.getBoolean("is_logged_in", false))
    val isUserRegistered: StateFlow<Boolean> = _isUserRegistered.asStateFlow()

    // Active Audio Room
    private val _activeRoom = MutableStateFlow<AudioRoom?>(null)
    val activeRoom: StateFlow<AudioRoom?> = _activeRoom.asStateFlow()

    private val _isRoomFloatingDock = MutableStateFlow(false)
    val isRoomFloatingDock: StateFlow<Boolean> = _isRoomFloatingDock.asStateFlow()

    // Reaction Bursts on Stage
    private val _stageReactions = MutableStateFlow<List<String>>(emptyList())
    val stageReactions: StateFlow<List<String>> = _stageReactions.asStateFlow()

    // Notification Preferences
    private val _notificationPrefs = MutableStateFlow(AppNotificationPreferences())
    val notificationPrefs: StateFlow<AppNotificationPreferences> = _notificationPrefs.asStateFlow()

    // All Users for Search & Follow
    private val _people = MutableStateFlow<List<User>>(emptyList())
    val people: StateFlow<List<User>> = _people.asStateFlow()

    // Houses / Clubs
    private val _houses = MutableStateFlow<List<HouseClub>>(emptyList())
    val houses: StateFlow<List<HouseClub>> = _houses.asStateFlow()

    // Live Rooms Feed
    private val _liveRooms = MutableStateFlow<List<AudioRoom>>(emptyList())
    val liveRooms: StateFlow<List<AudioRoom>> = _liveRooms.asStateFlow()

    // Scheduled Stages
    private val _scheduledStages = MutableStateFlow<List<ScheduledStage>>(emptyList())
    val scheduledStages: StateFlow<List<ScheduledStage>> = _scheduledStages.asStateFlow()

    // Messages per Room and Segment
    private val _roomMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val roomMessages: StateFlow<Map<String, List<ChatMessage>>> = _roomMessages.asStateFlow()

    // Encrypted Direct Chats
    private val _directChats = MutableStateFlow<List<DirectChatThread>>(emptyList())
    val directChats: StateFlow<List<DirectChatThread>> = _directChats.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val sarah = User(
            id = "user_1",
            name = "Sarah Lin",
            handle = "@sarahsound",
            phone = "+1 (555) 789-1020",
            bio = "Audio engineer & podcast producer. Vinyl collector.",
            avatarColorHex = 0xFF5B8DEF,
            contactTag = ContactTag.FRIENDS,
            signalStrength = 4,
            isModerator = true,
            isFollowing = true
        )
        val marcus = User(
            id = "user_2",
            name = "Marcus Aurel",
            handle = "@marcusaurel",
            phone = "+1 (555) 432-1188",
            bio = "Philosophy & modern tech ethics thinker.",
            avatarColorHex = 0xFFD87D4A,
            contactTag = ContactTag.FAMILY,
            signalStrength = 3,
            isFollowing = true
        )
        val priya = User(
            id = "user_3",
            name = "Priya Sharma",
            handle = "@priyasharma",
            phone = "+1 (555) 901-4455",
            bio = "Product designer & sound UI explorer. Speaking in Tech Cafe.",
            avatarColorHex = 0xFF4EBA6F,
            contactTag = ContactTag.FRIENDS,
            signalStrength = 4,
            isFollowing = true
        )
        val david = User(
            id = "user_4",
            name = "David Cho",
            handle = "@davidcho",
            phone = "+1 (555) 672-9911",
            bio = "Venture scout & AI researcher. Daily clubhouse curator.",
            avatarColorHex = 0xFF9C6ADE,
            signalStrength = 4
        )
        val elena = User(
            id = "user_5",
            name = "Elena Rostova",
            handle = "@elenasounds",
            phone = "+1 (555) 345-6677",
            bio = "Classical pianist and acoustic spatial mixer.",
            avatarColorHex = 0xFFDF5353,
            contactTag = ContactTag.COLLEAGUE,
            signalStrength = 2
        )

        _people.value = listOf(sarah, marcus, priya, david, elena)

        val techClub = HouseClub(
            id = "house_tech",
            name = "Acoustic Tech Lounge",
            badge = "🎙️",
            description = "Where engineers, audio architects, and founders discuss next-gen streaming and privacy.",
            memberCount = 14200,
            followerCount = 38900,
            isFollowing = true,
            categories = listOf("Tech", "Audio", "Privacy")
        )
        val cultureClub = HouseClub(
            id = "house_culture",
            name = "Late Night Philosophy",
            badge = "☕",
            description = "Unfiltered, thoughtful midnight discourse about society, books, and ethics.",
            memberCount = 9800,
            followerCount = 22400,
            isFollowing = true,
            categories = listOf("Culture", "Books", "Life")
        )
        val creatorsClub = HouseClub(
            id = "house_creators",
            name = "Sound & Creators Guild",
            badge = "🎨",
            description = "High-definition sound design, music production, and independent voice creators.",
            memberCount = 6700,
            followerCount = 15300,
            categories = listOf("Design", "Music")
        )

        _houses.value = listOf(techClub, cultureClub, creatorsClub)

        val room1 = AudioRoom(
            id = "room_1",
            title = "Zero-Knowledge Voice: Building Without User Tracking",
            houseName = "Acoustic Tech Lounge",
            houseBadge = "🎙️",
            description = "How peer privacy, end-to-end cryptographic handshakes, and local storage redefine social audio.",
            host = sarah,
            speakers = listOf(sarah, _currentUser.value, priya),
            audience = listOf(marcus, david, elena),
            isRecording = false,
            tags = listOf("Privacy", "Tech", "Audio"),
            memberCount = 148
        )

        val room2 = AudioRoom(
            id = "room_2",
            title = "Midnight Coffee: Spontaneous Life Chats & Acoustic Vinyl",
            houseName = "Late Night Philosophy",
            houseBadge = "☕",
            description = "Pull up a chair, unmute if you've got a reflection, or just listen to gentle banter.",
            host = marcus,
            speakers = listOf(marcus, david),
            audience = listOf(elena, sarah),
            isRecording = true,
            tags = listOf("Philosophy", "Chill", "Storytelling"),
            memberCount = 89
        )

        _liveRooms.value = listOf(room1, room2)

        _scheduledStages.value = listOf(
            ScheduledStage(
                id = "stage_1",
                title = "The Evolution of Social Audio: What Comes After Podcasting?",
                description = "Panel discussion with top spatial audio designers and interactive media researchers.",
                houseName = "Acoustic Tech Lounge",
                host = sarah,
                timeFormatted = "8:00 PM",
                dateFormatted = "Today",
                hasReminderSet = true,
                coHosts = listOf("Alex Vance", "David Cho")
            ),
            ScheduledStage(
                id = "stage_2",
                title = "Weekly Creator AMA: Production Secrets & Microphone Acoustics",
                description = "Live Q&A where stage listeners can ask questions directly through the segmented QA chat.",
                houseName = "Sound & Creators Guild",
                host = priya,
                timeFormatted = "6:30 PM",
                dateFormatted = "Tomorrow",
                hasReminderSet = false,
                coHosts = listOf("Elena Rostova")
            )
        )

        // Seed sample segmented chat messages for room_1
        val initialRoomMessages = listOf(
            ChatMessage(
                id = "msg_1",
                roomId = "room_1",
                sender = sarah,
                text = "Welcome everyone to the stage! Feel free to post questions in #Q&A or drop live reactions.",
                segment = ChatSegment.ANNOUNCEMENTS,
                reactions = mapOf("👋" to 12, "🔥" to 8)
            ),
            ChatMessage(
                id = "msg_2",
                roomId = "room_1",
                sender = priya,
                text = "Sharing our acoustic microphone blueprint photo below!",
                segment = ChatSegment.PHOTOS,
                photoResName = "sample_stage_photo",
                reactions = mapOf("📸" to 15, "✨" to 9)
            ),
            ChatMessage(
                id = "msg_3",
                roomId = "room_1",
                sender = marcus,
                text = "How does local signal quality monitoring avoid sending analytics to servers?",
                segment = ChatSegment.QA,
                reactions = mapOf("👍" to 7)
            ),
            ChatMessage(
                id = "msg_4",
                roomId = "room_1",
                sender = david,
                text = "The audio latency is incredible here, sounds like we are in the same lounge.",
                segment = ChatSegment.GENERAL,
                reactions = mapOf("🎯" to 11, "❤️" to 6)
            )
        )
        _roomMessages.value = mapOf("room_1" to initialRoomMessages)

        // Seed direct encrypted chats
        val dm1 = DirectChatThread(
            id = "dm_sarah",
            participant = sarah,
            messages = listOf(
                DirectMessage("m1", sarah.id, "Hey Alex, are you ready to co-host the zero-data voice stage tonight?", System.currentTimeMillis() - 3600000),
                DirectMessage("m2", "me", "Absolutely! Testing the real-time mic waveform analyzer now. Crisp quality.", System.currentTimeMillis() - 1800000),
                DirectMessage("m3", sarah.id, "Great! I also turned on moderator mute privileges for you.", System.currentTimeMillis() - 900000)
            ),
            unreadCount = 1
        )
        val dm2 = DirectChatThread(
            id = "dm_group_tech",
            participant = sarah,
            isGroup = true,
            groupName = "🎙️ Acoustic Tech Core",
            groupMembers = listOf(sarah, priya, marcus, _currentUser.value),
            messages = listOf(
                DirectMessage("gm1", priya.id, "Shared the scheduled stage flyer for our Thursday room.", System.currentTimeMillis() - 7200000),
                DirectMessage("gm2", marcus.id, "Notification rules are set to Friends & Family priority.", System.currentTimeMillis() - 3600000)
            ),
            unreadCount = 0
        )
        _directChats.value = listOf(dm1, dm2)
    }

    // Phone Registration & Login Flow
    fun registerWithPhone(phoneNumber: String, name: String, handle: String, bio: String = "") {
        val finalHandle = if (handle.startsWith("@")) handle else "@$handle"
        val finalName = name.ifBlank { "Club Speaker" }
        _currentUser.update {
            it.copy(
                phone = phoneNumber,
                name = finalName,
                handle = finalHandle,
                bio = bio.ifBlank { it.bio }
            )
        }
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_phone", phoneNumber)
            .putString("user_name", finalName)
            .putString("user_handle", finalHandle)
            .putString("user_bio", bio)
            .apply()
        _isUserRegistered.value = true
    }

    fun logout() {
        prefs.edit().putBoolean("is_logged_in", false).apply()
        _isUserRegistered.value = false
        leaveCurrentRoom()
    }

    // Room Actions
    fun joinRoom(room: AudioRoom) {
        _activeRoom.value = room
        _isRoomFloatingDock.value = false
    }

    fun leaveCurrentRoom() {
        _activeRoom.value = null
        _isRoomFloatingDock.value = false
        _stageReactions.value = emptyList()
    }

    fun minimizeRoomToDock() {
        _isRoomFloatingDock.value = true
    }

    fun maximizeRoomFromDock() {
        _isRoomFloatingDock.value = false
    }

    fun raiseHand() {
        val current = _activeRoom.value ?: return
        val updatedAudience = current.audience.map {
            if (it.id == "me") it.copy(hasHandRaised = !it.hasHandRaised) else it
        }
        _activeRoom.value = current.copy(audience = updatedAudience)
    }

    // Moderator Controls: Mute uncontrolled speakers
    fun moderatorMuteSpeaker(speakerId: String) {
        val current = _activeRoom.value ?: return
        val updatedSpeakers = current.speakers.map {
            if (it.id == speakerId) it.copy(isMuted = true, isSpeaking = false) else it
        }
        _activeRoom.value = current.copy(speakers = updatedSpeakers)
    }

    fun moderatorUnmuteSpeaker(speakerId: String) {
        val current = _activeRoom.value ?: return
        val updatedSpeakers = current.speakers.map {
            if (it.id == speakerId) it.copy(isMuted = false) else it
        }
        _activeRoom.value = current.copy(speakers = updatedSpeakers)
    }

    // Moderator: Invite listener to stage
    fun inviteToStage(user: User) {
        val current = _activeRoom.value ?: return
        if (current.speakers.any { it.id == user.id }) return
        val newAudience = current.audience.filterNot { it.id == user.id }
        val newSpeakers = current.speakers + user.copy(isSpeaking = false, isMuted = false, hasHandRaised = false)
        _activeRoom.value = current.copy(speakers = newSpeakers, audience = newAudience)
    }

    // Moderator: Move speaker back to audience
    fun demoteToAudience(speakerId: String) {
        val current = _activeRoom.value ?: return
        val speaker = current.speakers.find { it.id == speakerId } ?: return
        val newSpeakers = current.speakers.filterNot { it.id == speakerId }
        val newAudience = current.audience + speaker.copy(isSpeaking = false, isMuted = true)
        _activeRoom.value = current.copy(speakers = newSpeakers, audience = newAudience)
    }

    fun toggleRoomRecording() {
        val current = _activeRoom.value ?: return
        _activeRoom.value = current.copy(isRecording = !current.isRecording)
    }

    // Chatbox Segments
    fun setChatSegment(segment: ChatSegment) {
        val current = _activeRoom.value ?: return
        _activeRoom.value = current.copy(activeChatSegment = segment)
    }

    fun sendChatMessage(text: String, photoResName: String? = null, segment: ChatSegment) {
        val room = _activeRoom.value ?: return
        val newMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            roomId = room.id,
            sender = _currentUser.value,
            text = text,
            photoResName = photoResName,
            segment = segment,
            isEncrypted = true
        )
        val currentList = _roomMessages.value[room.id] ?: emptyList()
        _roomMessages.update {
            it + (room.id to (currentList + newMsg))
        }
    }

    fun addReactionToMessage(msgId: String, emoji: String) {
        val room = _activeRoom.value ?: return
        val currentList = _roomMessages.value[room.id] ?: return
        val updated = currentList.map { msg ->
            if (msg.id == msgId) {
                val count = msg.reactions.getOrDefault(emoji, 0) + 1
                msg.copy(reactions = msg.reactions + (emoji to count))
            } else msg
        }
        _roomMessages.update { it + (room.id to updated) }
    }

    fun triggerStageReaction(emoji: String) {
        _stageReactions.update { (it + emoji).takeLast(10) }
    }

    // Create New Instant Room
    fun createRoom(title: String, houseName: String, topicTags: List<String>, isRecording: Boolean) {
        val newRoom = AudioRoom(
            id = "room_${System.currentTimeMillis()}",
            title = title.ifBlank { "Open Lounge Chat" },
            houseName = houseName.ifBlank { "Independent Audio" },
            host = _currentUser.value,
            speakers = listOf(_currentUser.value),
            audience = _people.value.take(3),
            isRecording = isRecording,
            tags = topicTags.ifEmpty { listOf("Live", "Audio") },
            memberCount = 4
        )
        _liveRooms.update { listOf(newRoom) + it }
        joinRoom(newRoom)
    }

    // Schedule Room
    fun scheduleRoom(title: String, description: String, houseName: String, time: String, date: String) {
        val newStage = ScheduledStage(
            id = "stage_${System.currentTimeMillis()}",
            title = title,
            description = description,
            houseName = houseName,
            host = _currentUser.value,
            timeFormatted = time,
            dateFormatted = date,
            hasReminderSet = true
        )
        _scheduledStages.update { listOf(newStage) + it }
    }

    fun toggleScheduledReminder(stageId: String) {
        _scheduledStages.update { list ->
            list.map {
                if (it.id == stageId) it.copy(hasReminderSet = !it.hasReminderSet) else it
            }
        }
    }

    // Contacts & Friends/Family Tagging
    fun toggleFollow(userId: String) {
        _people.update { list ->
            list.map {
                if (it.id == userId) it.copy(isFollowing = !it.isFollowing) else it
            }
        }
    }

    fun updateContactTag(userId: String, tag: ContactTag) {
        _people.update { list ->
            list.map {
                if (it.id == userId) it.copy(contactTag = tag) else it
            }
        }
    }

    // Direct Messages
    fun sendDirectMessage(threadId: String, text: String, photoRes: String? = null) {
        val newMsg = DirectMessage(
            id = UUID.randomUUID().toString(),
            senderId = "me",
            text = text,
            timestamp = System.currentTimeMillis(),
            isEncrypted = true,
            photoRes = photoRes
        )
        _directChats.update { threads ->
            threads.map { thread ->
                if (thread.id == threadId) {
                    thread.copy(messages = thread.messages + newMsg)
                } else thread
            }
        }
    }

    // Settings
    fun updateNotificationPreferences(prefs: AppNotificationPreferences) {
        _notificationPrefs.value = prefs
    }
}

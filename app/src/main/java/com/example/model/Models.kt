package com.example.model

enum class ContactTag(val label: String) {
    NONE("None"),
    FRIENDS("Friend"),
    FAMILY("Family"),
    COLLEAGUE("Colleague")
}

enum class NotificationLevel(val label: String) {
    ALL("All Notifications"),
    HIGHLIGHTS("Highlights Only"),
    FRIENDS_FAMILY_ONLY("Only Friends & Family"),
    MUTED("Silent / Off")
}

data class User(
    val id: String,
    val name: String,
    val handle: String,
    val phone: String,
    val bio: String,
    val avatarColorHex: Long = 0xFFE5A93C,
    val contactTag: ContactTag = ContactTag.NONE,
    val signalStrength: Int = 4, // 1 to 4 bars
    val isModerator: Boolean = false,
    val isHost: Boolean = false,
    val isMuted: Boolean = false,
    val isSpeaking: Boolean = false,
    val hasHandRaised: Boolean = false,
    val isFollowing: Boolean = false
)

enum class ChatSegment(val displayName: String, val description: String) {
    GENERAL("#General Chat", "Casual live discussion and greetings"),
    QA("#Q&A Questions", "Questions queued for the speakers on stage"),
    PHOTOS("#Photos & Media", "Shared pictures and visual references (no video)"),
    ANNOUNCEMENTS("#Announcements", "Moderator links and stage rules")
}

data class ChatMessage(
    val id: String,
    val roomId: String,
    val sender: User,
    val text: String,
    val photoResName: String? = null,
    val photoBitmap: Any? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val segment: ChatSegment = ChatSegment.GENERAL,
    val isEncrypted: Boolean = true,
    val reactions: Map<String, Int> = emptyMap()
)

data class AudioRoom(
    val id: String,
    val title: String,
    val houseName: String,
    val houseBadge: String = "🎙️",
    val description: String = "",
    val host: User,
    val speakers: List<User>,
    val audience: List<User>,
    val isRecording: Boolean = false,
    val tags: List<String> = listOf("Audio", "Live"),
    val activeChatSegment: ChatSegment = ChatSegment.GENERAL,
    val memberCount: Int = 120
)

data class HouseClub(
    val id: String,
    val name: String,
    val badge: String,
    val description: String,
    val memberCount: Int,
    val followerCount: Int,
    val isFollowing: Boolean = false,
    val categories: List<String> = listOf("Tech", "Culture")
)

data class ScheduledStage(
    val id: String,
    val title: String,
    val description: String,
    val houseName: String,
    val host: User,
    val timeFormatted: String,
    val dateFormatted: String,
    val hasReminderSet: Boolean = false,
    val coHosts: List<String> = emptyList()
)

data class DirectMessage(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: Long,
    val isEncrypted: Boolean = true,
    val photoRes: String? = null
)

data class DirectChatThread(
    val id: String,
    val participant: User,
    val isGroup: Boolean = false,
    val groupName: String? = null,
    val groupMembers: List<User> = emptyList(),
    val messages: List<DirectMessage> = emptyList(),
    val unreadCount: Int = 0,
    val isEncrypted: Boolean = true
)

data class AppNotificationPreferences(
    val enablePushNotifications: Boolean = true,
    val contactGroupRule: NotificationLevel = NotificationLevel.FRIENDS_FAMILY_ONLY,
    val roomInvites: Boolean = true,
    val handRaisePings: Boolean = true,
    val lowLatencyAudioMode: Boolean = true,
    val darkTheme: Boolean = true
)

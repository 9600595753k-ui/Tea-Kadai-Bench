package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ClubRepository
import com.example.model.ChatSegment
import com.example.model.ContactTag
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ClubRepositoryTest {

    private lateinit var repository: ClubRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        repository = ClubRepository(context)
    }

    @Test
    fun testInitialRoomsAndSeedDataLoaded() {
        val liveRooms = repository.liveRooms.value
        assertTrue("Live rooms should be populated", liveRooms.isNotEmpty())

        val people = repository.people.value
        assertTrue("People should be seeded", people.isNotEmpty())

        val houses = repository.houses.value
        assertTrue("Houses should be seeded", houses.isNotEmpty())
    }

    @Test
    fun testJoinRoomAndModeratorMute() {
        val room = repository.liveRooms.value.first()
        repository.joinRoom(room)

        val active = repository.activeRoom.value
        assertNotNull(active)
        assertEquals(room.id, active?.id)

        // Test moderator mute
        val speakerToMute = active!!.speakers.first()
        repository.moderatorMuteSpeaker(speakerToMute.id)

        val updatedActive = repository.activeRoom.value
        val mutedSpeaker = updatedActive?.speakers?.find { it.id == speakerToMute.id }
        assertTrue("Speaker should be muted by leader", mutedSpeaker?.isMuted == true)
    }

    @Test
    fun testRaiseHandAction() {
        val room = repository.liveRooms.value.first()
        repository.joinRoom(room)

        repository.raiseHand()
        // verify audience state
        val active = repository.activeRoom.value
        assertNotNull(active)
    }

    @Test
    fun testSendSegmentedChatMessage() {
        val room = repository.liveRooms.value.first()
        repository.joinRoom(room)

        repository.sendChatMessage("Testing QA question", null, ChatSegment.QA)

        val messages = repository.roomMessages.value[room.id]
        val qaMsg = messages?.find { it.text == "Testing QA question" }
        assertNotNull("QA message should be stored in segment", qaMsg)
        assertEquals(ChatSegment.QA, qaMsg?.segment)
    }

    @Test
    fun testContactTaggingFriendsAndFamily() {
        val targetUser = repository.people.value.first()
        repository.updateContactTag(targetUser.id, ContactTag.FAMILY)

        val updated = repository.people.value.find { it.id == targetUser.id }
        assertEquals(ContactTag.FAMILY, updated?.contactTag)
    }

    @Test
    fun testCreateRoomAndToggleRecording() {
        repository.createRoom("New Tech Stage", "Acoustic Tech Lounge", listOf("Audio", "Live"), true)
        val active = repository.activeRoom.value
        assertNotNull(active)
        assertEquals("New Tech Stage", active?.title)
        assertTrue(active?.isRecording == true)

        repository.toggleRoomRecording()
        val afterToggle = repository.activeRoom.value
        assertFalse(afterToggle?.isRecording == true)
    }

    @Test
    fun testLoginAndLogoutPersistence() {
        assertFalse("Initially logged out on fresh install", repository.isUserRegistered.value)

        repository.registerWithPhone("+1 (555) 888-9999", "Tester One", "@testerone", "Testing bio")
        assertTrue("Logged in after registration", repository.isUserRegistered.value)
        assertEquals("+1 (555) 888-9999", repository.currentUser.value.phone)
        assertEquals("Tester One", repository.currentUser.value.name)

        repository.logout()
        assertFalse("Logged out after calling logout", repository.isUserRegistered.value)
    }
}

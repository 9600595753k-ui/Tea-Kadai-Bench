# ClubAudio - Live Audio Rooms & Encrypted Community Spaces

An authentic live audio clubhouse application featuring real-time audio rooms, speaker stage moderation, segmented topic chatboxes, photo sharing, end-to-end encrypted messaging, and custom group privacy controls—all wrapped in a warm bronze and amber lounge aesthetic.

## User Review & Critical Decisions

> [!IMPORTANT]
> The architectural directions selected from your preferences have been directly integrated:
> - **Visual Identity**: Warm Clubhouse Lounge styling with rich obsidian, deep bronze surfaces, and radiant amber accents.
> - **Audio Engine**: Real-time microphone capture with live speech waveform glow, simulated peer stream activity, signal strength metrics, and session recording.
> - **Navigation Architecture**: Bottom navigation bar dividing Live Hall & Houses, Segmented Topic Chats, Search Discovery, and Encrypted Direct Messages.
> - **Privacy Guarantee**: Zero data collection to external servers—all user credentials, contact tags, and encrypted keys remain secure on-device.

---

## 1. Overview & Core Concept

**ClubAudio** delivers an intimate, high-fidelity drop-in audio conversation platform reminiscent of Clubhouse with enhanced privacy and community moderation.

- **What It Does**: Users join or host live audio stages, raise their hands to speak, see who is actively speaking with glowing animated rings, send emojis, reactions, and photos, communicate in topic-organized chatboxes, schedule upcoming group stages, and conduct encrypted private conversations.
- **Target Audience**: Communities, creators, tech talks, book clubs, and close friends who desire spontaneous audio discussions with complete data privacy and granular notification controls.
- **Key Value**: Low-latency voice presence without video fatigue, coupled with robust moderation tools (moderator mute, speaker promotion, hand queue) and zero telemetry data collection.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Onboarding & Verification**:
   - Secure phone number verification modal (with instant one-time passcode simulation), generating local cryptographic key pairs without external telemetry.
2. **Live Hall (Discover & Join Rooms)**:
   - Feed of active live audio rooms segmented by Clubs/Houses and topics.
   - Live Room Header displays house badge, topic tags, room title, and recording indicator.
   - Stage area displays Speakers with glowing gold pulses when talking, mute badges, and signal strength bars.
   - Audience grid with "Others in the room" and instant "Raise Hand" button.
3. **Stage Moderation & Host Controls**:
   - Room creator/moderators can invite audience members to stage, mute uncontrolled or background-noisy speakers, demote speakers to listeners, and toggle recording.
4. **Segmented Chatboxes & Media Sharing**:
   - Integrated room side-drawer or bottom sheet with tabbed channels (e.g., `#general-chat`, `#qa-questions`, `#resources-and-photos`).
   - Reaction bar with emojis, floating sticker animations, and photo attachment capability (no video, preserving privacy and bandwidth).
   - Speaker status pill next to user avatars in chat showing whether they are currently unmuted or talking.
5. **Encrypted Direct & Group Messaging**:
   - Private 1-on-1 and group chats with end-to-end encryption status indicators, ephemeral message options, and media sharing.
6. **Search & Discovery**:
   - Multi-tab search separating **People**, **Active Rooms**, and **Houses/Clubs** with instant follow and "Friends & Family" categorization.
7. **Schedule & Calendar**:
   - Upcoming scheduled rooms with reminder toggle, host profile previews, and social share links/invite generation.
8. **Settings & Dark Mode**:
   - Dark mode toggle, contact group notification preferences (All, Highlights, Only Friends & Family, Off), audio quality settings, and local privacy wipe.

### Visual Identity & Theme

- **Vibe**: Warm Clubhouse Lounge, sophisticated, intimate, acoustic warmth.
- **Color Palette**:
  - `Background`: Deep Obsidian `#121110` (Dark) / Warm Cream `#F7F4EE` (Light)
  - `Surface`: Charcoal Bronze `#1E1C1A` / Soft Sand `#EDE8DF`
  - `Primary / Accent`: Radiant Amber Gold `#E5A93C` / Honey Bronze `#C68A28`
  - `Speaker Glow`: Pulse Amber `#FFC857` with gradient fade
  - `Signal Strength`: High-fidelity emerald green (`#4EBA6F`), amber warning, muted crimson
- **Typography**: Editorial display headers paired with clean sans-serif body text with tabular numbers for timestamps and participant counts.
- **Motion**:
  - Smooth spring transitions when expanding or minimizing active rooms to a persistent floating audio dock.
  - Floating emoji and sticker bubbles rising across the stage screen.
  - Fluid pulsating glow rings reacting to microphone volume levels.

---

## 3. Key Product Decisions & Trade-Offs

### Decision 1: Audio Processing & Low-Latency Feedback
- **Chosen Approach**: Native Android `AudioRecord` / microphone input capture with an RMS amplitude analyzer driving Compose canvas wave bars and avatar glow rings in real-time, coupled with simulated peer audio channels.
- **Why**: Allows true live microphone interaction on the device with zero lag, while providing realistic multi-speaker room dynamics without requiring expensive proprietary WebRTC cloud server subscriptions.
- **Alternatives Considered**: Mock timers without mic permissions (too artificial; fails user requirement for real-time live mic and active speaker detection).

### Decision 2: Zero Data Collection & Privacy Architecture
- **Chosen Approach**: Local-first encrypted Room database and Android Jetpack DataStore. No external user telemetry or cloud tracking trackers are initialized.
- **Why**: Strictly adheres to the requirement: "this app not collect any data from user. This ensures total privacy while maintaining a clean, user-friendly interface for all participants."
- **Alternatives Considered**: Cloud Firestore database (violates strict zero-telemetry local privacy request).

### Decision 3: Segmented Topic Chatboxes
- **Chosen Approach**: Multi-tab chat drawer within the active room containing dedicated segments: Q&A, General Discussion, Photos & Resources, and Moderator Announcements.
- **Why**: Prevents busy live stages from having unreadable chat streams, letting hosts filter directly for text questions.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        ClubAudio UI (Jetpack Compose)                 │
├──────────────┬───────────────┬─────────────────┬──────────────────────┤
│  Live Hall   │  Topic Chats  │ Search (People/ │   Encrypted DMs &    │
│  & Rooms     │  (Segmented)  │ Rooms / Houses) │   Contact Circles    │
└──────┬───────┴───────┬───────┴────────┬────────┴──────────┬───────────┘
       │               │                │                   │
┌──────▼───────────────▼────────────────▼───────────────────▼───────────┐
│                      ClubViewModel & State Layer                       │
│  • Active Room State (Speakers, Hand Queue, Waveform, Mod Controls)   │
│  • Segmented Chat Messages & Emojis/Stickers                          │
│  • Audio Engine (Mic AudioRecord + RMS Glow + Simulated Peers)        │
│  • E2EE Message Store & Contact Circles (Friends & Family Tags)       │
└──────────────────────────────┬────────────────────────────────────────┘
                               │
┌──────────────────────────────▼────────────────────────────────────────┐
│                      Local Persistence & Security                     │
│  • Room Database: Saved Rooms, Houses, Scheduled Stages, Messages     │
│  • DataStore: App Preferences (Dark Mode, Notification Rules, Auth)   │
│  • Cryptographic Keystore for E2EE Message Verification               │
└───────────────────────────────────────────────────────────────────────┘
```

### Key Data Entities

- **User**: ID, Name, Handle, Phone Number, Avatar Drawable, Bio, Friends & Family Tag, Signal Quality.
- **AudioRoom**: ID, Title, House/Club Name, Host ID, Speakers (with Mute status, Hand raised status, IsSpeaking glow), Listeners, Topic Tags, IsRecording, Active Chat Segment ID.
- **ChatMessage**: ID, RoomId / ThreadId, SenderId, SenderName, Text, PhotoUri, Timestamp, SegmentType (`GENERAL`, `QA`, `PHOTOS`), IsEncrypted.
- **ScheduledEvent**: ID, Title, Description, ClubName, Host, ScheduledTimeMillis, BellNotificationActive.
- **ContactCircle**: UserID, CircleType (`FRIENDS`, `FAMILY`, `ACQUAINTANCE`, `PUBLIC`), NotificationLevel (`ALL`, `HIGHLIGHTS`, `MUTED`).

---

## Plan Approval

Please review the implementation plan above. Once approved, we will build the complete ClubAudio application with all requested features.

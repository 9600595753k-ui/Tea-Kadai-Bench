package com.example.audio

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class AudioEngine(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var recordJob: Job? = null
    private var simulationJob: Job? = null
    private var audioRecord: AudioRecord? = null

    private val _micAmplitude = MutableStateFlow(0f)
    val micAmplitude: StateFlow<Float> = _micAmplitude.asStateFlow()

    private val _isUserSpeaking = MutableStateFlow(false)
    val isUserSpeaking: StateFlow<Boolean> = _isUserSpeaking.asStateFlow()

    private val _activeSpeakerId = MutableStateFlow<String?>(null)
    val activeSpeakerId: StateFlow<String?> = _activeSpeakerId.asStateFlow()

    private val _isMicrophoneMuted = MutableStateFlow(false)
    val isMicrophoneMuted: StateFlow<Boolean> = _isMicrophoneMuted.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    fun hasRecordPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun setMuted(muted: Boolean) {
        _isMicrophoneMuted.value = muted
        if (muted) {
            _isUserSpeaking.value = false
            _micAmplitude.value = 0f
        }
    }

    fun toggleMute() {
        setMuted(!_isMicrophoneMuted.value)
    }

    fun toggleRecording() {
        _isRecording.value = !_isRecording.value
    }

    @SuppressLint("MissingPermission")
    fun startCapture() {
        stopCapture()
        if (hasRecordPermission()) {
            recordJob = scope.launch {
                val sampleRate = 44100
                val channelConfig = AudioFormat.CHANNEL_IN_MONO
                val audioFormat = AudioFormat.ENCODING_PCM_16BIT
                val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
                val bufferSize = (minBufferSize * 2).coerceAtLeast(1024)

                try {
                    audioRecord = AudioRecord(
                        MediaRecorder.AudioSource.MIC,
                        sampleRate,
                        channelConfig,
                        audioFormat,
                        bufferSize
                    )
                    audioRecord?.startRecording()

                    val buffer = ShortArray(bufferSize)
                    while (isActive && audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                        if (_isMicrophoneMuted.value) {
                            _micAmplitude.value = 0f
                            _isUserSpeaking.value = false
                            delay(100)
                            continue
                        }

                        val readCount = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                        if (readCount > 0) {
                            var sum = 0.0
                            for (i in 0 until readCount) {
                                sum += buffer[i] * buffer[i]
                            }
                            val rms = sqrt(sum / readCount)
                            val normalized = (rms / 3000.0).toFloat().coerceIn(0f, 1f)
                            val speaking = normalized > 0.15f
                            if (_isUserSpeaking.value != speaking) {
                                _isUserSpeaking.value = speaking
                            }
                            _micAmplitude.value = normalized
                        }
                        delay(120)
                    }
                } catch (e: Exception) {
                    startTelemetrySimulation()
                }
            }
        } else {
            startTelemetrySimulation()
        }
    }

    private fun startTelemetrySimulation() {
        simulationJob = scope.launch {
            while (isActive) {
                if (_isMicrophoneMuted.value) {
                    _micAmplitude.value = 0f
                    _isUserSpeaking.value = false
                    delay(300)
                    continue
                }
                delay(400)
            }
        }
    }

    fun startPeerSpeakerSimulation(speakerIds: List<String>) {
        // Disabled artificial simulation: Only the real person actually speaking will have their profile glow
        _activeSpeakerId.value = if (_isUserSpeaking.value) "me" else null
    }

    fun stopCapture() {
        recordJob?.cancel()
        recordJob = null
        simulationJob?.cancel()
        simulationJob = null
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (_: Exception) {}
        audioRecord = null
        _micAmplitude.value = 0f
        _isUserSpeaking.value = false
    }
}

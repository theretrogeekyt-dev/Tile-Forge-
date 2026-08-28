package com.example.game

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log2
import kotlin.math.sin

class AudioEngine {

    var soundFxEnabled: Boolean = true
    var musicEnabled: Boolean = true

    private val sampleRate = 22050
    private val scope = CoroutineScope(Dispatchers.Default)

    fun playSlideSound() {
        if (!soundFxEnabled) return
        scope.launch {
            generateToneSweep(startFreq = 220.0, endFreq = 440.0, durationMs = 80)
        }
    }

    fun playMergeSound(tileValue: Int) {
        if (!soundFxEnabled) return
        scope.launch {
            val basePitch = 300.0 + (log2(tileValue.toDouble()) * 80.0)
            generateChimeChord(listOf(basePitch, basePitch * 1.25, basePitch * 1.5), durationMs = 150)
        }
    }

    fun playEnergySound() {
        if (!soundFxEnabled) return
        scope.launch {
            generateToneSweep(startFreq = 500.0, endFreq = 1200.0, durationMs = 120)
        }
    }

    fun playForgeSound() {
        if (!soundFxEnabled) return
        scope.launch {
            generateChimeChord(listOf(440.0, 554.37, 659.25, 880.0), durationMs = 300)
        }
    }

    fun playPowerSound() {
        if (!soundFxEnabled) return
        scope.launch {
            generateToneSweep(startFreq = 800.0, endFreq = 300.0, durationMs = 180)
        }
    }

    fun playGameOverSound() {
        if (!soundFxEnabled) return
        scope.launch {
            generateToneSweep(startFreq = 300.0, endFreq = 100.0, durationMs = 350)
        }
    }

    fun playHeartLostSound() {
        if (!soundFxEnabled) return
        scope.launch {
            generateToneSweep(startFreq = 420.0, endFreq = 140.0, durationMs = 220)
        }
    }

    fun playHeartRescueSound() {
        if (!soundFxEnabled) return
        scope.launch {
            generateChimeChord(listOf(349.23, 440.0, 523.25, 698.46), durationMs = 260)
        }
    }

    fun playHeartRecoverSound() {
        if (!soundFxEnabled) return
        scope.launch {
            generateChimeChord(listOf(523.25, 659.25, 783.99, 1046.50), durationMs = 200)
        }
    }

    private fun generateToneSweep(startFreq: Double, endFreq: Double, durationMs: Int) {
        try {
            val numSamples = (sampleRate * durationMs) / 1000
            val samples = ShortArray(numSamples)
            var currentPhase = 0.0

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val freq = startFreq + (endFreq - startFreq) * progress
                val phaseIncrement = 2.0 * Math.PI * freq / sampleRate
                currentPhase += phaseIncrement

                // Envelope fade out
                val envelope = 1.0 - progress
                val sampleValue = (sin(currentPhase) * 16384.0 * envelope).toInt().coerceIn(-32768, 32767)
                samples[i] = sampleValue.toShort()
            }

            playAudioBuffer(samples)
        } catch (_: Exception) {
            // AudioTrack safeguard
        }
    }

    private fun generateChimeChord(frequencies: List<Double>, durationMs: Int) {
        try {
            val numSamples = (sampleRate * durationMs) / 1000
            val samples = ShortArray(numSamples)
            val phases = DoubleArray(frequencies.size)

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                var combined = 0.0

                frequencies.forEachIndexed { index, freq ->
                    val phaseIncrement = 2.0 * Math.PI * freq / sampleRate
                    phases[index] += phaseIncrement
                    combined += sin(phases[index])
                }

                val envelope = 1.0 - progress
                val sampleValue = ((combined / frequencies.size) * 16384.0 * envelope).toInt().coerceIn(-32768, 32767)
                samples[i] = sampleValue.toShort()
            }

            playAudioBuffer(samples)
        } catch (_: Exception) {
            // Safeguard
        }
    }

    private fun playAudioBuffer(samples: ShortArray) {
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(maxOf(minBufferSize, samples.size * 2))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(samples, 0, samples.size)
        track.play()
        // Release track after playback completes
        scope.launch {
            kotlinx.coroutines.delay(samples.size * 1000L / sampleRate + 50)
            track.stop()
            track.release()
        }
    }
}

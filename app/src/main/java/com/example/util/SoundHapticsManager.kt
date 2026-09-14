package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * High-performance sound synthesis and haptic feedback manager.
 * Zero external audio assets required; synthesizes crystal-clear harmonic chimes and fanfares.
 */
object SoundHapticsManager {

    private fun getVibrator(context: Context): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }

    fun vibrateSuccess(context: Context) {
        val v = getVibrator(context) ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 50, 40, 70)
                val amplitudes = intArrayOf(0, 180, 0, 255)
                v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(longArrayOf(0, 50, 40, 70), -1)
            }
        } catch (_: Exception) {}
    }

    fun vibrateAchievement(context: Context) {
        val v = getVibrator(context) ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 40, 30, 50, 30, 130)
                val amplitudes = intArrayOf(0, 150, 0, 200, 0, 255)
                v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(longArrayOf(0, 40, 30, 50, 30, 130), -1)
            }
        } catch (_: Exception) {}
    }

    fun vibrateError(context: Context) {
        val v = getVibrator(context) ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(250)
            }
        } catch (_: Exception) {}
    }

    fun vibrateClick(context: Context) {
        val v = getVibrator(context) ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(20, 100))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(20)
            }
        } catch (_: Exception) {}
    }

    // Programmatic Sine Wave Synthesis for Sound Effects (100% offline, zero asset files required)
    private fun playToneSequence(notes: List<Pair<Double, Int>>) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val sampleRate = 22050
                val totalDurationMs = notes.sumOf { it.second }
                val totalSamples = (sampleRate * (totalDurationMs / 1000.0)).toInt()
                val buffer = ShortArray(totalSamples)

                var sampleIndex = 0
                for ((freq, durationMs) in notes) {
                    val noteSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                    val attack = (noteSamples * 0.08).toInt().coerceAtLeast(1)
                    val decay = (noteSamples * 0.35).toInt().coerceAtLeast(1)

                    for (i in 0 until noteSamples) {
                        if (sampleIndex >= totalSamples) break
                        val t = i.toDouble() / sampleRate
                        val envelope = when {
                            i < attack -> i.toDouble() / attack
                            i > noteSamples - decay -> (noteSamples - i).toDouble() / decay
                            else -> 1.0
                        }
                        val sampleVal = (sin(2.0 * PI * freq * t) * envelope * 20000.0).toInt().coerceIn(-32768, 32767)
                        buffer[sampleIndex++] = sampleVal.toShort()
                    }
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
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
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                Thread.sleep(totalDurationMs.toLong() + 100)
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {}
        }
    }

    fun playSuccessChime(context: Context) {
        vibrateSuccess(context)
        // High ascending 2-tone pleasant verification chime: E5 (659Hz) -> A5 (880Hz)
        playToneSequence(
            listOf(
                659.25 to 110,
                880.00 to 220
            )
        )
    }

    fun playAchievementFanfare(context: Context) {
        vibrateAchievement(context)
        // 4-note celebratory fanfare: C5 (523Hz) -> E5 (659Hz) -> G5 (784Hz) -> C6 (1046Hz)
        playToneSequence(
            listOf(
                523.25 to 90,
                659.25 to 90,
                783.99 to 110,
                1046.50 to 300
            )
        )
    }

    fun playErrorAlert(context: Context) {
        vibrateError(context)
        // Low double alert tone
        playToneSequence(
            listOf(
                220.0 to 130,
                180.0 to 200
            )
        )
    }

    fun playClick(context: Context) {
        vibrateClick(context)
        playToneSequence(
            listOf(
                950.0 to 25
            )
        )
    }
}

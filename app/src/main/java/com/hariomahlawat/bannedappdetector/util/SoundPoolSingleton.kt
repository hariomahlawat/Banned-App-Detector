package com.hariomahlawat.bannedappdetector.util

import android.media.AudioManager
import android.media.ToneGenerator

/**
 * Simple singleton to play a short alert tone. Used by the scan progress
 * animation when a banned application is revealed.
 */
object SoundPoolSingleton {
    private val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)

    fun beep() {
        tone.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
    }
}


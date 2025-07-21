package com.hariomahlawat.bannedappdetector.util

import android.media.AudioManager
import android.media.ToneGenerator

object BeepPlayer {
    private val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)

    fun beep() {
        tone.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
    }
}

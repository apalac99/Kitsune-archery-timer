package com.proyectotiro.archerytimer.logic

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import com.proyectotiro.archerytimer.R

class WhistleSystem(private val context: Context) {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
    private var mediaPlayer: MediaPlayer? = null

    fun playWhistle() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, R.raw.silvato)
            mediaPlayer?.start()
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun playToneShort() {
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
    }

    fun release() {
        mediaPlayer?.release()
        toneGenerator.release()
    }
}
package com.codedev.videoapp.ui.video_page

import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer

fun SimpleExoPlayer.addListener(
    ready: () -> Unit,
    buffering: () -> Unit,
    ended: () -> Unit,
    idle: () -> Unit,
    onListen: (Player.Listener) -> Unit
) {
    val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    ready()
                }
                Player.STATE_BUFFERING -> {
                    buffering()
                }
                Player.STATE_ENDED -> {
                    ended()
                }
                Player.STATE_IDLE -> {
                    idle()
                }
                else -> {

                }
            }
        }
    }
    onListen(listener)
    this.addListener(listener)
}

fun SimpleExoPlayer.removeListener() {
    this.removeListener()
}
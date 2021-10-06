package com.codedev.videoapp.ui.video_page

data class VideoState(
    val videoId: Int? = null,
    val playWhenReady: Boolean = true,
    val currentWindow: Int = 0,
    val playbackPosition: Long = 0L
)
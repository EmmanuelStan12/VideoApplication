package com.codedev.videoapp.ui.video_page

sealed class VideoPageEvents {
    data class GetVideoEvent(val videoId: Int): VideoPageEvents()
    data class SaveVideoState(val videoState: VideoState): VideoPageEvents()
}
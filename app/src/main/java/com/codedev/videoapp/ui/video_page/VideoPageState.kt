package com.codedev.videoapp.ui.video_page

import com.codedev.videoapp.data.models.video_response.VideoResponse

data class VideoPageState(
    val video: VideoResponse? = null,
    val isLoading: Boolean = false,
    val error: String = ""
)
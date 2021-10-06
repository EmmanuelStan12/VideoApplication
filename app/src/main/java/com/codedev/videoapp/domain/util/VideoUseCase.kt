package com.codedev.videoapp.domain.util

import com.codedev.videoapp.domain.use_cases.GetVideo
import com.codedev.videoapp.domain.use_cases.GetVideos
import com.codedev.videoapp.domain.use_cases.SearchVideo

data class VideoUseCase(
    val searchVideo: SearchVideo,
    val getVideo: GetVideo,
    val getVideos: GetVideos
)
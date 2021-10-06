package com.codedev.videoapp.ui.video_list

import com.codedev.videoapp.data.models.search_video_response.Video

data class VideoListState(
    val data: List<Video> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = ""
)
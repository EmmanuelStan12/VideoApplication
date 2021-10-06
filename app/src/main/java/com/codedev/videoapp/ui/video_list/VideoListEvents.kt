package com.codedev.videoapp.ui.video_list

import com.codedev.videoapp.data.models.search_video_response.Video

sealed class VideoListEvents {
    data class VideoItemClick(val video: Video) : VideoListEvents()
    object GetVideoList: VideoListEvents()
}
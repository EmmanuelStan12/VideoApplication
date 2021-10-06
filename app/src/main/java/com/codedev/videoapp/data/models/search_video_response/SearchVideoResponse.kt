package com.codedev.videoapp.data.models.search_video_response

data class SearchVideoResponse(
    val page: Int,
    val per_page: Int,
    val total_results: Int,
    val url: String,
    val videos: List<Video>
)
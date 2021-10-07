package com.codedev.videoapp.domain.util

import com.codedev.videoapp.domain.use_cases.*

data class VideoUseCase(
    val searchVideo: SearchVideo,
    val getVideo: GetVideo,
    val getVideos: GetVideos,
    val getQueries: GetQueries,
    val insertQuery: InsertQuery,
    val deleteQuery: DeleteQuery,
    val searchQuery: SearchQuery
)
package com.codedev.videoapp.ui.search_list

import com.codedev.videoapp.data.models.search_video_response.Video
import com.codedev.videoapp.data.room.AutoCompleteItem

data class SearchState(
    val query: String = "",
    val page: Int = 1,
    val loading: Boolean = false,
    val error: String = "",
    val data: List<Video> = emptyList(),
    val showVideos: Boolean = false,
    val showQueries: Boolean = true,
    val queries: List<AutoCompleteItem> = emptyList()
)

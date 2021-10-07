package com.codedev.videoapp.domain.repository

import com.codedev.videoapp.data.models.search_video_response.SearchVideoResponse
import com.codedev.videoapp.data.models.video_response.VideoResponse
import com.codedev.videoapp.data.room.AutoCompleteItem
import com.codedev.videoapp.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface VideoRepository {

    suspend fun getPopularVideos(
        page: Int
    ): Flow<Resource<SearchVideoResponse>>

    suspend fun getVideoById(id: Int): Flow<Resource<VideoResponse>>

    suspend fun searchVideo(
        query: String,
        page: Int,
        perPage: Int,
        orientation: String = "",
        size: String = ""
    ): Flow<Resource<SearchVideoResponse>>

    fun getQueries() : Flow<List<AutoCompleteItem>>

    suspend fun getQuery(id: Int): AutoCompleteItem

    suspend fun deleteQuery(item: AutoCompleteItem)

    suspend fun searchQuery(query: String): Flow<List<AutoCompleteItem>>

    suspend fun insert(item: AutoCompleteItem)

}
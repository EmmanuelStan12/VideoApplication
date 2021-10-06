package com.codedev.videoapp.data.api

import com.codedev.videoapp.data.models.search_video_response.SearchVideoResponse
import com.codedev.videoapp.data.models.video_response.VideoResponse
import com.codedev.videoapp.ui.util.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface VideoApi {

    @GET("/search")
    suspend fun searchVideo(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 15,
        @Query("orientation") orientation: String = "Landscape",
        @Query("size") size: String = "medium"
    ): Response<SearchVideoResponse>

    @GET("popular")
    suspend fun getPopularVideos(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 15
    ): Response<SearchVideoResponse>

    @GET("videos/{id}")
    suspend fun getVideoById(
        @Path("id") id: String
    ): Response<VideoResponse>
}
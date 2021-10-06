package com.codedev.videoapp.domain.repository

import android.util.Log
import com.codedev.videoapp.data.api.VideoApi
import com.codedev.videoapp.data.models.search_video_response.SearchVideoResponse
import com.codedev.videoapp.data.models.video_response.VideoResponse
import com.codedev.videoapp.domain.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val videoApi: VideoApi
) : VideoRepository {
    override suspend fun getPopularVideos(): Flow<Resource<SearchVideoResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = videoApi.getPopularVideos()
            if(response.isSuccessful) {
                response.body()?.let {
                    delay(3000L)
                    emit(Resource.Success<SearchVideoResponse>(it))
                }
            } else {
                Log.d("TAG", "getVideos: ${response.toString()}")
            }
        }catch (e: Exception) {
            emit(Resource.Error<SearchVideoResponse>(message = e.message.toString()))
        }
    }

    override suspend fun getVideoById(id: Int): Flow<Resource<VideoResponse>> = flow {
        emit(Resource.Loading<VideoResponse>())
        try {
            val response = videoApi.getVideoById(id.toString())
            if(response.isSuccessful) {
                response.body()?.let {
                    delay(3000L)
                    emit(Resource.Success<VideoResponse>(it))
                }
            } else {
                Log.d("TAG", "getVideos: ${response.toString()}")
            }
        }catch (e: Exception) {
            Log.d("TAG", "getVideos: ${e.toString()}")
            emit(Resource.Error<VideoResponse>(message = e.message.toString()))
        }
    }

    override suspend fun searchVideo(
        query: String,
        page: Int,
        perPage: Int,
        orientation: String,
        size: String
    ): Flow<Resource<SearchVideoResponse>> = flow {

    }

}
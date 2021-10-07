package com.codedev.videoapp.ui.video_list

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.codedev.videoapp.data.models.search_video_response.Video
import com.codedev.videoapp.domain.util.Resource
import com.codedev.videoapp.domain.util.VideoUseCase
import com.codedev.videoapp.ui.common.checkInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    application: Application,
    private val videoUseCase: VideoUseCase
): AndroidViewModel(application) {

    private var _videoListState = MutableStateFlow<VideoListState>(VideoListState())
    val videoListState : StateFlow<VideoListState> get() = _videoListState

    private var _uiEvent: Channel<UiEvent> = Channel()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        execute(VideoListEvents.GetVideoList)
    }

    fun execute(event: VideoListEvents) {
        viewModelScope.launch {
            when(event) {
                is VideoListEvents.VideoItemClick -> {
                    _uiEvent.send(UiEvent.NavigateScreen(event.video))
                }
                is VideoListEvents.GetVideoList -> {
                    checkInternetConnection(
                        getApplication(),
                        error = {
                            _videoListState.value = videoListState.value.copy(
                                error = it
                            )
                        }
                    ) {
                        getVideos()
                    }
                }
                is VideoListEvents.GetMoreVideos -> {
                    checkInternetConnection(getApplication(), error = { _videoListState.value = videoListState.value.copy(error = it) }) {
                        getVideos()
                    }
                }
                is VideoListEvents.UpdateCurrentPosition -> {
                    _videoListState.value = videoListState.value.copy(
                        currentPosition = event.position
                    )
                }
            }
        }
    }

    private suspend fun getVideos() {
        val state = videoListState.value
        videoUseCase.getVideos(videoListState.value.page).collectLatest {
            when(it) {
                is Resource.Loading -> {
                    _videoListState.value = videoListState.value.copy(
                        isLoading = state.page <= 1,
                        loadingMore = state.page > 1
                    )
                }
                is Resource.Error -> {
                    _videoListState.value = videoListState.value.copy(
                        isLoading = false,
                        loadingMore = false,
                        error = it.message ?: "Unknown Error Occurred"
                    )
                }
                is Resource.Success -> {
                    val list = state.data.map { item -> item }.toList()
                    list.toMutableList().addAll(it.data?.videos ?: emptyList())
                    _videoListState.value = state.copy(
                        isLoading = false,
                        data = list.toList(),
                        page = state.page + 1,
                        loadingMore = false
                    )
                }
            }
        }
    }

    sealed class UiEvent {
        data class NavigateScreen(val video: Video) : UiEvent()
    }
}
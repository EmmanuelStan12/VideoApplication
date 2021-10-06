package com.codedev.videoapp.ui.video_list

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.codedev.videoapp.data.models.search_video_response.Video
import com.codedev.videoapp.domain.util.Resource
import com.codedev.videoapp.domain.util.VideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
                    videoUseCase.getVideos().collectLatest {
                        when(it) {
                            is Resource.Loading -> {
                                _videoListState.value = videoListState.value.copy(
                                    isLoading = true
                                )
                            }
                            is Resource.Error -> {
                                _videoListState.value = videoListState.value.copy(
                                    isLoading = false,
                                    error = it.message ?: "Unknown Error Occurred"
                                )
                                Log.d("TAG", "execute: Error Occurred ${event.toString()}")
                            }
                            is Resource.Success -> {
                                _videoListState.value = videoListState.value.copy(
                                    isLoading = false,
                                    data = it.data?.videos ?: emptyList()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class NavigateScreen(val video: Video) : UiEvent()
    }
}
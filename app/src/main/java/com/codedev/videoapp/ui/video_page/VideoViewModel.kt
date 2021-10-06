package com.codedev.videoapp.ui.video_page

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codedev.videoapp.domain.util.Resource
import com.codedev.videoapp.domain.util.VideoUseCase
import com.codedev.videoapp.ui.video_list.VideoListState
import com.codedev.videoapp.ui.video_list.VideoListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val useCase: VideoUseCase
) : ViewModel() {

    private var _videoPageState = MutableStateFlow<VideoPageState>(VideoPageState())
    val videoPageState : StateFlow<VideoPageState> get() = _videoPageState

    private var _uiEvent: Channel<UiEvent> = Channel()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun execute(event: VideoPageEvents) {
        viewModelScope.launch {
            when(event) {
                is VideoPageEvents.GetVideoEvent -> {
                    useCase.getVideo(event.videoId).collectLatest {
                        when(it) {
                            is Resource.Loading -> {
                                _videoPageState.value = videoPageState.value.copy(
                                    isLoading = true
                                )
                            }
                            is Resource.Error -> {
                                _videoPageState.value = videoPageState.value.copy(
                                    isLoading = false,
                                    error = it.message ?: "Unknown Error Occurred"
                                )
                                Log.d("TAG", "execute: Error Occurred ${event.toString()}")
                            }
                            is Resource.Success -> {
                                _videoPageState.value = videoPageState.value.copy(
                                    isLoading = false,
                                    video = it.data
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    sealed class UiEvent {

    }
}
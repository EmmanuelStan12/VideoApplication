package com.codedev.videoapp.ui.search_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.codedev.videoapp.domain.util.Resource
import com.codedev.videoapp.domain.util.VideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    application: Application,
    private val useCase: VideoUseCase
): AndroidViewModel(application) {

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> get() = _searchState

    fun execute(event: SearchEvents) {
        viewModelScope.launch {
            when(event) {
                is SearchEvents.Submit -> {
                    useCase.searchVideo(searchState.value.query, searchState.value.page).collectLatest {
                        when(it) {
                            is Resource.Loading -> {
                                _searchState.value = searchState.value.copy(
                                    loading = true,
                                    showQueries = true,
                                    showVideos = false
                                )
                            }
                            is Resource.Error -> {
                                _searchState.value = searchState.value.copy(
                                    loading = false,
                                    showQueries = true,
                                    showVideos = false,
                                    error = it.message ?: "Unknown Error Occurred"
                                )
                            }
                            is Resource.Success -> {
                                _searchState.value = searchState.value.copy(
                                    loading = false,
                                    showQueries = true,
                                    showVideos = false,
                                    data = it.data?.videos ?: emptyList()
                                )
                            }
                        }
                    }
                }
                is SearchEvents.TextChangeListener -> {

                }
            }
        }
    }

    sealed class UiEvent {

    }

}
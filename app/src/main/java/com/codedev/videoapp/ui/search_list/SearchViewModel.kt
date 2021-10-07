package com.codedev.videoapp.ui.search_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.codedev.videoapp.data.room.AutoCompleteItem
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
                    _searchState.value = searchState.value.copy(
                        query = event.query
                    )
                    useCase.insertQuery(AutoCompleteItem(text = searchState.value.query))
                    useCase.searchVideo(searchState.value.query, searchState.value.page).collectLatest {
                        when(it) {
                            is Resource.Loading -> {
                                _searchState.value = searchState.value.copy(
                                    loading = true,
                                    showQueries = false,
                                    showVideos = true
                                )
                            }
                            is Resource.Error -> {
                                _searchState.value = searchState.value.copy(
                                    loading = false,
                                    showQueries = false,
                                    showVideos = true,
                                    error = it.message ?: "Unknown Error Occurred"
                                )
                            }
                            is Resource.Success -> {
                                _searchState.value = searchState.value.copy(
                                    loading = false,
                                    showQueries = false,
                                    showVideos = true,
                                    data = it.data?.videos ?: emptyList()
                                )
                            }
                        }
                    }
                }
                is SearchEvents.TextChangeListener -> {
                    useCase.searchQuery(event.query).collectLatest {
                        _searchState.value = searchState.value.copy(
                            queries = it,
                            loading = false,
                            showQueries = true,
                            showVideos = false,
                        )
                    }
                }
                is SearchEvents.GetAllQueries -> {
                    useCase.getQueries().collectLatest {
                        _searchState.value = searchState.value.copy(
                            queries = it,
                            loading = false,
                            showQueries = true,
                            showVideos = false,
                        )
                    }
                }
                is SearchEvents.DeleteQuery -> {
                    useCase.deleteQuery(event.item)
                }
                is SearchEvents.InsertQuery -> {
                    useCase.insertQuery(AutoCompleteItem(text = event.query))
                }
            }
        }
    }

    sealed class UiEvent {

    }

}
package com.codedev.videoapp.domain.use_cases

import com.codedev.videoapp.data.room.AutoCompleteItem
import com.codedev.videoapp.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetQueries constructor(
    private val repository: VideoRepository
) {
    operator fun invoke() : Flow<List<AutoCompleteItem>> {
        return repository.getQueries().map { notes ->
            notes.sortedByDescending {
                it.text
            }
        }

    }
}
package com.codedev.videoapp.domain.use_cases

import com.codedev.videoapp.data.room.AutoCompleteItem
import com.codedev.videoapp.domain.repository.VideoRepository

class DeleteQuery constructor(
    private val repository: VideoRepository
) {

    suspend operator fun invoke(item: AutoCompleteItem) {
        repository.deleteQuery(item)
    }
}
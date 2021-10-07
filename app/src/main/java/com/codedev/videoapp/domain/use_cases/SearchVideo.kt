package com.codedev.videoapp.domain.use_cases

import com.codedev.videoapp.domain.repository.VideoRepository
import javax.inject.Inject

class SearchVideo @Inject constructor(
    private val repository: VideoRepository
) {

    suspend operator fun invoke(
        query: String,
        page: Int,
        perPage: Int = 15
    ) = repository.searchVideo(
        query, page, perPage
    )
}
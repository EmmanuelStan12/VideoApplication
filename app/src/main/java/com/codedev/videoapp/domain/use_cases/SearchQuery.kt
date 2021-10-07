package com.codedev.videoapp.domain.use_cases

import com.codedev.videoapp.domain.repository.VideoRepository

class SearchQuery constructor(
    private val repository: VideoRepository
) {

    suspend operator fun invoke(query: String) = repository.searchQuery(query)
}
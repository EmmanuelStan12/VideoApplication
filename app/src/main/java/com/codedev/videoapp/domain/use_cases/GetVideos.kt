package com.codedev.videoapp.domain.use_cases

import com.codedev.videoapp.domain.repository.VideoRepository
import javax.inject.Inject

class GetVideos @Inject constructor(
    private val repository: VideoRepository
) {

    suspend operator fun invoke() = repository.getPopularVideos()
}
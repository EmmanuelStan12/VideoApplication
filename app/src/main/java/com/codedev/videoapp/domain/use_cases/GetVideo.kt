package com.codedev.videoapp.domain.use_cases

import com.codedev.videoapp.domain.repository.VideoRepository
import javax.inject.Inject

class GetVideo @Inject constructor(
    private val repository: VideoRepository
) {

    suspend operator fun invoke(id: Int) = repository.getVideoById(id)
}
package com.codedev.videoapp.data.models.video_response

data class VideoFile(
    val file_type: String,
    val height: Int,
    val id: Int,
    val link: String,
    val quality: String,
    val width: Int
)
package com.codedev.videoapp.ui.common

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.codedev.videoapp.R
import com.codedev.videoapp.data.models.search_video_response.Video

@BindingAdapter("image")
fun setImage(imageView: ImageView, video: Video){

    val circularProgressDrawable = CircularProgressDrawable(imageView.context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.backgroundColor = 255
    circularProgressDrawable.start()

    Glide.with(imageView.context)
        .load(video.image)
        .placeholder(circularProgressDrawable)
        .centerCrop()
        .transition(DrawableTransitionOptions.withCrossFade())
        .error(R.drawable.ic_error)
        .into(imageView)
}

fun setTime(video: Video): String {
    var seconds = 0
    var hours = 0
    var minutes = 0
    val time = video.duration / 60
    if (time > 60) {
        minutes = ((time / 60) - 1) * 60
        hours = (time / 60) - minutes
        seconds = ((minutes / 60) - 1) * 60
    }else if(time > 1) {
        seconds = (time - 1) * 60
        minutes = (video.duration - seconds) / 60
    } else {
        seconds = (time - 1) * 60
    }
    return "$hours:$minutes:$seconds"

}
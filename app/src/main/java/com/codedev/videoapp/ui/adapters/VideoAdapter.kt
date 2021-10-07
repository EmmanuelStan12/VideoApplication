package com.codedev.videoapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codedev.videoapp.data.models.search_video_response.Video
import com.codedev.videoapp.databinding.LayoutLoadingBinding
import com.codedev.videoapp.databinding.LayoutVideoItemBinding
import com.codedev.videoapp.ui.common.setTime

class VideoAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    var itemClickListener: ItemClickListener? = null
    private var visible = false

    private val itemCallback = object: DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem == newItem
        }
    }

    fun load(bool: Boolean) {
        if(bool) {
            list.currentList.add(
                list.currentList[list.currentList.size - 1]
            )
        } else {
            list.currentList.remove(list.currentList[list.currentList.size - 1])
        }
        visible = bool
    }

    val list = AsyncListDiffer(this, itemCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == 1) {
            VideoViewHolder(
                LayoutVideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            LoadingViewHolder(
                LayoutLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val video = list.currentList[position]
        when(holder) {
            is VideoViewHolder -> {
                holder.bind(video)
                holder.itemView.setOnClickListener {
                    itemClickListener?.onItemClick(video)
                }
            }
            is LoadingViewHolder -> {

            }
        }
    }

    override fun getItemCount(): Int {
        return list.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == list.currentList.size - 1 && visible) 2
        else 1
    }

    inner class VideoViewHolder(private val binding: LayoutVideoItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(video: Video) {
            binding.video = video
            val time = StringBuilder().append(video.duration).append("secs")
            binding.videoTime.text = time.toString()
        }
    }

    inner class LoadingViewHolder(private val binding: LayoutLoadingBinding): RecyclerView.ViewHolder(binding.root) {
        fun show() = run { binding.progressBar.visibility = View.VISIBLE }
        fun hide() = run { binding.progressBar.visibility = View.GONE }
    }
}

interface ItemClickListener {
    fun onItemClick(video: Video)
}

interface LoadingListener {
    fun visibility(isVisible: Boolean)
}
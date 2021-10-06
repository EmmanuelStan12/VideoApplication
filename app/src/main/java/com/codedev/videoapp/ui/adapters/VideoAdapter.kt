package com.codedev.videoapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codedev.videoapp.data.models.search_video_response.Video
import com.codedev.videoapp.databinding.LayoutVideoItemBinding
import com.codedev.videoapp.ui.common.setTime

class VideoAdapter: RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {
    
    var itemClickListener: ItemClickListener? = null

    private val itemCallback = object: DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem == newItem
        }
    }

    val list = AsyncListDiffer(this, itemCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            LayoutVideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = list.currentList[position]
        holder.bind(video)
        holder.itemView.setOnClickListener { 
            itemClickListener?.onItemClick(video)
        }
    }

    override fun getItemCount(): Int {
        return list.currentList.size
    }

    inner class VideoViewHolder(private val binding: LayoutVideoItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(video: Video) {
            binding.video = video
            val time = StringBuilder().append(video.duration).append("secs")
            binding.videoTime.text = time.toString()
        }
    }
}

interface ItemClickListener {
    fun onItemClick(video: Video)
}
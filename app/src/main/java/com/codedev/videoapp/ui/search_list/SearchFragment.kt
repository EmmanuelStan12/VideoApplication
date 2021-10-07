package com.codedev.videoapp.ui.search_list

import AutoCompleteAdapter
import AutoCompleteItemClickListener
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.codedev.videoapp.R
import com.codedev.videoapp.data.models.search_video_response.Video
import com.codedev.videoapp.databinding.FragmentSearchBinding
import com.codedev.videoapp.ui.adapters.ItemClickListener
import com.codedev.videoapp.ui.adapters.VideoAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment: Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AutoCompleteAdapter
    private lateinit var videoAdapter: VideoAdapter

    private val autoCompleteItemListener = object : AutoCompleteItemClickListener {
        override fun onItemClick(item: AutoCompleteItem) {

        }

        override fun onDeleteItem(item: AutoCompleteItem) {

        }
    }

    private val itemClickListener = object: ItemClickListener {
        override fun onItemClick(video: Video) {

        }
    }

    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentSearchBinding.bind(view)

        adapter = AutoCompleteAdapter()
        adapter.itemClickListener = autoCompleteItemListener
        videoAdapter = VideoAdapter()
        videoAdapter.itemClickListener = itemClickListener
        binding.hintsRecyclerView.adapter = adapter
        binding.videoRecyclerView.adapter = videoAdapter

        binding.searchField.addTextChangeListener {

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
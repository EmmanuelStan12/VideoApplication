package com.codedev.videoapp.ui.video_list

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codedev.videoapp.R
import com.codedev.videoapp.data.models.search_video_response.Video
import com.codedev.videoapp.databinding.FragmentVideoListBinding
import com.codedev.videoapp.ui.adapters.ItemClickListener
import com.codedev.videoapp.ui.adapters.VideoAdapter
import com.codedev.videoapp.ui.util.Constants.TAG
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class VideoListFragment : Fragment(R.layout.fragment_video_list) {

    private var _binding: FragmentVideoListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: VideoAdapter

    private val itemClickListener = object: ItemClickListener {
        override fun onItemClick(video: Video) {
            val action = VideoListFragmentDirections.actionVideoListFragmentToVideoActivity(video.id)
            findNavController().navigate(action)
        }
    }

    private val viewModel: VideoListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentVideoListBinding.bind(view)

        adapter = VideoAdapter()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        adapter.itemClickListener = itemClickListener

        lifecycleScope.launchWhenStarted {
            viewModel.videoListState.collect {
                if(it.data.isNotEmpty() && !it.isLoading) {
                    adapter.list.submitList(it.data)
                    binding.progressBar.visibility = View.GONE
                }
                if(it.isLoading) {
                    binding.progressBar.visibility = View.VISIBLE
                }
                if(it.error != "") {
                    Snackbar.make(view, it.error, Snackbar.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
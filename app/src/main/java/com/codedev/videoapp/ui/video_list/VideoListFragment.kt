package com.codedev.videoapp.ui.video_list

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Update
import com.codedev.videoapp.R
import com.codedev.videoapp.data.models.search_video_response.Video
import com.codedev.videoapp.databinding.FragmentVideoListBinding
import com.codedev.videoapp.ui.adapters.ItemClickListener
import com.codedev.videoapp.ui.adapters.LoadingListener
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
    private val args: VideoListFragmentArgs by navArgs()

    private var loadingListener: LoadingListener? = null

    private var layoutManager: LinearLayoutManager? = null

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
        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        adapter.itemClickListener = itemClickListener
        binding.searchIcon.setOnClickListener {
            findNavController().navigate(R.id.action_videoListFragment_to_searchFragment)
        }

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

        binding.recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val currentPosition = layoutManager?.findLastCompletelyVisibleItemPosition()
                val visiblePosition: Int = layoutManager?.findFirstCompletelyVisibleItemPosition() ?: -1
                if (visiblePosition > -1) {
                    viewModel.execute(VideoListEvents.UpdateCurrentPosition(visiblePosition))
                }
                if(currentPosition == (adapter.list.currentList.size - 1)) {
                    adapter.load(true)
                    viewModel.execute(VideoListEvents.GetMoreVideos)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
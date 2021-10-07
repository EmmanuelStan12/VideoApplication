package com.codedev.videoapp.ui.video_page

import android.annotation.SuppressLint
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import com.codedev.videoapp.R
import com.codedev.videoapp.data.models.video_response.VideoResponse
import com.codedev.videoapp.databinding.ActivityVideoBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.Util
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import android.content.pm.ActivityInfo

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.os.PersistableBundle
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar


@AndroidEntryPoint
class VideoActivity : AppCompatActivity() {

    private var _binding: ActivityVideoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VideoViewModel by viewModels()

    private var player: SimpleExoPlayer? = null
    private var listener: Player.Listener? = null

    private var cancelButton: AppCompatImageView? = null
    private var fullScreenButton: AppCompatImageView? = null
    private var titleTextView: AppCompatTextView? = null

    private var height: Int? = null

    private val args: VideoActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if(viewModel.videoState.value.videoId == null) {
            viewModel.execute(VideoPageEvents.GetVideoEvent(args.videoId))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.videoPageState.collect {
                if (it.isLoading) {
                    binding.progressBar.visibility = View.VISIBLE
                }
                if (it.video != null && !it.isLoading) {
                    binding.videoImage?.visibility = View.VISIBLE
                    Glide.with(this@VideoActivity)
                        .load(it.video.video_pictures[0].picture)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(R.drawable.ic_error)
                        .into(binding.videoImage!!)
                    playVideo(it.video)
                }
                if (it.error != "" && !it.isLoading) {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, it.error, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onBackPressed() {
        val orientation = viewModel.videoState.value.orientation
        if (orientation == UiOrientation.LANDSCAPE) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            viewModel.execute(VideoPageEvents.UpdateOrientation(UiOrientation.PORTRAIT))
        } else {
            super.onBackPressed()
        }
    }

    private fun releasePlayer() {
        player?.run {
            viewModel.execute(
                VideoPageEvents.SaveVideoState(
                    viewModel.videoState.value.copy(
                        playWhenReady = this.playWhenReady,
                        currentWindow = this.currentWindowIndex,
                        playbackPosition = this.currentPosition
                    )
                )
            )
            listener?.let { removeListener(it) }
            release()
        }
        player = null
    }

    private fun playVideo(video: VideoResponse) {
        val media = video.video_files[0].link
        val uri = Uri.parse(media)
        val mediaItem = MediaItem.fromUri(uri)
        binding.nameTv?.text = video.user.name
        binding.profileTv?.text = video.user.url
        binding.titleTv?.text = video.user.name
        binding.urlTv?.text = video.url
        titleTextView?.text = video.user.name
        val state = viewModel.videoState.value
        player?.let { exoPlayer ->
            exoPlayer.playWhenReady = state.playWhenReady
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.seekTo(
                state.currentWindow,
                state.playbackPosition
            )
            exoPlayer.prepare()
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun initializePlayer() {
        binding.videoImage?.visibility = View.VISIBLE

        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        player = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                binding.playerView.player = exoPlayer
            }
        val pageState = viewModel.videoPageState.value

        cancelButton = binding.playerView.findViewById(R.id.cross_im)
        fullScreenButton = binding.playerView.findViewById(R.id.full_screen)
        titleTextView = binding.playerView.findViewById(R.id.header_tv)

        fullScreenButton?.setOnClickListener {
            val orientation = viewModel.videoState.value.orientation
            if (orientation == UiOrientation.PORTRAIT) {
                this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                viewModel.execute(VideoPageEvents.UpdateOrientation(UiOrientation.LANDSCAPE))
            } else {
                this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                viewModel.execute(VideoPageEvents.UpdateOrientation(UiOrientation.PORTRAIT))
            }
        }
        cancelButton?.setOnClickListener {
            finish()
        }

        player?.addListener(
            idle = {
                binding.progressBar.visibility = View.VISIBLE
            },
            ready = {
                binding.videoImage?.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
            },
            buffering = {
                binding.progressBar.visibility = View.VISIBLE
            },
            ended = {

            },
            onListen = {
                listener = it
            }
        )

        if(pageState.video != null) {
            playVideo(pageState.video)
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUi()
            viewModel.execute(VideoPageEvents.UpdateOrientation(UiOrientation.LANDSCAPE))
            height = binding.playerView.layoutParams.height
            val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            binding.playerView.layoutParams = layoutParams
            binding.descriptionLayout?.visibility = View.GONE
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewModel.execute(VideoPageEvents.UpdateOrientation(UiOrientation.PORTRAIT))
            val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height!!)
            binding.playerView.layoutParams = layoutParams
            binding.descriptionLayout?.visibility = View.VISIBLE
            showSystemUi()
        }
    }

    private fun hideSystemUi() {
        if (Util.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        binding.playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun showSystemUi() {
        if (Util.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN.inv(),
                WindowManager.LayoutParams.FLAG_FULLSCREEN.inv())
        }

        binding.playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
        Log.d("TAG", "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.videoState.value.orientation == UiOrientation.LANDSCAPE) {
            hideSystemUi()
        }
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer()
            Log.d("TAG", "onStart: ")
        }
        Log.d("TAG", "onStart: ")
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
        Log.d("TAG", "onStart: ")
    }


    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
        Log.d("TAG", "onStart: ")
    }
}
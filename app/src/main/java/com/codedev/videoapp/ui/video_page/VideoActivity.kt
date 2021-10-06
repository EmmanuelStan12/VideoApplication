package com.codedev.videoapp.ui.video_page

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
import android.os.PersistableBundle
import com.google.android.material.snackbar.Snackbar

enum class UiOrientation {
    LANDSCAPE, PORTRAIT
}


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

    private var orientation: UiOrientation = UiOrientation.PORTRAIT

    private val args: VideoActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel.execute(VideoPageEvents.GetVideoEvent(args.videoId))

        lifecycleScope.launchWhenStarted {
            viewModel.videoPageState.collect {
                if (it.isLoading) {
                    binding.progressBar.visibility = View.VISIBLE
                }
                if (it.video != null && !it.isLoading) {
                    binding.progressBar.visibility = View.GONE
                    Log.d("TAG", "onViewCreated: ${it.video.toString()}")
                    playVideo(it.video)
                }
                if (it.error != "" && !it.isLoading) {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, it.error, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun releasePlayer() {
        player?.run {
            viewModel.execute(
                VideoPageEvents.SaveVideoState(
                    VideoState(this.playWhenReady, this.currentWindowIndex, this.currentPosition)
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
            exoPlayer.seekTo(
                state.currentWindow,
                state.playbackPosition
            )
            exoPlayer.prepare()
            exoPlayer.setMediaItem(mediaItem)
        }
    }

    private fun initializePlayer() {

        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        player = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                binding.playerView.player = exoPlayer
            }

        cancelButton = binding.playerView.findViewById(R.id.cross_im)
        fullScreenButton = binding.playerView.findViewById(R.id.full_screen)
        titleTextView = binding.playerView.findViewById(R.id.header_tv)

        fullScreenButton?.setOnClickListener {
            Log.d("PlayerActivity", "initializePlayer: fullScreenButton clicked")
            if (orientation == UiOrientation.PORTRAIT) {
                (this as Activity).requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                hideSystemUi()
                orientation = UiOrientation.LANDSCAPE
            } else {
                (this as Activity).requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                orientation = UiOrientation.PORTRAIT
            }
        }
        cancelButton?.setOnClickListener {
            finish()
        }

        player?.addListener(
            idle = {
                Log.d("TAG", "initializePlayer: Idle")
            },
            ready = {
                Log.d("TAG", "initializePlayer: ready")
            },
            buffering = {
                Log.d("TAG", "initializePlayer: buffering")
            },
            ended = {
                Log.d("TAG", "initializePlayer: ended")
            },
            onListen = {
                listener = it
            }
        )
    }

    private fun hideSystemUi() {
        binding.playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        //hideSystemUi()
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }


    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }
}
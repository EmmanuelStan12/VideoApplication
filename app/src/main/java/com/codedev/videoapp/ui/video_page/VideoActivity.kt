package com.codedev.videoapp.ui.video_page

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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

@AndroidEntryPoint
class VideoActivity : AppCompatActivity() {

    private var _binding: ActivityVideoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VideoViewModel by viewModels()

    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L
    private var listener: Player.Listener? = null

    private var cancelButton: AppCompatImageView? = null
    private var fullScreenButton: AppCompatImageView? = null

    private val args: VideoActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel.execute(VideoPageEvents.GetVideoEvent(args.videoId))

        lifecycleScope.launchWhenStarted {
            viewModel.videoPageState.collect {
                if (it.isLoading) {

                }
                if (it.video != null && !it.isLoading) {
                    Log.d("TAG", "onViewCreated: ${it.video.toString()}")
                    //playVideo(it.video)
                }
            }
        }
    }

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            listener?.let { removeListener(it) }
            release()
        }
        player = null
    }

    private fun initializePlayer() {

        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        val media = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        val uri = Uri.parse(media)
        val mediaItem = MediaItem.fromUri(uri)
        player = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                binding.playerView.player = exoPlayer
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentWindow, playbackPosition)
                exoPlayer.prepare()
                exoPlayer.setMediaItem(mediaItem)
            }

        cancelButton = binding.playerView.findViewById<AppCompatImageView>(R.id.cross_im)
        fullScreenButton = binding.playerView.findViewById<AppCompatImageView>(R.id.full_screen)

        fullScreenButton?.setOnClickListener {
            Log.d("PlayerActivity", "initializePlayer: fullScreenButton clicked")
        }
        cancelButton?.setOnClickListener {
            Log.d("PlayerActivity", "initializePlayer: cancel Clicked")
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
        hideSystemUi()
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
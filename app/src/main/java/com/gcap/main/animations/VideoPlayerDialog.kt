package com.gcap.main.animations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.gcap.R

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.ui.PlayerView

class VideoPlayerDialog(private val videoUrl: String) : DialogFragment() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_video_player, container, false)
    }

    override fun onStart() {
        super.onStart()

        // Make dialog fullscreen
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.black)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val playerView = view.findViewById<PlayerView>(R.id.playerView)

        player = ExoPlayer.Builder(requireContext()).build()
        playerView.player = player

        player?.apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            play()
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
package com.zack.exoplayer

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.util.Log
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player

import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import java.io.IOException

class Player(val context: Context) {

    private lateinit var mSimpleExoPlayer: SimpleExoPlayer
    private lateinit var mExoPlayerView: PlayerView

    init {

    }

    /**
     * 初始化player
     */
    private fun initPlayer() {
        val renderersFactory = DefaultRenderersFactory(context)
        //1. 创建一个默认的 TrackSelector
        val trackSelector = DefaultTrackSelector()
        //2.创建ExoPlayer
        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, renderersFactory, trackSelector)
        //3.创建SimpleExoPlayerView
        mExoPlayerView = PlayerView(context)
        //4.为SimpleExoPlayer设置播放器
        mExoPlayerView.player = mSimpleExoPlayer

    }

    private fun openPlay(url: String) {
        //测量播放过程中的带宽。 如果不需要，可以为null。
        val bandwidthMeter = DefaultBandwidthMeter()
        // 生成加载媒体数据的DataSource实例。
        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "useExoplayer"), bandwidthMeter)
        // 生成用于解析媒体数据的Extractor实例。

        val factory = ExtractorMediaSource.Factory(dataSourceFactory)
        factory.setExtractorsFactory(DefaultExtractorsFactory())
        val videoSource = factory.createMediaSource(Uri.parse(url))
        videoSource.addEventListener(Handler(), object : DefaultMediaSourceEventListener() {
            override fun onLoadCompleted(windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId?, loadEventInfo: MediaSourceEventListener.LoadEventInfo?, mediaLoadData: MediaSourceEventListener.MediaLoadData?) {
                super.onLoadCompleted(windowIndex, mediaPeriodId, loadEventInfo, mediaLoadData)
            }

            override fun onLoadError(windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId?, loadEventInfo: MediaSourceEventListener.LoadEventInfo?, mediaLoadData: MediaSourceEventListener.MediaLoadData?, error: IOException?, wasCanceled: Boolean) {
                super.onLoadError(windowIndex, mediaPeriodId, loadEventInfo, mediaLoadData, error, wasCanceled)
            }

        })

        mSimpleExoPlayer.addVideoListener(videoListener)
        mSimpleExoPlayer.addListener(eventListener)
        mSimpleExoPlayer.prepare(videoSource)


    }


    private fun releasePlayer() {
        mSimpleExoPlayer.release()
        mSimpleExoPlayer.removeListener(eventListener)
    }

    private val videoListener = object : VideoListener {
        override fun onSurfaceSizeChanged(width: Int, height: Int) {
            Log.i("zack", "onSurfaceSizeChanged [$width / $height]")
        }

        override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
            Log.i("zack", "onVideoSizeChanged  [$width / $height] , [$unappliedRotationDegrees / $pixelWidthHeightRatio]")
        }

        override fun onRenderedFirstFrame() {
            Log.i("zack", "onRenderedFirstFrame")
        }
    }

    private val eventListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    Log.i("zack", "STATE_READY")
                    mSimpleExoPlayer.playWhenReady
                }
                Player.STATE_ENDED -> {
                    Log.i("zack", "STATE_ENDED")
                }
                Player.STATE_BUFFERING -> {
                    Log.i("zack", "STATE_BUFFERING")
                }
                Player.STATE_IDLE -> {
                    Log.i("zack", "STATE_IDLE")
                }
            }

        }


        override fun onPlayerError(error: ExoPlaybackException?) {
            Log.i("zack", "onPlayerError > $error")
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            Log.i("zack", "onLoadingChanged > $isLoading")
        }

        override fun onPositionDiscontinuity(reason: Int) {
            Log.i("zack", "onPositionDiscontinuity > $reason")
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
            Log.i("zack", "onTimelineChanged > $timeline , $manifest , $reason")
        }

        override fun onSeekProcessed() {
            Log.i("zack", "onSeekProcessed")
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
            Log.i("zack", "onPlaybackParametersChanged [$playbackParameters]")
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            Log.i("zack", "onTracksChanged [$trackGroups] / [$trackSelections]")
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            Log.i("zack", "onRepeatModeChanged [$repeatMode]")
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            Log.i("zack", "onShuffleModeEnabledChanged [$shuffleModeEnabled]")
        }
    }

}
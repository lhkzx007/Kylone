package com.kylone;

import android.os.Bundle;

import com.kylone.base.BaseActivity;
import com.kylone.player.MainVideoView;
import com.kylone.player.R;
import com.kylone.player.callback.SimpleControlListener;
import com.kylone.player.controller.VodControllerManager;

/**
 * Created by zack
 */

public class PlayActivity extends BaseActivity {
    private MainVideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initView();
    }

    private void initView() {
        video = (MainVideoView) findViewById(R.id.play_video);
//        IPlayer.OnPreparedListener l;
//        video.setOnPreparedListener(l);

        VodControllerManager vod = new VodControllerManager(this);
        vod.setVideoPlayer(video);
        vod.setControlListener(new SimpleControlListener() {
            @Override
            public void onPrepared() {
//                tvTitle.setText(manager.getFilmTitle());
            }

            @Override
            public boolean onError() {
                ;
                return super.onError();
            }

            @Override
            public void onCompletion() {
                finish();
            }
        });
        Bundle arge = new Bundle();
//        arge.putString("url", getIntent().getStringExtra("url"));
        arge.putString("url", "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
        arge.putString("title", getIntent().getStringExtra("txt"));
        vod.changArguments(arge);


    }
}

package com.kylone.player.callback;


import com.kylone.video.VideoUrl;

public class SimpleControlListener implements ControlListener {
    @Override
    public void onInfo(int what) {
    }

    @Override
    public void onAdPrepared() {

    }

    @Override
    public void onPrepared() {

    }

    @Override
    public void onCompletion() {

    }

    @Override
    public boolean onError() {
        return false;
    }

    @Override
    public void onChangeSet() {

    }

    @Override
    public void onChangeFavorite(boolean isFav) {

    }

    @Override
    public void onPlayInfoChange(VideoUrl mInfo) {

    }

    @Override
    public void onCloseVod(boolean isComplication, int msg, String uuid) {

    }
}

package com.kylone.player.callback;

import com.kylone.video.VideoUrl;

public interface ControlListener {

    void onInfo(int what);

    void onAdPrepared();

    void onPrepared();

    void onCompletion();

    boolean onError();

    void onChangeSet();

    void onChangeFavorite(boolean isFav);

    void onPlayInfoChange(VideoUrl mInfo);

    void onCloseVod(boolean isComplication, int msg, String uuid);
}

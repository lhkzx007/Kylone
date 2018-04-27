package com.kylone.player.callback;

/**
 * 播放器控制回调
 */
public interface IPlayerControlCallback {
    public int getBufferPercentage();

    public boolean seekTo(int pos);

    public boolean isPlaying();

    public long getDuration();

    public long getPosition();

    public void executePlay();

    public void executePause();
}

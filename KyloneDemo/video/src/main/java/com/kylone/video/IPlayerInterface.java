package com.kylone.video;

/**
 * Created by  frank.z on 2016/8/10
 */
public interface IPlayerInterface extends
        IPlayer.OnInfoListener,
        IPlayer.OnPreparedListener,
        IPlayer.OnErrorListener,
        IPlayer.OnCompletionListener,
        IPlayer.OnDefinitionListener,
        IPlayer.OnPostrollAdPreparedListener,
        IPlayer.OnPreAdPreparedListener,
        IPlayer.OnMidAdPreparedListener,
        IPlayer.OnLogoPositionListener,
        IPlayer.OnLoadSDKListener,
        IPlayer.OnTimedTextChangedListener,
        IPlayer.OnVideoSizeChangedListener,
        IPlayer.OnBufferingUpdateListener,
        IPlayer.OnSeekCompleteListener
{
}

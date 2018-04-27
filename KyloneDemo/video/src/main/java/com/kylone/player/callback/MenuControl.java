package com.kylone.player.callback;

import android.util.SparseArray;

import java.util.ArrayList;

/**
 * Created by zack
 */
public interface MenuControl extends IPlayerControlCallback, IPlayerInfoCallback {
    int INDEX_VOLUME = 0;//音量调节
    int INDEX_DECODE = 1;//视频解码
    int INDEX_SCALE = 2; //画面比例
    int INDEX_PLATFORM = 3;//视频来源
    int INDEX_QUALITY = 4;//视频质量
    int INDEX_LIVE_PLATFORM = 5;//直播选源
    int INDEX_LIVE_TIME_SHIFT = 6;//时移选源
    int INDEX_FAVROITE = 7;//视频收藏
    int INDEX_LIVE_BARRAGE = 8;//弹幕开关
    int INDEX_OLDMAN_TYPE = 9;//老人模式
    int INDEX_LIVE_FEEDBACK = 10;//问题反馈
    int INDEX_PLAY_TYPE = 11;//播放模式---单个循环，列表循环,顺序播放


    /**
     * 需要开启的设置
     * <p/>
     * 引用资源文件string mipmap drawable   资源ID
     *
     * @return int[设置名称][设置图片]   设置显示的名称与图片
     */
    public SparseArray<SettingInfo> supportSetting();

    /**
     * 变成设置后的回调
     *
     * @param index  supportSttring 时数组的一纬下标
     * @param change 变更后的参数 参数的下标
     */
    public void changeSetting(int index, Object change);

    /**
     * 通过下标获取某项设置的当前值
     *
     * @param index supportSttring 时数组的一纬下标
     * @return 设置内容
     */
    public Object getSetting(int index);

    /**
     * 获取指定下标的所有设置内容
     *
     * @param index
     * @return
     */
    public ArrayList getSettings(int index);

    /**
     * 获取平台图标连接
     *
     * @param platform
     * @return
     */
    public String getPlatformIconUrl(String platform);

    public String parseName(int index, Object name);

    public void post(int index, Object... o);

    /**
     * 返回当前控制器是属于直播还是点播;
     *
     * @return "直播" or "点播" or other
     */
    String getControllerType();
}

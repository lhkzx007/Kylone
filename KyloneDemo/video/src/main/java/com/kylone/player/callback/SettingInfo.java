package com.kylone.player.callback;

import java.util.ArrayList;

/**
 * Created by zack
 */
public class SettingInfo<T> {
    int settingIndex;//菜单设置项的索引   //index不够灵活
    int settingTitle;//菜单设置的名称ID
    int settingPic; //菜单设置的图片资源ID
    ArrayList<T> settings;//子选项
    boolean isAdd = true;

    public SettingInfo(int settingIndex, int settingTitle, int settingPic, ArrayList<T> settings, T setting) {
        this(settingIndex, settingTitle, settingPic, settings, setting, true);
    }

    public SettingInfo(int settingIndex, int settingTitle, int settingPic, ArrayList<T> settings, T setting, boolean isAdd) {
        this.settingIndex = settingIndex;
        this.settingTitle = settingTitle;
        this.settingPic = settingPic;
        this.settings = settings;
        this.setting = setting;
        this.isAdd = isAdd;
    }

    public void setIsAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }

    public boolean isAdd() {
        return isAdd;
    }

    T setting; //当前被选择项

    public int getSettingTitle() {
        return settingTitle;
    }

    public void setSettingTitle(int settingTitle) {
        this.settingTitle = settingTitle;
    }

    public int getSettingPic() {
        return settingPic;
    }

    public void setSettingPic(int settingPic) {
        this.settingPic = settingPic;
    }

    public ArrayList<T> getSettings() {
        return settings;
    }

    public void setSettings(ArrayList<T> settings) {
        this.settings = settings;
    }

    public T getSetting() {
        return setting;
    }

    public void setSetting(T setting) {
        this.setting = setting;
    }

    public int getSettingIndex() {
        return settingIndex;
    }

    public void setSettingIndex(int settingIndex) {
        this.settingIndex = settingIndex;
    }

    public void putSetting(T setting) {
        if (settings == null) {
            settings = new ArrayList<T>();
        }
        if (!settings.contains(setting)) {
            settings.add(setting);
        }
    }

    public void clear() {
        if (settings != null) {
            settings.clear();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SettingInfo)) return false;

        SettingInfo<?> that = (SettingInfo<?>) o;

        if (settingIndex != that.settingIndex) return false;
        if (settingTitle != that.settingTitle) return false;
        if (settingPic != that.settingPic) return false;
        if (isAdd != that.isAdd) return false;
        return settings.equals(that.settings);
    }

    @Override
    public int hashCode() {
        int result = settingIndex;
        result = 31 * result + settingTitle;
        result = 31 * result + settingPic;
        result = 31 * result + settings.hashCode();
        result = 31 * result + (isAdd ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SettingInfo [settingIndex=");
        builder.append(settingIndex);
        builder.append(", settingTitle=");
        builder.append(settingTitle);
        builder.append(", settingPic=");
        builder.append(settingPic);
        builder.append(", settings=");
        builder.append(settings);
        builder.append(", isAdd=");
        builder.append(isAdd);
        builder.append(", setting=");
        builder.append(setting);
        builder.append("]");
        return builder.toString();
    }


}

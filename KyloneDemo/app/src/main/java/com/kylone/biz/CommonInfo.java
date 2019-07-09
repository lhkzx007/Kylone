package com.kylone.biz;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zack
 */
public class CommonInfo {
    private String mTitle;
    private String mImage;
    private String mIcon;
    private String Action;
    private String key;
    private String value;

    private HashMap<String, String> values = null;

    public CommonInfo() {
    }

    public CommonInfo(JSONObject json) {
        try {
            mTitle = json.optString("title");
            mImage = json.optString("image");
            mIcon = json.optString("icon");
            Action = json.optString("action");
            key = json.optString("key");
            value = json.optString("value");
            parse(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * key - value
     * @param key    split - "\\|"
     * @param value  split - "\\|"
     */
    public void parse(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            String[] keys = key.split("\\|");
            String[] values = value.split("\\|");
            if (keys.length == values.length) {
                this.values = new HashMap<>();
                for (int i = 0; i < keys.length; i++) {
//                    intent.putExtra(keys[i], values[i]);
                    this.values.put(keys[i], values[i]);
                }
            }
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImage() {
        return mImage;
    }

    public String getIcon() {
        return mIcon;
    }

    public String getAction() {
        return Action;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public HashMap<String ,String> getValues(){
        return values;
    }

    public String getValue(String key) {
        return values != null ? values.get(key) : null;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public void setIcon(String mIcon) {
        this.mIcon = mIcon;
    }

    public void setAction(String action) {
        Action = action;
    }

    public void setValue(String key, String value) {
        this.key = key;
        this.value = value;
        parse(key, value);
    }

    public static ArrayList<CommonInfo> parseInfo(String infoJ) {
        ArrayList<CommonInfo> infos = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(infoJ);
            JSONArray items = object.optJSONArray("item");
            for (int i = 0; i < items.length(); i++) {
                CommonInfo info = new CommonInfo(items.optJSONObject(i));
                infos.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infos;
    }
}


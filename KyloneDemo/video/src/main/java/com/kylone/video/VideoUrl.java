package com.kylone.video;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class VideoUrl {
    public int start = -1;
    public int end = -1;
    public int quality = 0;   //清晰度
    public String srtUrl = null; //字幕连接
    public HashMap<String, LinkedHashMap<Long, String>> mAllUrls = new HashMap<String, LinkedHashMap<Long, String>>(); //所有Url
    private String mLanguage;  //语言
    public String url = null;  //连接
    public String name = null;  //
    private int index = 0;


    public boolean hasMoreLanguage() {
        return mAllUrls.size() > 1;
    }

    @Override
    public String toString() {
        return "VideoUrl [start=" + start + ", name =" + name + ", end=" + end + ", quality=" + quality + ", url=" + url
                + ", srtUrl=" + srtUrl + "]";
    }

}

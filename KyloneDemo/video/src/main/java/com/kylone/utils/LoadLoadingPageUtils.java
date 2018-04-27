package com.kylone.utils;

import android.text.TextUtils;
import android.widget.ImageView;

import com.kylone.base.ComponentContext;

import java.io.File;

/**
 * Created by frank.z on 2017/5/23
 */

public class LoadLoadingPageUtils {

    private static final String VOD_START_PIC_NAME = "vod_start_pic.png";
    private static final String KEY_VOD_URL = "vod_play_loading_url";
    private final int mType;
    private final String mSpKey;
    private final String mName;
//    private final DisplayImageOptions options;
    private String mUrl;
    private String mPicUrl;
    private String smallPic;
    private String pic;
    private File fullFile;
    private File file;

    public LoadLoadingPageUtils(int type) {
        this(type, null);
    }

    public LoadLoadingPageUtils(String uri) {
        this(-1, uri);
    }

    private LoadLoadingPageUtils(int type, String uri) {
        mUrl = uri;
        mType = type;
        mSpKey = String.format("%s_%s", mType, KEY_VOD_URL);
        mName = String.format("%s_%s", mType, VOD_START_PIC_NAME);
//        boolean isExcellentDevice = Utils.isExcellentDevice(ComponentContext.getContext());
//        Bitmap.Config config = !isExcellentDevice ? Bitmap.Config.RGB_565 : Bitmap.Config.ARGB_8888;
//        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true)
//                .showImageForEmptyUri(R.mipmap.vod_loading)
//                .showImageOnFail(R.mipmap.vod_loading)
//                .considerExifParams(true).bitmapConfig(config).build();
        if (TextUtils.isEmpty(uri)) {
            loadNewPage();
        } else {
            ThreadManager.execute(new Runnable() {
                @Override
                public void run() {
                    checkoutLoadPic(mUrl, true);
                }
            });
        }
    }

    private void loadNewPage() {
        ThreadManager.execute(new Runnable() {
            @Override
            public void run() {
//                try {
//                    if (TextUtils.isEmpty(mUrl)) {
//                        HashMap<String, String> map = new LinkedHashMap<String, String>();
//                        map.put("type", String.valueOf(mType));
//                        map.put("channel", Utils.getUmengChannel(ComponentContext.getContext()));
//                        map.put("version", String.valueOf(Utils.getVersionCode()));
//
//                        mUrl = VstRequestHelper.getRequestUrl(map, "syspic");
//                    }
//                    String json = HttpHelper.getJsonContent(mUrl);
//                    LogUtil.i("json :" + json);
//                    if (!TextUtils.isEmpty(json)) {
//                        JSONObject oJson = new JSONObject(json);
//                        String area = oJson.optString("area");
//                        String areaBlock = oJson.optString("areaBlock");
//                        String boxBlock = oJson.optString("boxBlock");
//                        String box = oJson.optString("box");
//                        pic = oJson.optString("data");
//
//                        if (!TextUtils.isEmpty(pic) && ADManager.isAddThisAdPic(area, areaBlock, box, boxBlock)) {
//                            checkoutLoadPic(pic, true);
//                        }
//
//                        JSONObject info = oJson.optJSONObject("info");
//                        if (info != null && info.has("smallPic") && ADManager.isAddThisAdPic(area, areaBlock, box, boxBlock)) {
//                            smallPic = info.optString("smallPic");
//                            if (!TextUtils.isEmpty(smallPic) && !"null".equalsIgnoreCase(smallPic))
//                                checkoutLoadPic(smallPic, false);
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    private File getPath(boolean isFull) {
        File tempFile = null;

        if (fullFile != null || file != null) {
            tempFile = isFull ? fullFile : file;
        }

        if (tempFile == null) {
            File picture = new File(ComponentContext.getContext().getCacheDir(),"picture");
            if (!picture.exists()){
                picture.mkdirs();
            }
            tempFile = new File(picture, String.format("%s%s", isFull ? "" : "small_", mName));
        }

        return tempFile;
    }

    public String getPicUri() {
        return mPicUrl;
    }

    private void checkoutLoadPic(String pageUrl, boolean isFull) {
//        mPicUrl = pageUrl;
//        String mKey = !isFull ? "small_" + mSpKey : mSpKey;
//        File liveStartPicFile = getPath(isFull);
//        LogUtil.d("checkoutLoadPic -- " + liveStartPicFile);
//        boolean isNeedDown = false;//是否需要下载
//        if (liveStartPicFile.exists()) {
//            String saveUrl = PreferenceUtil.getString(mKey);
//            LogUtil.i(" checkoutLoadPic saveUrl :" + saveUrl);
//            if (TextUtils.isEmpty(saveUrl)) {
//                saveUrl = "default";
//            }
//            if (!saveUrl.equals(pageUrl)) {
//                isNeedDown = true;
//            }
//        } else {
//            isNeedDown = true;
//        }
//        if (isNeedDown) {
//            LogUtil.d("checkoutLoadPic isNeedDown : " + liveStartPicFile);
//            boolean result = Utils.downLoafFileFromNet(liveStartPicFile, pageUrl);
//            LogUtil.i(" checkoutLoadPic isDownLoad :" + result);
//            if (result) {
//                PreferenceUtil.putString(mKey, pageUrl);
//            }
//        }
    }

    public void setImage(ImageView view, boolean isFull) {
//        if (view != null) {
//            File file = getPath(isFull);
//            if (file.exists()) {
//                ImageLoader.getInstance().displayImage("file:/" + file.getAbsolutePath(), view, options);
//                return;
//            }
//            view.setImageResource(R.mipmap.vod_loading);
//        }
    }
}

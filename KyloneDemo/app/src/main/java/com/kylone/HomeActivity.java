package com.kylone;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.kylone.adapter.CommonAdapter;
import com.kylone.base.BaseActivity;
import com.kylone.biz.CommonInfo;
import com.kylone.player.R;
import com.kylone.shcapi.shApiMain;
import com.kylone.utils.ApiUtils;
import com.kylone.utils.HandlerUtils;
import com.kylone.utils.LogUtil;
import com.kylone.utils.ScreenParameter;
import com.kylone.utils.ThreadManager;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by zack
 */

public class HomeActivity extends BaseActivity {
    private CommonAdapter adapter;
    private RecyclerView rv;
    private ArrayList<CommonInfo> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        super.onCreate(savedInstanceState);
        initView();
        initDate();
    }

    private void initView() {
        setActivityTitle("");
        rv = (RecyclerView) findViewById(R.id.home_list);
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int index = parent.getChildAdapterPosition(view);
                if (index > 0) {
                    //从第二个条目开始，距离上方Item的距离
                    outRect.left = ScreenParameter.getFitWidth(20);
                }
                outRect.bottom = ScreenParameter.getFitWidth(20);
            }
        });
        GridLayoutManager layoutManage = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManage);
        adapter = new CommonAdapter(R.layout.item_home, rv);
        adapter.setWeenRecyclerView(rv);
        rv.setAdapter(adapter);
    }

    void initDate() {
        ThreadManager.execute(new Runnable() {
            @Override
            public void run() {
                try {
//                    HandlerUtils.runUITask(new Runnable() {
//                        @Override
//                        public void run() {
//                            String lll = ApiUtils.shApi.getConfigVal("bgnd");
//                            LogUtil.i("  lllll   "+lll);
//                            findViewById(R.id.activity_home).setBackgroundDrawable(ApiUtils.shApi.createBitmapFromURL(lll));
//
//                        }
//                    });




                    items.clear();
                    if (ApiUtils.shApi.contentlist != null) {
                        shApiMain.ContentItem movie = ApiUtils.shApi.contentlist.get("item", "0");
                        if (movie == null || movie.size() == 0) {
                            LogUtil.i("无数据");
                            return;
                        }
                        Enumeration<String> keys = movie.keys();
                        while (keys.hasMoreElements()) {
                            String key = keys.nextElement();
                            shApiMain.ContentAttribute value = movie.get(key);
                            CommonInfo contextInfo = new CommonInfo();
                            StringBuilder keyCommon = new StringBuilder();
                            StringBuilder valueCommon = new StringBuilder();
                            Enumeration<String> vKeys = value.keys();
                            int index = value.size();
                            while (vKeys.hasMoreElements()) {
                                String vvkey = vKeys.nextElement();
                                    LogUtil.i("--" + vvkey);
                                String vvalue = value.get(vvkey);
                                    LogUtil.i("---" + vvalue);
                                keyCommon.append(vvkey);
                                valueCommon.append(vvalue);
//                                    LogUtil.i("--" + index);
                                if (--index != 0) {
                                    keyCommon.append("|");
                                    valueCommon.append("|");
                                }
                                if (TextUtils.equals(vvkey, "logo")) {
                                    contextInfo.setImage(vvalue);
                                } else if (TextUtils.equals(vvkey, "txt")) {
                                    contextInfo.setTitle(vvalue);
                                    contextInfo.setAction("kylone.intent.action." + vvalue);
                                }
                            }
                            LogUtil.i(" --zack--  :" + keyCommon.toString());
                            contextInfo.setValue(keyCommon.toString(), valueCommon.toString());
                            items.add(contextInfo);

                        }

                        HandlerUtils.runUITask(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setData(items);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

//
////        String a = "{\"item\":[{\"title\":\"视频点播\",\"action\":\"kylone.intent.action.VodList\",\"image\":\"1\"},{\"title\":\"电视直播\",\"action\":\"kylone.intent.action.Live\",\"image\":\"2\"},{\"title\":\"设置\",\"image\":\"2\"}]}";
//        ArrayList<CommonInfo> infos = new ArrayList<>();
//        CommonInfo info1 = new CommonInfo();
//        info1.setTitle("视频点播");
//        info1.setImage(String.valueOf(R.mipmap.image_vod));
//        info1.setAction("kylone.intent.action.VodList");
//        infos.add(info1);
//
//        CommonInfo info2 = new CommonInfo();
//        info2.setTitle("电视直播");
//        info2.setImage(String.valueOf(R.mipmap.image_live));
//        info2.setAction("kylone.intent.action.Live");
//        infos.add(info2);
//
//        CommonInfo info3 = new CommonInfo();
//        info3.setTitle("设置");
//        info3.setImage(String.valueOf(R.mipmap.image_setting));
//        infos.add(info3);
//
//        CommonInfo info4 = new CommonInfo();
//        info4.setTitle("关于");
//        info4.setImage(String.valueOf(R.mipmap.image_info));
//        infos.add(info4);
//
//        adapter.setData(infos);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
}

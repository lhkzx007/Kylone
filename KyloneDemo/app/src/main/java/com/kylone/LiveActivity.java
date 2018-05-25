package com.kylone;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kylone.adapter.CommonAdapter;
import com.kylone.base.BaseActivity;
import com.kylone.biz.CommonInfo;
import com.kylone.player.MainVideoView;
import com.kylone.player.R;
import com.kylone.player.callback.SimpleControlListener;
import com.kylone.player.controller.VodControllerManager;
import com.kylone.shcapi.shApiMain;
import com.kylone.utils.ApiUtils;
import com.kylone.utils.Conver;
import com.kylone.utils.HandlerUtils;
import com.kylone.utils.LogUtil;
import com.kylone.utils.ScreenParameter;
import com.kylone.utils.ThreadManager;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by zack
 */

public class LiveActivity extends BaseActivity {
    RecyclerView item, item_title;
//    TextView tvTitle;
    TextView tvHint;
    MainVideoView video;
    private CommonAdapter adapterItem;
    private CommonAdapter adapterItemTitle;

    private VodControllerManager manager;
    private ArrayList<CommonInfo> items = new ArrayList<>();
    private Hashtable<String, ArrayList<CommonInfo>> contents = new Hashtable<>();
    private View view_farm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        initView();
        initAdapter();
        initListener();
        initDate();
    }

    private void initView() {
        item = (RecyclerView) findViewById(R.id.live_item);
        item_title = (RecyclerView) findViewById(R.id.live_item_title);
//        tvTitle = (TextView) findViewById(R.id.live_title);
        tvHint = (TextView) findViewById(R.id.view_hint);
        view_farm = findViewById(R.id.view_farm);
        video = (MainVideoView) findViewById(R.id.live_video);
        video.setEnabled(true);
        manager = new VodControllerManager(this);
        manager.setLive(true);
        manager.setVideoPlayer(video);
        manager.setEnabled(true);
    }

    private void initListener() {
        adapterItem.setOnItemListener(new CommonAdapter.OnItemListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder v, int position) {
                LogUtil.i("item-----" + position);
                if (items != null && items.size() > 0) {
                    ArrayList<CommonInfo> infos = contents.get(items.get(position).getTitle());
                    if (infos.size() > 0) {
                        adapterItemTitle.clearSelect();
                        adapterItemTitle.setData(infos);
                        String s = "";
//                        String s = (String) manager.getFilmTitle();
                        for (int i = 0; i < infos.size(); i++) {
                            CommonInfo info = infos.get(i);
                            if (TextUtils.equals(info.getTitle(), s)) {
                                LogUtil.i("------" + s);
                                adapterItemTitle.setSelected((CommonAdapter.BaseMViewHolder) v, i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFocusChange(RecyclerView.ViewHolder v, int position, boolean hasFocus) {

            }

            @Override
            public void onSelectChange(RecyclerView.ViewHolder v, int position, boolean hasSelect) {

            }
        });

        adapterItemTitle.setOnItemListener(new CommonAdapter.OnItemListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder v, int position) {
                LogUtil.i("item_title-----" + position);
                if (!adapterItemTitle.isSelect(position)) {
                    CommonInfo data = adapterItemTitle.getData(position);
                    if (data != null) {
                        play(data.getValue("arc"), data.getTitle());
                    }
                } else {
                    fullScreen(true);
                }
            }

            @Override
            public void onFocusChange(RecyclerView.ViewHolder v, int position, boolean hasFocus) {
            }

            @Override
            public void onSelectChange(RecyclerView.ViewHolder v, int position, boolean hasSelect) {

            }
        });

        video.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && manager.isEnabled()) {
                    view_farm.setVisibility(View.VISIBLE);
                } else {
                    view_farm.setVisibility(View.GONE);
                }
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullScreen(manager.isEnabled());
            }
        });

        manager.setControlListener(new SimpleControlListener() {
            @Override
            public void onPrepared() {
//                tvTitle.setText(manager.getFilmTitle());
            }

            @Override
            public boolean onError() {
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText("No Signal");
                return super.onError();
            }

            @Override
            public void onCompletion() {
                super.onCompletion();
                fullScreen(false);
            }
        });
    }

    private void initAdapter() {
        item.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = ScreenParameter.getFitSize(10);
            }
        });
        item.setLayoutManager(new LinearLayoutManager(this));
        adapterItem = new CommonAdapter(R.layout.list_item, item);
        adapterItem.setScaleDuration(0);
        adapterItem.openSingleSelect();
        item.setAdapter(adapterItem);

        item_title.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = ScreenParameter.getFitSize(5);
            }
        });
        item_title.setLayoutManager(new LinearLayoutManager(this));
        adapterItemTitle = new CommonAdapter(R.layout.list_item_title, item_title);
        adapterItemTitle.setScaleDuration(0);
        adapterItemTitle.openSingleSelect();
        item_title.setAdapter(adapterItemTitle);



    }

    void initDate() {
//        ThreadManager.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    items.clear();
//                    contents.clear();
//                    if (ApiUtils.shApi.contentlist != null) {
//                        shApiMain.ContentCategory movie = ApiUtils.shApi.contentlist.get("tv");
//                        if (movie == null || movie.size() == 0) {
//                            LogUtil.i("无数据");
//                            HandlerUtils.runUITask(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(LiveActivity.this, "No data , Exit the page after 3 seconds. ", Toast.LENGTH_LONG).show();
//                                    HandlerUtils.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            finish();
//
//                                        }
//                                    }, 3000);
//                                }
//                            });
//                            return;
//                        }
//                        Enumeration<String> keys = movie.keys();
//
//                        while (keys.hasMoreElements()) {
//                            String key = keys.nextElement();
//                            shApiMain.ContentItem value = movie.get(key);
//                            CommonInfo infoItem = new CommonInfo();
//
//                            infoItem.setTitle(Conver.conver(key));
//                            if (TextUtils.equals("0", key)) {
//                                items.add(0, infoItem);
//                            } else {
//                                items.add(infoItem);
//                            }
//
//                            LogUtil.i(key);
//
//
//                            ArrayList<CommonInfo> contentInfos = new ArrayList<CommonInfo>();
//                            Enumeration<String> vKeys = value.keys();
//
//                            while (vKeys.hasMoreElements()) {
//                                String vKey = vKeys.nextElement();
//                                shApiMain.ContentAttribute vvalue = value.get(vKey);
//
//                                CommonInfo contextInfo = new CommonInfo();
//                                contextInfo.setTitle(vKey);
//                                contextInfo.setAction("kylone.intent.action.Player");
//
////                                LogUtil.i("-" + vKey);
//                                StringBuilder keyCommon = new StringBuilder();
//                                StringBuilder valueCommon = new StringBuilder();
//                                Enumeration<String> vvKeys = vvalue.keys();
//                                int index = vvalue.size();
//                                while (vvKeys.hasMoreElements()) {
//                                    String vvkey = vvKeys.nextElement();
////                                    LogUtil.i("--" + vvkey);
//                                    String vvvalue = vvalue.get(vvkey);
////                                    LogUtil.i("---" + vvvalue);
//                                    keyCommon.append(vvkey);
//                                    valueCommon.append(vvvalue);
////                                    LogUtil.i("--" + index);
//                                    if (--index != 0) {
//                                        keyCommon.append("|");
//                                        valueCommon.append("|");
//                                    }
//                                    if (TextUtils.equals(vvkey, "logo")) {
//                                        contextInfo.setImage(vvvalue);
//                                    }
//                                }
//                                LogUtil.i(" --zack--  :" + keyCommon.toString());
//                                contextInfo.setValue(keyCommon.toString(), valueCommon.toString());
//                                contentInfos.add(contextInfo);
//                            }
//                            contents.put(infoItem.getTitle(), contentInfos);
//                        }
//
//                        HandlerUtils.runUITask(new Runnable() {
//                            @Override
//                            public void run() {
//                                //默认选中第一条
//                                adapterItem.setSelected(0);
//                                adapterItem.setData(items);
//                                if (items != null && items.size() > 0) {
//                                    ArrayList<CommonInfo> infos = contents.get(items.get(0).getTitle());
//                                    if (infos.size() > 0) {
//                                        //默认选中第一条
//                                        adapterItemTitle.setSelected(0);
//                                        adapterItemTitle.setData(infos);
//                                        CommonInfo info = infos.get(0);
//                                        play(info.getValue("arc"), info.getTitle());
//                                    }
//                                }
//                            }
//                        });
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });


        adapterItem.setSelected(0);
        adapterItemTitle.setSelected(0);

        String itemJsons = "{\"item\":[{\"title\":\"ALL\"},{\"title\":\"卫视\"}]}";
        adapterItem.setData(CommonInfo.parseInfo(itemJsons));

        String titleJsons = "{\"item\":[{\"title\":\"cctv-1\"},{\"title\":\"cctv-2\"}]}";
        adapterItemTitle.setData(CommonInfo.parseInfo(titleJsons));

    }

    @Override
    public void onBackPressed() {
        if (!manager.isEnabled()) {
            fullScreen(false);
        } else {
            super.onBackPressed();
        }
    }

    private void play(String url, String title) {
        tvHint.setVisibility(View.GONE);
//        this.tvTitle.setText(title);
        Bundle arge = new Bundle();
        arge.putString("url", url);
        arge.putString("title", title);
        manager.changArguments(arge);
    }

    /**
     * @param enabled
     */
    private void fullScreen(boolean isFull) {
        if (isFull == manager.isEnabled()) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) video.getLayoutParams();
            if (isFull) {
                //  播放器全屏
                params.setMargins(0, 0, 0, 0);
                params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                params.addRule(RelativeLayout.BELOW, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                view_farm.setVisibility(View.GONE);
            } else {
                params.width = ScreenParameter.getFitSize(530);
                params.height = ScreenParameter.getFitHeight(300);
                params.setMargins(0, ScreenParameter.getFitSize(60), ScreenParameter.getFitSize(90), 0);
                // 播放器小窗口
                params.addRule(RelativeLayout.BELOW, R.id.activity_title);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                view_farm.setVisibility(View.VISIBLE);
            }
            video.setLayoutParams(params);
            manager.setEnabled(!isFull);
        }
    }


}

package com.kylone;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
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
    TextView tvHint, tvDoc;
    MainVideoView video;
    private CommonAdapter adapterItem;
    private CommonAdapter adapterItemTitle;

    private VodControllerManager manager;
    private ArrayList<CommonInfo> items = new ArrayList<>();
    private Hashtable<String, ArrayList<CommonInfo>> contents = new Hashtable<>();
    private String mTitle;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        initView();
        initAdapter();
        initListener();
        initDate();
    }


    Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    item_title.setVisibility(View.GONE);
                    item.setVisibility(View.GONE);
                    break;
                case 1:


                    fullScreen(true);
                    break;
            }

            return false;
        }
    };

    private void initView() {
        item = (RecyclerView) findViewById(R.id.live_item);
        item_title = (RecyclerView) findViewById(R.id.live_item_title);
        tvDoc = (TextView) findViewById(R.id.live_doc);
        tvHint = (TextView) findViewById(R.id.view_hint);
        video = (MainVideoView) findViewById(R.id.live_video);
//        video.setEnabled(true);
        manager = new VodControllerManager(this);
        manager.setLive(true);
        manager.setVideoPlayer(video);
        manager.setEnabled(true);
        handler = new Handler(callback);
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
                        for (int i = 0; i < infos.size(); i++) {
                            CommonInfo info = infos.get(i);
                            if (TextUtils.equals(info.getTitle(), mTitle)) {
                                LogUtil.i("------" + mTitle);
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

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullScreen(manager.isEnabled());
            }
        });
        manager.setLive(true);
        manager.setControlListener(new SimpleControlListener() {
            @Override
            public void onPrepared() {
//                tvTitle.setText(manager.getFilmTitle());
                handler.sendEmptyMessageDelayed(1, 5000);
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

        if (tvDoc != null) {
            String ptxt = getIntent().getStringExtra("ptxt");
            tvDoc.setText(ptxt);
        }

        ThreadManager.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    items.clear();
                    contents.clear();
                    if (ApiUtils.shApi.contentlist != null) {
                        shApiMain.ContentCategory movie = ApiUtils.shApi.contentlist.get("tv");

                        if (movie == null || movie.size() == 0) {
                            LogUtil.i("无数据");
                            HandlerUtils.runUITask(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LiveActivity.this, "No data , Exit the page after 3 seconds. ", Toast.LENGTH_LONG).show();
                                    HandlerUtils.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();

                                        }
                                    }, 3000);
                                }
                            });
                            return;
                        }
                        Enumeration<String> keys = movie.keys();

                        while (keys.hasMoreElements()) {
                            String key = keys.nextElement();
                            shApiMain.ContentItem value = movie.get(key);
                            CommonInfo infoItem = new CommonInfo();

                            infoItem.setTitle(Conver.conver(key));
                            if (TextUtils.equals("0", key)) {
                                items.add(0, infoItem);
                            } else {
                                items.add(infoItem);
                            }

                            LogUtil.i(key);


                            ArrayList<CommonInfo> contentInfos = new ArrayList<CommonInfo>();
                            Enumeration<String> vKeys = value.keys();

                            while (vKeys.hasMoreElements()) {
                                String vKey = vKeys.nextElement();
                                shApiMain.ContentAttribute vvalue = value.get(vKey);

                                CommonInfo contextInfo = new CommonInfo();
                                contextInfo.setTitle(vKey);
                                contextInfo.setAction("kylone.intent.action.Player");

//                                LogUtil.i("-" + vKey);
                                StringBuilder keyCommon = new StringBuilder();
                                StringBuilder valueCommon = new StringBuilder();
                                Enumeration<String> vvKeys = vvalue.keys();
                                int index = vvalue.size();
                                while (vvKeys.hasMoreElements()) {
                                    String vvkey = vvKeys.nextElement();
//                                    LogUtil.i("--" + vvkey);
                                    String vvvalue = vvalue.get(vvkey);
//                                    LogUtil.i("---" + vvvalue);
                                    keyCommon.append(vvkey);
                                    valueCommon.append(vvvalue);
//                                    LogUtil.i("--" + index);
                                    if (--index != 0) {
                                        keyCommon.append("|");
                                        valueCommon.append("|");
                                    }
                                    if (TextUtils.equals(vvkey, "logo")) {
                                        contextInfo.setImage(vvvalue);
                                    }
                                }
                                LogUtil.i(" --zack--  :" + keyCommon.toString());
                                contextInfo.setValue(keyCommon.toString(), valueCommon.toString());
                                contentInfos.add(contextInfo);
                            }
                            contents.put(infoItem.getTitle(), contentInfos);
                        }

                        HandlerUtils.runUITask(new Runnable() {
                            @Override
                            public void run() {
                                //默认选中第一条
                                adapterItem.setSelected(0);
                                adapterItem.setData(items);
                                if (items != null && items.size() > 0) {
                                    ArrayList<CommonInfo> infos = contents.get(items.get(0).getTitle());
                                    if (infos.size() > 0) {
                                        //默认选中第一条
                                        adapterItemTitle.setSelected(0);
                                        adapterItemTitle.setData(infos);
                                        CommonInfo info = infos.get(0);
                                        play(info.getValue("arc"), info.getTitle());
                                    }
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


//        adapterItem.setSelected(0);
//        adapterItemTitle.setSelected(0);
//
//        String itemJsons = "{\"item\":[{\"title\":\"ALL\"},{\"title\":\"卫视\"}]}";
//        adapterItem.setData(CommonInfo.parseInfo(itemJsons));
//
//        String titleJsons = "{\"item\":[{\"title\":\"cctv-1\"},{\"title\":\"cctv-2\"}]}";
//        adapterItemTitle.setData(CommonInfo.parseInfo(titleJsons));

    }

    @Override
    public void onBackPressed() {
        if (!manager.isEnabled()) {
            if (item_title.getVisibility() == View.VISIBLE) {
                item_title.setVisibility(View.GONE);
                item.setVisibility(View.GONE);
                video.requestFocus();
            } else {
                fullScreen(false);
            }
        } else {
            super.onBackPressed();
        }
    }

    private void play(String url, String title) {
        final String mUrl = url;
        mTitle = title;
        tvHint.setVisibility(View.GONE);
//        this.tvTitle.setText(title);
        Bundle arge = new Bundle();
//        url = "http://10.47.48.1:82/movie/1/doublesniper.mp4";
        arge.putString("url", url);
        arge.putString("title", title);
        manager.changArguments(arge);

//        HandlerUtils.runUITask(new Runnable() {
//            @Override
//            public void run() {
//                LogUtil.i(" url = " +mUrl);
//                Toast.makeText(getApplicationContext(),"url = "+mUrl,Toast.LENGTH_LONG).show();
//            }
//        });
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
                item_title.setVisibility(View.GONE);
                item.setVisibility(View.GONE);
            } else {
                handler.removeCallbacksAndMessages(null);
                params.width = ScreenParameter.getFitSize(530);
                params.height = ScreenParameter.getFitHeight(300);
                params.setMargins(0, ScreenParameter.getFitSize(60), ScreenParameter.getFitSize(90), 0);
                // 播放器小窗口
                params.addRule(RelativeLayout.BELOW, R.id.activity_title);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                item_title.setVisibility(View.VISIBLE);
                item.setVisibility(View.VISIBLE);
//                item_title.getLayoutManager().findViewByPosition(adapterItemTitle.getSelect(0));
                item_title.findViewHolderForAdapterPosition(adapterItemTitle.getSelect(0)).itemView.requestFocus();
//                item_title.smoothScrollToPosition(adapterItemTitle.getSelect(0));
            }
            video.setLayoutParams(params);
            manager.setEnabled(!isFull);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            handler.removeCallbacksAndMessages(null);
            if (manager != null && !manager.isEnabled()) {
                handler.sendEmptyMessageDelayed(0, 5000);
                if (item_title.getVisibility() != View.VISIBLE) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                        item.setVisibility(View.VISIBLE);
                        item_title.setVisibility(View.VISIBLE);
                        item_title.findViewHolderForAdapterPosition(adapterItemTitle.getSelect(0)).itemView.requestFocus();
//                item_title.smoothScrollToPosition(adapterItemTitle.getSelect(0));
                        return true;
                    }
                }
            } else if (manager != null && manager.isEnabled()) {
                handler.sendEmptyMessageDelayed(1, 5000);
            }

        }

        return super.dispatchKeyEvent(event);
    }
}

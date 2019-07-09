package com.kylone;

import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.kylone.adapter.CommonAdapter;
import com.kylone.adapter.SettingAdapter;
import com.kylone.base.BaseActivity;
import com.kylone.biz.CommonInfo;
import com.kylone.player.R;
import com.kylone.utils.ApiUtils;
import com.kylone.utils.HandlerUtils;
import com.kylone.utils.LogUtil;
import com.kylone.utils.MediaPerference;
import com.kylone.base.Density;
import com.kylone.utils.ThreadManager;
import com.kylone.utils.UIRunnable;
import com.kylone.video.IPlayer;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class SettingsActivity extends BaseActivity {

    private static final String ITEM_PLAY_SETTING = "play";
    private static final String ITEM_APP_INFO_SETTING = "info";

    private RecyclerView itemR, contentR;
    private CommonAdapter adapterItem;
    private CommonAdapter adapterContent;
    private TextView infoR;
    private StringBuilder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
        initAdapter();
        testData();
    }

    private void testData() {

        ArrayList<CommonInfo> items = new ArrayList<>();
        CommonInfo a = new CommonInfo();
        a.setTitle("Play setting");
        a.setAction(ITEM_PLAY_SETTING);
        items.add(a);

        CommonInfo aInfo = new CommonInfo();
        aInfo.setTitle("App info");
        aInfo.setAction(ITEM_APP_INFO_SETTING);
        items.add(aInfo);

        adapterItem.setData(items);

        ArrayList<CommonInfo> contents = new ArrayList<>();
        CommonInfo commonInfo = new CommonInfo();
        commonInfo.setTitle("Soft decode");
        commonInfo.setAction(IPlayer.SETTING_SOFT_DECODE);
        commonInfo.parse(SettingAdapter.KEY_IS_OFF,
                MediaPerference.getBoolean(
                        IPlayer.SETTING_SOFT_DECODE) ? SettingAdapter.ON : SettingAdapter.OFF);
        contents.add(commonInfo);


        CommonInfo subtitleC = new CommonInfo();
        subtitleC.setTitle("Subtitle show");
        subtitleC.setAction(IPlayer.SETTING_SUBTITLE_SHOW);
        subtitleC.parse(SettingAdapter.KEY_IS_OFF,
                MediaPerference.getBoolean(
                        IPlayer.SETTING_SUBTITLE_SHOW,true) ? SettingAdapter.ON : SettingAdapter.OFF);
        contents.add(subtitleC);

        adapterContent.setData(contents);


        StringBuilder builder = new StringBuilder();
        builder.append("Version\t\t").append(ApiUtils.currentVersion);
        builder.append("\n");
        builder.append("IP\t\t").append(ApiUtils.currentIp);
        infoR.setText(builder);
    }


    private void initView() {
        itemR = findViewById(R.id.settings_item);
        contentR = findViewById(R.id.settings_content);
        infoR = findViewById(R.id.settings_app_info);

    }

    private void initAdapter() {
        itemR.setLayoutManager(new LinearLayoutManager(this));
        itemR.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = Density.INSTANCE.dp2px(2);
            }
        });
        adapterItem = new CommonAdapter(R.layout.list_item, itemR);
        adapterItem.openSingleSelect();
        adapterItem.setSelected(0);
        itemR.setAdapter(adapterItem);


        contentR.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = Density.INSTANCE.dp2px(2);
            }
        });
        contentR.setLayoutManager(new LinearLayoutManager(this));
        adapterContent = new SettingAdapter(R.layout.item_settings, contentR);
        adapterContent.setScaleDefault(1);
        contentR.setAdapter(adapterContent);


        /* 监听器 */
        adapterItem.setOnItemListener(new CommonAdapter.OnItemListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder v, int position) {
                LogUtil.i(position + " item onClick");
            }

            @Override
            public void onFocusChange(RecyclerView.ViewHolder v, int position, boolean hasFocus) {
                LogUtil.i(position + " item onFocusChange");
            }

            @Override
            public void onSelectChange(RecyclerView.ViewHolder v, int position, boolean hasSelect) {
                LogUtil.i(position + "item onSelectChange");
                if (hasSelect) {
                    CommonInfo info = adapterItem.getData(position);
                    switch (info.getAction()) {
                        case ITEM_APP_INFO_SETTING:
                            contentR.setVisibility(View.GONE);
                            infoR.setVisibility(View.VISIBLE);
                            break;
                        case ITEM_PLAY_SETTING:
                            infoR.setVisibility(View.GONE);
                            contentR.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
        });
        adapterContent.setOnItemListener(new CommonAdapter.OnItemListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder v, int position) {
                LogUtil.i("setting onClick");
                final Switch switchView = v.itemView.findViewById(R.id.setting_switch);
                CommonInfo d = adapterContent.getData(position);
                String action = d.getAction();

                switch (action) {
                    case IPlayer.SETTING_SOFT_DECODE:
                        final boolean isOff = !switchView.isChecked();
                        switchView.setChecked(isOff);
                        MediaPerference.putBoolean(action, isOff);
                        break;
                    case IPlayer.SETTING_SUBTITLE_SHOW:
                        ThreadManager.exectueSingleTask(new Runnable() {
                            @Override
                            public void run() {

                                boolean isPermission=ApiUtils.verifyStoragePermissions(SettingsActivity.this);
                                if (!isPermission){
                                    return;
                                }
                                synchronized (switchView) {
                                    File file = new File(Environment.getExternalStorageDirectory(), ".subfilter");
                                    boolean isShow = !switchView.isChecked();
                                    //  判断是否需要显示字幕
                                    if (isShow) {
                                        //如果需要显示字幕, 需要删除文件
//                                        boolean isDelete;
                                        if (file.exists()) {
                                            // 如果删除了文件,就显示字幕
                                            isShow = file.delete();
                                            LogUtil.i(" is delete : " + isShow);
                                        } else {
                                            isShow = true;
                                        }
//                                        isShow = isDelete;
                                    } else {
                                        //如果不需要显示字幕, 就需要创建文件
                                        boolean isCrateFile = false;
                                        try {
                                            if (!file.exists()) {
                                                isCrateFile = file.createNewFile();
                                                LogUtil.i("is crate file .subfilter :" + isCrateFile);
                                            } else {
                                                isCrateFile = true;
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        //如果创建了文件, 就关闭字幕
                                        isShow = !isCrateFile;
                                    }
                                    HandlerUtils.runUITask(new UIRunnable(isShow) {
                                        @Override
                                        public void run() {
                                            switchView.setChecked((Boolean) getObjs()[0]);
                                        }
                                    });
                                    MediaPerference.putBoolean(IPlayer.SETTING_SUBTITLE_SHOW, isShow);
                                }
                            }
                        });
                        break;
                }
            }

            @Override
            public void onFocusChange(RecyclerView.ViewHolder v, int position, boolean hasFocus) {
                LogUtil.i("setting onFocusChange");
            }

            @Override
            public void onSelectChange(RecyclerView.ViewHolder v, int position, boolean hasSelect) {
                LogUtil.i("setting onSelectChange");
            }
        });

    }




}

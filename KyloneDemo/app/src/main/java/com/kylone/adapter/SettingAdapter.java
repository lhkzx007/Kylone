package com.kylone.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.kylone.biz.CommonInfo;
import com.kylone.player.R;

public class SettingAdapter extends CommonAdapter {

    /**
     *  0 关闭 - OFF
     *  1 打开 - ON
     */
    public static final String KEY_IS_OFF = "isOff";
    public static final String OFF = "0";
    public static final String ON = "1";

    public SettingAdapter(int id_layout, RecyclerView recyclerView) {
        super(id_layout, recyclerView);
    }

    @NonNull
    @Override
    public BaseMViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(id_layout, parent, false);
        return new SettingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseMViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        CommonInfo commonInfo=getData(position);
        String isOff = commonInfo.getValue(KEY_IS_OFF);
        if (TextUtils.equals(isOff,ON)){
            ((SettingHolder)holder).aSwitch.setChecked(true);
        }else{
            ((SettingHolder)holder).aSwitch.setChecked(false);
        }
    }

    public class SettingHolder extends BaseMViewHolder {
        Switch aSwitch = null;
        public SettingHolder(View itemView) {
            super(itemView);
            aSwitch = itemView.findViewById(R.id.setting_switch);
        }
    }

}

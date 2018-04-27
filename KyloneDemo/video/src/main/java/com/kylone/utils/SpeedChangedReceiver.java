package com.kylone.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SpeedChangedReceiver extends BroadcastReceiver {

    public static final String SPEED_CHANGED_BROADCAST = "intent.action.Speed_Changed_BROADCAST";
    private static final String SPEED = "SPEED";

    private CallBack callBack;

    public SpeedChangedReceiver(CallBack callBack) {
        super();
        this.callBack = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SPEED_CHANGED_BROADCAST)) {
            String speed = intent.getStringExtra(SPEED);
            if (callBack != null) {
                callBack.speedChanged(speed);
            }
        }
    }

    public interface CallBack {
        void speedChanged(String speed);
    }

}

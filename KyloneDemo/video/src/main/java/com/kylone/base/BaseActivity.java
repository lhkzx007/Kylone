package com.kylone.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.kylone.utils.LogUtil;
import com.kylone.video.R;

import java.util.Calendar;
import java.util.Locale;

/**
 */

public class BaseActivity extends FragmentActivity {
    private TextView mTime;
    private TextView mDate;
    private TextView mWifi;
    private BroadcastReceiver mClockReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(" onCreate ");
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initTitleView();
    }

    private void initTitleView() {
        mWifi = (TextView) findViewById(R.id.title_wifi);
        mTime = (TextView) findViewById(R.id.title_time);
        mDate = (TextView) findViewById(R.id.title_date);
    }

    protected void initTime() {
        if (mTime == null) {
            return;
        }

        mClockReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction()) || Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                    timeChange();
                }
            }
        };
        IntentFilter clockFilter = new IntentFilter();
        clockFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        clockFilter.addAction(Intent.ACTION_TIME_TICK);

        registerReceiver(mClockReceiver, clockFilter);
        timeChange();
    }

    protected void unTime() {
        if (mClockReceiver == null)
            return;
        unregisterReceiver(mClockReceiver);
    }

    protected void timeChange() {
        Calendar c = Calendar.getInstance();
        //取得系统日期:
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        //取得系统时间：
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        if (mTime != null)
            mTime.setText(String.format(Locale.getDefault(), "%02d : %02d", hour, minute));
        if (mDate != null)
            mDate.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year, month, day));
    }


    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(" onStart ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.d(" onRestart ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(" onResume ");
        initTime();
        if (mWifi != null) {
            mWifi.setText("xxxxx Password : xxxxxxx");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(" onPause ");
        unTime();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(" onStop ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(" onDestroy ");
        mClockReceiver = null;
        mTime = null;
        mDate = null;
    }



}

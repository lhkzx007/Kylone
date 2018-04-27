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
    TextView activityTitle;
    private TextView mTime;
    private TextView mDate;

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
        mTime = (TextView) findViewById(R.id.title_time);
        mDate = (TextView) findViewById(R.id.title_date);
    }

    protected void initTime() {
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
            mDate.setText(String.format(Locale.getDefault(), "%d 年 %02d 月 %02d 日", year, month, day));
    }


    public void setActivityTitle(String title) {
        activityTitle = (TextView) findViewById(R.id.title_title);
        if (activityTitle != null) {
            this.activityTitle.setText(title);
        }
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
        activityTitle = null;
        mTime = null;
        mDate = null;
    }


    private BroadcastReceiver mClockReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction()) || Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                timeChange();
            }
        }
    };
}

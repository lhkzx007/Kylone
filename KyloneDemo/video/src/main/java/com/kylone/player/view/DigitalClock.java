package com.kylone.player.view;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;

import com.kylone.view.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @Description: TODO(只显示24小时制的时和分)
 */
public class DigitalClock extends TextView {
    private String mFormatStr = "HH:mm";
    private Handler mHandler = new Handler();

    public DigitalClock(Context context) {
        super(context);
    }

    public DigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler.post(mTicker);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }

    public void setFormatStr(String formatStr) {
        mFormatStr = formatStr;
    }

    Runnable mTicker = new Runnable() {
        public void run() {
            Date date = new Date(System.currentTimeMillis());
            final String time = new SimpleDateFormat(mFormatStr, Locale.CHINA).format(date);
            setText(time);
            invalidate();
            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);
            mHandler.postAtTime(mTicker, next);
        }
    };

}

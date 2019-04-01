package com.kylone.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.kylone.base.ComponentContext;
import com.kylone.player.R;
import com.kylone.utils.AniUtils;
import com.kylone.utils.ApiUtils;
import com.kylone.utils.StringUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MsgDialog extends Dialog implements MarqueeTextView.OnMarqueeListener {


    private   int mGravity = Gravity.BOTTOM;
    private String bcol;
    private int numt;
    private int numl;
    private int speed;
    private int bmarg;
    private int tsize;
    private String tcol;
    private MarqueeTextView marqueeTextView;
    private String text;
//    private String[] mData;

    private static ArrayList<WeakReference<MsgDialog>> msgDialog;

    public static ArrayList<WeakReference<MsgDialog>> getMsgDialog(){
        return msgDialog;
    }

    /**
     * Creates a dialog window that uses the default dialog theme.
     * <p>
     * The supplied {@code context} is used to obtain the window manager and
     * base theme used to present the dialog.
     *
     * @param context the context in which the dialog should run
     */
    private MsgDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
        if (msgDialog == null){
            msgDialog = new ArrayList<>();
        }
        msgDialog.add(new WeakReference<>(this));
    }

    public MsgDialog(Context context, String[] data ,int gravity) {
        this(context);
//        mData = data;
        mGravity = gravity;
        setData(data);
    }

    public MsgDialog setData(String[] data) {
        tsize = StringUtils.parseInt(data[0], 0);   //文字长度 An integer which represents the pixel size of the text
        tcol = data[1];    //文字颜色 Color with alpha channel in HTML notation like #FFFFFFFF for white
        bcol = data[2];    //文字背景颜色 Color with alpha channel in HTML notation like #FFAABBCC for black
        numt = StringUtils.parseInt(data[3], 1);    //文本出现的总数 The total number of appearance of the text
        numl = StringUtils.parseInt(data[4], 1);    //文本组的总遍历数(20表示无限)。 The total number pass of the group of texts (20 means infinite).
        speed = StringUtils.parseInt(data[5], 3);   //速度可根据以下值设置; Speed can be set according to following values
        // 24 Slowest
        // 12 Slower
        // 6 Slow
        // 3 Normal
        // 2 Fast
        // 1 Faster
        // 0.7 Fastest
        bmarg = StringUtils.parseInt(data[6], 0);    //从底部以像素为单位的边距 Margin from bottom in pixels
        StringBuilder t = new StringBuilder();
        for (int i = 0; i < numt; i++) {
            if (i > 0) {
                t.append("\t\t");
            }
            t.append(data[7]);     //需要显示的文本 Text to be displayed.
        }
        text = t.toString();
        return this;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        marqueeTextView = new MarqueeTextView(getContext().getApplicationContext());
        marqueeTextView.setTextSize(tsize);
        marqueeTextView.setTextColor(Color.parseColor(tcol));
        marqueeTextView.setBackgroundColor(Color.parseColor(bcol));
        marqueeTextView.setMarqueeText(text);
        marqueeTextView.setRndDuration(speed * 1000);
        marqueeTextView.setScrollFirstDelay(500);
        if (numl >= 20) {
            marqueeTextView.setScrollMode(MarqueeTextView.SCROLL_FOREVER);
        }else{
            marqueeTextView.setScrollCount(numl);
        }

        marqueeTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        marqueeTextView.setOnMarqueeListener(this);
        setContentView(marqueeTextView);

        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            window.setGravity(mGravity);
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }

        setCanceledOnTouchOutside(false);

        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                marqueeTextView.startScroll();
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                marqueeTextView.stopScroll();
            }
        });
    }

    @Override
    public void show() {
        super.show();
//        marqueeTextView.startScroll();
    }


    @Override
    public void marqueeStop() {
        dismiss();
    }

    @Override
    public void marquee(int index) {

    }
}

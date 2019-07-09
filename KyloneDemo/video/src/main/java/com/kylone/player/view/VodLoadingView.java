package com.kylone.player.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kylone.player.callback.MenuControl;
import com.kylone.utils.HandlerUtils;
import com.kylone.utils.LoadLoadingPageUtils;
import com.kylone.utils.LogUtil;
import com.kylone.base.Density;
import com.kylone.video.R;


public class VodLoadingView extends FrameLayout {
    public static final String VODLOADING_CONTROLLER = "VodLoadingController";
    public static final int INDEX_VODLOADING = 0xF00001;
    private static final int DEFAULT_PIC_TYPE = 17;
    private static final int DEFAULT_VIP_PIC_TYPE = 23;

    private final static int UPDATE = 0;
    private final LoadLoadingPageUtils loadUtils;
    private String AdImg;
    private MenuControl mControl;
    private TextView mTxtTitle;
    private TextView mTxtHint;

    private int mPicType = DEFAULT_PIC_TYPE;//loading图类型，用来区分电影、电视剧等
    private boolean mIsFull = true;//是否是全屏，非全屏隐藏版权和标题

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (getContext() == null || (getContext() instanceof Activity
                    && ((Activity) getContext()).isFinishing())) {
                return false;
            }
            switch (msg.what) {
                case UPDATE:
                    updateView();
                    if (View.VISIBLE == getVisibility()) {
                        mHandler.removeMessages(UPDATE);
                        mHandler.sendEmptyMessageDelayed(UPDATE, 200);
                    }
                    break;
            }
            return false;
        }
    });
//    private ImageView mRunImage;
//    private ImageView mRunLoad;
    private TextView mTxtSpeed;
//    private TextView mTxtSource;
    private ImageView mBgImage;
    private LoadingRun loadingRun;
    private View runRoot;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public void setControl(MenuControl control) {
        mControl = control;
    }

    public VodLoadingView(Context context, MenuControl control) {
        this(context, DEFAULT_VIP_PIC_TYPE, true);
    }

    public VodLoadingView(Context context, int picType, boolean isFull) {
        super(context);
        mPicType = picType;
        loadUtils = new LoadLoadingPageUtils(mPicType);
        mIsFull = isFull;
        init();
        setIsFull(isFull);
    }

    public VodLoadingView(final Context context, MenuControl control, String adImg) {
        super(context);
        mControl = control;
        loadUtils = new LoadLoadingPageUtils(adImg);
        init();
    }

    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    public void setIsFull(boolean isFull) {
        if (mIsFull != isFull) {
            mIsFull = isFull;
        }
        setPageImage(isFull);
//        mTxtSource.setVisibility(mIsFull ? View.VISIBLE : View.INVISIBLE);
        mTxtTitle.setVisibility(mIsFull ? View.VISIBLE : View.INVISIBLE);
        mTxtHint.setVisibility(mIsFull ? View.VISIBLE : View.INVISIBLE);
    }

    public void show() {
        this.setVisibility(VISIBLE);
        loadingRun.show();

//        String saveUrl = loadUtils.getPicUri();
//        if (!TextUtils.isEmpty(saveUrl)) {
//            SoManagerUtil.analyticMiaoZhen(saveUrl, ADManager.MiaoZhenKey.AD_PAGE_VOD + "");
//            Analytics.onEvent(getContext(), AnalyticContans.VOD_LOADING_PAGE, saveUrl);
//            MobclickAgent.onEvent(getContext(), AnalyticContans.VOD_LOADING_PAGE, saveUrl);
//        }

        ViewParent parent = getParent();
        if (parent != null) {
            int w = ((ViewGroup) parent).getWidth();
            int h = ((ViewGroup) parent).getHeight();

            DisplayMetrics dm = getResources().getDisplayMetrics();

            LogUtil.i(w + " :" + h);
            float sw = (float) w /  dm.widthPixels;
            float sh = (float) h / dm.heightPixels;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) runRoot.getLayoutParams();

            if (sw < 0.5) {
                runRoot.setScaleX(sw * 2f);
                runRoot.setScaleY(sh * 2f);
                params.setMargins(0, 0, 0, 0);
            } else {
                runRoot.setScaleX(1);
                runRoot.setScaleY(1);
                params.setMargins(0, 0, 0, 20);
            }
        }

        mHandler.sendEmptyMessage(UPDATE);
//        startAnimation();
    }

    public void hideProgressView() {
        HandlerUtils.runUITask(new Runnable() {
            @Override
            public void run() {
                LogUtil.i("-------hideProgressView------");
                loadingRun.hide();
            }
        });
    }

    public void showProgressView() {
        HandlerUtils.runUITask(new Runnable() {
            @Override
            public void run() {
                loadingRun.show();
            }
        });
    }

    public void hide() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(this, "alpha", 1f, 0.5f);
        alpha.setDuration(200);//设置动画时间
        alpha.setInterpolator(new DecelerateInterpolator());//设置动画插入器，减速
        alpha.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationEnd(Animator animator) {
                LogUtil.i("------动画结束");
                setAlpha(1);
                setVisibility(GONE);
                hideProgressView();
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        alpha.start();//启动动画。
        mHandler.removeCallbacksAndMessages(null);
    }

    private void init() {
        View view2 = LayoutInflater.from(getContext()).inflate(R.layout.layout_control_loading, this, false);
        mBgImage = (ImageView) view2.findViewById(R.id.loading_img_bg);
        mTxtHint = (TextView) view2.findViewById(R.id.loading_tv_name_t);
        if (!TextUtils.isEmpty(AdImg)) {
            mTxtHint.setVisibility(View.GONE);
        }
        mTxtTitle = (TextView) view2.findViewById(R.id.loading_tv_name);
        mTxtSpeed = (TextView) view2.findViewById(R.id.loading_tv_speed);
//        mRunImage = (ImageView) view2.findViewById(R.id.loading_img_run);
//        mRunLoad = (ImageView) view2.findViewById(R.id.loading_img_load);
//        mTxtSource = (TextView) view2.findViewById(R.id.loading_tv_source);
        runRoot = view2.findViewById(R.id.loading_l_root);
        loadingRun = new LoadingRun(runRoot);
        setPageImage(mIsFull);
//        setScaleX();
        this.addView(view2);
    }

//    public void startAnimation() {
//        RotateAnimation rotateAnimation = new RotateAnimation(0f, -360f, Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//        rotateAnimation.setDuration(500);
//        rotateAnimation.setInterpolator(new LinearInterpolator());
//        rotateAnimation.setRepeatCount(-1);
//        mRunLoad.setAnimation(rotateAnimation);
//
//    }

    private void setPageImage(boolean isFull) {
        loadUtils.setImage(mBgImage, isFull);
        LogUtil.i("checkoutLoadPic ---------------");
    }

    private void updateView() {
        if (mControl != null) {
            mControl.post(INDEX_VODLOADING, mTxtTitle, mTxtSpeed);
        }
    }
}

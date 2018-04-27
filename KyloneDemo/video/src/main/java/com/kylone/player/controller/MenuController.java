package com.kylone.player.controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kylone.player.callback.MenuControl;
import com.kylone.player.callback.SettingInfo;
import com.kylone.player.view.SimpleWheelLayout;
import com.kylone.video.R;

import java.util.ArrayList;

/**
 * Created by 张兴 on 2015/4/8
 */
public class MenuController extends Controller {
    public static final String MENU_CONTROLLER = "MenuController";


    /**
     * 这里记录的是 SimpleWheelLayout 选中的位置,是SimpleWheelLayout的里边view的下标位
     */
    private int mPosition = -1;
    private ViewGroup menuRootView;
    private int mItemWidth;
    private int mItemHeight;
    private Drawable radioBackground;
    private LinearLayout mContainer;
    private RelativeLayout mBorder; // 飞框
    private GestureDetector mGestureDetector; // 手势
    // private int mSelectedIndex=0; // 选中的下标
    private boolean isLeftRight; // 左边或者右边 如果是按左键 为true 右键为false
    private boolean isIncreament;
    public Rect mTempRect = new Rect();
    private int iconHeight;
    private MenuControl mControl;
    private SparseArray<SettingInfo> mSettinges = new SparseArray<SettingInfo>();
    private boolean isShow;
    private SimpleWheelLayout wheel;

    // private int iconWidth;

    private MenuController(Context context) {
        super(context);
    }

    public MenuController(Context context, MenuControl control) {
        this(context);
        mControl = control;
    }


    /**
     * 创建控制器视图
     *
     * @return 视图
     */
    @Override
    protected View createControlView() {
        // 手势
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float delta = (e1.getY() - e2.getY());
                if (delta >= 20) {
                    int keyCode = KeyEvent.KEYCODE_DPAD_DOWN;
                    KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, keyCode, 0);
                    wheel.onKeyDown(keyCode, event);
                } else if (delta <= -20) {
                    int keyCode = KeyEvent.KEYCODE_DPAD_UP;
                    KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, keyCode, 0);
                    wheel.onKeyDown(keyCode, event);
                }
                return true;
            }
        });
        // 创建选中的背景位图
        radioBackground = createBgDrawable();
        menuRootView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.layout_control_menu, null);
        menuRootView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
        mBorder = (RelativeLayout) menuRootView.findViewById(R.id.menu_controller_border);
        mContainer = (LinearLayout) menuRootView.findViewById(R.id.menu_controller_container);

        wheel = (SimpleWheelLayout) menuRootView.findViewById(R.id.menu_controller_wheel);
        wheel.setFocusable(false);
        wheel.setOnItemSelectedListener(new SimpleWheelLayout.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selected) {
                if (mPosition > -1) {// 将上一个选中item重新设置图片
                    TextView oldview = (TextView) wheel.getChildAt(mPosition);
                    SettingInfo settin = mSettinges.get(mSettinges.keyAt(mPosition));
                    setWheelItemIcon(oldview, getContext().getResources().getDrawable(settin.getSettingPic()));
                }
                // 去除当前选中的图片
                TextView view = (TextView) wheel.getChildAt(selected);
                setWheelItemIcon(view, null);
                // 重新构建mContainer内容
                buildView(mSettinges.keyAt(selected));

                mPosition = selected;
            }
        });
        return menuRootView;
    }

    /**
     * 设置 wheel菜单 item 的图标
     *
     * @param tv
     * @param drawable
     */
    private void setWheelItemIcon(TextView tv, Drawable drawable) {
        if (drawable != null) {
            int w = 64;
            int h = 50;
            drawable.setBounds(0, 0, w, h);
        }
        tv.setCompoundDrawables(null, drawable, null, null);
    }

    public void initWheelView() {
        isShow = true;
        SparseArray<SettingInfo> settinges = mControl.supportSetting();
        SparseArray<SettingInfo> trueSettinges = new SparseArray<SettingInfo>();
        if (settinges == null) {
            return;
        }
        int defaultSelectedIndex = -1;
        settinges.get(MenuControl.INDEX_VOLUME);
        // 拿出需要显示的item
        for (int i = 0; i < settinges.size(); i++) {
            int key = settinges.keyAt(i);
            SettingInfo setting = settinges.get(key);
            ArrayList s = setting.getSettings();

            if (MenuControl.INDEX_VOLUME == setting.getSettingIndex() || (setting.isAdd() && s != null && !s.isEmpty())) {
                trueSettinges.put(setting.getSettingIndex(), setting);
            }
        }
        int size = trueSettinges.size();
        for (int i = 0; i < trueSettinges.size(); i++) {
            int key = trueSettinges.keyAt(i);
            if (key == MenuControl.INDEX_LIVE_PLATFORM || key == MenuControl.INDEX_LIVE_TIME_SHIFT
                    || key == MenuControl.INDEX_PLATFORM) {
                defaultSelectedIndex = i;
                break;
            }
        }
        if (defaultSelectedIndex == -1) {
            defaultSelectedIndex = size / 2;
        }
        // 重新赋值给变量
        mSettinges = trueSettinges.clone();
        // 如果相同
        // if (!c) {
        wheel.removeAllViews();
        mContainer.removeAllViews();
        for (int i = 0; i < mSettinges.size(); i++) {
            int key = mSettinges.keyAt(i);
            SettingInfo setting = mSettinges.get(key);
            makeView(wheel, getContext().getResources().getString(setting.getSettingTitle()), setting.getSettingPic());
        }
        // }
        // 默认选中位置
        wheel.setSelected(defaultSelectedIndex);
    }

    /**
     * 传入的是Menu的索引
     *
     * @param index
     */
    private void buildView(int index) {
        if (index == MenuControl.INDEX_VOLUME) {
            buildVolumeView();
        } else {
            buildMenuView(index);
        }
    }

    /**
     * 设置音量界面
     */
    private void buildVolumeView() {
        if (mContainer != null && mControl != null) {
            mContainer.removeAllViews();
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_volume_seek, mContainer, false);
            mContainer.addView(view);
            SeekBar seek = (SeekBar) view.findViewById(R.id.volume_seek);
            Drawable d = getContext().getResources().getDrawable(R.mipmap.ic_vol_vernier);
//            Drawable dd = Utils.zoomDrawable(d, ScreenParameter.getRatioX(getContext()) / 1.5f);
            seek.setThumb(d);
            final AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            final int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            seek.setMax(max);
            seek.setProgress(volume);
            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
                }

                @Override
                public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
//                    try {
//                        JSONObject json = new JSONObject();
//                        json.put("menuName", "音量调解");
//                        json.put("selectType", "变更音量");
//                        Map<String, String> map = new HashMap<String, String>();
//                        map.put("menuName", "音量调解");
//                        map.put("selectType", "变更音量");
//                        MobclickAgent.onEvent(getContext(), PlayAnalytic.ANALYTIC_PLAY_TEN_MENU, map);
//                        Analytics.onEvent(getContext(), PlayAnalytic.ANALYTIC_PLAY_TEN_MENU, json);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }
            });
            seek.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        ViewGroup viewGroup = (ViewGroup) v.getParent();
                        _fly(viewGroup, viewGroup.getWidth(), mItemHeight);
                    }
                }
            });
        }
    }

    private void buildMenuView(final int index) {
        if (mContainer != null && mControl != null) {
            mContainer.removeAllViewsInLayout();
            final ArrayList<Object> settingsItem = mControl.getSettings(index);
            if (settingsItem == null || settingsItem.isEmpty()) {
                return;
            }
            for (int i = 0; i < settingsItem.size(); i++) {
                Object setting = settingsItem.get(i);
                TextView button;
                if (index == MenuControl.INDEX_OLDMAN_TYPE && !"关".equals(setting)) {
                    button = createOldmanButton(mControl.parseName(index, setting));
                } else {
                    button = createRadioButton(mControl.parseName(index, setting));
                }
                if (index == MenuControl.INDEX_LIVE_PLATFORM || index == MenuControl.INDEX_LIVE_TIME_SHIFT) {
                    CharSequence text = button.getText();
                    if (!TextUtils.isEmpty(text)) {
                        String string = ((String) text);
                        boolean ix = string.matches(".*[\\(]{1}.*[\\)]{1}");
                        if (ix) {
                            SpannableString span = new SpannableString(string);
                            int left = string.indexOf("(");
                            int right = string.indexOf(")");
                            span.setSpan(new RelativeSizeSpan(0.7f), left, right + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            button.setText(span);
                        }
                    }
                }

                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 选中的view设置为父控件的tag
                        Object cTag = mContainer.getTag();
                        if (cTag != null && cTag instanceof View) {
                            ((View) cTag).setSelected(false);
                        }

                        mContainer.setTag(view);
                        view.setSelected(true);
                        int nextSet = (Integer) view.getTag();
                        Object currentSet = mControl.getSetting(index);
                        Object changeSet = settingsItem.get(nextSet);

                        boolean isRepeat = false;
                        if (index == MenuControl.INDEX_LIVE_BARRAGE || index == MenuControl.INDEX_LIVE_FEEDBACK
                                || index == MenuControl.INDEX_LIVE_PLATFORM
                                || index == MenuControl.INDEX_LIVE_TIME_SHIFT) {
                            // 可以重复点击
                            isRepeat = true;
                        }

                        if (!currentSet.equals(changeSet) || isRepeat) {
                            mControl.changeSetting(index, changeSet);
                        }
                    }
                });
//
//                if (index == MenuControl.INDEX_PLATFORM) {
//                    button.setText("");
//                    int padding = ScreenParameter.getFitSize(getContext(), 26);
//                    button.setPadding(padding, 0, 0, 0);
//                    Drawable drawable = MediaResourceHelper.getPlatformIcon(getContext(), (String) setting);
//                    if (drawable == null) {
//                        drawable = Utils.getLocalDrawable(getContext(), R.drawable.ic_platform_other);
//                        setPlatform(button, drawable);
//                        String iconUrl = null;
//                        if (mControl != null) {
//                            iconUrl = mControl.getPlatformIconUrl((String) setting);
//                        }
//                        if (!TextUtils.isEmpty(iconUrl)) {//从网络获取
//                            ImageLoader imageLoader = ImageLoader.getInstance();
//                            imageLoader.loadImage(iconUrl, new MImageLoaderListener(button));
//                        }
//                    } else {
//                        setPlatform(button, drawable);
//                    }
//                }
                button.setTag(i);
                mContainer.addView(button);
                Object currentSetting = mControl.getSetting(index);
                if (setting.equals(currentSetting)) {
                    mContainer.setTag(button);
                    // mSelectedIndex = mContainer.getChildCount() - 1;
                }
                if (i < settingsItem.size() - 1) {
                    View line = new View(getContext());
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(1, mItemHeight / 3);
                    params.gravity = Gravity.CENTER_VERTICAL;
                    line.setBackgroundColor(0x30FFFFFF);
                    mContainer.addView(line, params);
                }
            }
            mContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    View view = (View) mContainer.getTag();
                    if (view != null) {
                        view.setSelected(true);
                        view.requestFocus();
                    }
                }
            });
        }
    }


    public void setPlatform(View view, Drawable drawable) {
        if (view instanceof android.widget.TextView) {
            if (drawable != null) {
                drawable.setBounds(0, 0, 148, 56);
            }
            ((android.widget.TextView) view).setCompoundDrawables(drawable, null, null, null);
        }
    }

    /**
     * 创建Wheel的子View
     */
    private void makeView(ViewGroup parent, CharSequence text, int resourcesId) {
        TextView tv = new TextView(getContext());
        Drawable drawable = getContext().getResources().getDrawable(resourcesId);
        setWheelItemIcon(tv, drawable);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(32);
        tv.setText(text);
        tv.setTextColor(getContext().getResources().getColorStateList(R.color.color_sel_wheel));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(tv, params);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();

        if (action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                wheel.onKeyDown(keyCode, event);
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                isLeftRight = true;
                mContainer.onKeyDown(keyCode, event);
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                isLeftRight = false;
                mContainer.onKeyDown(keyCode, event);
            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                getControllerManager().hide();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 创建单选按钮
     */
    public TextView createRadioButton(String text) {
        TextView textView = (TextView) View.inflate(getContext(), R.layout.layout_radio_menu_item, null);
        textView.setText(text);
        textView.setTextColor(getContext().getResources().getColorStateList(R.color.color_sel_item));
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_selected}, radioBackground);
        drawable.addState(new int[0], new ColorDrawable(Color.TRANSPARENT));
        textView.setBackgroundDrawable(drawable);
        textView.setFocusable(true);
        textView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, boolean hasFocus) {
                if (hasFocus) {
                    if (isShow) {
                        mContainer.getViewTreeObserver().addOnGlobalLayoutListener(
                                new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        mContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                        _fly(v, mItemWidth, mItemHeight);// 飞框
                                        isShow = false;
                                    }
                                });
                    } else {
                        isShow = false;
                        _fly(v, mItemWidth, mItemHeight);// 飞框
                    }
                }
            }
        });
        textView.setLayoutParams(new ViewGroup.LayoutParams(200, 140));
        return textView;
    }

    public TextView createOldmanButton(String text) {
        TextView textView = (TextView) View.inflate(getContext(), R.layout.layout_radio_menu_item, null);
        textView.setText(text);
        textView.setTextColor(getContext().getResources().getColorStateList(R.color.color_sel_item));
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_selected}, radioBackground);
        drawable.addState(new int[0], new ColorDrawable(Color.TRANSPARENT));
        textView.setBackgroundDrawable(drawable);
        textView.setFocusable(true);
        textView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, boolean hasFocus) {
                if (hasFocus) {
                    if (isShow) {
                        mContainer.getViewTreeObserver().addOnGlobalLayoutListener(
                                new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        mContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                        _fly(v, 500, mItemHeight);// 飞框
                                        isShow = false;
                                    }
                                });
                    } else {
                        isShow = false;
                        _fly(v, 500, mItemHeight);// 飞框
                    }
                }
            }
        });
        textView.setLayoutParams(new ViewGroup.LayoutParams(500, 140));
        return textView;
    }

    private int mLastWidth = 0;

    /**
     * 飞框
     *
     * @param tagView 目标View
     * @param width   宽
     * @param height  高
     */
    private void _fly(View tagView, int width, int height) {
        int location[] = new int[2];
        tagView.getLocationInWindow(location);
        View childView = mBorder.getChildAt(0);
        if (width <= 0) {
            width = mLastWidth;
        }
        mLastWidth = width;
        if (!mBorder.isShown() || childView.getWidth() != width) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mBorder.getLayoutParams();
            RelativeLayout.LayoutParams childRelaLparams = (RelativeLayout.LayoutParams) childView.getLayoutParams();
            childRelaLparams.height = height - iconHeight / 2;
            childRelaLparams.width = width;
            childView.setLayoutParams(childRelaLparams);
            lp.leftMargin = (int) (location[0] - childView.getX());
            // lp.topMargin = (int) (location[1] - childView.getY());
            mBorder.setLayoutParams(lp);
            mBorder.setVisibility(View.VISIBLE);
        }
        ViewPropertyAnimator animator = mBorder.animate();
        animator.setDuration(200);
        animator.x(location[0] - childView.getX());
        // animator.y(location[1] - childView.getY());
        animator.start();
    }

    private Drawable createBgDrawable() {
        Drawable checked_icon = getContext().getResources().getDrawable(R.drawable.ic_menu_item);
        iconHeight = 63;
        // iconWidth = ScreenParameter.getFitSize(getContext(), 45);
        mItemWidth = 200;
        mItemHeight = 140;
        checked_icon.setBounds(0, 0, (int) (mItemWidth * 2 / 3), (int) (mItemHeight * 2 / 3));
        return checked_icon;
    }

    @Override
    public void release() {

    }

    @Override
    public void updateControlView(Build build) {

    }

    @Override
    public void onShow() {
        initWheelView();
    }

    @Override
    public void onHide() {
        mPosition = -1;
        // if (mBorder != null) {
        // mBorder.setVisibility(View.GONE);
        // }
        mTempRect = null;
    }

}

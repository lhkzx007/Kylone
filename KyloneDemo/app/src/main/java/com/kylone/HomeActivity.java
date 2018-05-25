package com.kylone;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kylone.adapter.CommonAdapter;
import com.kylone.base.BaseActivity;
import com.kylone.biz.CommonInfo;
import com.kylone.player.R;
import com.kylone.shcapi.shApiMain;
import com.kylone.utils.ApiUtils;
import com.kylone.utils.HandlerUtils;
import com.kylone.utils.LogUtil;
import com.kylone.utils.ScreenParameter;
import com.kylone.utils.ThreadManager;
import com.kylone.view.LinearLayout;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by zack
 */

public class HomeActivity extends BaseActivity {
    private CommonAdapter adapter;
    private RecyclerView rv;
    private ArrayList<CommonInfo> items = new ArrayList<>();
    private ImageView iv;
    private RecyclerView rv_language;
    private CommonAdapter language_adapter;
    private ViewPager img_pager;
    private List<ImageView> imgList;
    private List<String> imageTitleBeanList;
    private int count;
    private SparseBooleanArray isLarge;
    private Animator animatorToLarge;
    private Animator animatorToSmall;
    private LinearLayout llDot;
    private int dotSize = 0;
    private int dotSpace = 0;
    private int delay = 3000;
    private Handler handler;
    private boolean isAutoPlay;
    private int currentItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        super.onCreate(savedInstanceState);
        dotSize = ScreenParameter.getFitSize(8);
        dotSpace = dotSize;
        initView();
        initPage();
//        initDate();
        test();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        starPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    private void initPage() {
        initAnimator();
        imgList = new ArrayList<>();


    }


    private void initView() {
        rv = (RecyclerView) findViewById(R.id.home_list);
        llDot = (LinearLayout) findViewById(R.id.home_dot);
        iv = (ImageView) findViewById(R.id.home_bg);
        img_pager = (ViewPager) findViewById(R.id.home_image);
        img_pager.setFocusable(false);

        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                //从第二个条目开始，距离上方Item的距离
                outRect.left = ScreenParameter.getFitWidth(7);
                outRect.right = ScreenParameter.getFitWidth(7);
                outRect.top = ScreenParameter.getFitWidth(7);
                outRect.bottom = ScreenParameter.getFitWidth(7);
            }
        });
        GridLayoutManager layoutManage = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManage);
        adapter = new CommonAdapter(R.layout.item_home, rv);
        rv.setAdapter(adapter);

        rv_language = (RecyclerView) findViewById(R.id.home_language);
        rv_language.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_language.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int index = parent.getChildAdapterPosition(view);
                if (index > 0) {
                    outRect.left = ScreenParameter.getFitWidth(30);
                }
            }
        });
        language_adapter = new CommonAdapter(R.layout.item_language, rv_language);
        rv_language.setAdapter(language_adapter);
    }

    void initDate() {
        ThreadManager.execute(new Runnable() {
            @Override
            public void run() {
                try {
//                    HandlerUtils.runUITask(new Runnable() {
//                        @Override
//                        public void run() {
//                            String bgnd = ApiUtils.shApi.getConfigVal("bgnd");
//                            Glide.with(HomeActivity.this).load(bgnd).into(iv);
//                            if (mIcon != null) {
//                                String logo = ApiUtils.shApi.getConfigVal("logo");
//                                Glide.with(HomeActivity.this).load(logo).into(mIcon);
//                            }
//                        }
//                    });

                    items.clear();
                    if (ApiUtils.shApi.contentlist != null) {
                        shApiMain.ContentItem movie = ApiUtils.shApi.contentlist.get("item", "0");
                        if (movie == null || movie.size() == 0) {
                            LogUtil.i("无数据");
                            return;
                        }
                        Enumeration<String> keys = movie.keys();
                        while (keys.hasMoreElements()) {
                            String key = keys.nextElement();
                            shApiMain.ContentAttribute value = movie.get(key);
                            CommonInfo contextInfo = new CommonInfo();
                            StringBuilder keyCommon = new StringBuilder();
                            StringBuilder valueCommon = new StringBuilder();
                            Enumeration<String> vKeys = value.keys();
                            int index = value.size();
                            while (vKeys.hasMoreElements()) {
                                String vvkey = vKeys.nextElement();
                                String vvalue = value.get(vvkey);
                                keyCommon.append(vvkey);
                                valueCommon.append(vvalue);
                                if (--index != 0) {
                                    keyCommon.append("|");
                                    valueCommon.append("|");
                                }
                                if (TextUtils.equals(vvkey, "logo")) {
                                    contextInfo.setImage(vvalue);
                                } else if (TextUtils.equals(vvkey, "txt")) {
                                    contextInfo.setTitle(vvalue);
                                    contextInfo.setAction("kylone.intent.action." + vvalue);
                                }
                            }
                            LogUtil.i(" --zack--  :" + keyCommon.toString());
                            contextInfo.setValue(keyCommon.toString(), valueCommon.toString());
                            items.add(contextInfo);

                        }

                        HandlerUtils.runUITask(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setData(items);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


//        String a = "{\"item\":[{\"title\":\"视频点播\",\"action\":\"kylone.intent.action.VodList\",\"image\":\"1\"},{\"title\":\"电视直播\",\"action\":\"kylone.intent.action.Live\",\"image\":\"2\"},{\"title\":\"设置\",\"image\":\"2\"}]}";

    }

    void test() {
        ArrayList<CommonInfo> infos = new ArrayList<>();
        CommonInfo info1 = new CommonInfo();
        info1.setTitle("视频点播");
        info1.setImage(String.valueOf(R.mipmap.home_item_1));
        info1.setAction("kylone.intent.action.Movie");
        infos.add(info1);

        CommonInfo info2 = new CommonInfo();
        info2.setTitle("电视直播");
        info2.setImage(String.valueOf(R.mipmap.home_item_2));
        info2.setAction("kylone.intent.action.Television");
        infos.add(info2);

        CommonInfo info3 = new CommonInfo();
        info3.setTitle("客房服务");
        info3.setImage(String.valueOf(R.mipmap.home_item_3));
        info3.setAction("kylone.intent.action.Room");
        infos.add(info3);

        CommonInfo info4 = new CommonInfo();
        info4.setTitle("关于");
        info4.setImage(String.valueOf(R.mipmap.home_item_4));
        info4.setAction("kylone.intent.action.Information");
        infos.add(info4);

        adapter.setData(infos);


        List<CommonInfo> testData = new ArrayList<>();
        CommonInfo test1 = new CommonInfo();
        test1.setTitle("China");
        test1.setImage("https://www.ifreesite.com/world/image/china_flag.png");
//        test1.setAction("kylone.intent.action.VodList");
        testData.add(test1);


        CommonInfo test2 = new CommonInfo();
        test2.setTitle("United Kingdom");
        test2.setImage("https://www.ifreesite.com/world/image/united_kingdom_flag.png");
//        test2.setAction("kylone.intent.action.VodList");
        testData.add(test2);


        CommonInfo test3 = new CommonInfo();
        test3.setTitle("Thailand");
        test3.setImage("https://www.ifreesite.com/world/image/thailand_flag.png");
//        test3.setAction("kylone.intent.action.VodList");
        testData.add(test3);


        CommonInfo test4 = new CommonInfo();
        test4.setTitle("Russia");
        test4.setImage("https://www.ifreesite.com/world/image/russia_flag.png");
//        test4.setAction("kylone.intent.action.VodList");
        testData.add(test4);


        CommonInfo test5 = new CommonInfo();
        test5.setTitle("France");
        test5.setImage("https://www.ifreesite.com/world/image/france_flag.png");
//        test4.setAction("kylone.intent.action.VodList");
        testData.add(test5);



        language_adapter.setData(testData);


        imageTitleBeanList = new ArrayList<>();
        imageTitleBeanList.add("http://pic1.win4000.com/wallpaper/2017-12-26/5a41adbb5fa18.jpg");
        imageTitleBeanList.add("http://pic1.win4000.com/wallpaper/2017-12-26/5a41adbd6316c.jpg");
        imageTitleBeanList.add("http://pic1.win4000.com/wallpaper/2017-12-26/5a41adbed16b7.jpg");
        imageTitleBeanList.add("http://pic1.win4000.com/wallpaper/2017-12-26/5a41adc32219d.jpg");

        commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = imgList.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imgList.get(position));
        }
    }


    private void initAnimator() {
        animatorToLarge = AnimatorInflater.loadAnimator(this, R.animator.scale_to_large);
        animatorToSmall = AnimatorInflater.loadAnimator(this, R.animator.scale_to_small);
    }

    // 设置完后最终提交
    public void commit() {
        if (imageTitleBeanList != null) {
            count = imageTitleBeanList.size();
            // 设置ViewPager
            setViewList(imageTitleBeanList);
            img_pager.setAdapter(new ImagePagerAdapter());
            currentItem = 0;
            img_pager.setCurrentItem(currentItem);
            img_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    // 遍历一遍子View，设置相应的背景。
                    for (int i = 0; i < llDot.getChildCount(); i++) {
                        if (i == position) {// 被选中
                            llDot.getChildAt(i).setBackgroundResource(R.drawable.dot_selected);
                            if (!isLarge.get(i)) {
                                animatorToLarge.setTarget(llDot.getChildAt(i));
                                animatorToLarge.start();
                                isLarge.put(i, true);
                            }
                        } else {// 未被选中
                            llDot.getChildAt(i).setBackgroundResource(R.drawable.dot_unselected);
                            if (isLarge.get(i)) {
                                animatorToSmall.setTarget(llDot.getChildAt(i));
                                animatorToSmall.start();
                                isLarge.put(i, false);
                            }
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            // 设置指示器
            setIndicator();
            // 开始播放
            starPlay();
        }
    }


    /**
     * 开始自动播放图片
     */
    private void starPlay() {
        // 如果少于2张就不用自动播放了
        if (count < 2) {
            isAutoPlay = false;
        } else {
            isAutoPlay = true;
            handler = new Handler();
            handler.postDelayed(task, delay);
        }
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            if (isAutoPlay) {

                if (++currentItem == count) {
                    currentItem = 0;
                }
                LogUtil.i(" currentItem : " + currentItem);
                // 正常每隔3秒播放一张图片
                img_pager.setCurrentItem(currentItem);
                handler.postDelayed(task, delay);
            } else {
                // 如果处于拖拽状态停止自动播放，会每隔5秒检查一次是否可以正常自动播放。
                handler.postDelayed(task, 5000);
            }
        }
    };


    /**
     * 根据出入的数据设置View列表
     *
     * @param imageTitleBeanList
     */
    private void setViewList(List<String> imageTitleBeanList) {
        imgList = new ArrayList<ImageView>();
        for (int i = 0; i < count; i++) {
            ImageView ivImage = new ImageView(this);
            ivImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(this).load(imageTitleBeanList.get(i)).into(ivImage);
            // 将设置好的View添加到View列表中
            imgList.add(ivImage);
        }
    }

    /**
     * 设置指示器
     */
    private void setIndicator() {
        isLarge = new SparseBooleanArray();
        // 记得创建前先清空数据，否则会受遗留数据的影响。
        llDot.removeAllViews();
        for (int i = 0; i < count; i++) {
            View view = new View(this);
            view.setBackgroundResource(R.drawable.dot_unselected);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dotSize, dotSize);
            layoutParams.leftMargin = dotSpace / 2;
            layoutParams.rightMargin = dotSpace / 2;
            layoutParams.topMargin = dotSpace / 2;
            layoutParams.bottomMargin = dotSpace / 2;
            view.setFocusable(false);
            llDot.addView(view, layoutParams);
            isLarge.put(i, false);
        }
        llDot.getChildAt(0).setBackgroundResource(R.drawable.dot_selected);
        animatorToLarge.setTarget(llDot.getChildAt(0));
        animatorToLarge.start();
        isLarge.put(0, true);
    }
}

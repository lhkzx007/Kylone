package com.kylone.utils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kylone.WelcomeActivity;
import com.kylone.base.ComponentContext;
import com.kylone.shcapi.shApiMain;
import com.kylone.widget.MarqueeTextView;
import com.kylone.widget.MsgDialog;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by Zack on 2018/4/19
 */

public class ApiUtils {
    private static final String TAG = "ApiUtils";
    private static shApi m_api;

    private static String ip = "10.47.48.1";

    public static WeakReference<Activity> currentTopActivity = null;

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        ApiUtils.ip = ip;
    }

    public static shApi getShApi(Context context) {
        if (m_api == null)
            m_api = new shApi(context);
        return m_api;
    }

    // Extend shApiMain to override some required functions (callbacks)
    public static class shApi extends shApiMain {
        public int connectState;
        private MarqueeTextView marqueeTextView;

        /*
                   It is mandatory to create constructor and save context
                   which will be used in static functions in API class.
                */
        shApi(Context ctx) {
            m_apiContext = ctx;
            LogUtil.v(TAG, "constructed");
//            m_handler = new Handler()
        }

        public void setBannerText() {
            final String banner = shgetbannertext();
//            welcometext.setText(banner);
            LogUtil.v(TAG, "banner text set as " + banner + "\n");
        }

        /*
           RemoteMessage callback may triggered as per config changes on server side.
           Server will send such info as "message" to inform app.
        */
        @Override
        public void cbRemoteMessage(final String msg) {
            HandlerUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtil.v(TAG, "~ cbRemoteMessage(" + msg + "): ");
                    if (msg.equals(SHC_MESSAGE_BANNERTEXT)) {
                        setBannerText();
                    } else if (msg.equals(SHC_MESSAGE_RESTART)) {
                        LogUtil.v(TAG, "app-restart requested by server\n");
                        restartApp();
                    } else if (msg.equals(SHC_MESSAGE_SUSPEND)) {
                        LogUtil.v(TAG, "suspending or quit requested by server\n");
                        //重启
                        restartApp();
                    } else if (msg.equals(SHC_MESSAGE_REBOOTSYSTEM)) {
                        LogUtil.v(TAG, "system-reboot requested by server\n");
                    } else if (msg.equals(SHC_MESSAGE_FWUPDATE)) {
                        LogUtil.v(TAG, "firmware update requested by server\n");
                    } else if (msg.equals(SHC_MESSAGE_EMERG)) {
                        LogUtil.v(TAG, " emerg  = " + msg);
                        String data = shgetremotedata(msg);
                        LogUtil.i("data  =  " + data);
                        if (data != null) {
                            String[] d = data.split(String.valueOf((char) 27));
                            LogUtil.i(data);
                            new MsgDialog(m_apiContext, d, Gravity.TOP).show();
                        }
                    } else {
                        char s = 27;
                        String data = shgetremotedata(msg);
                        if (data != null) {
                            String[] d = data.split(String.valueOf(s));
                            LogUtil.i(data);
                            new MsgDialog(m_apiContext, d, Gravity.BOTTOM).show();
                        }
                        LogUtil.v(TAG, "unknown messsage " + msg + " , data = " + data);
                    }
                }
            }, 50);
        }

//        public void createText(String[] d, Activity a) {
//            Activity activity = a;
//            LogUtil.i(" --- 1 ----");
//            if (d.length < 8) {
//                LogUtil.e(" The length of this data is less than 8");
//                return;
//            }
//
//            if (a == null && ApiUtils.currentTopActivity != null) {
//                activity = ApiUtils.currentTopActivity.get();
//            }
//
//
//            if (activity == null) {
//                return;
//            }
//
//            int tsize = StringUtils.parseInt(d[0], 0);   //文字长度 An integer which represents the pixel size of the text
//            String tcol = d[1];    //文字颜色 Color with alpha channel in HTML notation like #FFFFFFFF for white
//            String bcol = d[2];    //文字背景颜色 Color with alpha channel in HTML notation like #FFAABBCC for black
//            int numt = StringUtils.parseInt(d[3], 1);    //文本出现的总数 The total number of appearance of the text
//            int numl = StringUtils.parseInt(d[4], 1);    //文本组的总遍历数(20表示无限)。 The total number pass of the group of texts (20 means infinite).
//            int speed = StringUtils.parseInt(d[5], 3);   //速度可根据以下值设置; Speed can be set according to following values
//            // 24 Slowest
//            // 12 Slower
//            // 6 Slow
//            // 3 Normal
//            // 2 Fast
//            // 1 Faster
//            // 0.7 Fastest
//            int bmarg = StringUtils.parseInt(d[6], 0);    //从底部以像素为单位的边距 Margin from bottom in pixels
//            StringBuilder text = new StringBuilder();
//            for (int i = 0; i < numt; i++) {
//                if (i > 0) {
//                    text.append("\t\t");
//                }
//                text.append(d[7]);     //需要显示的文本 Text to be displayed.
//
//            }
//            marqueeTextView = new MarqueeTextView(m_apiContext);
//            marqueeTextView.setTextSize(tsize);
//            marqueeTextView.setTextColor(Color.parseColor(tcol));
//            marqueeTextView.setBackgroundColor(Color.parseColor(bcol));
//            marqueeTextView.setText(text.toString());
//            marqueeTextView.setRndDuration(speed * 1000);
//            marqueeTextView.setScrollFirstDelay(500);
//            if (numl >= 20) {
//                marqueeTextView.setScrollMode(MarqueeTextView.SCROLL_FOREVER);
//            }
//
//            ViewGroup content = activity.getWindow().getDecorView().findViewById(android.R.id.content);
//            FrameLayout.LayoutParams cparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//            cparams.gravity = Gravity.BOTTOM;
//            content.addView(marqueeTextView, cparams);
//            marqueeTextView.startScroll();
//
////                CoordinatorLayout coordinatorLayout = new CoordinatorLayout(m_apiContext);
////                FrameLayout.LayoutParams coordinatorParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////                coordinatorParams.gravity = Gravity.TOP;
////                content.addView(coordinatorLayout,coordinatorParams);
//
////                Snackbar snackbar = Snackbar.make(content, "", Snackbar.LENGTH_INDEFINITE);
////                Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
////                snackbarLayout.setBackgroundColor(0x00000000);
////                snackbarLayout.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
////                Snackbar.SnackbarLayout.LayoutParams params = new Snackbar.SnackbarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////                params.gravity = Gravity.CENTER;
////                snackbarLayout.addView(marqueeTextView, params);
////                snackbar.show();
////                snackbar.addCallback(new Snackbar.Callback() {
////                    @Override
////                    public void onShown(Snackbar sb) {
////                        super.onShown(sb);
////                        marqueeTextView.startScroll();
////                    }
////
////                    @Override
////                    public void onDismissed(Snackbar transientBottomBar, int event) {
////                        super.onDismissed(transientBottomBar, event);
////                        marqueeTextView.stopScroll();
////                    }
////                });
//
//        }

        public void restartApp() {
            Intent intent = new Intent(ComponentContext.getContext(), WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentContext.getContext().startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
        }


        // Progress callback
        @Override
        public void cbProgress(final int p) {
            HandlerUtils.runUITask(new Runnable() {
                @Override
                public void run() {
//                    progressdialog.setIndeterminate(false);
//                    progressdialog.setProgress(p);
                    LogUtil.v(TAG, "progress: " + p + "\n");
                }
            }, 50);
        }

        // Handling connection errors
        @Override
        public void cbConnectFailed(final int reason) {
            HandlerUtils.runUITask(new Runnable() {
                @Override
                public void run() {
                    connectState = -1;
//                    progressdialog.setProgress(0);
//                    progressdialog.cancel();
                    LogUtil.v(TAG, "! connect failed with reason code: " + reason + "\n");
                }
            });
        }

        // Handling config-ready trigger, see details on shconnect() below
        @Override
        public void cbConfigReady() {
            HandlerUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtil.v(TAG, "~ confguration is ready\n");
                    connectState = 1;
                    // since config is arrived, we can utilise some values
//                    Drawable res = createBitmapFromURL(getConfigVal("bgnd"));
//                    if (res != null) {
//                        appback.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                        appback.setAlpha(getAlpha(getConfigVal("opac")));
//                        appback.setImageDrawable(res);
//                        appback.setVisibility(View.VISIBLE);
//                    LogUtil.v(TAG, "~ background image set\n");
//                    }
//                    res = createBitmapFromURL(getConfigVal("logo"));
//                    if (res != null) {
//                        logoview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                        logoview.setImageDrawable(res);
//                        logoview.setVisibility(View.VISIBLE);
//                    LogUtil.v(TAG, "~ logo image set\n");
//                    }
                }
            }, 50);
        }

        // Handling content-ready trigger, see details on shconnect() below
        @Override
        public void cbContentReady() {
            HandlerUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    progressdialog.setProgress(0);
//                    progressdialog.cancel();
                    LogUtil.v(TAG, "~ content is ready\n");
               /* 
                  When content arrived, shApiMain.contentlist variable (shApiMain.ContentList) class will be constructed.
                  You can access all content infornation through this constructed class

                  Following example shows how to get list of main content items.
                  It can be determined whether there is Main Menu, Movie, TV, Music etc contents exists or not.
               */
                    LogUtil.v(TAG, "\n> showing list of root items in content as an example\n");
                    String[] rootlist = contentlist.getList();
                    for (int i = 0; i < contentlist.size(); i++) {
                        LogUtil.v(TAG, "   - " + rootlist[i] + "\n");
                    }
                    LogUtil.v(TAG, "\n");
               /*
                  Below example shows how to get list of Main Menu content (with category all = 0)
                  We will print details witll calling feddback function and add a Button for every Menu Item.
               */
//                    ContentItem menuitemsinallcategory = contentlist.get("item", "0");
//                    if (menuitemsinallcategory != null) {
//                        feedbackPrintListItems("showing list of Menu Items as an example", menuitemsinallcategory);
//                        addMenuButtons(menuitemsinallcategory);
//                    }

               /*
                  Another example below shows to access list of Movies. Instead of using category id directly from
                  get() method of shApiContentList, we will walk over on the data structure.
               */
//                    ContentCategory movies = contentlist.get("movie");
//                    if (movies != null) {
//                        ContentItem movieinallcategory = movies.get("0");
//                        if (movieinallcategory != null) {
//                            LogUtil.v(TAG,"\n> showing list of Movies with attributes as an example\n");
//                            String[] l = movieinallcategory.getList();
//                            for (int i = 0; i < movieinallcategory.size(); i++) {
//                                ContentAttribute movieattr = movieinallcategory.get(i);
//                                if (movieattr == null)
//                                    continue;
////                                feedbackPrintAttributes(l[i], movieattr);
//                        /*
//                           We will just display the first item by using break below;
//                           Please remove it to see all items with attributes in the movie list
//                        */
//                                break;
//                            }
//                        }
//                    }
//                    LogUtil.v(TAG,"\n> click one of the Menu Buttons to show its content in ListView\n");
//                    setButtonClickListeners();
                }
            }, 50);
        }
    }


    public static boolean checkPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }
}

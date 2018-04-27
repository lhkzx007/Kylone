package com.kylone.utils;

import android.content.Context;

import com.kylone.shcapi.shApiMain;

/**
 * Created by Zack on 2018/4/19
 */

public class ApiUtils {
    private static final String TAG = "ApiUtils";
    private static shApi m_api;

    public static shApi getShApi(Context context) {
        if (m_api == null)
            m_api = new shApi(context);
        return m_api;
    }

    // Extend shApiMain to override some required functions (callbacks)
    public static class shApi extends shApiMain {
        public int connectState;

        /*
                   It is mandatory to create constructor and save context
                   which will be used in static functions in API class.
                */
        public shApi(Context ctx) {
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
                    } else if (msg.equals(SHC_MESSAGE_SUSPEND)) {
                        LogUtil.v(TAG, "suspending or quit requested by server\n");
                    } else if (msg.equals(SHC_MESSAGE_REBOOTSYSTEM)) {
                        LogUtil.v(TAG, "system-reboot requested by server\n");
                    } else if (msg.equals(SHC_MESSAGE_FWUPDATE)) {
                        LogUtil.v(TAG, "firmware update requested by server\n");
                    } else {
                        LogUtil.v(TAG, "unknown messsage\n");
                    }
                }
            }, 50);
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
}

package com.kylone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kylone.base.BaseActivity;
import com.kylone.player.R;
import com.kylone.utils.ApiUtils;
import com.kylone.utils.ApiUtils.shApi;
import com.kylone.utils.HandlerUtils;
import com.kylone.utils.IntentUtils;
import com.kylone.utils.LogUtil;
import com.kylone.utils.ThreadManager;
import com.kylone.widget.HostDialog;
import com.kylone.widget.MsgDialog;
import com.kylone.widget.PasscodeDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zack
 */

public class WelcomeActivity extends BaseActivity {
    private shApi api;
    private String targetHost;
    private TimerTask task;
    private SharedPreferences sp;
    private String mBootimg;
    private ImageView imgWelcome;
    private View warningView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        warningView = findViewById(R.id.layout_warning);
        imgWelcome = (ImageView) findViewById(R.id.img_welcome);
        sp = getSharedPreferences("kylone", Context.MODE_PRIVATE);

        mBootimg = sp.getString("splash", null);
        if (!TextUtils.isEmpty(mBootimg)) {
            Glide.with(getApplicationContext()).load(mBootimg).diskCacheStrategy(DiskCacheStrategy.ALL).into(imgWelcome);
        }

        if (!ApiUtils.checkPermission(getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        }
        //test code
//        HandlerUtils.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                String[] strings = {"30","#FFFFFFFF","#FF889911","5","2","10","0","奇偶奇偶奇偶奇偶奇偶2哦id金佛is觉得"};
//                new MsgDialog(getApplicationContext(),strings, Gravity.TOP).show();
//                new PasscodeDialog(WelcomeActivity.this).show("Input Password");
//            }
//        },5000);
        targetHost = sp.getString("ip", ApiUtils.getIp());
        ApiUtils.setIp(targetHost);
        connectServer();
    }

    private void showEditHost() {
        HostDialog hostDialog = new HostDialog(this);
        hostDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                connectServer();
            }
        });
        hostDialog.show();

//        PasscodeDialog pcDialog = new PasscodeDialog(this);
//        pcDialog.show();
    }

    private void connectServer() {

        ThreadManager.execute(new Runnable() {
            @Override
            public void run() {
                api = ApiUtils.getShApi(getBaseContext());

                if (shApi.shcreate(getFilesDir().getAbsolutePath()) != shApi.SHC_OK) {
                    close("! onCreate(): Failed to create API");
                    return;
                }

                String apiver = shApi.shgetapiversion();
                LogUtil.i(" api version : " + apiver);

                if (!shApi.inetConnected(0).equals("yes")) {
                    // required tasks should be implemented
                    close("! there is no network connectivity");
                    return;
                }
                if (shApi.shserverisready(targetHost, 5) != shApi.SHC_OK) {
                /*
                  Server may still be doing boot or its services may not ready yet.
                  Custom application may try it few more times and then may give up.
                  Required tasks should be implemented
                */
                    close("! server is not ready yet, should be tried later");
                    return;
                }


                if (shApi.shconnect(targetHost, shApi.SHC_OPT_DOCACHE) != shApi.SHC_OK) {
                    // required tasks should be implemented
                    close("shconnect(): Failed to connect target host\n");
                    return;
                }
                LogUtil.i(" shconnect(): launched");

                task = new TimerTask() {
                    @Override
                    public void run() {
                        if (api.connectState > 0) {
//                            getConfigVal("bgnd")


                            String bootimg = ApiUtils.shApi.getConfigVal("splash");
                            if (!TextUtils.isEmpty(bootimg) && !TextUtils.equals(mBootimg, bootimg)) {
                                SharedPreferences.Editor edit = sp.edit();
                                edit.putString("splash", bootimg).apply();

//                                Glide.with(getApplicationContext())
//                                        .load(bootimg)
//                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                        .into(ScreenParameter.getScreenWidth(), ScreenParameter.getScreenHeight());
                            }


                            String wifi = null;
                            String mmtype = ApiUtils.shApi.getConfigVal("mmtype");
                            if (TextUtils.equals(mmtype, "def")) {

                            } else if (TextUtils.equals(mmtype, "pre")) {
                                wifi = ApiUtils.shApi.getConfigVal("prewftx");
//                                ApiUtils.shApi.getConfigVal("prewftx");
//                                ApiUtils.shApi.getConfigVal("prewftx");

                            } else if (TextUtils.equals(mmtype, "csr")) {

                            }

                            String pretbgi = shApi.getConfigVal("pretbgi");
                            if (!TextUtils.isEmpty(pretbgi))
                                sp.edit().putString("pretbgi", pretbgi).apply();

                            String bgnd = ApiUtils.shApi.getConfigVal("preback");
                            LogUtil.i("wifi==> " + wifi);

                            sp.edit().putString("bgnd", bgnd).apply();

                            sp.edit().putString("wifi", wifi).apply();


                            HandlerUtils.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    IntentUtils.startActivityForAction("kylone.intent.action.Home");
                                    finish();
                                }
                            }, 2000);
                            task.cancel();
                            task = null;
                        } else if (api.connectState < 0) {
                            close("Con't connect service ! ");
                            task.cancel();
                            task = null;
                        }
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 1000, 1000);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void close(String log) {
        final String info = log + " ,  Exit the page after 5 seconds.";
        LogUtil.e("WelcomeActivity", info);
        HandlerUtils.runUITask(new Runnable() {
            @Override
            public void run() {
                warningView.setVisibility(View.VISIBLE);
            }
        });
        HandlerUtils.postDelayed(new Runnable() {
            @Override
            public void run() {
                showEditHost();
            }
        }, 1200);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}

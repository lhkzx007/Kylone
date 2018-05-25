package com.kylone;

import android.os.Bundle;
import android.widget.TextView;

import com.kylone.base.BaseActivity;
import com.kylone.player.R;
import com.kylone.utils.ApiUtils;
import com.kylone.utils.ApiUtils.shApi;
import com.kylone.utils.HandlerUtils;
import com.kylone.utils.IntentUtils;
import com.kylone.utils.LogUtil;
import com.kylone.utils.ThreadManager;

import java.util.TimerTask;

/**
 * Created by zack
 */

public class WelcomeActivity extends BaseActivity {
    private shApi api;
    private final String targetHost = "10.47.48.1";
    private TimerTask task;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        tvInfo = (TextView) findViewById(R.id.info_welcome);
        HandlerUtils.postDelayed(new Runnable() {
            @Override
            public void run() {
                IntentUtils.startActivityForAction("kylone.intent.action.Home");
                finish();
            }
        }, 2000);
//        connectServer();
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


//                if (shApi.shconnect(targetHost, shApi.SHC_OPT_DOCACHE) != shApi.SHC_OK) {
//                    // required tasks should be implemented
//                    close("shconnect(): Failed to connect target host\n");
//                    return;
//                }
//                LogUtil.i(" shconnect(): launched");
//
//                task = new TimerTask() {
//                    @Override
//                    public void run() {
//                        if (api.connectState > 0) {
////                            getConfigVal("bgnd")
//                            HandlerUtils.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    IntentUtils.startActivityForAction("kylone.intent.action.Home");
//                                    finish();
//                                }
//                            }, 2000);
//                            task.cancel();
//                            task = null;
//                        } else if (api.connectState < 0) {
//                            close("Con't connect service ! ");
//                            task.cancel();
//                            task = null;
//                        }
//                    }
//                };
//                Timer timer = new Timer();
//                timer.schedule(task, 1000, 1000);

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
                tvInfo.setText(info);
            }
        });
        HandlerUtils.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}

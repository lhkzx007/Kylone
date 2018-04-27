package com.kylone;

import android.os.Bundle;
import android.widget.Toast;

import com.kylone.base.BaseActivity;
import com.kylone.player.R;
import com.kylone.utils.ApiUtils;
import com.kylone.utils.ApiUtils.shApi;
import com.kylone.utils.HandlerUtils;
import com.kylone.utils.IntentUtils;
import com.kylone.utils.LogUtil;
import com.kylone.utils.ThreadManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zack
 */

public class WelcomeActivity extends BaseActivity {
    private shApi api;
    private final String targetHost = "10.47.48.1";
    private TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        connectServer();
    }

    private void connectServer() {
        ThreadManager.execute(new Runnable() {
            @Override
            public void run() {
                api = ApiUtils.getShApi(getBaseContext());
                if (shApi.shcreate(getFilesDir().getAbsolutePath()) != shApi.SHC_OK) {
                    LogUtil.e("! onCreate(): Failed to create API");
                    return;
                }

                String apiver = shApi.shgetapiversion();
                LogUtil.i(" api version : " + apiver);

                if (!shApi.inetConnected(0).equals("yes")) {
                    LogUtil.e("! there is no network connectivity");
                    // required tasks should be implemented
                    return;
                }
                if (shApi.shserverisready(targetHost, 5) != shApi.SHC_OK) {
                /*
                  Server may still be doing boot or its services may not ready yet.
                  Custom application may try it few more times and then may give up.
                  Required tasks should be implemented
                */
                    LogUtil.e("! server is not ready yet, should be tried later");
                    return;
                }
                if (shApi.shconnect(targetHost, shApi.SHC_OPT_DOCACHE) != shApi.SHC_OK) {
                    LogUtil.e("shconnect(): Failed to connect target host\n");
                    // required tasks should be implemented
                    return;
                }
                LogUtil.i(" shconnect(): launched");

                task = new TimerTask() {
                    @Override
                    public void run() {
                        if (api.connectState > 0) {
//                            getConfigVal("bgnd")
                            HandlerUtils.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    IntentUtils.startActivityForAction("kylone.intent.action.Home");
                                    finish();
                                }
                            }, 2000);
                            task.cancel();
                        } else if (api.connectState < 0) {
                            Toast.makeText(WelcomeActivity.this, "Con't connect service  , Exit the page after 3 seconds. ", Toast.LENGTH_LONG).show();
                            HandlerUtils.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 3000);
                            task.cancel();
                        }
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 1000, 1000);

            }
        });
    }
}

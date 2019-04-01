package com.kylone.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.widget.Toast;

import com.kylone.base.ComponentContext;

import java.util.HashMap;
import java.util.List;
import java.util.Set;


/**
 * Created by zack
 */
public class IntentUtils {

    public static void startActivityForAction(String action) {
        startActivityForAction(action, null);
    }

    public static void startActivityForAction(String action, HashMap<String, String> value) {
        try {
            if (TextUtils.isEmpty(action)) {
                return;
            }

            if (isActionSupport(ComponentContext.getContext(), action)) {
                Intent intent = new Intent(action);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage(ComponentContext.getContext().getPackageName());
                if (value != null) {
                    Set<String> keys = value.keySet();
                    for (String key : keys) {
                        intent.putExtra(key, value.get(key));
                    }
                }
                ComponentContext.getContext().startActivity(intent);
            } else {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ComponentContext.getContext(), "There is no content to display", Toast.LENGTH_LONG).show();
                    }
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static boolean isActionSupport(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> resolveInfo =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo.size() > 0) {
            return true;
        }
        return false;
    }
}

package com.kylone.utils;

import android.content.Intent;
import android.text.TextUtils;

import com.kylone.base.ComponentContext;

import java.util.HashMap;
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
            if (TextUtils.isEmpty(action)){
                return;
            }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

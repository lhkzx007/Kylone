package com.kylone.utils;

import android.util.Log;

public class LogUtil {
    private static boolean logGate = true;

    private static final String TAG = "LogUtil-";

    /**
     * 打开
     *
     * @param b
     */
    public static void openGate(boolean b) {
        logGate = b;
    }

    public static void i(String text) {
        if (logGate) {
            Log.i(TAG + "info", buildMessage(text));
        }
    }

    public static void d(String text) {
        if (logGate) {
            Log.d(TAG + "debug", buildMessage(text));
        }
    }

    public static void e(String text) {
        if (logGate) {
            Log.e(TAG + "error", buildMessage(text));
        }
    }

    public static void v(String text) {
        if (logGate) {
            Log.v(TAG + "verbose", buildMessage(text));
        }
    }

    public static void w(String string) {
        if (logGate) {

            Log.w(TAG + "warn", buildMessage(string));
        }
    }

    public static void i(String tag, String text) {
        if (logGate) {
            Log.i(tag, buildMessage(text));
        }
    }

    public static void d(String tag, String text) {
        if (logGate) {
            Log.d(tag, buildMessage(text));
        }
    }

    public static void e(String tag, String text) {
        if (logGate) {
            Log.e(tag, buildMessage(text));
        }
    }

    public static void v(String tag, String text) {
        if (logGate) {
            Log.v(tag, buildMessage(text));
        }
    }

    public static void w(String tag, String text) {
        if (logGate) {
            Log.w(tag, buildMessage(text));
        }
    }

    /**
     * Building Message
     *
     * @param msg The message you would like logged.
     * @return Message String
     */
    public static String buildMessage(String msg) {



        StackTraceElement caller = new Throwable().fillInStackTrace().getStackTrace()[2];
        return new StringBuilder().append(" [").append(caller.getFileName()).append(".")
                .append(caller.getMethodName()).append("#").append(caller.getLineNumber() + "]").append(msg)
                .toString();
    }

}

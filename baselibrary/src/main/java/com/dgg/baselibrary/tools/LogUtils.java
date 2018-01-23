package com.dgg.baselibrary.tools;

import android.util.Log;

import com.dgg.baselibrary.BuildConfig;


/**
 * Created by qiqi on 17/6/28.
 */

public class LogUtils {


    private static boolean logStatus = false;
    private static final String TAG = "appLog";

    /**
     * 用于 输出 日志 方便
     * xnq
     *
     * @param text
     */
    public static void d(String text) {
        if (logStatus) {
            Log.d(TAG, text);
        }
    }

    public static void d(String TAG, String text) {
        if (logStatus) {
            Log.d(TAG, TAG + text);
        }
    }

    public static void setLogStatus(boolean logStatus) {
        LogUtils.logStatus = logStatus;
    }
}

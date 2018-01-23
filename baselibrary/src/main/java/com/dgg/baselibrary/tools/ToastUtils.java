package com.dgg.baselibrary.tools;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by tsang on 2016/10/14.
 */

public class ToastUtils {
    private static Toast toast;

    public static void showToast(Context mContext, int msgId) {
        showToast(mContext, mContext.getResources().getString(msgId));
    }

    public static void showToast(Context mContext, String msg) {
        if (toast == null) {
            toast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }
}

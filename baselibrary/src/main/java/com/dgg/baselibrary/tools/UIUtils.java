package com.dgg.baselibrary.tools;

import android.content.res.Resources;

import com.dgg.baselibrary.BaseApplication;

/**
 * Created by qiqi on 17/7/26.
 */

public class UIUtils {

    /**
     * pix转dip 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int pix2dip(int pix) {
        final float densityDpi = getResources().getDisplayMetrics().density;
        return (int) (pix / densityDpi + 0.5f);
    }

    /**
     * 获得资源
     */
    public static Resources getResources() {
        return BaseApplication.getContext().getResources();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}

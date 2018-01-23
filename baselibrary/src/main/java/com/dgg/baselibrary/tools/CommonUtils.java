package com.dgg.baselibrary.tools;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;

import com.dgg.baselibrary.html5.Html5Activity;
import com.google.gson.Gson;
import com.jingewenku.abrahamcaijin.commonutil.AppBigDecimal;

import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 公共工具类
 * Created by qiqi on 17/7/10.
 */

public class CommonUtils {
    private static final String TAG = "LOG";
    public static final String KEY = "cd962540";
    private static Gson gson;
    private static byte[] iv = {1, 2, 3, 4, 5, 6, 7, 8};

    /**
     * 改变文字颜色
     *
     * @param content
     */
    public static SpannableStringBuilder modifyTextColor(String content, int color, int start, int end) {

        if (TextUtils.isEmpty(content) || (start >= content.length() || end > content.length() || start > end || end < 0 || start < 0)) {
            return null;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        // ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan redSpan = new ForegroundColorSpan(color);
        builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    public static String encryptDES(HashMap<String, Object> map) {
        Gson gson = new Gson();
        LogUtils.d("JIE加密的字符串：" + decryptDES(encryptDES(gson.toJson(map), KEY), KEY));
        LogUtils.d("加密的字符串：" + encryptDES(gson.toJson(map), KEY));
        return encryptDES(gson.toJson(map), KEY);
    }

    /**
     * 将字符串DES加密
     *
     * @param encryptString 需要加密的明文
     * @return encryptKey 秘钥
     */
    public static String encryptDES(String encryptString, String encryptKey) {
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
            return Base64.encodeToString(encryptedData, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 将字符串DES解密
     *
     * @param decryptString 需要解密的明文
     * @return encryptKey 秘钥
     */
    public static String decryptDES(String decryptString, String decryptKey) {
        try {
            byte[] byteMi = Base64.decode(decryptString, Base64.DEFAULT);
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            byte decryptedData[] = cipher.doFinal(byteMi);
            return new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static final View.OnTouchListener TouchLight = new View.OnTouchListener() {
//
//        public final float[] BT_SELECTED = new float[]{1, 0, 0, 0, 50, 0, 1, 0, 0, 50, 0, 0, 1, 0, 50, 0, 0, 0, 1, 0};
//        public final float[] BT_NOT_SELECTED = new float[]{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0};
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                v.getBackground().setColorFilter(
//                        new ColorMatrixColorFilter(BT_SELECTED));
//                v.setBackgroundDrawable(v.getBackground());
//            } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                v.getBackground().setColorFilter(
//                        new ColorMatrixColorFilter(BT_NOT_SELECTED));
//                v.setBackgroundDrawable(v.getBackground());
//            }
//            return false;
//        }
//    };


    private static final View.OnTouchListener TouchDark = new View.OnTouchListener() {

        public final float[] BT_SELECTED = new float[]{1, 0, 0, 0, -50, 0, 1, 0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0};
        public final float[] BT_NOT_SELECTED = new float[]{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0};

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setAlpha(0.5f);
//                v.getBackground().setColorFilter(
//                        new ColorMatrixColorFilter(BT_SELECTED));
//                v.setBackgroundDrawable(v.getBackground());
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setAlpha(1f);
//                v.getBackground().setColorFilter(
//                        new ColorMatrixColorFilter(BT_NOT_SELECTED));
//                v.setBackgroundDrawable(v.getBackground());
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                v.setAlpha(1f);
            }
            return false;
        }
    };

    public static void setOnTouch(View v) {
        v.setOnTouchListener(TouchDark);
    }

    public static void previewContent(Context context, String path, String title) {
        Intent intent = new Intent(context, Html5Activity.class);
        intent.putExtra(Html5Activity.WEB_PATH, path);
        intent.putExtra(Html5Activity.PAGE_TITLE, title);
        context.startActivity(intent);
    }

    public static void previewContent(Context context, String path) {
        Intent intent = new Intent(context, Html5Activity.class);
        intent.putExtra(Html5Activity.WEB_PATH, path);
        context.startActivity(intent);

    }

    /*获取百分数  d1分子  d2分母*/
    public static String getPercentage(double d1, double d2) {
        if (d2 == 0) {
            return "0";
        } else {
            double number = AppBigDecimal.divide(d1, d2, 2) * 100;
            if (d1 > 0 && number < 1)
                number = 1;
            return AppBigDecimal.round(number + "", 0);
        }
    }

    public static Gson getGson() {
        if (gson == null)
            gson = new Gson();
        return gson;
    }

}

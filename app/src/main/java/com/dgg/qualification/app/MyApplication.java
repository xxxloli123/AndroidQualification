package com.dgg.qualification.app;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.support.multidex.MultiDex;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.dgg.baselibrary.BaseApplication;
import com.dgg.baselibrary.tools.LogUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.qualification.BuildConfig;
import com.dgg.qualification.baidu.service.LocationService;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.mob.MobSDK;
import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

/**
 * Created by qiqi on 17/6/28.
 */

public class MyApplication extends BaseApplication {

    public static LocationService locationService;
    public Vibrator mVibrator;
    public static String IMEI;
    //    private  MyApplication app;
    public static boolean isJpush = false;

    private static class Holder {
        private static MyApplication instanc = (MyApplication) getContext();
    }

    public static MyApplication getInstance() {
        return new Holder().instanc;
    }

    /**
     * compile 'com.android.support:multidex:1.0.1'
     * 处理 超65k的方法
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        app = this;
        LogUtils.setLogStatus(BuildConfig.DEBUG);
        initSdk();
    }

    private void initSdk() {
        MobclickAgent.setDebugMode(BuildConfig.DEBUG);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);


        JPushInterface.setDebugMode(BuildConfig.DEBUG);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);

        Stetho.initializeWithDefaults(this);
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        MobSDK.init(this, "20402e4f85f2c", "e35efce606f0ff72a85eb1c015541378");
        LogUtils.d(android.os.Build.CPU_ABI2 + " cpu信息---==：" + android.os.Build.CPU_ABI);
    }

    public String getUID() {
        return (String) SharedPreferencesUtils.getParam(getApplicationContext(), "uid", "");
    }

    public String getImei() {
        if (TextUtils.isEmpty(IMEI)) {
            TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            MyApplication.IMEI = TelephonyMgr.getDeviceId();
        }
        return IMEI;
    }

}

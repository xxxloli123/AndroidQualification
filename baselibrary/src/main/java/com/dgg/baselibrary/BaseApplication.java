package com.dgg.baselibrary;

import android.app.Application;
import android.content.Context;

import com.dgg.hdforeman.base.KtBaseActivity;

import java.util.LinkedList;

/**
 * Created by qiqi on 17/7/22.
 */

public class BaseApplication extends Application {
    public LinkedList<KtBaseActivity> mActivityList;
    //    BaseApplication  myAPP= (BaseApplication) getApplicationContext();
    static private BaseApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication=this;
//synchronized (KtBaseActivity.class){
//    myAPP.getActivityList().add(this);
//}
    }

    /**
     * 返回一个存储所有存在的activity的列表
     *
     * @return
     */
    public LinkedList<KtBaseActivity> getActivityList() {
        if (mActivityList == null) {
            mActivityList = new LinkedList<KtBaseActivity>();
        }
        return mActivityList;
    }
    /**
     * 返回上下文
     *
     * @return
     */
    public static Context getContext() {
        return mApplication;
    }
}

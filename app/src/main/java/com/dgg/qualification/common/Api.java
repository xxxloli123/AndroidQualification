package com.dgg.qualification.common;


import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.qualification.BuildConfig;
import com.dgg.qualification.app.MyApplication;

import java.util.HashMap;

/**
 * Created by qiqi on 17/7/5.
 */

public class Api {

    public static final String app_domain = "http://college.dgg.net/";//正式环境环境
//    public static final String app_domain = "http://zzappyfb.dggweb.com/";//预发布环境
//    public static final String app_domain = "http://172.16.0.23:8017/";//http://172.16.0.23:8017  测试
//    public static final String app_domain = "http://172.16.2.192:8080/";//张文
//    public static final String app_domain = "http://172.16.2.217:8080/";//罗夕阳

    public static HashMap<String, Object> getCommonData() {
        HashMap<String, Object> map = getLoginCommonData();
        map.put("uid", SharedPreferencesUtils.getParam(MyApplication.getInstance(), "uid", ""));
        return map;
    }

    public static HashMap<String, Object> getLoginCommonData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("platform", "android");
        map.put("appVersion", BuildConfig.VERSION_CODE);
        map.put("appVersionName", BuildConfig.VERSION_NAME);
        map.put("equipmentId", MyApplication.getInstance().getImei());
        return map;
    }

}

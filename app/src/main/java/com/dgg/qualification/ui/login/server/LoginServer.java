package com.dgg.qualification.ui.login.server;

import android.content.Context;

import com.dgg.baselibrary.db.been.Topic;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.HttpServer;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.ui.login.Login;
import com.dgg.qualification.ui.topic.been.Area;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by qiqi on 17/6/22.
 */

public class LoginServer {
    /*获取【验证码】*/
    public static Observable<BaseJson> sendMssg(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(LoginPath.class)
                .sendMssg(pams)
                .compose(HttpServer.<BaseJson>compatApplySchedulers());
    }

    /*【登录系统】*/
    public static Observable<BaseJson<Login>> sendLoginData(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(LoginPath.class)
                .loginData(pams)
                .compose(HttpServer.<BaseJson<Login>>compatApplySchedulers());
    }
}

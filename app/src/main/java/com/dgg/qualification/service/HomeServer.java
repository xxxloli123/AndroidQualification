package com.dgg.qualification.service;

import android.content.Context;

import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.HttpServer;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.service.been.UpdateVertion;

import rx.Observable;

/**
 * Created by qiqi on 17/6/22.
 */

public class HomeServer {

    public static Observable<BaseJson<UpdateVertion>> getUpdateVersion(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(HomePath.class)
                .checkUpdateVersion(pams)
                .compose(HttpServer.<BaseJson<UpdateVertion>>compatApplySchedulers());
    }
}

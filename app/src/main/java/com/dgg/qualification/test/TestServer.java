package com.dgg.qualification.test;

import android.content.Context;

import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.HttpServer;
import com.dgg.qualification.common.Api;

import rx.Observable;

/**
 * Created by qiqi on 17/6/22.
 */

public class TestServer {

    public static Observable<BaseJson> getTest(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(TestYY.class)
                .projectShut(pams)
                .compose(HttpServer.<BaseJson>compatApplySchedulers());
    }
}

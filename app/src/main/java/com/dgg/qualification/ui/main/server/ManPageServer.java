package com.dgg.qualification.ui.main.server;

import android.content.Context;

import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.HttpServer;
import com.dgg.qualification.common.Api;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by qiqi on 17/6/22.
 */

public class ManPageServer {
    /*获取 首页所以数据*/
    public static Observable<BaseJson<HomePage>> getAllData(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(ManPgePath.class)
                .getAllData(pams)
                .compose(HttpServer.<BaseJson<HomePage>>compatApplySchedulers());
    }

    /*分页获取 资讯数据*/
    public static Observable<BaseJson<ArrayList<HomePageItem>>> getNewList(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(ManPgePath.class)
                .getNewList(pams)
                .compose(HttpServer.<BaseJson<ArrayList<HomePageItem>>>compatApplySchedulers());
    }
}

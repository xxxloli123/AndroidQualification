package com.dgg.qualification.ui.main.server;


import com.dgg.baselibrary.network.BaseJson;
import com.dgg.qualification.ui.topic.been.Area;

import java.util.ArrayList;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by qiqi on 17/6/22.
 */

public interface ManPgePath {
    /**
     * 获取 首页所有数据
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("openapi/bannerList/v1")
    Observable<BaseJson<HomePage>> getAllData(@Field("param") String params);

    /**
     * 分页获取资讯列表
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("openapi/contentList/v1")
    Observable<BaseJson<ArrayList<HomePageItem>>> getNewList(@Field("param") String params);
}

package com.dgg.qualification.service;


import com.dgg.baselibrary.network.BaseJson;
import com.dgg.qualification.service.been.UpdateVertion;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by qiqi on 17/6/22.
 */

public interface HomePath {
    /**
     * 检查版本更新信息
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("openapi/findNewVersion/v1")
    Observable<BaseJson<UpdateVertion>> checkUpdateVersion(@Field("param") String params);
}

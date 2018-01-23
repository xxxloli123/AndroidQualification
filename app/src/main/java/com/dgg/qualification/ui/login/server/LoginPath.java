package com.dgg.qualification.ui.login.server;


import com.dgg.baselibrary.db.been.Topic;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.qualification.ui.login.Login;
import com.dgg.qualification.ui.topic.been.Area;

import java.util.ArrayList;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by qiqi on 17/6/22.
 */

public interface LoginPath {
    /**
     * 获验证码
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("/openapi/sendMsgCode/v1")
    Observable<BaseJson<ArrayList<Area>>> sendMssg(@Field("param") String params);

    /**
     * 登录系统
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("/openapi/appLogin/v1")
    Observable<BaseJson<Login>> loginData(@Field("param") String params);
}

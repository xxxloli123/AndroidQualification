package com.dgg.qualification.test;


import com.dgg.baselibrary.network.BaseJson;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by qiqi on 17/6/22.
 */

public interface TestYY {
    /**
     * 申请停工
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("baseArea/findAllProvince/")
    Observable<BaseJson> projectShut(@Field("param") String params);
}

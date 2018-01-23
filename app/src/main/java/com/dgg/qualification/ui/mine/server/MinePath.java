package com.dgg.qualification.ui.mine.server;


import com.dgg.baselibrary.db.been.Topic;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.qualification.ui.mine.been.Message;
import com.dgg.qualification.ui.topic.been.Area;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by qiqi on 17/6/22.
 */

public interface MinePath {
    /**
     //     * 更新个人信息  【头像】
     //     *
     //     * @return
     //     */
////    @FormUrlEncoded
//    @Multipart
//    @POST("/appAuth/updateCustomerImg/v1")
//    Observable<BaseJson> updatePersonInfo(@Part("param") RequestBody params, @Part MultipartBody.Part file);


    /**
     * 更新个人信息  【头像】
     *
     * @return
     */
    @POST("/appAuth/updateCustomerImg/v1")
    Observable<BaseJson<String>> updatePersonInfo(@Query("param") String params, @Body RequestBody file);


    /**
     * 更新 个人信息 【姓名 性别】
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("appAuth/updateCustomer/v1")
    Observable<BaseJson> updatePersonInfo2(@Field("param") String params);

    /**
     * 意见 反馈
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("/appAuth/insertFeedBack/v1")
    Observable<BaseJson> addFeedback(@Field("param") String params);

    /**
     * 退出问题
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("/openapi/loginOut/v1")
    Observable<BaseJson> exit(@Field("param") String params);

    /**
     * 获取消息了吧
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("/openapi/findPublicMsgPage/v1")
    Observable<BaseJson<ArrayList<Message>>> getMessageList(@Field("param") String params);
}

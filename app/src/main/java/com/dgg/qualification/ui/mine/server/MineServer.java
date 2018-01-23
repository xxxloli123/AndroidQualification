package com.dgg.qualification.ui.mine.server;

import android.content.Context;

import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.HttpServer;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.ui.mine.been.Message;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;

/**
 * Created by qiqi on 17/6/22.
 */

public class MineServer {
    /*  更新个人信息*/
    public static Observable<BaseJson<String>> updatePersonInfo(Context context, String pams, File file) {

//        File file = new File(picName);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("file", file.getName(), requestBody);
        MultipartBody multipartBody = builder.build();

//        LogUtils.d("文件路径：" + file.getPath());
//        RequestBody requestFile =
//                RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        MultipartBody.Part body =
//                MultipartBody.Part.createFormData("image/*", file.getName(), requestFile);
//        String descriptionString = pams;
//        RequestBody description =
//                RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(MinePath.class)
                .updatePersonInfo(pams, multipartBody)
                .compose(HttpServer.<BaseJson<String>>compatApplySchedulers());
    }

    /*更新个人信息*/
    public static Observable<BaseJson> updatePersonInfo2(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(MinePath.class)
                .updatePersonInfo2(pams)
                .compose(HttpServer.<BaseJson>compatApplySchedulers());
    }

    /*意见反馈*/
    public static Observable<BaseJson> addFeedback(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(MinePath.class)
                .addFeedback(pams)
                .compose(HttpServer.<BaseJson>compatApplySchedulers());
    }

    /*意见反馈*/
    public static Observable<BaseJson> exit(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(MinePath.class)
                .exit(pams)
                .compose(HttpServer.<BaseJson>compatApplySchedulers());
    }

    /*获取消息了吧*/
    public static Observable<BaseJson<ArrayList<Message>>> getMessageList(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(MinePath.class)
                .getMessageList(pams)
                .compose(HttpServer.<BaseJson<ArrayList<Message>>>compatApplySchedulers());
    }
}

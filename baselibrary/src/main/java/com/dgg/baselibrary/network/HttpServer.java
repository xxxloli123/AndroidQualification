package com.dgg.baselibrary.network;

import android.content.Context;
import android.text.TextUtils;

import com.dgg.baselibrary.tools.LogUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 封装网络框架
 * Created by qiqi on 17/6/28.
 */

public class HttpServer {
    private static class Holder {
        private static HttpServer instance = new HttpServer();
    }

    public static HttpServer getInstance() {
        return Holder.instance;
    }

    public Retrofit build(final Context context, String baseUrl) {
        //Interceptor 拦截器
        Interceptor mTokenInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //Response 响应 original 原版的
                Response originalResponse = chain.proceed(chain.request());
                String token = (String) SharedPreferencesUtils.getParam(context, "hdmsToken", "");
                if (!TextUtils.isEmpty(token)) {
                    final Request.Builder builder = chain.request().newBuilder();
                    builder.addHeader("Cookie", token);
                } else {
                    LogUtils.d("获得的 cooker--》" + originalResponse.headers("uid"));
                    //这里获取请求返回的 cookie
                    if (!originalResponse.headers("uid").isEmpty()) {
                        //保存Cookie数据 Observable (可观察者，即被观察者)
                        Observable.from(originalResponse.headers("Set-Cookie"))
//                                filter 过滤
                                .filter(new Func1<String, Boolean>() {
                                    @Override
                                    public Boolean call(String str) {
                                        //          contains 包含
                                        return str.contains("hdmsToken");
                                    }
                                })
                                .map(new Func1<String, String>() {
                                    @Override
                                    public String call(String s) {
                                        String[] headArray = s.split(";");
                                        return headArray[0];
                                    }
                                })
//                                subscribe 订阅 Action 措施
                                .subscribe(new Action1<String>() {
                                    @Override
                                    public void call(String cookie) {
                                        LogUtils.d("hdmsToken " + cookie);
                                        SharedPreferencesUtils.setParam(context, "hdmsToken", cookie);
                                    }
                                });
                    }
                }
                return originalResponse;
            }
        };

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(mTokenInterceptor)//添加后台自定的hdmsToken
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .addNetworkInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())// 使用Gson作为数据转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())// 使用RxJava作为回调适配器
                .client(client)
                .build();
        return retrofit;
    }

    public static <T> Observable.Transformer<T, T> compatApplySchedulers() {
        return (Observable.Transformer<T, T>) compatTransformer;
    }

    static final Observable.Transformer compatTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Func1() {
                        @Override
                        public Object call(Object response) {
                            return compatFlatResponse(response);
                        }
                    });
        }
    };

    public static <T> Observable<T> compatFlatResponse(final T response) {
        return Observable.create(new Observable.OnSubscribe<T>() {

            @Override
            public void call(Subscriber<? super T> subscriber) {
                if (response != null) {
                    if (!subscriber.isUnsubscribed()) {
                        if (response instanceof BaseJson
                                && ((BaseJson) response).code != 0) {

                            subscriber.onError(
                                    new APIException(
                                            ((BaseJson) response).code,
                                            ((BaseJson) response).msg, response));

                        } else {
                            subscriber.onNext(response);
                        }
                    }
                } else {
                    if (!subscriber.isUnsubscribed()) {

                        subscriber.onError(new APIException(-1000/* TODO: */, "请求出错"/* TODO: */,
                                response));

                    }
                    return;
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }

            }
        });
    }

}

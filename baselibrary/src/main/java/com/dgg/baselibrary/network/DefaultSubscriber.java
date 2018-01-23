package com.dgg.baselibrary.network;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.dgg.baselibrary.loading.ILoadingHelper;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.baselibrary.tools.ToastUtils;

import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by xeq 2017/07/5.
 */

public abstract class DefaultSubscriber<T> extends Subscriber<T> {

    private WeakReference<ILoadingHelper> mLoadingLayoutRef;
    //    private LoyoProgressHUD mHUD;
//    private LoyoError mError;
    private String successTip;
    private boolean dismissOnlyWhenError;

    public final static int SILENCE = 1 << 0;
    public final static int TOAST = 1 << 1;
    public final static int LOADING_LAYOUT = 1 << 2;
    public final static int PROGRESS_HUD = 1 << 3;

    @IntDef({SILENCE, TOAST, LOADING_LAYOUT, PROGRESS_HUD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CheckType {
    }

    @CheckType
    int type;
    private Context context;

    public DefaultSubscriber(Context context) {
        type = TOAST;
        this.context = context;
    }

    public DefaultSubscriber(@CheckType int checktype) {
        type = checktype;
    }

    public DefaultSubscriber(ILoadingHelper layout) {
        if (layout != null) {
            mLoadingLayoutRef = new WeakReference<ILoadingHelper>(layout);
            type = LOADING_LAYOUT;
        }
    }
//
//    public DefaultSubscriber(LoyoProgressHUD hud) {
//        if (hud != null) {
//            mHUD = hud;
//            type = PROGRESS_HUD;
//        }
//    }

//    public DefaultSubscriber(LoyoProgressHUD hud, String successTip) {
//        if (hud != null) {
//            mHUD = hud;
//            type = PROGRESS_HUD;
//            this.successTip = successTip;
//        }
//    }

//    public DefaultSubscriber(LoyoProgressHUD hud, boolean dismissOnlyWhenError) {
//        if (hud != null) {
//            mHUD = hud;
//            type = PROGRESS_HUD;
//            this.dismissOnlyWhenError = dismissOnlyWhenError;
//        }
//    }

    private ILoadingHelper getLoadingLayout() {
        if (mLoadingLayoutRef == null) {
            return null;
        }
        return mLoadingLayoutRef.get();
    }

    @Override
    public void onCompleted() {
//        if (mHUD != null) {
//            if (mError != null) {
//                mHUD.dismissWithError(mError.message);
//            } else if (!dismissOnlyWhenError) {
//                mHUD.dismissWithSuccess(this.successTip);
//            }
//            // TODO:
//            if (mHUD.getStyle() != LoyoProgressHUD.Style.LOYO_COMMIT) {
//                if (mError != null) {
//                    LoyoToast.error(MainApp.getMainApp().getApplicationContext(), mError.message);
//                }
//            }
//        }
    }

    @Override
    public void onError(Throwable e) {
        checkLoyoError(e, type, getLoadingLayout());
        onCompleted();
    }

    @Override
    public abstract void onNext(T t);

    private static String loyoError(Throwable e) {
        String message = "";

//        if (e instanceof RetrofitError) {
//            RetrofitError error = (RetrofitError) e;
//            if (error.getResponse() == null) {
//                message = "网络异常(网络超时或者无网络)";
//            } else if (error.getKind() == RetrofitError.Kind.NETWORK) {
//                message = "请检查您的网络连接";
//            }
//        } else

        if (e instanceof APIException) {
            message = e.getMessage();
            if (((APIException) e).code == 80006) {//退出到登录
                EventBus.getDefault().post("80006", "app_exit");
            }
        } else if (e instanceof ConnectException) {
            message = "服务器开小差";
        } else if (e instanceof HttpException) {
            message = "服务器开小差！";
        } else if (e instanceof SocketTimeoutException) {
            message = "网络超时";
        } else if (e instanceof UnknownHostException) {
            message = "网络异常";
        } else {
            message = "网络异常或数据异常";
        }
        return message;
    }


    public void checkLoyoError(Throwable e, @CheckType int type, ILoadingHelper layout) {
        String message = loyoError(e);
        if (type == LOADING_LAYOUT && layout == null) {
            type = TOAST;
        }

//        if (error.loadingState == AuthFail) {
//            // LoyoToast.info(MainApp.getMainApp().getApplicationContext(), error.message);
//            //到侧边栏 退出系统到登录界面
//            Intent in = new Intent();
//            in.setAction(ExtraAndResult.ACTION_USER_VERSION);
//            in.putExtra(ExtraAndResult.EXTRA_DATA, "exite");
//            LocalBroadcastManager.getInstance(MainApp.getMainApp()).sendBroadcast(in);
//            return null;
//        }
        switch (type) {
            case SILENCE: {
                // 静默处理，不提示
            }
            break;
            case LOADING_LAYOUT: {//处理loading状态
                if (e instanceof UnknownHostException) {
                    layout.setNoNetworkMessage(message);
                    layout.showNoNetwork(); // TODO:
                } else {
                    layout.setErrorMessage(message);
                    layout.showError(); // TODO:
                }

            }
            break;
            case PROGRESS_HUD: {
                // onCompleted 处理提示
            }
            break;
            default: {
//                LoyoToast.error(MainApp.getMainApp().getApplicationContext(), error.message);
                if (layout != null) {
                    layout.restore();
                } else {
                    ToastUtils.showToast(context, message);
                }
            }
            break;
        }

    }
}

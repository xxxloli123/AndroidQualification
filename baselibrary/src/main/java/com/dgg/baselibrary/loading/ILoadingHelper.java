package com.dgg.baselibrary.loading;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * Created by LuoHaifeng on 2017/3/21.
 */

public interface ILoadingHelper {
    int a = 0;
    enum State{
        LOADING,
        EMPTY,
        NO_NETWORK,
        ERROR,
        NO_LOGIN,
        NORMAL
    }

    //加载中状态
    ILoadingHelper showLoading();
    ILoadingHelper setLoadingMessage(@StringRes int loadingMessage);
    ILoadingHelper setLoadingMessage(String loadingMessage);
    ILoadingHelper setLoadingIcon(@DrawableRes int loadingIcon);

    //空数据状态
    ILoadingHelper showEmpty();
    ILoadingHelper setEmptyMessage(@StringRes int emptyMessage);
    ILoadingHelper setEmptyMessage(String emptyMessage);
    ILoadingHelper setEmptyIcon(@DrawableRes int emptyIcon);

    //没有网络状态
    ILoadingHelper showNoNetwork();
    ILoadingHelper setNoNetworkMessage(@StringRes int emptyMessage);
    ILoadingHelper setNoNetworkMessage(String emptyMessage);
    ILoadingHelper setNoNetworkIcon(@DrawableRes int emptyIcon);

    //加载失败状态
    ILoadingHelper showError();
    ILoadingHelper setErrorMessage(@StringRes int errorMessage);
    ILoadingHelper setErrorMessage(String errorMessage);
    ILoadingHelper setErrorIcon(@DrawableRes int errorIcon);
    ILoadingHelper setRetryListener(OnRetryListener retryListener);

    //需要登录状态
    ILoadingHelper showLogin();
    ILoadingHelper setLoginMessage(@StringRes int loginMessage);
    ILoadingHelper setLoginMessage(String loginMessage);
    ILoadingHelper setLoginIcon(@DrawableRes int loginIcon);
    ILoadingHelper setLoginListener(OnLoginListener loginListener);

    //正常状态
    ILoadingHelper restore();

    State getState();
    ILoadingHelper showState(State state);

    interface OnRetryListener{
        void onRetry(ILoadingHelper loadingHelper);
    }

    interface OnLoginListener{
        void login(ILoadingHelper loadingHelper);
    }
}

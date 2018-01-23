package com.dgg.baselibrary.loading;

import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgg.baselibrary.R;


/**
 * 切换加载布局
 * Created by LuoHaifeng on 2017/3/21.
 */

public class LoadingHelper implements ILoadingHelper {
    private static ILoadingHelperConfig config = new DefaultLoadingConfig();

    private State state = State.NORMAL;
    private IVaryViewHelper helper;
    private String loadingMessage;
    private int loadingDrawable;
    private String errorMessage;
    private int errorDrawable;
    private OnRetryListener retryListener;
    private String noNetWorkMessage;
    private int noNetWorkDrawable;
    private String emptyMessage;
    private int emptyDrawable;
    private String loginMessage;
    private int loginDrawable;
    private OnLoginListener loginListener;

    public static void setConfig(ILoadingHelperConfig config) {
        LoadingHelper.config = config;
    }

    private LoadingHelper(@NonNull View view) {
        helper = new VaryViewHelper(view);
    }

    public static synchronized LoadingHelper with(View view) {
        Object tag = view.getTag(R.id.id_tag_loading_helper);
        if (tag != null && tag instanceof LoadingHelper) {
            return (LoadingHelper) tag;
        } else {
            LoadingHelper loadingHelper = new LoadingHelper(view);
            view.setTag(R.id.id_tag_loading_helper, loadingHelper);
            return loadingHelper;
        }
    }

    private void setIcon(ImageView ivIcon, @DrawableRes int iconRes) {
        if (iconRes != 0 && iconRes != -1 && ivIcon != null) {
            ivIcon.setImageResource(iconRes);
        }

        if (ivIcon != null) {
            if (iconRes == -1) {
                ivIcon.setVisibility(View.GONE);
            } else {
                ivIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setMessage(TextView tvMessage, String message) {
        if (message != null && tvMessage != null) {
            tvMessage.setText(message);
        }

        if (tvMessage != null) {
            if (TextUtils.isEmpty(tvMessage.getText().toString())) {
                tvMessage.setVisibility(View.GONE);
            } else {
                tvMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setButtonRefresh(View btnRefresh) {
        setButtonRetry(btnRefresh);
    }

    private void setButtonRetry(View btnRetry) {
        if (btnRetry != null) {
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (retryListener != null) {
                        retryListener.onRetry(LoadingHelper.this);
                    }
                }
            });
        }
    }

    private void setButtonSetting(View btnSetting) {
        if (btnSetting != null) {
            btnSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    helper.getContext().startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            });
        }
    }

    private void setButtonLogin(View btnLogin) {
        if (btnLogin != null) {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (loginListener != null) {
                        loginListener.login(LoadingHelper.this);
                    }
                }
            });
        }
    }

    @Override
    public ILoadingHelper showLoading() {
        synchronized (helper.getView()) {
            state = State.LOADING;
            View view = helper.inflate(config.provideLoadingLayoutId());
            setIcon((ImageView) view.findViewById(config.provideIconViewId()), loadingDrawable);
            setMessage((TextView) view.findViewById(config.provideMessageViewId()), loadingMessage);
            helper.showLayout(view);
            return this;
        }
    }

    @Override
    public ILoadingHelper setLoadingMessage(@StringRes int loadingMessage) {
        this.loadingMessage = helper.getContext().getResources().getString(loadingMessage);
        return this;
    }

    @Override
    public ILoadingHelper setLoadingMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
        return this;
    }

    @Override
    public ILoadingHelper setLoadingIcon(@DrawableRes int loadingIcon) {
        this.loadingDrawable = loadingIcon;
        return this;
    }

    @Override
    public ILoadingHelper showEmpty() {
        synchronized (helper.getView()) {
            state = State.EMPTY;
            View view = helper.inflate(config.provideEmptyLayoutId());
            setIcon((ImageView) view.findViewById(config.provideIconViewId()), emptyDrawable);
            setMessage((TextView) view.findViewById(config.provideMessageViewId()), emptyMessage);
            setButtonRefresh(view.findViewById(config.provideRefreshBtnViewId()));
            helper.showLayout(view);
            return this;
        }
    }

    @Override
    public ILoadingHelper setEmptyMessage(@StringRes int emptyMessage) {
        this.emptyMessage = helper.getContext().getResources().getString(emptyMessage);
        return this;
    }

    @Override
    public ILoadingHelper setEmptyMessage(String emptyMessage) {
        this.emptyMessage = emptyMessage;
        return this;
    }

    @Override
    public ILoadingHelper setEmptyIcon(@DrawableRes int emptyIcon) {
        this.emptyDrawable = emptyIcon;
        return this;
    }

    @Override
    public ILoadingHelper showNoNetwork() {
        synchronized (helper.getView()) {
            state = State.NO_NETWORK;
            View view = helper.inflate(config.provideNoNetworkLayoutId());
            setIcon((ImageView) view.findViewById(config.provideIconViewId()), noNetWorkDrawable);
            setMessage((TextView) view.findViewById(config.provideMessageViewId()), noNetWorkMessage);
            setButtonSetting(view.findViewById(config.provideSettingBtnViewId()));
            setButtonRetry(view.findViewById(config.provideRetryBtnViewId()));
            helper.showLayout(view);
            return this;
        }
    }

    @Override
    public ILoadingHelper setNoNetworkMessage(@StringRes int noNetWorkMessage) {
        this.noNetWorkMessage = helper.getContext().getResources().getString(noNetWorkMessage);
        return this;
    }

    @Override
    public ILoadingHelper setNoNetworkMessage(String noNetWorkMessage) {
        this.noNetWorkMessage = noNetWorkMessage;
        return this;
    }

    @Override
    public ILoadingHelper setNoNetworkIcon(@DrawableRes int noNetWorkDrawable) {
        this.noNetWorkDrawable = noNetWorkDrawable;
        return this;
    }

    @Override
    public ILoadingHelper showError() {
        synchronized (helper.getView()) {
            state = State.ERROR;
            View view = helper.inflate(config.provideErrorLayoutId());
            setIcon((ImageView) view.findViewById(config.provideIconViewId()), errorDrawable);
            setMessage((TextView) view.findViewById(config.provideMessageViewId()), errorMessage);
            setButtonRetry(view.findViewById(config.provideRetryBtnViewId()));
            helper.showLayout(view);
            return this;
        }
    }

    @Override
    public ILoadingHelper setErrorMessage(@StringRes int errorMessage) {
        this.errorMessage = helper.getContext().getResources().getString(errorMessage);
        return this;
    }

    @Override
    public ILoadingHelper setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    @Override
    public ILoadingHelper setErrorIcon(@DrawableRes int errorIcon) {
        this.errorDrawable = errorIcon;
        return this;
    }

    @Override
    public ILoadingHelper setRetryListener(OnRetryListener retryListener) {
        this.retryListener = retryListener;
        return this;
    }

    @Override
    public ILoadingHelper showLogin() {
        synchronized (helper.getView()) {
            state = State.NO_LOGIN;
            View view = helper.inflate(config.provideNoLoginLayoutId());
            setIcon((ImageView) view.findViewById(config.provideIconViewId()), loginDrawable);
            setMessage((TextView) view.findViewById(config.provideMessageViewId()), loginMessage);
            setButtonLogin(view.findViewById(config.provideLoginBtnViewId()));
            helper.showLayout(view);
            return this;
        }
    }

    @Override
    public ILoadingHelper setLoginMessage(@StringRes int loginMessage) {
        this.loginMessage = helper.getContext().getResources().getString(loginMessage);
        return this;
    }

    @Override
    public ILoadingHelper setLoginMessage(String loginMessage) {
        this.loginMessage = loginMessage;
        return this;
    }

    @Override
    public ILoadingHelper setLoginIcon(@DrawableRes int loginIcon) {
        this.loginDrawable = loginIcon;
        return this;
    }

    @Override
    public ILoadingHelper setLoginListener(OnLoginListener loginListener) {
        this.loginListener = loginListener;
        return this;
    }

    @Override
    public ILoadingHelper restore() {
        synchronized (helper.getView()) {
            if (getState() == State.NORMAL) {
                return this;
            }
            state = State.NORMAL;
            helper.restoreView();
            return this;
        }
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public ILoadingHelper showState(State state) {
        switch (state) {
            case LOADING:
                showLoading();
                break;
            case EMPTY:
                showEmpty();
                break;
            case NO_NETWORK:
                showNoNetwork();
                break;
            case ERROR:
                showError();
                break;
            case NO_LOGIN:
                showLogin();
                break;
            case NORMAL:
                restore();
                break;
        }
        return this;
    }
}

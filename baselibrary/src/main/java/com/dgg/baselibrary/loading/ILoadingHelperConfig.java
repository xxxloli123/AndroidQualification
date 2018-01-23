package com.dgg.baselibrary.loading;

/**
 * 功能描述:提供加载过程替换的样式
 * Created by LuoHaifeng on 2017/5/2.
 * Email:496349136@qq.com
 */

public interface ILoadingHelperConfig {
    int provideEmptyLayoutId();
    int provideLoadingLayoutId();
    int provideNoNetworkLayoutId();
    int provideErrorLayoutId();
    int provideNoLoginLayoutId();

    int provideIconViewId();
    int provideMessageViewId();
    int provideRefreshBtnViewId();
    int provideRetryBtnViewId();
    int provideLoginBtnViewId();
    int provideSettingBtnViewId();
}

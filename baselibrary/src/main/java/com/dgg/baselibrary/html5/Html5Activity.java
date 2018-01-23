package com.dgg.baselibrary.html5;

import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.dgg.baselibrary.R;
import com.dgg.baselibrary.loading.ILoadingHelper;
import com.dgg.baselibrary.loading.LoadingHelper;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.github.lzyzsd.jsbridge.BridgeWebView;

/**
 * Created by qiqi on 17/7/19.
 */

public class Html5Activity extends KtBaseActivity {
    public static final String WEB_PATH = "web_path";
    public static final String IS_SUPPORT = "isSupport";
    public static final String PAGE_TITLE = "pageTitle";
    private ILoadingHelper loading;
    private boolean isSupport;

    private BridgeWebView wb_view;

    @Override
    protected int contentLayoutId() {
        return R.layout.activity_html5;
    }

    @Override
    protected void initData() {
        String title = getIntent().getStringExtra(PAGE_TITLE);
        initTitle(TextUtils.isEmpty(title) ? "详情" : title);

        wb_view = (BridgeWebView) findViewById(R.id.wb_view);
        loading = LoadingHelper.with(wb_view).setRetryListener(new ILoadingHelper.OnRetryListener() {
            @Override
            public void onRetry(ILoadingHelper loadingHelper) {
                wb_view.loadUrl(getIntent().getStringExtra(WEB_PATH));
            }
        });
        isSupport = getIntent().getBooleanExtra(IS_SUPPORT, false);
        if (isSupport) {
            // 设置可以支持缩放
            wb_view.getSettings().setSupportZoom(true);
// 设置出现缩放工具
            wb_view.getSettings().setBuiltInZoomControls(true);
//扩大比例的缩放
            wb_view.getSettings().setUseWideViewPort(true);
//自适应屏幕、
//        wb_view.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
            wb_view.getSettings().setLoadWithOverviewMode(true);
        }
        wb_view.loadUrl(getIntent().getStringExtra(WEB_PATH));
        loading.showLoading();
        wb_view.setWebChromeClient(new WebChromeClient() {
                                       @Override
                                       public void onProgressChanged(WebView view, int newProgress) {
                                           if (newProgress > 90) {
                                               loading.restore();
                                           }
                                           super.onProgressChanged(view, newProgress);
                                       }
                                   }
        );

    }

    @Override
    public void reLoadingData() {
        wb_view.loadUrl(getIntent().getStringExtra(WEB_PATH));
    }
}

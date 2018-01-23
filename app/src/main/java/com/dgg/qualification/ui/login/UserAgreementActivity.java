package com.dgg.qualification.ui.login;

import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.R;

/**
 * Created by qiqi on 17/7/26.
 */

public class UserAgreementActivity extends KtBaseActivity {
    @Override
    protected int contentLayoutId() {
        return R.layout.activity_user_agreement;
    }

    @Override
    protected void initData() {
        initTitle("顶呱呱学院用户协议");

    }

    @Override
    public void reLoadingData() {

    }
}

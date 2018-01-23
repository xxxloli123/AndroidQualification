package com.dgg.qualification.ui.mine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgg.baselibrary.db.User;
import com.dgg.baselibrary.db.UserDao;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.MainActivity;
import com.dgg.qualification.R;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.common.AppCommon;
import com.dgg.qualification.service.CheckUpdateService;
import com.dgg.qualification.ui.login.LoginActivity;
import com.dgg.qualification.ui.login.WelcomeActivity;
import com.dgg.qualification.ui.mine.server.MineServer;
import com.jingewenku.abrahamcaijin.commonutil.AppApplicationMgr;
import com.jingewenku.abrahamcaijin.commonutil.AppCleanMgr;

import exam.e8net.com.exam.DBManager;

/**
 * Created by qiqi on 17/7/8.
 */

public class SetActivity extends KtBaseActivity implements View.OnClickListener {

    private LinearLayout feedback, cacheAction, upVersion;
    private TextView cacheSize, exit, appVersion;
    private ImageView appBNewVersion;

    @Override
    protected int contentLayoutId() {
        return R.layout.activity_set;
    }

    @Override
    protected void initData() {
        initTitle("设置");
        feedback = (LinearLayout) findViewById(R.id.feedback);
        feedback.setOnClickListener(this);
        CommonUtils.setOnTouch(feedback);
        cacheAction = (LinearLayout) findViewById(R.id.cacheAction);
        cacheAction.setOnClickListener(this);
        CommonUtils.setOnTouch(cacheAction);
        cacheSize = (TextView) findViewById(R.id.cacheSize);
        cacheSize.setText(AppCleanMgr.getAppClearSize(this));
        exit = (TextView) findViewById(R.id.exit);
        exit.setOnClickListener(this);
        CommonUtils.setOnTouch(exit);
        upVersion = (LinearLayout) findViewById(R.id.upVersion);
        upVersion.setOnClickListener(this);
        CommonUtils.setOnTouch(upVersion);
        appVersion = (TextView) findViewById(R.id.appVersion);
        appBNewVersion = (ImageView) findViewById(R.id.appBNewVersion);

//        String userPhone = (String) SharedPreferencesUtils.getParam(this, LoginActivity.USER_PHONE, "");
        User user = DBManager.getInstance(this).getCrrentUser();
        if (user != null) {
            exit.setVisibility(!user.isLogin ? View.GONE : View.VISIBLE);
        } else {
            exit.setVisibility(View.GONE);
        }
        appVersion.setText(AppApplicationMgr.getVersionName(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feedback:
                if (!AppCommon.chechIsLogin(SetActivity.this, false)) {
                    return;
                }
                startActivity(new Intent(SetActivity.this, FeedbackActivity.class));
                break;
            case R.id.cacheAction://清理缓存
                AlertDialog.Builder dilog = new AlertDialog.Builder(this);
                dilog.setTitle("提示");
                dilog.setMessage("确定对缓存进行清理？");
                dilog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppCleanMgr.cleanInternalCache(SetActivity.this);
                        cacheSize.setText(AppCleanMgr.getAppClearSize(SetActivity.this));
                    }
                });
                dilog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dilog.show();
                break;
            case R.id.exit:
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("温馨提示").setMessage("确定要退出登录吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MineServer.exit(SetActivity.this, CommonUtils.encryptDES(Api.getCommonData()))
                                        .subscribe(new DefaultSubscriber<BaseJson>(SetActivity.this) {
                                            @Override
                                            public void onNext(BaseJson baseJson) {
                                                cleanLoginExit();
                                                ToastUtils.showToast(SetActivity.this, "退出成功");
                                                onBackPressed();
                                            }
                                        });
                            }
                        }).create();
                dialog.show();
                break;
            case R.id.upVersion:
                startService(new Intent(this, CheckUpdateService.class));
                break;
        }
    }


    public void cleanLoginExit() {
        UserDao ud = new UserDao(this);
        User user = DBManager.getInstance(this).getCrrentUser();
        if (user != null) {
            user.isLogin = false;
            ud.refreshUser(user);
        }
        killAll();
        startActivity(new Intent(SetActivity.this, MainActivity.class));
        SharedPreferencesUtils.setParam(SetActivity.this, WelcomeActivity.IS_TO_VIEW, true);
    }

    @Override
    public void reLoadingData() {

    }
}

package com.dgg.qualification.ui.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dgg.baselibrary.db.User;
import com.dgg.baselibrary.db.UserDao;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.dgg.baselibrary.tools.UIUtils;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.MainActivity;
import com.dgg.qualification.R;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.ui.login.server.LoginServer;
import com.dgg.qualification.ui.topic.activity.ReginChooseActivity;

import org.simple.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import exam.e8net.com.exam.DBManager;
import exam.e8net.com.exam.QuestionActivity;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by qiqi on 17/7/12.
 */

public class LoginActivity extends KtBaseActivity implements View.OnClickListener {

    public static final String USER_PHONE = "userPhone";
    public static final String IS_EXIT = "isExit";

    private static Subscription RXJAVA = null;
    private EditText phone, msg;
    private TextView sendMSG, login, userAgreement;
    private CheckBox checkbox;
    private RelativeLayout root;
    private ImageView logo;
    private Long time = 60L;
    private Animation loginInputAnim1, loginInputAnim2, logoAnim1, logoAnim2;
    private LinearLayout inputLayout;
    private boolean isExit = false;
    // 状态栏的高度
    private int statusBarHeight;
    // 软键盘的高度
    private int keyboardHeight;
    // 软键盘的显示状态
    private boolean isShowKeyboard;
    private float inputDistance = 300;//输入框位移值
    private long durationMillis = 500;//动画运行时间


    @Override
    protected int contentLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {
        initTitle("登录");
        isExit = getIntent().getBooleanExtra(IS_EXIT, false);
        statusBarHeight = getStatusBarHeight(getApplicationContext());
        phone = (EditText) findViewById(R.id.phone);
        msg = (EditText) findViewById(R.id.msg);
        sendMSG = (TextView) findViewById(R.id.sendMSG);
        login = (TextView) findViewById(R.id.login);
        sendMSG.setOnClickListener(this);
        login.setOnClickListener(this);
        CommonUtils.setOnTouch(login);
        CommonUtils.setOnTouch(sendMSG);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        userAgreement = (TextView) findViewById(R.id.userAgreement);
        userAgreement.setOnClickListener(this);
        root = (RelativeLayout) findViewById(R.id.root);
        inputLayout = (LinearLayout) findViewById(R.id.inputLayout);
        logo = (ImageView) findViewById(R.id.logo);
        root.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        initAnim();
    }

    private void initAnim() {
        loginInputAnim1 = new TranslateAnimation(0, 0, 0, -inputDistance);
        loginInputAnim1.setDuration(durationMillis);
        loginInputAnim1.setFillAfter(true);
        loginInputAnim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //清除动画
                inputLayout.clearAnimation();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) inputLayout.getLayoutParams();
                params.topMargin = params.topMargin - (int) inputDistance;
                inputLayout.setLayoutParams(params);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        loginInputAnim2 = new TranslateAnimation(0, 0, 0, inputDistance);
        loginInputAnim2.setDuration(durationMillis);
        loginInputAnim2.setFillAfter(true);
        loginInputAnim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //清除动画
                inputLayout.clearAnimation();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) inputLayout.getLayoutParams();
                params.topMargin = params.topMargin + (int) inputDistance;
                inputLayout.setLayoutParams(params);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        logoAnim1 = AnimationUtils.loadAnimation(this, R.anim.login_logo1);
        logoAnim2 = AnimationUtils.loadAnimation(this, R.anim.login_logo2);
    }

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener =
            new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    // 应用可以显示的区域。此处包括应用占用的区域，
                    // 以及ActionBar和状态栏，但不含设备底部的虚拟按键。
                    Rect r = new Rect();
                    root.getWindowVisibleDisplayFrame(r);

                    // 屏幕高度。这个高度不含虚拟按键的高度
                    int screenHeight = root.getRootView().getHeight();

                    int heightDiff = screenHeight - (r.bottom - r.top);

                    // 在不显示软键盘时，heightDiff等于状态栏的高度
                    // 在显示软键盘时，heightDiff会变大，等于软键盘加状态栏的高度。
                    // 所以heightDiff大于状态栏高度时表示软键盘出现了，
                    // 这时可算出软键盘的高度，即heightDiff减去状态栏的高度
                    if (keyboardHeight == 0 && heightDiff > statusBarHeight) {
                        keyboardHeight = heightDiff - statusBarHeight;
                    }

                    if (isShowKeyboard) {
                        // 如果软键盘是弹出的状态，并且heightDiff小于等于状态栏高度，
                        // 说明这时软键盘已经收起
                        if (heightDiff <= statusBarHeight) {
                            isShowKeyboard = false;
                            onHideKeyboard();
                        }
                    } else {
                        // 如果软键盘是收起的状态，并且heightDiff大于状态栏高度，
                        // 说明这时软键盘已经弹出
                        if (heightDiff > statusBarHeight) {
                            isShowKeyboard = true;
                            onShowKeyboard();
                        }
                    }
                }
            };

    private void onShowKeyboard() {
        // 在这里处理软键盘弹出的回调
        logo.startAnimation(logoAnim1);
        inputLayout.startAnimation(loginInputAnim1);
    }

    private void onHideKeyboard() {
        // 在这里处理软键盘收回的回调
        logo.startAnimation(logoAnim2);
        inputLayout.startAnimation(loginInputAnim2);
    }

    // 获取状态栏高度
    private int getStatusBarHeight(Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendMSG:
                if (TextUtils.isEmpty(phone.getText().toString().trim())) {
                    ToastUtils.showToast(LoginActivity.this, "请输入手机号");
                    return;
                }
                if (!(phone.getText().toString().trim().length() == 11)) {
                    ToastUtils.showToast(LoginActivity.this, "输入11位数字");
                    return;
                }
                timingAction();
                HashMap<String, Object> map = Api.getLoginCommonData();
                map.put("phone", phone.getText().toString().trim());

                LoginServer.sendMssg(this, CommonUtils.encryptDES(map)).subscribe(new DefaultSubscriber<BaseJson>(this) {
                    @Override
                    public void onNext(BaseJson arrayListBaseJson) {
                        ToastUtils.showToast(LoginActivity.this, "验证码获取成功");
                        msg.requestFocus();
                    }
                });
                break;
            case R.id.login:
                final UserDao ud = new UserDao(LoginActivity.this);
//                final User user = new User();
                if (TextUtils.isEmpty(phone.getText().toString().trim())) {
                    ToastUtils.showToast(LoginActivity.this, "请输入手机号");
                    return;
                }
                if (!(phone.getText().toString().trim().length() == 11)) {
                    ToastUtils.showToast(LoginActivity.this, "输入11位数字");
                    return;
                }
                if (TextUtils.isEmpty(msg.getText().toString().trim())) {
                    ToastUtils.showToast(LoginActivity.this, "请输验证码");
                    return;
                }
                if (!checkbox.isChecked()) {
                    ToastUtils.showToast(LoginActivity.this, "请阅读并同意《用户协议》");
                    return;
                }
                if ((int) SharedPreferencesUtils.getParam(this, ReginChooseActivity.AREA_ID, 0) == 0) {
                    startActivity(new Intent(LoginActivity.this, ReginChooseActivity.class));
                    return;
                }
                HashMap<String, Object> map2 = Api.getLoginCommonData();
                map2.put("phone", phone.getText().toString().trim());
                map2.put("msgCode", msg.getText().toString().trim());
                map2.put("areaId", (int) SharedPreferencesUtils.getParam(this, ReginChooseActivity.AREA_ID, 0));

                LoginServer.sendLoginData(this, CommonUtils.encryptDES(map2)).subscribe(new DefaultSubscriber<BaseJson<Login>>(this) {
                    @Override
                    public void onNext(BaseJson<Login> arrayListBaseJson) {
                        SharedPreferencesUtils.setParam(LoginActivity.this, USER_PHONE, phone.getText().toString().trim());
                        User user;
                        user = DBManager.getInstance(LoginActivity.this).getCrrentUser();
                        if (user == null)
                            user = new User();
                        user.sex = arrayListBaseJson.data.sex;
                        user.setName(arrayListBaseJson.data.username);
                        user.areaId = arrayListBaseJson.data.areaId;
                        user.head = arrayListBaseJson.data.userImgUrl;
                        user.phone = arrayListBaseJson.data.phone;
                        user.userID = arrayListBaseJson.data.id;
                        user.uid = arrayListBaseJson.data.uid;
                        user.isLogin = true;
                        if (DBManager.getInstance(LoginActivity.this).getUser(arrayListBaseJson.data.phone) == null) {
                            user.id = (int) ud.getUserCount();
                            ud.add(user);
                        } else {
                            ud.refreshUser(user);
                        }
                        ToastUtils.showToast(LoginActivity.this, "登录成功");
                        if (isExit)
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        onBackPressed();
                        SharedPreferencesUtils.setParam(LoginActivity.this, "uid", arrayListBaseJson.data.uid);
                        EventBus.getDefault().post("1", QuestionActivity.UP_TOPIC_DATA);//通知刷新数据
                    }
                });
                break;
            case R.id.userAgreement:
                startActivity(new Intent(LoginActivity.this, UserAgreementActivity.class));
                break;
        }
    }

    private void timingAction() {

        if (sendMSG.isEnabled())
            RXJAVA = Observable.timer(1, 1, TimeUnit.SECONDS)
//                .subscribeOn(Schedulers.io()
//                .OnSubscribe(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            if (time > 0 && time < 61) {
                                setSendMsgInfo("获取验证码(" + time + "s)", false);
                            } else if (time <= 0) {
                                RXJAVA.unsubscribe();
                                setSendMsgInfo("获取验证码", true);
                                time = 60L;
                            }
                            time--;
                        }
                    });

    }

    private void setSendMsgInfo(final String tt, final boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendMSG.setText(tt);
                sendMSG.setEnabled(enable);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RXJAVA != null)
            RXJAVA.unsubscribe();
    }

    @Override
    public void reLoadingData() {

    }
}

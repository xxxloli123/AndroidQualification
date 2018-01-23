package com.dgg.qualification.ui.login;

import android.Manifest;
import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.telephony.TelephonyManager;

import com.dgg.baselibrary.tools.LogUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.MainActivity;
import com.dgg.qualification.R;
import com.dgg.qualification.app.MyApplication;
import com.permissionlibrary.HiPermission;
import com.permissionlibrary.PermissionCallback;
import com.permissionlibrary.PermissonItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by qiqi on 17/7/25.
 */

public class StartPageActivity extends KtBaseActivity {
    @Override
    protected int contentLayoutId() {
        return R.layout.activity_start_page;
    }

    @Override
    protected void initData() {

        List<PermissonItem> permissonItems = new ArrayList<PermissonItem>();
        permissonItems.add(new PermissonItem(Manifest.permission.READ_PHONE_STATE, "获取设备信息", R.drawable.permission_ic_phone));
        HiPermission.create(this)
//                        .title(getString(R.string.permission_cus_title))
                .permissions(permissonItems)
                .filterColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getTheme()))
//                        .msg(getString(R.string.permission_cus_msg))
//                        .style(R.style.PermissionBlueStyle)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        LogUtils.d("定位权限" + "onClose");
                        showToast("你拒绝了授权");
                        finish();
                    }

                    @Override
                    public void onFinish() {
//                                showToast("权限申请完成");
//                        // -----------location config ------------
                        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                        MyApplication.IMEI = TelephonyMgr.getDeviceId();
                        Observable.timer(2, TimeUnit.SECONDS)
                                .subscribe(new Action1<Long>() {
                                    @Override
                                    public void call(Long aLong) {
                                        boolean isToView = (boolean) SharedPreferencesUtils.getParam(StartPageActivity.this, WelcomeActivity.IS_TO_VIEW, false);
                                        StartPageActivity.this.startActivity(new Intent(StartPageActivity.this, isToView ? MainActivity.class : WelcomeActivity.class));
                                        finish();
                                    }
                                });
                    }

                    @Override
                    public void onDeny(String permisson, int position) {
                    }

                    @Override
                    public void onGuarantee(String permisson, int position) {
                    }
                });




    }

    @Override
    public void reLoadingData() {

    }
}

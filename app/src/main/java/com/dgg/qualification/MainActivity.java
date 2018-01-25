package com.dgg.qualification;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.dgg.baselibrary.db.User;
import com.dgg.baselibrary.db.UserDao;
import com.dgg.baselibrary.tools.LogUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.app.MyApplication;
import com.dgg.qualification.baidu.service.LocationService;
import com.dgg.qualification.service.CheckUpdateService;
import com.dgg.qualification.ui.TabFrgmentAdapter;
import com.dgg.qualification.ui.login.LoginActivity;
import com.dgg.qualification.ui.login.WelcomeActivity;
import com.dgg.qualification.ui.main.fragment.HomePageFragment;
import com.dgg.qualification.ui.main.fragment.MineFragment;
import com.dgg.qualification.ui.main.fragment.TopicFragment;
import com.dgg.qualification.ui.mine.MessageActivity;
import com.dgg.qualification.ui.mine.SetActivity;
import com.dgg.qualification.ui.topic.activity.ReginChooseActivity;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.permissionlibrary.HiPermission;
import com.permissionlibrary.PermissionCallback;
import com.permissionlibrary.PermissonItem;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import exam.e8net.com.exam.DBManager;

public class MainActivity extends KtBaseActivity {
    public static final String APP_EXIT = "app_exit";
    public static final String IS_SET_ALIAS = "is_set_alias";
    private static final int MSG_SET_ALIAS = 1001;
    private LocationService locationService;
    private ViewPager page;
    private CommonTabLayout tab;
    private String[] mTitles = {"首页", "题库", "我的"};
    private int[] mIconUnselectIds = {
            R.mipmap.nav_shouye_n,
            R.mipmap.nav_tiku_n, R.mipmap.nav_me_n};
    private int[] mIconSelectIds = {
            R.mipmap.nav_shouye_s,
            R.mipmap.nav_tiku_s, R.mipmap.nav_me_s};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private boolean isCreat = false;
    private long exitTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int contentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
//        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        String tel = tm.getLine1Number();//手机号码
//        Log.d("appLog", "当前手机号码：--- " + tel);
        setAlias();
        page = (ViewPager) findViewById(R.id.page);
        tab = (CommonTabLayout) findViewById(R.id.tab);
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        tab.setTabData(mTabEntities);
        mFragments.add(new HomePageFragment());
        mFragments.add(new TopicFragment());
        mFragments.add(new MineFragment());
        TabFrgmentAdapter mAdapter = new TabFrgmentAdapter(getSupportFragmentManager(), mFragments);
        page.setAdapter(mAdapter);
        String s;
        page.setOffscreenPageLimit(3);
        tab.setCurrentTab(page.getCurrentItem());
        tab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                page.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        page.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tab.setCurrentTab(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        User uu = DBManager.getInstance(this).getCrrentUser();
        if (uu == null) {
            int areaID = (int) SharedPreferencesUtils.getParam(this, ReginChooseActivity.AREA_ID, 0);
            isCreat = areaID == 0 ? true : false;
        } else {
            isCreat = uu.areaId == 0 ? true : false;
        }
        startService(new Intent(this, CheckUpdateService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<PermissonItem> permissonItems = new ArrayList<PermissonItem>();
        permissonItems.add(new PermissonItem(Manifest.permission.ACCESS_FINE_LOCATION, "定位权限", R.drawable.permission_ic_location));
//        permissonItems.add(new PermissonItem(Manifest.permission.READ_PHONE_STATE, "定位权限", R.drawable.permission_ic_location));
        HiPermission.create(MainActivity.this)
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
                    }

                    @Override
                    public void onFinish() {
//                                showToast("权限申请完成");
//                        // -----------location config ------------
                        locationService = MyApplication.locationService;
                        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
                        locationService.registerListener(mListener);
                        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
                        locationService.start();
                    }

                    @Override
                    public void onDeny(String permisson, int position) {
                        LogUtils.d("定位权限" + "onDeny");
                    }

                    @Override
                    public void onGuarantee(String permisson, int position) {
                        LogUtils.d("定位权限" + "onGuarantee");
                    }
                });

//        page.setCurrentItem(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JpushAction();
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
            /*储存定位的省份信息*/
                SharedPreferencesUtils.setParam(MainActivity.this, "BDLocation", location.getProvince());
                if (isCreat) {
                    startActivity(new Intent(MainActivity.this, ReginChooseActivity.class));
                    isCreat = false;
                }

            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
//                moveTaskToBack(false);
                //home键退出
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                this.startActivity(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void JpushAction() {
        if (MyApplication.isJpush) {
            Intent intent = new Intent();
            Class<?> cls = null;
//            switch (HDApplication.jpushData.newsType) {//(1为公告2为停工复工类3为收入4为升级项材料5施工图片)
//                case 1:
////                    cls = AnnouncementActivity.class;
//                    break;
//            }
            cls = MessageActivity.class;
            intent.setClass(MainActivity.this, cls);
            MainActivity.this.startActivity(intent);
            MyApplication.isJpush = false;
        }
    }

    @Subscriber(tag = APP_EXIT)
    private void exitApp(String code) {
        if (!"80006".equals(code)) {
            return;
        }
        UserDao ud = new UserDao(this);
        User user = DBManager.getInstance(this).getCrrentUser();
        if (user != null && user.isLogin) {
            user.isLogin = false;
            ud.refreshUser(user);
            killAll();
            Intent inttent = new Intent(this, LoginActivity.class);
            inttent.putExtra(LoginActivity.IS_EXIT, true);
            startActivity(inttent);
            SharedPreferencesUtils.setParam(this, WelcomeActivity.IS_TO_VIEW, true);
            showToast("您已在其他设备登录,请重新登录");
            LogUtils.d("您已在其他设备登录");
        }
    }

    @Override
    public void reLoadingData() {

    }

    private void setAlias() {
        if ((boolean) SharedPreferencesUtils.getParam(getApplicationContext(), IS_SET_ALIAS, false)) {
            LogUtils.d("之前设置成功过别名");
            return;
        }
        String alias = (BuildConfig.DEBUG ? "t_" : "p_") + MyApplication.IMEI;
        Log.d("appLog", this + "+++++++++++++++++++++ " + alias);
        // 调用 Handler 来异步设置别名
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            switch (code) {
                case 0:
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    SharedPreferencesUtils.setParam(getApplicationContext(), IS_SET_ALIAS, true);
                    LogUtils.d("成功set在别名");
                    break;
                case 6002:
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    LogUtils.d("重新设置别名");
                    break;
            }
        }
    };

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAlias(getApplicationContext(), (String) msg.obj, mAliasCallback);
                    break;
            }
        }
    };
}

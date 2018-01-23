package com.dgg.qualification.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.LogUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.dgg.qualification.BuildConfig;
import com.dgg.qualification.R;
import com.dgg.qualification.app.MyApplication;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.service.been.UpdateVertion;
import com.facebook.stetho.common.LogUtil;
import com.jingewenku.abrahamcaijin.commonutil.AppNetworkMgr;
import com.jingewenku.abrahamcaijin.commonutil.AppWifiHelperMgr;
//import com.loyo.oa.v2.BuildConfig;
//import com.loyo.oa.v2.R;
//import com.loyo.oa.v2.activityui.home.api.HomeService;
//import com.loyo.oa.v2.application.MainApp;
//import com.loyo.oa.v2.common.ExtraAndResult;
//import com.loyo.oa.v2.common.Global;
//import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
//import com.loyo.oa.v2.network.LoyoErrorChecker;
//import com.loyo.oa.v2.tool.LogUtil;
//import com.loyo.oa.v2.tool.UpdateTipActivity;
//import com.loyo.oa.v2.tool.Utils;

import java.io.File;
import java.util.HashMap;

/**
 * 版本
 */
public class CheckUpdateService extends Service {

    public static final int PARAM_STOP_SELF = 2;
    public static final int PARAM_START_DOWNLOAD = 3;

    boolean isChecking = false, isToast = false;
    CompleteReceiver completeReceiver;
    DownloadManager downloadManager;
    long enqueue = 0;
    UpdateVertion mUpdateInfo;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        completeReceiver = new CompleteReceiver();
        registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //检查更新时间
        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey("EXTRA_TOAST")) {
            isToast = intent.getBooleanExtra("EXTRA_TOAST", false);
        }

        int param = intent.getIntExtra("data", 0);
        if (param == PARAM_START_DOWNLOAD && mUpdateInfo != null) {
            downloadApp();
            return START_REDELIVER_INTENT;
        } else if (param == PARAM_STOP_SELF) {
            stopSelf();
            return START_REDELIVER_INTENT;
        }
        checkUpdate();
        return START_REDELIVER_INTENT;
    }

    void downloadApp() {

        if (enqueue == 0) {
            ToastUtils.showToast(this, "WIFI环境,正在更新最新版...");
            LogUtils.d("版本更新地址:" + mUpdateInfo.url);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mUpdateInfo.url))
                    .setTitle(getResources().getString(R.string.app_name))
                    .setDescription("下载" + mUpdateInfo.name)
                    //写入到应用的存储目录下，避免申请权限
                    .setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, mUpdateInfo.name)
                    .setVisibleInDownloadsUi(true);

            try {
                enqueue = downloadManager.enqueue(request);
            } catch (Exception ex) {
                LogUtil.d("下载异常抛出");
//                Global.ProcException(ex);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (completeReceiver != null) {
            unregisterReceiver(completeReceiver);
        }
        super.onDestroy();
    }

    private void checkUpdate() {
        if (isChecking) {
            stopSelf();
            return;
        }

        isChecking = true;

        //应该仅在wifi下升级
        if (!AppNetworkMgr.isWifiByType(this)) {
            if (isToast) {
                Toast.makeText(this, "没有网络连接，仅在有WiFi升级", Toast.LENGTH_SHORT).show();
            }
            isChecking = false;
            stopSelf();
            return;
        }
        HashMap<String, Object> map = Api.getCommonData();
        map.put("envFlag", BuildConfig.DEBUG ? "1" : "2");
        LogUtils.d("阿布模式" + BuildConfig.DEBUG);

        HomeServer.getUpdateVersion(this, CommonUtils.encryptDES(map))
                .subscribe(new DefaultSubscriber<BaseJson<UpdateVertion>>(DefaultSubscriber.SILENCE) {
                    @Override
                    public void onNext(BaseJson<UpdateVertion> baseJson) {
                        mUpdateInfo = baseJson.data;
                        if (null == mUpdateInfo) {
                            LogUtil.d(" 版本信息为空 ");
                            return;
                        }

                        try {
                            if (mUpdateInfo.no > BuildConfig.VERSION_CODE) {
                                //有新版本
                                AppWifiHelperMgr appWifiHelperMgr = new AppWifiHelperMgr(CheckUpdateService.this);
                                if (mUpdateInfo.isForceUpdate()) {//后台自动更新
                                    if (appWifiHelperMgr.isWifiEnabled()) {
                                        deleteFile();
                                        downloadApp();
                                    } else {
                                        ToastUtils.showToast(MyApplication.getContext(), "请在WIFI环境下更新最新版本");
                                    }
                                } else if (!mUpdateInfo.isForceUpdate() || isToast) {//弹窗提示更新
                                    deleteFile();
                                    Intent intentUpdateTipActivity = new Intent(CheckUpdateService.this, UpdateTipActivity.class);
                                    intentUpdateTipActivity.putExtra("data", mUpdateInfo.updateLog);
                                    intentUpdateTipActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intentUpdateTipActivity);
                                } else {
                                    stopSelf();
                                }
                            } else {
                                if (isToast) {
                                    ToastUtils.showToast(MyApplication.getContext(), "你的软件已经是最新版本");
                                }
                                stopSelf();
                            }
                            isChecking = false;
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        stopSelf();
                    }
                });
    }

    /**
     * 删除APK
     */
    private void deleteFile() {
        File path = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, mUpdateInfo.name);
        if (file.exists()) {
            file.delete();
        }
    }

    private void installApk() {
        File apkfile = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), mUpdateInfo.name);
        if (!apkfile.exists()) {
            stopSelf();
            return;
        }

        /**
         * Android 7.0 自动升级bug
         * http://stackoverflow.com/questions/39147608/android-install-apk-with-intent-view-action-not-working-with-file-provider
         */
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24 /*Build.VERSION_CODES.N*/) {
            Uri apkUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", apkfile);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivity(intent);
        } else {
            intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            startActivity(intent);
        }
        stopSelf();
    }

    class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(enqueue);
            Cursor cursor = downloadManager.query(query);
            if (cursor.moveToFirst()) {
                int culumnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(culumnIndex)) {
                    installApk();
                }
            }

            stopSelf();
        }
    }

//    public static class UpdateInfo implements Serializable {
//
//        public String versionName;
//        public String appDescription;
//        public String apkUrl;
//        public boolean forceUpdate;
//        public boolean autoUpdate;
//        public int versionCode;
//
//        public String apkName() {
//            return apkUrl.substring(apkUrl.lastIndexOf("/") + 1);
//        }
//    }

}

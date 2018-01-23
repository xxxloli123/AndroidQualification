package com.dgg.qualification.ui.mine;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgg.baselibrary.db.User;
import com.dgg.baselibrary.db.UserDao;
import com.dgg.baselibrary.loading.ldialog.LoadingDialog;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.LogUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.R;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.common.compress.Compressor;
import com.dgg.qualification.ui.mine.server.MineServer;
import com.jingewenku.abrahamcaijin.commonutil.GlideUtils;
import com.permissionlibrary.HiPermission;
import com.permissionlibrary.PermissionCallback;
import com.permissionlibrary.PermissonItem;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.bingoogolapple.photopicker.activity.PhotoPickerActivity;
import exam.e8net.com.exam.DBManager;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by qiqi on 17/7/8.
 */

public class PersonInfoActivity extends KtBaseActivity implements View.OnClickListener {
    public static final String EDIT_HEAD = "edit_head";
    public static final String EDIT_NAME = "edit_name";


    private LinearLayout head, edit_name, edit_sex;
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 120;
    private TextView sex, name, phone;
    final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
    private Handler handler = new Handler();
    private ImageView headImg;
    private UserDao ud;
    private LoadingDialog ld;

    @Override
    protected int contentLayoutId() {
        return R.layout.activity_person_info;
    }

    @Override
    protected void initData() {
        initTitle("个人资料");
        head = (LinearLayout) findViewById(R.id.head);
        head.setOnClickListener(this);
        CommonUtils.setOnTouch(head);
        edit_name = (LinearLayout) findViewById(R.id.edit_name);
        edit_name.setOnClickListener(this);
        CommonUtils.setOnTouch(edit_name);
        edit_sex = (LinearLayout) findViewById(R.id.edit_sex);
        edit_sex.setOnClickListener(this);
        CommonUtils.setOnTouch(edit_sex);
        sex = (TextView) findViewById(R.id.sex);
        name = (TextView) findViewById(R.id.name);
        headImg = (ImageView) findViewById(R.id.headImg);
        phone = (TextView) findViewById(R.id.phone);
        ud = new UserDao(PersonInfoActivity.this);
        User user = DBManager.getInstance(this).getCrrentUser();
        sex.setText(user.sex);
        name.setText(user.getName());
        phone.setText(user.phone);
        initHead(user.head);
    }

    private void initHead(String path) {
        if (!TextUtils.isEmpty(path))
            GlideUtils.getInstance().LoadContextCircleBitmap(PersonInfoActivity.this, path, headImg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head:
                List<PermissonItem> permissonItems = new ArrayList<PermissonItem>();
                permissonItems.add(new PermissonItem(Manifest.permission.CAMERA, "照相机", R.drawable.permission_ic_camera));
                permissonItems.add(new PermissonItem(Manifest.permission.READ_EXTERNAL_STORAGE, "存储卡", R.drawable.permission_ic_memory));

                HiPermission.create(PersonInfoActivity.this)
//                        .title(getString(R.string.permission_cus_title))READ_EXTERNAL_STORAGE
                        .permissions(permissonItems)
                        .filterColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getTheme()))
//                        .msg(getString(R.string.permission_cus_msg))
//                        .style(R.style.PermissionBlueStyle)
                        .checkMutiPermission(new PermissionCallback() {
                            @Override
                            public void onClose() {
                                LogUtils.d("照相机" + "onClose");
                                showToast("你拒绝了授权");
                            }

                            @Override
                            public void onFinish() {
//                                showToast("权限申请完成");
                                File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "HDPhotoPickerTakePhoto");
                                startActivityForResult(PhotoPickerActivity.newIntent(PersonInfoActivity.this,
                                        takePhotoDir, 1, null, true), REQUEST_CODE_CHOOSE_PHOTO);
                            }

                            @Override
                            public void onDeny(String permisson, int position) {
                                LogUtils.d("照相机" + "onDeny");
                            }

                            @Override
                            public void onGuarantee(String permisson, int position) {
                                LogUtils.d("照相机" + "onGuarantee");
                            }
                        });
                break;

            case R.id.edit_name:
                Intent intenNAME = new Intent(this, EditorNameSexActivity.class);
                intenNAME.putExtra(EditorNameSexActivity.type, EditorNameSexActivity.editorName);
                intenNAME.putExtra("name", name.getText().toString());
                startActivityForResult(intenNAME, EditorNameSexActivity.editorName);
                break;
            case R.id.edit_sex:
                Intent intenSEX = new Intent(this, EditorNameSexActivity.class);
                intenSEX.putExtra(EditorNameSexActivity.type, EditorNameSexActivity.editorSex);
                intenSEX.putExtra("sex", sex.getText().toString());
                startActivityForResult(intenSEX, EditorNameSexActivity.editorSex);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case REQUEST_CODE_CHOOSE_PHOTO:
                    ArrayList<String> tt = PhotoPickerActivity.getSelectedImages(data);
                    uploadHead(tt.get(0));
                    break;
                case EditorNameSexActivity.editorName:
                    final String nameText = data.getStringExtra("name");
                    HashMap<String, Object> map = Api.getCommonData();
                    map.put("realName", nameText);

                    MineServer.updatePersonInfo2(this, CommonUtils.encryptDES(map)).
                            subscribe(new DefaultSubscriber<BaseJson>(this) {
                                @Override
                                public void onNext(BaseJson baseJson) {
                                    ToastUtils.showToast(PersonInfoActivity.this, "修改成功");
                                    name.setText(nameText);
                                    User user = DBManager.getInstance(PersonInfoActivity.this).getCrrentUser();
                                    user.setName(nameText);
                                    ud.refreshUser(user);
                                    EventBus.getDefault().post(nameText, EDIT_NAME);
                                }
                            });
                    break;
                case EditorNameSexActivity.editorSex:
                    final String sexText = data.getStringExtra("sex");
                    HashMap<String, Object> map2 = Api.getCommonData();
                    map2.put("sex", sexText);
                    MineServer.updatePersonInfo2(this, CommonUtils.encryptDES(map2)).
                            subscribe(new DefaultSubscriber<BaseJson>(this) {
                                @Override
                                public void onNext(BaseJson baseJson) {
                                    ToastUtils.showToast(PersonInfoActivity.this, "修改成功");
                                    sex.setText(sexText);
                                    User user = DBManager.getInstance(PersonInfoActivity.this).getCrrentUser();
                                    user.sex = sexText;
                                    ud.refreshUser(user);
                                }
                            });

                    break;
            }
        }
    }

    /*对图片进行压缩处理 并上传*/
    private void uploadHead(final String path) {
        ld = new LoadingDialog(PersonInfoActivity.this);
        ld.setCustomMessage("处理中");
        final Compressor compressedImage = new Compressor.Builder(PersonInfoActivity.this)
                .setMaxWidth(800)
                .setMaxHeight(800)
                .setQuality(50)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .build();


        Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                try {
                    File file = compressedImage.compressToFile(new File(path));
                    subscriber.onNext(file);
                    subscriber.onCompleted();
                } catch (OutOfMemoryError error) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(PersonInfoActivity.this, "压缩处理失败请重试");
                            ld.dismiss();
                        }
                    });
                }
            }
        })
                .subscribeOn(Schedulers.from(fixedThreadPool))
//                        .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file) {
                /*网络图片上传文件*/
                        MineServer.updatePersonInfo(PersonInfoActivity.this, CommonUtils.encryptDES(Api.getCommonData()), file)
                                .subscribe(new DefaultSubscriber<BaseJson<String>>(PersonInfoActivity.this) {
                                    @Override
                                    public void onNext(BaseJson<String> result) {
                                        User user = DBManager.getInstance(PersonInfoActivity.this).getCrrentUser();
                                        user.head = result.data;
                                        ud.refreshUser(user);
                                        initHead(path);
                                        ToastUtils.showToast(PersonInfoActivity.this, "头像上传成功");
                                        EventBus.getDefault().post(path, EDIT_HEAD);
                                        ld.dismiss();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        super.onError(e);
                                        ld.dismiss();
                                    }
                                });

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(final Throwable e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showToast(PersonInfoActivity.this, "处理上传失败，请重试");
                                ld.dismiss();
                            }
                        });
                    }
                });
    }

    @Override
    public void reLoadingData() {

    }
}

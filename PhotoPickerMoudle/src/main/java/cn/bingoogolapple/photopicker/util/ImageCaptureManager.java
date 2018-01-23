package cn.bingoogolapple.photopicker.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.BuildConfig;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kelvin on 2016/11/16.
 */
public class ImageCaptureManager {
    private final static String CAPTURED_PHOTO_PATH_KEY = "CAPTURED_PHOTO_PATH_KEY";
    private String mCurrentPhotoPath;
    private Context mContext;
    private File mImageDir;

    /**
     * @param context
     * @param imageDir 拍照后图片保存的目录
     */
    public ImageCaptureManager(Context context, File imageDir) {
        mContext = context;
        mImageDir = imageDir;
        if (!mImageDir.exists()) {
            mImageDir.mkdirs();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName, ".jpg", mImageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * 获取拍照意图
     *
     * @return
     * @throws IOException
     */
    public Intent getTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri uri = null;
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", photoFile);
                } else {
                    uri = Uri.fromFile(photoFile);
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
        }
        return takePictureIntent;
    }

    /**
     * 刷新图库
     */
    public void refreshGallery() {
        if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(new File(mCurrentPhotoPath)));
            mContext.sendBroadcast(mediaScanIntent);
            mCurrentPhotoPath = null;
        }
    }

    /**
     * 删除拍摄的照片
     */
    public void deletePhotoFile() {
        if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
            try {
                File photoFile = new File(mCurrentPhotoPath);
                photoFile.delete();
                mCurrentPhotoPath = null;
            } catch (Exception e) {
            }
        }
    }

    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && mCurrentPhotoPath != null) {
            savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, mCurrentPhotoPath);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
            mCurrentPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
        }
    }
}
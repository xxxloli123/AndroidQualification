package cn.bingoogolapple.photopicker.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;

import cn.bingoogolapple.photopicker.R;

/**
 * Created by kelvin on 2016/11/16.
 */
public class BGASavePhotoTask extends BaseAsyncTask<Void, Void> {
    private Context mContext;
    private SoftReference<Bitmap> mBitmap;
    private File mNewFile;

    public BGASavePhotoTask(Callback<Void> callback, Context context, File newFile) {
        super(callback);
        mContext = context.getApplicationContext();
        mNewFile = newFile;
    }

    public void setBitmapAndPerform(Bitmap bitmap) {
        mBitmap = new SoftReference<>(bitmap);

        if (Build.VERSION.SDK_INT >= 11) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            execute();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mNewFile);
            mBitmap.get().compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();

            // 通知图库更新
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mNewFile)));

            PhotoPickerUtil.showSafe(mContext, mContext.getString(R.string.bga_pp_save_img_success_folder, mNewFile.getParentFile().getAbsolutePath()));
        } catch (Exception e) {
            PhotoPickerUtil.showSafe(mContext, R.string.bga_pp_save_img_failure);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    PhotoPickerUtil.showSafe(mContext, R.string.bga_pp_save_img_failure);
                }
            }
            recycleBitmap();
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        recycleBitmap();
    }

    private void recycleBitmap() {
        if (mBitmap != null && mBitmap.get() != null && !mBitmap.get().isRecycled()) {
            mBitmap.get().recycle();
            mBitmap = null;
        }
    }

}

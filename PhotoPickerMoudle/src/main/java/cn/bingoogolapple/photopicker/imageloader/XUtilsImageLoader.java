package cn.bingoogolapple.photopicker.imageloader;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;


/**
 * Created by kelvin on 2016/11/16.
 */
public class XUtilsImageLoader extends BGAImageLoader {

    @Override
    public void displayImage(Activity activity, final ImageView imageView, String path, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height, final DisplayDelegate delegate) {
//        x.Ext.init(activity.getApplication());
//
//        ImageOptions options = new ImageOptions.Builder()
//                .setLoadingDrawableId(loadingResId)
//                .setFailureDrawableId(failResId)
//                .setSize(width, height)
//                .build();
//
//        final String finalPath = getPath(path);
//        x.image().bind(imageView, finalPath, options, new Callback.CommonCallback<Drawable>() {
//            @Override
//            public void onSuccess(Drawable result) {
//                if (delegate != null) {
//                    delegate.onSuccess(imageView, finalPath);
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//            }
//
//            @Override
//            public void onFinished() {
//            }
//        });
    }

    @Override
    public void downloadImage(Context context, String path, final DownloadDelegate delegate) {
//        x.Ext.init((Application) context.getApplicationContext());
//
//        final String finalPath = getPath(path);
//        x.image().loadDrawable(finalPath, new ImageOptions.Builder().build(), new Callback.CommonCallback<Drawable>() {
//            @Override
//            public void onSuccess(Drawable result) {
//                if (delegate != null) {
//                    delegate.onSuccess(finalPath, ((BitmapDrawable) result).getBitmap());
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                if (delegate != null) {
//                    delegate.onFailed(finalPath);
//                }
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//            }
//
//            @Override
//            public void onFinished() {
//            }
//        });
    }

    @Override
    public void pause(Activity activity) {
    }

    @Override
    public void resume(Activity activity) {

    }
}
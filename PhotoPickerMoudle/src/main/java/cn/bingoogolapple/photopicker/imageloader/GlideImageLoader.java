package cn.bingoogolapple.photopicker.imageloader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

/**
 * Created by kelvin on 2016/11/16.
 */
public class GlideImageLoader extends BGAImageLoader {
    @Override
    public void displayImage(Activity activity, final ImageView imageView, String path, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height, final DisplayDelegate delegate) {
        final String finalPath = getPath(path);
        Glide.with(activity).load(finalPath).asBitmap().placeholder(loadingResId).error(failResId).override(width, height).listener(new RequestListener<String, Bitmap>() {
            @Override
            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (delegate != null) {
                    delegate.onSuccess(imageView, finalPath);
                }
                return false;
            }
        }).into(imageView);
    }

    @Override
    public void downloadImage(Context context, String path, final DownloadDelegate delegate) {
        final String finalPath = getPath(path);
        Glide.with(context.getApplicationContext()).load(finalPath).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (delegate != null) {
                    delegate.onSuccess(finalPath, resource);
                }
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                if (delegate != null) {
                    delegate.onFailed(finalPath);
                }
            }
        });
    }

    @Override
    public void pause(Activity activity) {
        try {
            Glide.with(activity).pauseRequests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resume(Activity activity) {
        try {
            Glide.with(activity).resumeRequestsRecursive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
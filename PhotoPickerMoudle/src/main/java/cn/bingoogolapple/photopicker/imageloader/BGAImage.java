package cn.bingoogolapple.photopicker.imageloader;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

/**
 * Created by kelvin on 2016/11/16.
 */
public class BGAImage {
    private static BGAImageLoader sImageLoader;

    private BGAImage() {
    }

    private static final BGAImageLoader getImageLoader() {
        if (sImageLoader == null) {
            synchronized (BGAImage.class) {
                if (sImageLoader == null) {
                    if (isClassExists("com.bumptech.glide.Glide")) {
                        sImageLoader = new GlideImageLoader();
                    } else if (isClassExists("com.squareup.picasso.Picasso")) {
                        sImageLoader = new PicassoImageLoader();
                    } else if (isClassExists("com.nostra13.universalimageloader.core.ImageLoader")) {
                        sImageLoader = new UILImageLoader();
                    } else if (isClassExists("org.xutils.x")) {
                        sImageLoader = new XUtilsImageLoader();
                    } else {
                        throw new RuntimeException("必须在你的build.gradle文件中配置「Glide、Picasso、universal-image-loader、XUtils3」中的某一个图片加载库的依赖");
                    }
                }
            }
        }
        return sImageLoader;
    }

    private static final boolean isClassExists(String classFullName) {
        try {
            Class.forName(classFullName);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void displayImage(Activity activity, ImageView imageView, String path, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height, final BGAImageLoader.DisplayDelegate delegate) {
        getImageLoader().displayImage(activity, imageView, path, loadingResId, failResId, width, height, delegate);
    }

    public static void downloadImage(Context context, String path, final BGAImageLoader.DownloadDelegate delegate) {
        getImageLoader().downloadImage(context, path, delegate);
    }

    public static void pause(Activity activity) {
        getImageLoader().pause(activity);
    }

    public static void resume(Activity activity) {
        getImageLoader().resume(activity);
    }
}

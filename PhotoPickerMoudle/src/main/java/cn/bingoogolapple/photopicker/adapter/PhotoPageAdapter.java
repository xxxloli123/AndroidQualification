package cn.bingoogolapple.photopicker.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.imageloader.BGAImageLoader;
import cn.bingoogolapple.photopicker.util.BrowserPhotoViewAttacher;
import cn.bingoogolapple.photopicker.util.PhotoPickerUtil;
import cn.bingoogolapple.photopicker.widget.BGAImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by kelvin on 2016/11/16.
 */
public class PhotoPageAdapter extends PagerAdapter {
    private ArrayList<String> mPreviewImages;
    private PhotoViewAttacher.OnViewTapListener mOnViewTapListener;
    private Activity mActivity;
    private LayoutInflater inflater;

    public PhotoPageAdapter(Activity activity, PhotoViewAttacher.OnViewTapListener onViewTapListener, ArrayList<String> previewImages) {
        mOnViewTapListener = onViewTapListener;
        mPreviewImages = previewImages;
        mActivity = activity;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return mPreviewImages == null ? 0 : mPreviewImages.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.item_photo_page, null, false);
        final BGAImageView imageView = (BGAImageView) view.findViewById(R.id.img);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        container.addView(view);
//        final BGAImageView imageView = new BGAImageView(container.getContext());
//        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final BrowserPhotoViewAttacher photoViewAttacher = new BrowserPhotoViewAttacher(imageView);
        photoViewAttacher.setOnViewTapListener(mOnViewTapListener);
        imageView.setDelegate(new BGAImageView.Delegate() {
            @Override
            public void onDrawableChanged(Drawable drawable) {
                if (drawable != null && drawable.getIntrinsicHeight() > drawable.getIntrinsicWidth() && drawable.getIntrinsicHeight() > PhotoPickerUtil.getScreenHeight(imageView.getContext())) {
                    photoViewAttacher.setIsSetTopCrop(true);
                    photoViewAttacher.setUpdateBaseMatrix();
                } else {
                    photoViewAttacher.update();
                }
            }
        });

        BGAImage.displayImage(mActivity, imageView, mPreviewImages.get(position), R.mipmap.bga_pp_ic_holder_dark,
                R.mipmap.bga_pp_ic_holder_dark, PhotoPickerUtil.getScreenWidth(imageView.getContext()), PhotoPickerUtil.getScreenHeight(imageView.getContext()),
                new BGAImageLoader.DisplayDelegate() {
                    @Override
                    public void onSuccess(View view, String path) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public String getItem(int position) {
        return mPreviewImages == null ? "" : mPreviewImages.get(position);
    }
}
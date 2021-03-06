package cn.bingoogolapple.photopicker.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.zhy.autolayout.utils.AutoUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.adapter.BaseAdapter;
import cn.bingoogolapple.photopicker.helper.BaseHelper;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.util.PhotoPickerUtil;

/**
 * Created by kelvin on 2016/11/20.
 */
public class BGANinePhotoLayout extends FrameLayout implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener, View.OnLongClickListener {
    private PhotoAdapter mPhotoAdapter;
    private ImageView mPhotoIv;
    private HeightWrapGridView mPhotoGv;
    private Delegate mDelegate;
    private int mCurrentClickItemPosition;
    private Activity mActivity;
    private int widthParent = AutoUtils.getPercentWidthSize(226);
    private int spacing = AutoUtils.getPercentWidthSize(12);

    public void setBigImgUrl(ArrayList<String> bigImgUrl) {
        this.bigImgUrl = bigImgUrl;
    }

    public ArrayList<String> getBigImgUrl() {
        return bigImgUrl;
    }

    private ArrayList<String> bigImgUrl = new ArrayList<>();





    public BGANinePhotoLayout(Context context) {
        this(context, null);
    }

    public BGANinePhotoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BGANinePhotoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPhotoIv = new ImageView(context);
        mPhotoIv.setClickable(true);
        mPhotoIv.setOnClickListener(this);
        mPhotoIv.setOnLongClickListener(this);

        mPhotoGv = new HeightWrapGridView(context);
//        int spacing = AutoUtils.getPercentWidthSize(12);
        mPhotoGv.setHorizontalSpacing(spacing);
        mPhotoGv.setVerticalSpacing(spacing);
        mPhotoGv.setNumColumns(3);
        mPhotoGv.setOnItemClickListener(this);
        mPhotoGv.setOnItemLongClickListener(this);
        mPhotoAdapter = new PhotoAdapter(context);
        mPhotoGv.setAdapter(mPhotoAdapter);

        addView(mPhotoIv, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mPhotoGv);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mCurrentClickItemPosition = position;
        if (mDelegate != null) {
            mDelegate.onClickNinePhotoItem(this, view, mCurrentClickItemPosition, mPhotoAdapter.getItem(mCurrentClickItemPosition), mPhotoAdapter.getData());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        mCurrentClickItemPosition = position;
        if (mDelegate != null) {
            return mDelegate.onLongClickNinePhotoItem(this, view, position, mPhotoAdapter.getItem(position), mPhotoAdapter.getData());
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        mCurrentClickItemPosition = 0;
        if (mDelegate != null) {
            mDelegate.onClickNinePhotoItem(this, view, mCurrentClickItemPosition, mPhotoAdapter.getItem(mCurrentClickItemPosition), mPhotoAdapter.getData());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        mCurrentClickItemPosition = 0;
        if (mDelegate != null) {
            return mDelegate.onLongClickNinePhotoItem(this, view, mCurrentClickItemPosition, mPhotoAdapter.getItem(mCurrentClickItemPosition), mPhotoAdapter.getData());
        }
        return false;
    }

    public void init(Activity activity) {
        mActivity = activity;
    }

    public void setData(List<String> photos) {
        if (mActivity == null) {
            throw new RuntimeException("请先调用init方法进行初始化");
        }
        Log.d("LOG", "------------>" + photos.get(0));
        int itemWidth = PhotoPickerUtil.getScreenWidth(getContext()) / 4;
        if (photos.size() == 0) {
            setVisibility(GONE);
        } else if (photos.size() == 1) {
            setVisibility(VISIBLE);
            mPhotoGv.setVisibility(GONE);
            mPhotoAdapter.setData(photos);
            mPhotoIv.setVisibility(VISIBLE);

            mPhotoIv.setMaxWidth(itemWidth * 2);
            mPhotoIv.setMaxHeight(itemWidth * 2);

            BGAImage.displayImage(mActivity, mPhotoIv, photos.get(0), R.mipmap.bga_pp_ic_holder_light, R.mipmap.bga_pp_ic_holder_light, itemWidth * 2, itemWidth * 2, null);
        } else {
            setVisibility(VISIBLE);
            mPhotoIv.setVisibility(GONE);
            mPhotoGv.setVisibility(VISIBLE);

            ViewGroup.LayoutParams layoutParams = mPhotoGv.getLayoutParams();
            if (photos.size() == 2) {
                mPhotoGv.setNumColumns(2);
                layoutParams.width = widthParent * 2 + spacing;
                layoutParams.height = widthParent;
            } else if (photos.size() == 4) {
                mPhotoGv.setNumColumns(2);
                layoutParams.width = widthParent * 2 + spacing;
                layoutParams.height = widthParent * 2 + spacing;
            } else {
                mPhotoGv.setNumColumns(3);
                layoutParams.width = widthParent * 3 + spacing * 2;
                layoutParams.height = widthParent * 3 + spacing * 2;
            }
            mPhotoGv.setLayoutParams(layoutParams);
            mPhotoAdapter.setData(photos);
        }
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    public ArrayList<String> getData() {
        return (ArrayList<String>) mPhotoAdapter.getData();
    }

    public int getItemCount() {
        return mPhotoAdapter.getCount();
    }

    public String getCurrentClickItem() {
        return mPhotoAdapter.getItem(mCurrentClickItemPosition);
    }

    public int getCurrentClickItemPosition() {
        return mCurrentClickItemPosition;
    }

    private class PhotoAdapter extends BaseAdapter<String> {
        private int mImageWidth;
        private int mImageHeight;

        public PhotoAdapter(Context context) {
            super(context, R.layout.bga_pp_item_nine_photo);
            mImageWidth = widthParent;
            mImageHeight = mImageWidth;
        }

        @Override
        protected void fillData(BaseHelper helper, int position, String model) {
            BGAImage.displayImage(mActivity, helper.getImageView(R.id.iv_item_nine_photo_photo), model, R.mipmap.bga_pp_ic_holder_light, R.mipmap.bga_pp_ic_holder_light, mImageWidth, mImageHeight, null);
        }
    }

    public interface Delegate {
        void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models);

        boolean onLongClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models);
    }

    public abstract static class SimpleDelegate implements Delegate {
        @Override
        public boolean onLongClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view, int position, String model, List<String> models) {
            return false;
        }
    }
}
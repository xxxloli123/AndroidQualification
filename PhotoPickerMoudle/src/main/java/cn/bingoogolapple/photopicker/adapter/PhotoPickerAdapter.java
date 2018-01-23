package cn.bingoogolapple.photopicker.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.helper.BaseHelper;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.model.ImageFolderModel;
import cn.bingoogolapple.photopicker.util.PhotoPickerUtil;

/**
 * Created by kelvin on 2016/11/16.
 */
public class PhotoPickerAdapter extends BaseRecyclerAdapter<String> {
    private ArrayList<String> mSelectedImages = new ArrayList<>();
    private int mImageWidth;
    private int mImageHeight;
    private boolean mTakePhotoEnabled;
    private Activity mActivity;

    public PhotoPickerAdapter(Activity activity, RecyclerView recyclerView) {
        super(recyclerView, R.layout.bga_pp_item_photo_picker);
        mImageWidth = PhotoPickerUtil.getScreenWidth(recyclerView.getContext()) / 6;
        mImageHeight = mImageWidth;
        mActivity = activity;
    }

    @Override
    public void setItemChildListener(BaseHelper helper) {
        helper.setItemChildClickListener(R.id.iv_item_photo_picker_flag);
        helper.setItemChildClickListener(R.id.iv_item_photo_picker_photo);
    }

    @Override
    protected void fillData(BaseHelper helper, int position, String model) {
        if (mTakePhotoEnabled && position == 0) {
            helper.setVisibility(R.id.tv_item_photo_picker_tip, View.VISIBLE);
            helper.getImageView(R.id.iv_item_photo_picker_photo).setScaleType(ImageView.ScaleType.CENTER);
//            helper.setImageResource(R.id.iv_item_photo_picker_photo, R.mipmap.bga_pp_ic_gallery_camera);
            Glide.with(mActivity).load(R.mipmap.bga_pp_ic_gallery_camera).asBitmap().into(helper.getImageView(R.id.iv_item_photo_picker_photo));
            helper.setVisibility(R.id.iv_item_photo_picker_flag, View.GONE);
        } else {
            helper.setVisibility(R.id.tv_item_photo_picker_tip, View.GONE);
            helper.getImageView(R.id.iv_item_photo_picker_photo).setScaleType(ImageView.ScaleType.CENTER_CROP);
//            BGAImage.displayImage(mActivity, helper.getImageView(R.id.iv_item_photo_picker_photo), model, R.mipmap.bga_pp_ic_holder_dark, R.mipmap.bga_pp_ic_holder_dark, mImageWidth, mImageHeight, null);
            Glide.with(mActivity).load(new File(model)).asBitmap().into(helper.getImageView(R.id.iv_item_photo_picker_photo));
            helper.setVisibility(R.id.iv_item_photo_picker_flag, View.VISIBLE);

            if (mSelectedImages.contains(model)) {
                helper.setImageResource(R.id.iv_item_photo_picker_flag, R.mipmap.btn_selected);
                helper.getImageView(R.id.iv_item_photo_picker_photo).setColorFilter(helper.getConvertView().getResources().getColor(R.color.bga_pp_photo_selected_mask));
            } else {
                helper.setImageResource(R.id.iv_item_photo_picker_flag, R.mipmap.btn_normal);
                helper.getImageView(R.id.iv_item_photo_picker_photo).setColorFilter(null);
            }
        }
    }

    public void setSelectedImages(ArrayList<String> selectedImages) {
        if (selectedImages != null) {
            mSelectedImages = selectedImages;
        }
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedImages() {
        return mSelectedImages;
    }

    public int getSelectedCount() {
        return mSelectedImages.size();
    }

    public void setImageFolderModel(ImageFolderModel imageFolderModel) {
        mTakePhotoEnabled = imageFolderModel.isTakePhotoEnabled();
        setData(imageFolderModel.getImages());
    }
}

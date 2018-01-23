package cn.bingoogolapple.photopicker.model;

import java.util.ArrayList;

/**
 * Created by kelvin on 2016/11/16.
 */
public class ImageFolderModel {
    public String name;
    public String coverPath;
    private ArrayList<String> mImages = new ArrayList<>();
    private boolean mTakePhotoEnabled;

    public ImageFolderModel(boolean takePhotoEnabled) {
        mTakePhotoEnabled = takePhotoEnabled;
        if (takePhotoEnabled) {
            // 拍照
            mImages.add("");
        }
    }

    public ImageFolderModel(String name, String coverPath) {
        this.name = name;
        this.coverPath = coverPath;
    }

    public boolean isTakePhotoEnabled() {
        return mTakePhotoEnabled;
    }

    public void addLastImage(String imagePath) {
        mImages.add(imagePath);
    }

    public ArrayList<String> getImages() {
        return mImages;
    }

    public int getCount() {
        return mTakePhotoEnabled ? mImages.size() - 1 : mImages.size();
    }
}
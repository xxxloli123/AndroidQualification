package cn.bingoogolapple.photopicker.imageloader;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

/**
 * Created by kelvin on 2016/11/16.
 */
public class OnScrollListener extends RecyclerView.OnScrollListener {
    private Activity mActivity;

    public OnScrollListener(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            BGAImage.resume(mActivity);
        } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            BGAImage.pause(mActivity);
        }
    }
}

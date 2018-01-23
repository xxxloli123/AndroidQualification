package cn.bingoogolapple.photopicker.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by kelvin on 2016/11/16.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    public static final int SPAN_COUNT = 3;
    private int mSpace;

    public SpaceItemDecoration(int space) {
        mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.top = mSpace;
        outRect.bottom = mSpace;
    }
}
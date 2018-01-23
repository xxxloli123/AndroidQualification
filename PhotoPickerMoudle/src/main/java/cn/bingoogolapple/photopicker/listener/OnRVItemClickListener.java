package cn.bingoogolapple.photopicker.listener;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kelvin on 2016/11/16.
 * 描述:RecyclerView的item点击事件监听器
 */
public interface OnRVItemClickListener {
    void onRVItemClick(ViewGroup parent, View itemView, int position);
}

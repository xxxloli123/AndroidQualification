package cn.bingoogolapple.photopicker.listener;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kelvin on 2016/11/16.
 * 描述:RecyclerView的item长按事件监听器
 */
public interface OnRVItemLongClickListener {
    boolean onRVItemLongClick(ViewGroup parent, View itemView, int position);
}

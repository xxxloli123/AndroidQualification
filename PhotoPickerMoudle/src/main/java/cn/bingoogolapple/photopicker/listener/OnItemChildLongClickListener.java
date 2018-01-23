package cn.bingoogolapple.photopicker.listener;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kelvin on 2016/11/16.
 * 描述:AdapterView和RecyclerView的item中子控件长按事件监听器
 */
public interface OnItemChildLongClickListener {
    boolean onItemChildLongClick(ViewGroup parent, View childView, int position);
}

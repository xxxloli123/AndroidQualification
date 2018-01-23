package cn.bingoogolapple.photopicker.listener;

import android.view.ViewGroup;
import android.widget.CompoundButton;

/**
 * Created by kelvin on 2016/11/16.
 * 描述:AdapterView和RecyclerView的item中子控件选中状态变化事件监听器
 */
public interface OnItemChildCheckedChangeListener {
    void onItemChildCheckedChanged(ViewGroup parent, CompoundButton childView, int position, boolean isChecked);
}

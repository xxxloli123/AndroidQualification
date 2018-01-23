package cn.bingoogolapple.photopicker.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.bingoogolapple.photopicker.helper.BaseHelper;

/**
 * Created by kelvin on 2016/11/16.
 */
public class BaseHolder {
    protected View mConvertView;
    protected BaseHelper mViewHolderHelper;

    private BaseHolder(ViewGroup parent, int layoutId) {
        mConvertView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
        mViewHolderHelper = new BaseHelper(parent, mConvertView);
    }

    /**
     * 拿到一个可重用的ViewHolder对象
     *
     * @param convertView
     * @param parent
     * @param layoutId
     * @return
     */
    public static BaseHolder dequeueReusableAdapterViewHolder(View convertView, ViewGroup parent, int layoutId) {
        if (convertView == null) {
            return new BaseHolder(parent, layoutId);
        }
        return (BaseHolder) convertView.getTag();
    }

    public BaseHelper getViewHolderHelper() {
        return mViewHolderHelper;
    }

    public View getConvertView() {
        return mConvertView;
    }
}

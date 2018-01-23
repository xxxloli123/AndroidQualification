package cn.bingoogolapple.photopicker.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

//import com.zhy.autolayout.utils.AutoUtils;

import cn.bingoogolapple.photopicker.helper.BaseHelper;
import cn.bingoogolapple.photopicker.listener.OnRVItemClickListener;
import cn.bingoogolapple.photopicker.listener.OnRVItemLongClickListener;

/**
 * Created by kelvin on 2016/11/16.
 */
public class BaseRecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    protected Context mContext;
    protected OnRVItemClickListener mOnRVItemClickListener;
    protected OnRVItemLongClickListener mOnRVItemLongClickListener;
    protected BaseHelper mViewHolderHelper;
    protected RecyclerView mRecyclerView;

    public BaseRecyclerHolder(RecyclerView recyclerView, View itemView, OnRVItemClickListener onRVItemClickListener, OnRVItemLongClickListener onRVItemLongClickListener) {
        super(itemView);
//        AutoUtils.autoSize(itemView);//适配
        mRecyclerView = recyclerView;
        mContext = mRecyclerView.getContext();
        mOnRVItemClickListener = onRVItemClickListener;
        mOnRVItemLongClickListener = onRVItemLongClickListener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        mViewHolderHelper = new BaseHelper(mRecyclerView, this.itemView);
        mViewHolderHelper.setRecyclerViewHolder(this);
    }

    public BaseHelper getViewHolderHelper() {
        return mViewHolderHelper;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == this.itemView.getId() && null != mOnRVItemClickListener) {
            mOnRVItemClickListener.onRVItemClick(mRecyclerView, v, getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == this.itemView.getId() && null != mOnRVItemLongClickListener) {
            return mOnRVItemLongClickListener.onRVItemLongClick(mRecyclerView, v, getAdapterPosition());
        }
        return false;
    }

}

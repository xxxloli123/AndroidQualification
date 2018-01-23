package com.dgg.qualification.ui.mine.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.widget.refresh.adapter.SWRecyclerAdapter;
import com.dgg.baselibrary.widget.refresh.adapter.SWViewHolder;
import com.dgg.qualification.R;
import com.dgg.qualification.ui.mine.been.Message;

import java.util.ArrayList;

/**
 * Created by Angel on 2017/4/7.
 */
public class MessageAdapter extends SWRecyclerAdapter<Message> {

    private ArrayList<Message> list;
    private Context context;

    public MessageAdapter(Context context, ArrayList<Message> list) {
        super(context, list);
        this.context = context;
        this.list = list;
    }

    public void setData(ArrayList<Message> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item_message;
    }

    @Override
    public void bindData(SWViewHolder holder, int position, final Message item) {
        holder.getTextView(R.id.title).setText(item.title);
        try {
            holder.getTextView(R.id.time).setText(item.createTime.substring(0, item.createTime.length() - 3));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.getTextView(R.id.content).setText(item.press);
        holder.getView(R.id.action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(item.forwardUrl))
                    CommonUtils.previewContent(context, item.forwardUrl);
            }
        });

    }
}

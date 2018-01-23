package com.dgg.qualification.ui.main.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.widget.refresh.adapter.SWRecyclerAdapter;
import com.dgg.baselibrary.widget.refresh.adapter.SWViewHolder;
import com.dgg.qualification.R;
import com.dgg.qualification.ui.main.server.HomePageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Angel on 2017/4/7.
 */
public class NumAdapter extends SWRecyclerAdapter<HomePageItem> {

    private ArrayList<HomePageItem> list;
    private Context context;

    public NumAdapter(Context context, ArrayList<HomePageItem> list) {
        super(context, list);
        this.context = context;
        this.list = list;
    }

    public void setData(ArrayList<HomePageItem> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item;
    }

    @Override
    public void bindData(SWViewHolder holder, int position, final HomePageItem item) {
//        holder.getTextView(R.id.text).setText(list.get(position) + "");.placeholder(R.drawable.loading).error(R.drawable.load_fail)
        Glide.with(context).load(item.imgPath)
                .placeholder(R.drawable.loading)
                .error(R.drawable.load_fail)
                .into((ImageView) holder.getView(R.id.img));
        TextView title = (TextView) holder.getView(R.id.text);
        final TextView content = (TextView) holder.getView(R.id.text2);
        title.setText(item.imgName);
        content.setText(item.imgDesc);
        holder.getView(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(item.imgUrl))
                    CommonUtils.previewContent(context, item.imgUrl);
            }
        });
    }
}

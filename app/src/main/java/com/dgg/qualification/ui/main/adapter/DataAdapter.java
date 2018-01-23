package com.dgg.qualification.ui.main.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.LogUtils;
import com.dgg.qualification.R;
import com.dgg.qualification.common.UmengAnalytics;
import com.dgg.qualification.ui.main.adapter.base.ListBaseAdapter;
import com.dgg.qualification.ui.main.adapter.base.SuperViewHolder;
import com.dgg.qualification.ui.main.server.HomePageItem;

/**
 * Created by Lzx on 2016/12/30.
 */

public class DataAdapter extends ListBaseAdapter<HomePageItem> {
    private Context context;

    public DataAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        final HomePageItem item = mDataList.get(position);
        ImageView img = (ImageView) holder.getView(R.id.img);
//        LogUtils.d("宽 " + img.getWidth() + " 高 " + img.getHeight());
        Glide.with(context).load(item.imgPath)
                .placeholder(R.drawable.loading)
                .error(R.drawable.load_fail)
                .into(img);
        TextView title = (TextView) holder.getView(R.id.text);
        final TextView content = (TextView) holder.getView(R.id.text2);
        title.setText(item.imgName);
        content.setText(item.imgDesc);
        holder.getView(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(item.imgUrl))
                    CommonUtils.previewContent(context, item.imgUrl);
                UmengAnalytics.umengSend(UmengAnalytics.home_news);
            }
        });
    }
}

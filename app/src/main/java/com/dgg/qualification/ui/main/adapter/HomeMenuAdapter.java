package com.dgg.qualification.ui.main.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.widget.nestgridview.ViewHolder;
import com.dgg.baselibrary.widget.nestgridview.adapter.SingleAdapter;
import com.dgg.qualification.R;
import com.dgg.qualification.common.UmengAnalytics;
import com.dgg.qualification.ui.main.server.HomePageItem;

import java.util.List;

/**
 * Created by qiqi on 17/7/20.
 */

public class HomeMenuAdapter extends SingleAdapter<HomePageItem> {
    private Context context;
//    String[] DD = {"https://fastdfs.dgg.net/group3/M00/00/16/CgAA8lnctKWAU_dnAAokQZk7PJw106.jpg",
//    "https://fastdfs.dgg.net/group3/M00/00/16/CgAA8lncufuAEvkKAAiE21EX2Xw212.jpg",
//    "https://fastdfs.dgg.net/group3/M00/00/16/CgAA81ncuVaALFojAAUM6_dEvwA015.jpg"};

    public HomeMenuAdapter(Context context, List<HomePageItem> datas, int itemLayoutId) {
        super(context, datas, itemLayoutId);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(ViewGroup parent, ViewHolder holder, final HomePageItem data, int pos) {
        ImageView icon = holder.getView(R.id.icon);
        TextView content = holder.getView(R.id.content);
        Glide.with(context).load(data.imgPath)
//                .placeholder(R.mipmap.homepage_icon_baoming)
//                .error(R.drawable.load_fail)
                .into(icon);
        content.setText(data.imgName);
        holder.getView(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(data.imgUrl))
                    CommonUtils.previewContent(context, data.imgUrl, data.imgName);
                UmengAnalytics.umengSend(UmengAnalytics.home_menu);
            }
        });

    }
}

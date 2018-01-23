package com.dgg.qualification.ui.topic.activity;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dgg.baselibrary.db.been.Options;
import com.dgg.baselibrary.db.been.Topic;
import com.dgg.baselibrary.loading.ILoadingHelper;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.recycle.CommonAdapter;
import com.dgg.baselibrary.recycle.ViewHolder;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.R;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.ui.topic.been.Countdown;
import com.dgg.qualification.ui.topic.server.TopicServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by qiqi on 17/7/13.
 */

public class CountdownActivity extends KtBaseActivity {

    private RecyclerView list;
    public static final String COUNTDOWN_DATA = "countdown_data";
    public static final String COUNTDOWN_ID = "countdown_id";

    @Override
    protected int contentLayoutId() {
        return R.layout.activity_countdown;
    }

    @Override
    protected void initData() {
        initTitle("倒计时");
        list = (RecyclerView) findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        String dd = (String) SharedPreferencesUtils.getParam(this, COUNTDOWN_DATA, "");
        ArrayList<Countdown> arrayList = new Gson().fromJson(dd, new TypeToken<ArrayList<Countdown>>() {
        }.getType());
        ILoadingHelper loading = initLoading(list);
        if (arrayList == null || arrayList.size() <= 0)
            loading.showEmpty();
        list.setAdapter(new CommonAdapter<Countdown>(CountdownActivity.this, R.layout.item_countdown, arrayList) {
            @Override
            public void convert(ViewHolder holder, final Countdown o,int position) {
                TextView area = holder.getView(R.id.area);
                TextView examinationTime = holder.getView(R.id.examinationTime);
                TextView countdownTime = holder.getView(R.id.countdownTime);
                CheckBox explain = holder.getView(R.id.explain);
                area.setText(o.areaName);
                try {
                    examinationTime.setText("考试时间：" + o.testTime.substring(0, o.testTime.length() - 8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                countdownTime.setText("考试倒计时：" + o.examComeTime);
                explain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferencesUtils.setParam(CountdownActivity.this, COUNTDOWN_ID, o.id);
                        EventBus.getDefault().post(o, COUNTDOWN_ID);//通知刷新数据
                        finish();
                    }
                });
                if ((int) SharedPreferencesUtils.getParam(CountdownActivity.this, COUNTDOWN_ID, 0) == o.id) {
                    explain.setChecked(true);
                    explain.setTextColor(Color.parseColor("#3c7ef8"));
                }
            }
        });
    }

    @Override
    public void reLoadingData() {

    }
}

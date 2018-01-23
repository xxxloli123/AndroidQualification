package com.dgg.qualification.ui.topic.activity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dgg.baselibrary.loading.ILoadingHelper;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.recycle.CommonAdapter;
import com.dgg.baselibrary.recycle.ViewHolder;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.R;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.ui.topic.been.Countdown;
import com.dgg.qualification.ui.topic.been.Question;
import com.dgg.qualification.ui.topic.server.TopicServer;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by qiqi on 17/7/13.
 */

public class QuestionBankSelectActivity extends KtBaseActivity {
    public static final String EVENT_SELECT_QUESTION = "event_select_question";
    private RecyclerView list;
    private ILoadingHelper loading;

    @Override
    protected int contentLayoutId() {
        return R.layout.activity_question_bank_select;
    }

    @Override
    protected void initData() {
        initTitle("考试类别");
        list = (RecyclerView) findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setItemAnimator(new DefaultItemAnimator());
        loading = initLoading(list);
        loading.showLoading();
        getData();
    }

    public void getData() {
        TopicServer.getQuestionData(this, CommonUtils.encryptDES(Api.getCommonData()))
                .subscribe(new DefaultSubscriber<BaseJson<ArrayList<Question>>>(loading) {
                    @Override
                    public void onNext(BaseJson<ArrayList<Question>> arrayListBaseJson) {
                        list.setAdapter(new CommonAdapter<Question>(QuestionBankSelectActivity.this,
                                R.layout.item_question_select, arrayListBaseJson.data) {
                            @Override
                            public void convert(ViewHolder holder, final Question o,int position) {
                                TextView nameText = holder.getView(R.id.name);
                                nameText.setText(o.name);
                                nameText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        EventBus.getDefault().post(o, EVENT_SELECT_QUESTION);
                                        onBackPressed();
                                    }
                                });
                            }
                        });
                        try {
                            if (arrayListBaseJson.data.size() <= 0) {
                                loading.showEmpty();
                            } else {
                                loading.restore();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            loading.showError();
                        }

                    }
                });
    }

    @Override
    public void reLoadingData() {
        loading.showLoading();
        getData();
    }
}

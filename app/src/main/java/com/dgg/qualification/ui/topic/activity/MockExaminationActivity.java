package com.dgg.qualification.ui.topic.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgg.baselibrary.db.User;
import com.dgg.baselibrary.loading.ILoadingHelper;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.recycle.CommonAdapter;
import com.dgg.baselibrary.recycle.ViewHolder;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.R;
import com.dgg.qualification.common.Api;

import exam.e8net.com.exam.DBManager;
import exam.e8net.com.exam.QuestionActivity;
import exam.e8net.com.exam.been.Mock;

import com.dgg.qualification.ui.topic.server.TopicServer;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by qiqi on 17/9/8.
 */

public class MockExaminationActivity extends KtBaseActivity {
    private RecyclerView list;
    private TextView action1;
    private ILoadingHelper loading;
    private int[] iconList = {R.mipmap.icon_list1, R.mipmap.icon_list2, R.mipmap.icon_list3, R.mipmap.icon_list4};

    @Override
    protected int contentLayoutId() {
        return R.layout.activity_mock_examniation;
    }

    @Override
    protected void initData() {
        initTitle("模拟试题");
        list = (RecyclerView) findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        action1 = (TextView) findViewById(R.id.action1);
        action1.setText("成绩单");
        CommonUtils.setOnTouch(action1);
        loading = initLoading(list);
        action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MockExaminationActivity.this, MyGradeActivity.class));
            }
        });
        getMock();
    }

    @Override
    public void reLoadingData() {
        getMock();
    }

    private void getMock() {
        User cu = DBManager.getInstance(this).getCrrentUser();
        if (cu == null) {
            return;
        }
        loading.showLoading();
        HashMap<String, Object> map = Api.getCommonData();
        map.put("areaId", cu.areaId);
        map.put("examTypeId", cu.questionId);
        TopicServer.getMock(this, CommonUtils.encryptDES(map))
                .subscribe(new DefaultSubscriber<BaseJson<ArrayList<Mock>>>(loading) {
                    @Override
                    public void onNext(BaseJson<ArrayList<Mock>> data) {
                        if (data != null && data.data != null && data.data.size() > 0) {
                            list.setAdapter(new ItemAdapter(MockExaminationActivity.this, R.layout.item_mock_examination, data.data));
                            loading.restore();
                        } else {
                            loading.showEmpty();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post("1", QuestionActivity.UP_TOPIC_DATA);
    }

    class ItemAdapter extends CommonAdapter<Mock> {

        public ItemAdapter(Context context, int layoutId, ArrayList<Mock> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(ViewHolder holder, final Mock s, int position) {
            TextView index = holder.getView(R.id.index);
            index.setBackgroundResource(iconList[(int) (0 + Math.random() * (3))]);
            TextView name = holder.getView(R.id.name);
            name.setText(s.title);
            TextView type = holder.getView(R.id.type);
            type.setText(s.examTypeName + "-" + s.subjectName);
            index.setText(s.title.substring(0, 1));
            LinearLayout root = holder.getView(R.id.root);
            CommonUtils.setOnTouch(root);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MockExaminationActivity.this, MockDetailActivity.class);
                    intent.putExtra(MockDetailActivity.PAG_DATA, s);
                    startActivity(intent);
                }
            });
        }
    }
}

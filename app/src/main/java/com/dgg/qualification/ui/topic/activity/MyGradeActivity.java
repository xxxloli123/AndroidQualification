package com.dgg.qualification.ui.topic.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgg.baselibrary.db.been.Topic;
import com.dgg.baselibrary.loading.ILoadingHelper;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.recycle.CommonAdapter;
import com.dgg.baselibrary.recycle.ViewHolder;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.R;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.ui.topic.TopicCommon;
import com.dgg.qualification.ui.topic.server.TopicServer;
import com.jingewenku.abrahamcaijin.commonutil.AppDateMgr;

import java.util.ArrayList;
import java.util.HashMap;

import exam.e8net.com.exam.Question;
import exam.e8net.com.exam.QuestionActivity;
import exam.e8net.com.exam.been.MockHistoryGrade;
import exam.e8net.com.exam.been.MockLookErrorTopic;

/**
 * Created by qiqi on 17/9/11.
 */

public class MyGradeActivity extends KtBaseActivity {

    public static final String HISTORY_GRADE_MAX = "History_Grade_max";
    private RecyclerView list;
    private ArrayList<MockHistoryGrade> historyGD;
    private NestedScrollView root;
    private ILoadingHelper loading;
    private TextView recentGradeNumber, correctNumber, errorNumber;

    @Override
    protected int contentLayoutId() {
        return R.layout.activity_my_grade;
    }

    @Override
    protected void initData() {
        initTitle("我的成绩");
        root = (NestedScrollView) findViewById(R.id.root);
        loading = initLoading(root);
        list = (RecyclerView) findViewById(R.id.list);
        recentGradeNumber = (TextView) findViewById(R.id.recentGradeNumber);
        correctNumber = (TextView) findViewById(R.id.correctNumber);
        errorNumber = (TextView) findViewById(R.id.errorNumber);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setSmoothScrollbarEnabled(true);
        llm.setAutoMeasureEnabled(true);
        list.setLayoutManager(llm);
        list.setHasFixedSize(true);
        list.setNestedScrollingEnabled(false);
        findHistoryGrade();
    }

    @Override
    public void reLoadingData() {
        findHistoryGrade();
    }

    private void findHistoryGrade() {
        loading.showLoading();
        HashMap<String, Object> map = Api.getCommonData();
        TopicServer.findHistoryGrade(this, CommonUtils.encryptDES(map))
                .subscribe(new DefaultSubscriber<BaseJson<ArrayList<MockHistoryGrade>>>(loading) {
                    @Override
                    public void onNext(BaseJson<ArrayList<MockHistoryGrade>> result) {
                        if (result != null && result.data != null && result.data.size() > 0) {
                            historyGD = result.data;
                            list.setAdapter(new ItemAdapter(MyGradeActivity.this, R.layout.item_my_grade, historyGD));
                            MockHistoryGrade recentGrade = historyGD.get(0);
                            recentGradeNumber.setText(recentGrade.sumScore + "分");
                            correctNumber.setText(recentGrade.rightNum + "");
                            errorNumber.setText(recentGrade.errorNum + "");
                            loading.restore();
                        } else {
                            loading.showEmpty();
                        }
                    }
                });
    }

    class ItemAdapter extends CommonAdapter<MockHistoryGrade> {

        public ItemAdapter(Context context, int layoutId, ArrayList<MockHistoryGrade> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(ViewHolder holder, final MockHistoryGrade s, int position) {
            TextView index = holder.getView(R.id.index);
            TextView time = holder.getView(R.id.time);
            TextView title = holder.getView(R.id.title);
            index.setText((position + 1) + "");
            time.setText(AppDateMgr.formatDateTime(s.testTime));
            title.setText(s.title);
            TextView gradeName = holder.getView(R.id.gradeName);
            gradeName.setText(s.examTypeName + "-" + s.subjectName);
            TextView totalNumber = holder.getView(R.id.totalNumber);
            totalNumber.setText("总题数:" + s.questionSum);
            TextView rightNumber = holder.getView(R.id.rightNumber);
            rightNumber.setText("做对数:" + s.rightNum);
            TextView errorNumber = holder.getView(R.id.errorNumber);
            errorNumber.setText("做错数:" + s.errorNum);
            TextView totalTime = holder.getView(R.id.totalTime);
            int allSecond = (int) s.time;//秒
            int minute = allSecond / 60;
            int second = allSecond - (minute * 60);
            totalTime.setText("总用时:" + minute + "分" + second + "秒");
            TextView totalGrade = holder.getView(R.id.totalGrade);
            totalGrade.setText("总得分:" + s.sumScore);
            TextView correct = holder.getView(R.id.correct);
            correct.setText("正确率:" + s.pa);
            TextView wrongTopic = holder.getView(R.id.wrongTopic);
            wrongTopic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lookErrorTopic(s.id + "");
                }
            });
        }
    }

    /*回顾 模拟试题的错题*/
    public void lookErrorTopic(String rId) {
        HashMap<String, Object> map = Api.getCommonData();
        map.put("rId", rId);
        TopicServer.lookErrorTopic(this, CommonUtils.encryptDES(map))
                .subscribe(new DefaultSubscriber<BaseJson<ArrayList<Topic>>>(this) {
                    @Override
                    public void onNext(BaseJson<ArrayList<Topic>> result) {
                        Intent intentCollection = new Intent(MyGradeActivity.this, QuestionActivity.class);
                        if (result != null && result.data != null) {
                            Question.setResult(result.data);
                            if (result.data.size() > 0) {
                                intentCollection.putExtra(QuestionActivity.TOPIC_MODLE, QuestionActivity.ERROR_TOPIC_RETROSPECT);
                                startActivity(intentCollection);
                                finish();
                            }
                        }
                    }
                });
    }
}

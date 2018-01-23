package com.dgg.qualification.ui.topic.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgg.baselibrary.db.User;
import com.dgg.baselibrary.db.been.Topic;
import com.dgg.baselibrary.loading.ldialog.LoadingDialog;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.R;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.ui.main.fragment.MineFragment;
import com.dgg.qualification.ui.topic.TopicCommon;
import com.dgg.qualification.ui.topic.server.TopicServer;
import com.jingewenku.abrahamcaijin.commonutil.GlideUtils;

import java.util.ArrayList;
import java.util.HashMap;

import exam.e8net.com.exam.DBManager;
import exam.e8net.com.exam.Question;
import exam.e8net.com.exam.QuestionActivity;
import exam.e8net.com.exam.been.Mock;
import exam.e8net.com.exam.been.MockGrade;
import exam.e8net.com.exam.been.MockHistoryGrade;
import exam.e8net.com.exam.been.MockLookErrorTopic;

/**
 * Created by qiqi on 17/9/11.
 */

public class GradeActivity extends KtBaseActivity implements View.OnClickListener {

    public static final String GRADE_DATA = "Grade_data";/*考试成绩 key  */
    public static final String MOCK_DATA = "mock_data";/*试卷信息 key  */

    private LinearLayout myGrade, ll_errorTopic, afreshMock;
    private TextView name, grade, gradeTime, errorNumber, historyGrade;
    private ImageView headImg, mockImg;
    private MockGrade MG;
    private ArrayList<MockHistoryGrade> historyGradeData;

    @Override
    protected int contentLayoutId() {
        return R.layout.activity_grade;
    }

    @Override
    protected void initData() {
        initTitle("考试成绩");
        getMyIntent();
        myGrade = (LinearLayout) findViewById(R.id.myGrade);
        CommonUtils.setOnTouch(myGrade);
        myGrade.setOnClickListener(this);
        name = (TextView) findViewById(R.id.name);
        headImg = (ImageView) findViewById(R.id.headImg);
        mockImg = (ImageView) findViewById(R.id.mockImg);
        grade = (TextView) findViewById(R.id.grade);
        gradeTime = (TextView) findViewById(R.id.gradeTime);
        errorNumber = (TextView) findViewById(R.id.errorNumber);
        ll_errorTopic = (LinearLayout) findViewById(R.id.ll_errorTopic);
        CommonUtils.setOnTouch(ll_errorTopic);
        ll_errorTopic.setOnClickListener(this);
        afreshMock = (LinearLayout) findViewById(R.id.afreshMock);
        CommonUtils.setOnTouch(afreshMock);
        afreshMock.setOnClickListener(this);
        historyGrade = (TextView) findViewById(R.id.historyGrade);
        User user = DBManager.getInstance(this).getCrrentUser();
        if (user != null) {
            name.setText(TextUtils.isEmpty(user.name) ? "顶呱呱学院学员" : user.name + "学员");
            if (!TextUtils.isEmpty(user.head))
                GlideUtils.getInstance().LoadContextCircleBitmap(this, user.head, headImg);
        }
        grade.setText(MG.score + "分");
        mockImg.setImageResource(MG.score > MG.passScore ? R.mipmap.img_grade_yes : R.mipmap.img_grade_no);
        int allSecond = (int) MG.time;//秒
        int hours = allSecond / 3600;
        int minute = (allSecond - (hours * 3600)) / 60;
        int second = allSecond - (hours * 3600) - (minute * 60);
        gradeTime.setText("用时：" + hours + ":" + minute + ":" + second);
        errorNumber.setText("错题" + MG.errorNumber + "道，快来回顾");
        TopicCommon.getHisteryGradeMax(this, historyGrade, "历史最好成绩：");
    }

    private void getMyIntent() {
        MG = (MockGrade) getIntent().getSerializableExtra(GRADE_DATA);
    }

    @Override
    public void reLoadingData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myGrade:
                Intent intent = new Intent(GradeActivity.this, MyGradeActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_errorTopic:
                lookErrorTopic();
                break;
            case R.id.afreshMock:
                String mockData = (String) SharedPreferencesUtils.getParam(GradeActivity.this, GradeActivity.MOCK_DATA, "ok");
                Mock mock = CommonUtils.getGson().fromJson(mockData, Mock.class);
                if (mock != null)
                    getMockInfo(mock);
                break;
        }
    }

    /*回顾 模拟试题的错题*/
    public void lookErrorTopic() {
        HashMap<String, Object> map = Api.getCommonData();
        map.put("rId", MG.rId);
        TopicServer.lookErrorTopic(this, CommonUtils.encryptDES(map))
                .subscribe(new DefaultSubscriber<BaseJson<ArrayList<Topic>>>(this) {
                    @Override
                    public void onNext(BaseJson<ArrayList<Topic>> result) {
                        Intent intentCollection = new Intent(GradeActivity.this, QuestionActivity.class);
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

    LoadingDialog ld;

    /*重新新再次模拟试题*/
    private void getMockInfo(final Mock data) {
        ld = new LoadingDialog(this);
        ld.setCustomMessage("解压试题");
        HashMap<String, Object> map = Api.getCommonData();
        map.put("id", data.id);
        TopicServer.getMockInfo(this, CommonUtils.encryptDES(map)).subscribe(new DefaultSubscriber<BaseJson<ArrayList<Topic>>>(this) {
            @Override
            public void onNext(BaseJson<ArrayList<Topic>> arrayListBaseJson) {
                Question.setResult(arrayListBaseJson.data);
                Intent intentCollection = new Intent(GradeActivity.this, QuestionActivity.class);
                if (arrayListBaseJson != null && arrayListBaseJson.data != null && arrayListBaseJson.data.size() > 0) {
                    intentCollection.putExtra(QuestionActivity.TOPIC_MODLE, QuestionActivity.SIMULATION_TEST);
                    intentCollection.putExtra(QuestionActivity.MOCK_DATA, data);
                    startActivity(intentCollection);
                    finish();
                } else {
                    showToast("解压试题失败");
                }
                ld.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ld.dismiss();
            }
        });
    }
}

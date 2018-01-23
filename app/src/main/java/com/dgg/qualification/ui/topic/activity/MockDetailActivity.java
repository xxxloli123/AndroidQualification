package com.dgg.qualification.ui.topic.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgg.baselibrary.db.User;
import com.dgg.baselibrary.db.been.Topic;
import com.dgg.baselibrary.loading.ldialog.LoadingDialog;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.R;
import com.dgg.qualification.common.Api;

import exam.e8net.com.exam.been.Mock;

import com.dgg.qualification.ui.topic.server.TopicServer;
import com.jingewenku.abrahamcaijin.commonutil.GlideUtils;

import java.util.ArrayList;
import java.util.HashMap;

import exam.e8net.com.exam.DBManager;
import exam.e8net.com.exam.Question;
import exam.e8net.com.exam.QuestionActivity;

/**
 * Created by qiqi on 17/9/11.
 */

public class MockDetailActivity extends KtBaseActivity {
    public static final String PAG_DATA = "pag_data";

    private TextView action, type, subject, time, standard, passMuster, totalScore, name;
    private ImageView headImg;
    private LoadingDialog ld;

    @Override
    protected int contentLayoutId() {
        return R.layout.activty_mock_detail;
    }

    @Override
    protected void initData() {
        final Mock data = (Mock) getIntent().getSerializableExtra(PAG_DATA);
        action = (TextView) findViewById(R.id.action);
        CommonUtils.setOnTouch(action);
        type = (TextView) findViewById(R.id.type);
        subject = (TextView) findViewById(R.id.subject);
        time = (TextView) findViewById(R.id.time);
        standard = (TextView) findViewById(R.id.standard);
        passMuster = (TextView) findViewById(R.id.passMuster);
        totalScore = (TextView) findViewById(R.id.totalScore);
        name = (TextView) findViewById(R.id.name);
        headImg = (ImageView) findViewById(R.id.headImg);
        if (data != null) {
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getMockInfo(data);
//                    startActivity(new Intent(MockDetailActivity.this, GradeActivity.class));
                }
            });
            initTitle(data.examTypeName);
            type.setText(data.examTypeName);
            subject.setText(data.subjectName);
            totalScore.setText(data.sumScore + "分");
            time.setText(data.duration + "分钟");
            standard.setText(data.scNum + "道单选题，" + data.mcNum + "道多选");
            passMuster.setText(data.passScore + "分及格");
        }
        User user = DBManager.getInstance(this).getCrrentUser();
        if (user != null) {
            name.setText(TextUtils.isEmpty(user.name) ? "顶呱呱学院学员" : user.name + "学员");
            if (!TextUtils.isEmpty(user.head))
                GlideUtils.getInstance().LoadContextCircleBitmap(this, user.head, headImg);
        }
        SharedPreferencesUtils.setParam(this, GradeActivity.MOCK_DATA, CommonUtils.getGson().toJson(data));//储存试卷信息用于再次模拟考试
    }

    private void getMockInfo(final Mock data) {
        ld = new LoadingDialog(this);
        ld.setCustomMessage("解压试题");
        HashMap<String, Object> map = Api.getCommonData();
        map.put("id", data.id);
        TopicServer.getMockInfo(this, CommonUtils.encryptDES(map)).subscribe(new DefaultSubscriber<BaseJson<ArrayList<Topic>>>(this) {
            @Override
            public void onNext(BaseJson<ArrayList<Topic>> arrayListBaseJson) {
                Question.setResult(arrayListBaseJson.data);
                Intent intentCollection = new Intent(MockDetailActivity.this, QuestionActivity.class);
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

    @Override
    public void reLoadingData() {

    }
}

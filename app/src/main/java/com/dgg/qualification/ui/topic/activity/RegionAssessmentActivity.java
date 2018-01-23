package com.dgg.qualification.ui.topic.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgg.baselibrary.db.TopicDao;
import com.dgg.baselibrary.db.User;
import com.dgg.baselibrary.db.UserDao;
import com.dgg.baselibrary.db.been.Topic;
import com.dgg.baselibrary.db.been.TopicAction;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.dgg.baselibrary.widget.progress.CustomProgressView;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;

import exam.e8net.com.exam.DBManager;
import exam.e8net.com.exam.Question;
import exam.e8net.com.exam.QuestionActivity;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by qiqi on 17/7/11.  提示建议
 * 0%~50%；提升建议：想要通过考试，就继续加油哦
 * 51~100%：提升建议：哇，不错哦，成功近在咫尺
 */

public class RegionAssessmentActivity extends KtBaseActivity implements View.OnClickListener {

    public static final String IS_CLEAN = "isClean";
    private boolean isClean = false;
    private TopicDao td;
    private UserDao userDao;
    private ArrayList<Topic> errorData = new ArrayList<>();//错题数据
    //    private ArrayList<Topic> correctData = new ArrayList<>();//答对题数据
//    private ArrayList<Topic> topicData = new ArrayList<>();//已近做题 总共题数据
    private TextView correctNumber, correctPercentage, errorNumber, errorPercentage, completeNumber,
            completePercentage, assess, practice, questionName, advice, isTips;
    private CustomProgressView percentage;
    private long completeErrorNumber, completeIndex, completeCorrectNumber;//完成错误次数\完成次数\完成正确次数


    @Override
    protected int contentLayoutId() {
        return R.layout.activity_region_assessment;
    }

    @Override
    protected void initData() {
        initTitle("练习评估");
        percentage = (CustomProgressView) findViewById(R.id.percentage);
        correctNumber = (TextView) findViewById(R.id.correctNumber);
        correctPercentage = (TextView) findViewById(R.id.correctPercentage);
        errorNumber = (TextView) findViewById(R.id.errorNumber);
        errorPercentage = (TextView) findViewById(R.id.errorPercentage);
        completeNumber = (TextView) findViewById(R.id.completeNumber);
        completePercentage = (TextView) findViewById(R.id.completePercentage);
        questionName = (TextView) findViewById(R.id.questionName);
        advice = (TextView) findViewById(R.id.advice);
        assess = (TextView) findViewById(R.id.assess);
        assess.setOnClickListener(this);
        CommonUtils.setOnTouch(assess);
        practice = (TextView) findViewById(R.id.practice);
        practice.setOnClickListener(this);
        CommonUtils.setOnTouch(practice);
        isTips = (TextView) findViewById(R.id.isTips);
//        percentage.setLayoutParams(new LinearLayout.LayoutParams(850,850));
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                td = new TopicDao(RegionAssessmentActivity.this);
                userDao = new UserDao(RegionAssessmentActivity.this);
                User crentUser = DBManager.getInstance(RegionAssessmentActivity.this).getCrrentUser();
                if (crentUser == null || TextUtils.isEmpty(crentUser.TopicData)) {
                    return;
                }
                TopicDao td = new TopicDao(RegionAssessmentActivity.this);
                ArrayList<TopicAction> newData = new Gson().fromJson(crentUser.TopicData, new TypeToken<ArrayList<TopicAction>>() {
                }.getType());
                completeIndex = 0;//完成次数
//                long completeErrorNumber = 0;//完成错误次数
                completeCorrectNumber = 0;//完成正确次数
                long collectionNumber = 0;//试题是收藏 数

                for (TopicAction ele : newData) {
                    Topic topic = td.queryById(ele.id);
                    if (topic != null) {
                        if (ele.completeError != 0 && !ele.isCorrect) {
                            completeErrorNumber++;
                            errorData.add(topic);
                        }
                        if (ele.completeCorrect != 0 && ele.isCorrect)
                            completeCorrectNumber++;
                        if (ele.complete != 0)
                            completeIndex++;
                        if (ele.isCollection)
                            collectionNumber++;
                    }
                }
                subscriber.onNext("");
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {

                    @Override
                    public void call(Object o) {
                        errorNumber.setText(completeErrorNumber + "");
                        correctNumber.setText(completeCorrectNumber + "");
                        completeNumber.setText(completeIndex + "");
                        correctPercentage.setText("正确率" + CommonUtils.getPercentage(completeCorrectNumber, completeIndex) + "%");
                        errorPercentage.setText("错误率" + CommonUtils.getPercentage(completeErrorNumber, completeIndex) + "%");
                        completePercentage.setText("完成率" + CommonUtils.getPercentage(completeIndex, td.getTopicCount()) + "%");
                        percentage.setProgress(Integer.parseInt(CommonUtils.getPercentage(completeIndex, td.getTopicCount())));
                        questionName.setText("题库：" + userDao.getUser().questionName);
                        isClean = getIntent().getBooleanExtra(IS_CLEAN, false);
                        if (td.getTopicCount() != 0) {
                            long adviceNumber = completeIndex / td.getTopicCount();
                            if (adviceNumber < 0.5) {
                                advice.setText("提升建议：想要通过考试，就继续加油哦");
                            } else {
                                advice.setText("提升建议：哇，不错哦，成功近在咫尺");
                            }
                        }
                        if (isClean)
                            isTips.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.assess:
                Intent intentTopic = new Intent(this, QuestionActivity.class);
                cleanErrorData();
                Question.setResult(td.queryAllData());
                intentTopic.putExtra(QuestionActivity.TOPIC_MODLE, QuestionActivity.PRACTICE);
                startActivity(intentTopic);
                onBackPressed();
                break;
            case R.id.practice:
                Intent intentTopic2 = new Intent(this, QuestionActivity.class);
                Question.setResult(errorData);
                if (Question.getResult().size() > 0) {
                    intentTopic2.putExtra(QuestionActivity.TOPIC_MODLE, QuestionActivity.PRACTICE_ERROR);
                    startActivity(intentTopic2);
                    onBackPressed();
                } else {
                    ToastUtils.showToast(RegionAssessmentActivity.this, "暂无错题");
                }
                break;
        }
    }

    private void cleanErrorData() {
        User crentUser = DBManager.getInstance(this).getCrrentUser();
        ArrayList<TopicAction> topicData = CommonUtils.getGson().fromJson(crentUser.TopicData, new TypeToken<ArrayList<TopicAction>>() {
        }.getType());
        if (crentUser == null || topicData == null) {
            return;
        }
        for (TopicAction ele : topicData) {
            ele.completeError = 0;
            ele.completeCorrect = 0;
            ele.complete = 0;
        }
        crentUser.TopicData = CommonUtils.getGson().toJson(topicData);
        userDao.refreshUser(crentUser);
        EventBus.getDefault().post("1", QuestionActivity.UP_TOPIC_DATA);//通知刷新数据
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isClean)
            cleanErrorData();
    }

    @Override
    public void reLoadingData() {

    }
}

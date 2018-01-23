package com.dgg.qualification.ui.main.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgg.baselibrary.KtBaseFragment;
import com.dgg.baselibrary.db.DatabaseHelper;
import com.dgg.baselibrary.db.TopicDao;
import com.dgg.baselibrary.db.User;
import com.dgg.baselibrary.db.UserDao;
import com.dgg.baselibrary.db.been.Topic;
import com.dgg.baselibrary.loading.ldialog.LoadingDialog;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.LogUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.dgg.baselibrary.widget.progress.CustomProgressView;
import com.dgg.qualification.R;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.common.AppCommon;
import com.dgg.qualification.common.UmengAnalytics;
import com.dgg.qualification.ui.main.contract.TopicContract;
import com.dgg.qualification.ui.mine.PersonInfoActivity;
import com.dgg.qualification.ui.topic.TopicCommon;
import com.dgg.qualification.ui.topic.activity.ChapterPracticeActivity;
import com.dgg.qualification.ui.topic.activity.GradeActivity;
import com.dgg.qualification.ui.topic.activity.MockExaminationActivity;
import com.dgg.qualification.ui.topic.activity.ReginChooseActivity;
import com.dgg.qualification.ui.topic.activity.CountdownActivity;
import com.dgg.qualification.ui.topic.activity.ErrorTopicBookActivity;
import com.dgg.qualification.ui.topic.activity.QuestionBankSelectActivity;
import com.dgg.qualification.ui.topic.activity.RegionAssessmentActivity;
import com.dgg.qualification.ui.topic.activity.SurveyActivity;
import com.dgg.qualification.ui.topic.been.Area;
import com.dgg.qualification.ui.topic.been.Countdown;
import com.dgg.qualification.ui.topic.server.TopicServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import exam.e8net.com.exam.DBManager;
import exam.e8net.com.exam.Question;
import exam.e8net.com.exam.QuestionActivity;
import exam.e8net.com.exam.been.MockGrade;
import exam.e8net.com.exam.been.MockResult;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by qiqi on 17/7/4.
 */

public class TopicFragment extends KtBaseFragment implements View.OnClickListener, TopicContract.View {
    private static final String SURVEY_ACTION = "surveyAction";/*问卷调查的 key */
    public static final String TOPIC_UPDATING = "topic_updating";/*题库正在跟新中 key */
    private TextView region, errorTopic, noCompleteTopic, regionAssessment,
            questionSelect, collection, countdownNumber, gradeMax, chapter;//练习\错题本\未做习题、练习评估\题库选择\收藏\倒计时数字
    private LinearLayout practice, countdown, mockExamination, chapterPractice;//练习题\倒计时
    private UserDao ud;
    private User user;
    private CustomProgressView percentage;
    private Handler handler;
    private LoadingDialog ld;

    @NotNull
    @Override
    protected View initView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_topic, null, false);
        region = (TextView) view.findViewById(R.id.region);
        practice = (LinearLayout) view.findViewById(R.id.practice);
        errorTopic = (TextView) view.findViewById(R.id.errorTopic);
        noCompleteTopic = (TextView) view.findViewById(R.id.noCompleteTopic);
        regionAssessment = (TextView) view.findViewById(R.id.regionAssessment);
        countdown = (LinearLayout) view.findViewById(R.id.countdown);
        questionSelect = (TextView) view.findViewById(R.id.questionSelect);
        collection = (TextView) view.findViewById(R.id.collection);
        countdownNumber = (TextView) view.findViewById(R.id.countdownNumber);
        percentage = (CustomProgressView) view.findViewById(R.id.percentage);
        mockExamination = (LinearLayout) view.findViewById(R.id.mockExamination);
        chapterPractice = (LinearLayout) view.findViewById(R.id.chapterPractice);
        gradeMax = (TextView) view.findViewById(R.id.gradeMax);
        chapter = (TextView) view.findViewById(R.id.chapter);
        return view;
    }

    @Override
    protected void initData() {
        practice.setOnClickListener(this);
        CommonUtils.setOnTouch(practice);
        region.setOnClickListener(this);
        CommonUtils.setOnTouch(region);
        errorTopic.setOnClickListener(this);
        CommonUtils.setOnTouch(errorTopic);
        noCompleteTopic.setOnClickListener(this);
        CommonUtils.setOnTouch(noCompleteTopic);
        regionAssessment.setOnClickListener(this);
        CommonUtils.setOnTouch(regionAssessment);
        countdown.setOnClickListener(this);
        CommonUtils.setOnTouch(countdown);
        questionSelect.setOnClickListener(this);
        CommonUtils.setOnTouch(questionSelect);
        collection.setOnClickListener(this);
        CommonUtils.setOnTouch(collection);
        mockExamination.setOnClickListener(this);
        CommonUtils.setOnTouch(mockExamination);
        chapterPractice.setOnClickListener(this);
        CommonUtils.setOnTouch(chapterPractice);
        Observable.create(new Observable.OnSubscribe<Boolean>() {

            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                Question.setResult(new TopicDao(getActivity()).queryAllData());
                subscriber.onNext(Question.getResult().size() > 0);
                subscriber.onCompleted();

            }
        }).subscribe(new Subscriber<Boolean>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean o) {
                ud = new UserDao(getActivity());
                user = DBManager.getInstance(getActivity()).getCrrentUser();
                if (user != null) {
                    if (!TextUtils.isEmpty(user.areaName))
                        region.setText(user.areaName);
                    if (!TextUtils.isEmpty(user.questionName))
                        questionSelect.setText(user.questionName);
                    if (!o)
                        getTopicData(user.areaId, user.questionId);
                }
            }
        });
        getCountdown();
        checkUpdataTopic();
        TopicCommon.getHisteryGradeMax(getContext(), gradeMax, "");
        upChapterNumber(true);
    }

    /*进入专题练习*/
    private void doTopicAction() {
        user = DBManager.getInstance(getActivity()).getCrrentUser();
        Intent intentPractice = new Intent(getActivity(), QuestionActivity.class);
        ArrayList<Topic> data = new TopicDao(getActivity()).queryAllData();
        Question.setResult(data);
        if (data.size() > 0) {
            intentPractice.putExtra("currentIndex", user.currentIndex);
            intentPractice.putExtra(QuestionActivity.TOPIC_MODLE, QuestionActivity.PRACTICE);
            startActivity(intentPractice);
        } else {
            showToast("暂无数据");
            getTopicData(user.areaId, user.questionId);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.practice:
                if (!AppCommon.chechIsLogin(getActivity(), true)) {
                    return;
                }
                if (checkIsLoadingTopic()) {
                    return;
                }
                if ((boolean) SharedPreferencesUtils.getParam(getContext(), SURVEY_ACTION, true)) {
                    SharedPreferencesUtils.setParam(getContext(), SURVEY_ACTION, false);
                    surveyAction();
                    return;
                }
                doTopicAction();
                UmengAnalytics.umengSend(UmengAnalytics.topic_practise);
                break;
            case R.id.region:/*地区选择*/
                getActivity().startActivity(new Intent(getActivity(), ReginChooseActivity.class));
                UmengAnalytics.umengSend(UmengAnalytics.topic_area);
                break;
            case R.id.errorTopic:
                if (!AppCommon.chechIsLogin(getActivity(), true)) {
                    return;
                }
                if (checkIsLoadingTopic()) {
                    return;
                }
                startActivity(new Intent(getActivity(), ErrorTopicBookActivity.class));
                UmengAnalytics.umengSend(UmengAnalytics.topic_error_book);
                break;
            case R.id.noCompleteTopic:
                if (!AppCommon.chechIsLogin(getActivity(), true)) {
                    return;
                }
                if (checkIsLoadingTopic()) {
                    return;
                }
                Question.setResult(MineFragment.completeData);
                Intent intentNoTopic = new Intent(getActivity(), QuestionActivity.class);
                if (MineFragment.completeData.size() > 0) {
                    intentNoTopic.putExtra(QuestionActivity.TOPIC_MODLE, QuestionActivity.PRACTICE_CORRECT);
                    startActivity(intentNoTopic);
                } else {
                    showToast("暂无未做习题");
                }
                UmengAnalytics.umengSend(UmengAnalytics.topic_no_practise);
                break;
            case R.id.regionAssessment:/*练习评估*/
                if (!AppCommon.chechIsLogin(getActivity(), true)) {
                    return;
                }
                if (checkIsLoadingTopic()) {
                    return;
                }
                startActivity(new Intent(getActivity(), RegionAssessmentActivity.class));
                UmengAnalytics.umengSend(UmengAnalytics.topic_assess);
                break;
            case R.id.countdown:/*倒计时*/
                if (!AppCommon.chechIsLogin(getActivity(), true)) {
                    return;
                }
                startActivity(new Intent(getActivity(), CountdownActivity.class));
                UmengAnalytics.umengSend(UmengAnalytics.topic_countdown);
                break;
            case R.id.questionSelect:/*题库选择*/
                if (!AppCommon.chechIsLogin(getActivity(), true)) {
                    return;
                }
                startActivity(new Intent(getActivity(), QuestionBankSelectActivity.class));
                UmengAnalytics.umengSend(UmengAnalytics.topic_exam);
                break;
            case R.id.collection://收藏
                if (!AppCommon.chechIsLogin(getActivity(), true)) {
                    return;
                }
                Question.setResult(MineFragment.collectionData);
                Intent intentCollection = new Intent(getActivity(), QuestionActivity.class);
                if (MineFragment.collectionData.size() > 0) {
                    intentCollection.putExtra(QuestionActivity.TOPIC_MODLE, QuestionActivity.COLLECT);
                    startActivity(intentCollection);
                } else {
                    showToast("暂无收藏");
                }
                UmengAnalytics.umengSend(UmengAnalytics.topic_collect);
                break;
            case R.id.mockExamination:/*模拟考试*/
                if (!AppCommon.chechIsLogin(getActivity(), true)) {
                    return;
                }
                startActivity(new Intent(getActivity(), MockExaminationActivity.class));
                break;
            case R.id.chapterPractice:/*章节练习*/
                if (!AppCommon.chechIsLogin(getActivity(), true)) {
                    return;
                }
                if (checkIsLoadingTopic()) {
                    return;
                }
                ActivityOptionsCompat compat = ActivityOptionsCompat.
                        makeScaleUpAnimation(v, v.getWidth() / 2, v.getHeight() / 2, 0, 0);
                ActivityCompat.startActivity(getActivity(),new Intent(getActivity(), ChapterPracticeActivity.class),compat.toBundle());
                break;
        }
    }

    /*检查 此时是否在更新题库*/
    private boolean checkIsLoadingTopic() {
        boolean cilt = (boolean) SharedPreferencesUtils.getParam(getContext(), TopicFragment.TOPIC_UPDATING, false);
        if (cilt) {
            ld = new LoadingDialog(getContext());
            ld.setCustomMessage("解压试题中");
            ld.show();
        }
        return cilt;
    }

    private void getTopicData(int areaId, int examTypeId) {
        HashMap<String, Object> map = Api.getCommonData();
        map.put("areaId", areaId);
        map.put("examTypeId", examTypeId);
        SharedPreferencesUtils.setParam(getContext(), TOPIC_UPDATING, true);//题库开始更新标志
        TopicServer.getTopic(getActivity(), CommonUtils.encryptDES(map))
                .subscribe(new DefaultSubscriber<BaseJson<ArrayList<Topic>>>(DefaultSubscriber.SILENCE) {
                    @Override
                    public void onNext(final BaseJson<ArrayList<Topic>> arrayListBaseJson) {
                        Observable.create(new Observable.OnSubscribe<Boolean>() {

                            @Override
                            public void call(Subscriber<? super Boolean> subscriber) {
                                DatabaseHelper.getHelper(getActivity()).uodateTopic();
                                for (Topic ele : arrayListBaseJson.data) {
                                    new TopicDao(getActivity()).add(ele);
                                }
                                LogUtils.d("当前现象 3+==" + Thread.currentThread().getName());
                                subscriber.onNext(true);
                                subscriber.onCompleted();
                            }
                        }).subscribeOn(Schedulers.io())
                                .subscribe(new Subscriber<Boolean>() {

                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        SharedPreferencesUtils.setParam(getContext(), TOPIC_UPDATING, false);//题库更新完成标志
                                        if (ld != null)
                                            ld.dismiss();
                                    }

                                    @Override
                                    public void onNext(Boolean o) {
                                        EventBus.getDefault().post("1", QuestionActivity.UP_TOPIC_DATA);//通知刷新数据
                                        SharedPreferencesUtils.setParam(getContext(), TOPIC_UPDATING, false);//题库更新完成标志
                                        if (ld != null)
                                            ld.dismiss();
                                    }
                                });

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        SharedPreferencesUtils.setParam(getContext(), TOPIC_UPDATING, false);//题库更新完成标志
                        if (ld != null)
                            ld.dismiss();
                    }
                });
    }

    @org.simple.eventbus.Subscriber(tag = ReginChooseActivity.EVENT_SELECT_AREA)
    private void selectArea(Area area) {
        region.setText(area.name);
        UserDao ud = new UserDao(getActivity());
        User user = DBManager.getInstance(getActivity()).getCrrentUser();
        if (user != null) {
            user.areaId = area.id;
            user.areaName = area.name;
            ud.refreshUser(user);
        }
        SharedPreferencesUtils.setParam(getActivity(), ReginChooseActivity.AREA_ID, area.id);
        getTopicData(user.areaId, user.questionId);
        getCountdown();
        //TODO 【地区】选择完了的下部操作
    }

    @org.simple.eventbus.Subscriber(tag = QuestionBankSelectActivity.EVENT_SELECT_QUESTION)
    private void selectQuestion(com.dgg.qualification.ui.topic.been.Question question) {
        questionSelect.setText(question.name);
        UserDao ud = new UserDao(getActivity());
        User user = DBManager.getInstance(getActivity()).getCrrentUser();
        if (user != null) {
            user.questionId = question.id;
            user.questionName = question.name;
            ud.refreshUser(user);
        }
        getTopicData(user.areaId, user.questionId);
        getCountdown();
        //TODO 【题库】选择完了的下部操作
    }

    /*结束作答*/
    @org.simple.eventbus.Subscriber(tag = QuestionActivity.IS_SHOW_DIALOG)
    private void intentRegionAssessment(String ok) {
        Intent intent = new Intent(getActivity(), RegionAssessmentActivity.class);
        intent.putExtra(RegionAssessmentActivity.IS_CLEAN, true);
        startActivity(intent);
    }

    /*退出练习记录当前题号*/
    @org.simple.eventbus.Subscriber(tag = QuestionActivity.SET_CURRENT_INDEX)
    private void setCurrentIndex(int currentIndex) {
        user = DBManager.getInstance(getActivity()).getCrrentUser();
        user.currentIndex = currentIndex;
        ud.refreshUser(user);
    }

    /*收藏的题数*/
    @org.simple.eventbus.Subscriber(tag = QuestionActivity.UP_COLLECTIONNUMBER)
    public void upCollectionNumber(String number) {
        collection.setText(number);
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void hitLoading() {

    }

    @Override
    public void showToast(String MSG) {
        ToastUtils.showToast(getActivity(), MSG);
    }

    /*获取倒计时数据*/
    private void getCountdown() {
        user = DBManager.getInstance(getActivity()).getCrrentUser();
        if (user == null) {
            return;
        }
        HashMap<String, Object> map = Api.getCommonData();
        map.put("areaId", user.areaId);
        map.put("examTypeId", user.questionId);

        TopicServer.getCountDownData(getActivity(), CommonUtils.encryptDES(map))
                .subscribe(new DefaultSubscriber<BaseJson<ArrayList<Countdown>>>(DefaultSubscriber.SILENCE) {
                    @Override
                    public void onNext(BaseJson<ArrayList<Countdown>> arrayListBaseJson) {
                        boolean isSet = false;
                        SharedPreferencesUtils.setParam(getActivity(), CountdownActivity.COUNTDOWN_DATA, new Gson().toJson(arrayListBaseJson.data));
                        for (Countdown ele : arrayListBaseJson.data) {
                            if ((int) SharedPreferencesUtils.getParam(getActivity(), CountdownActivity.COUNTDOWN_ID, 0) == ele.id) {
                                countdownNumber.setText(ele.examComeTime);
//                                countdownNumber.setText(AppBigDecimal.adjustDouble(ele.examComeTime, 2, 0));
                                isSet = true;
                            }
                        }
                        if (!isSet && arrayListBaseJson.data != null)
                            if (arrayListBaseJson.data.size() > 0) {
                                countdownNumber.setText(arrayListBaseJson.data.get(0).examComeTime);
                            } else {
                                countdownNumber.setText("0");
                            }
//                            countdownNumber.setText(AppBigDecimal.adjustDouble(arrayListBaseJson.data.get(0).examComeTime, 2, 0));
                    }

                });
    }


    @org.simple.eventbus.Subscriber(tag = QuestionActivity.UP_PERCENTAGE)
    private void upPercentage(String number) {
        TopicDao udt = new TopicDao(getActivity());
        percentage.setProgress(Integer.parseInt(CommonUtils.getPercentage(Double.valueOf(number), udt.getTopicCount())));
    }

    /*更新倒计时数据*/
    @org.simple.eventbus.Subscriber(tag = CountdownActivity.COUNTDOWN_ID)
    private void upCountdown(Countdown o) {
        countdownNumber.setText(o.examComeTime);
    }

    @Override
    public void reLoadingData() {

    }

    /*问卷调查*/
    private void surveyAction() {
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle("提示").setMessage("亲，帮忙完善一下问卷呗！")
                .setNegativeButton("直接练习", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doTopicAction();
                    }
                }).setPositiveButton("接受", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getContext(), SurveyActivity.class));
                    }
                }).create();
        dialog.show();
    }

    /*检查题库版本更新*/
    private void checkUpdataTopic() {
        User crentUser = DBManager.getInstance(getContext()).getCrrentUser();
        if (crentUser != null && crentUser.areaId != 0 && crentUser.questionId != 0) {
            TopicServer.checkUpdataTopic(getContext(), CommonUtils.encryptDES(Api.getCommonData()))
                    .subscribe(new DefaultSubscriber<BaseJson<Integer>>(DefaultSubscriber.SILENCE) {
                        @Override
                        public void onNext(BaseJson<Integer> baseJson) {
                            int topicVersion = (int) SharedPreferencesUtils.getParam(getContext(), "topicVersion", 0);
                            LogUtils.d("tk题库版本 " + baseJson.data);
                            if (topicVersion < baseJson.data) {
                                AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle("提示").setMessage("亲，题库数据有更新，你是否更新")
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                            }
                                        }).setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                getTopicData(user.areaId, user.questionId);
                                                showToast("后台更新中");
                                            }
                                        }).create();
                                dialog.show();
                            }
                            SharedPreferencesUtils.setParam(getContext(), "topicVersion", baseJson.data);
                        }
                    });
        }
    }

    /*上传 模拟式成绩*/
    @org.simple.eventbus.Subscriber(tag = QuestionActivity.UP_MOCK_GRADE)
    private void upMockGrade(final MockGrade mg) {
        HashMap<String, Object> map = Api.getCommonData();
        map.put("paperId", mg.paperId);
        map.put("time", mg.time);
        map.put("score", mg.score);
        map.put("detail", mg.detail);
        TopicServer.upMockGrade(getContext(), CommonUtils.encryptDES(map))
                .subscribe(new DefaultSubscriber<BaseJson<MockResult>>(DefaultSubscriber.SILENCE) {
                    @Override
                    public void onNext(BaseJson<MockResult> result) {
                        EventBus.getDefault().post(true, QuestionActivity.UP_MOCK_GRADE_COMPLETE);
                        if (result != null && result.data != null) {
                            mg.rId = result.data.rId;
                            Intent intent = new Intent(getContext(), GradeActivity.class);
                            intent.putExtra(GradeActivity.GRADE_DATA, mg);
                            startActivity(intent);
                            //跟新最高分
                            TopicCommon.getHisteryGradeMax(getContext(), gradeMax, "");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        EventBus.getDefault().post(false, QuestionActivity.UP_MOCK_GRADE_COMPLETE);
                    }
                });
    }

    /*更新章节练习信息*/
    @org.simple.eventbus.Subscriber(tag = QuestionActivity.UP_CHAPTER)
    private void upChapterNumber(boolean chapterBB) {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user != null) {
                    HashSet<String> chapterNumber = CommonUtils.getGson().fromJson(user.chaptersNumber, new TypeToken<HashSet<String>>() {
                    }.getType());
                    chapter.setText((chapterNumber == null ? 0 : chapterNumber.size()) + "章");
                    LogUtils.d("genx 1111111111更新了章节练习----- " + chapter.getText());
                }
            }
        }, 2000);
    }
}

package com.dgg.qualification.ui.topic.server;

import android.content.Context;

import com.dgg.baselibrary.db.been.Topic;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.HttpServer;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.ui.topic.been.Area;
import com.dgg.qualification.ui.topic.been.Countdown;

import exam.e8net.com.exam.been.Mock;

import com.dgg.qualification.ui.topic.been.Question;

import java.util.ArrayList;

import exam.e8net.com.exam.been.MockHistoryGrade;
import exam.e8net.com.exam.been.MockHistoryGradeMax;
import exam.e8net.com.exam.been.MockLookErrorTopic;
import exam.e8net.com.exam.been.MockResult;
import rx.Observable;

/**
 * Created by qiqi on 17/6/22.
 */

public class TopicServer {
    /*获取地区数据*/
    public static Observable<BaseJson<ArrayList<Area>>> getRegin(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(TopicPath.class)
                .getRegin(pams)
                .compose(HttpServer.<BaseJson<ArrayList<Area>>>compatApplySchedulers());
    }

    /*获取 考题 的数据*/
    public static Observable<BaseJson<ArrayList<Topic>>> getTopic(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(TopicPath.class)
                .getTopic(pams)
                .compose(HttpServer.<BaseJson<ArrayList<Topic>>>compatApplySchedulers());
    }

    /*获取倒计时数据*/
    public static Observable<BaseJson<ArrayList<Countdown>>> getCountDownData(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(TopicPath.class)
                .getCountDownData(pams)
                .compose(HttpServer.<BaseJson<ArrayList<Countdown>>>compatApplySchedulers());
    }

    /*获取 题库数据*/
    public static Observable<BaseJson<ArrayList<Question>>> getQuestionData(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(TopicPath.class)
                .getQuestionData(pams)
                .compose(HttpServer.<BaseJson<ArrayList<Question>>>compatApplySchedulers());
    }

    /*提交用户调查数据*/
    public static Observable<BaseJson> sendSurvey(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(TopicPath.class)
                .sendSurveyData(pams)
                .compose(HttpServer.<BaseJson>compatApplySchedulers());
    }

    /*检查 题库版本更新*/
    public static Observable<BaseJson<Integer>> checkUpdataTopic(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(TopicPath.class)
                .checkUpdataTopic(pams)
                .compose(HttpServer.<BaseJson<Integer>>compatApplySchedulers());
    }

    /*获取 模拟试题信息*/
    public static Observable<BaseJson<ArrayList<Mock>>> getMock(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(TopicPath.class)
                .getMock(pams)
                .compose(HttpServer.<BaseJson<ArrayList<Mock>>>compatApplySchedulers());
    }

    /*获取 模拟试题信息(具体的试题)*/
    public static Observable<BaseJson<ArrayList<Topic>>> getMockInfo(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(TopicPath.class)
                .getMockInfo(pams)
                .compose(HttpServer.<BaseJson<ArrayList<Topic>>>compatApplySchedulers());
    }

    /*上传模拟试题成绩*/
    public static Observable<BaseJson<MockResult>> upMockGrade(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(TopicPath.class)
                .upMockGrade(pams)
                .compose(HttpServer.<BaseJson<MockResult>>compatApplySchedulers());
    }

    /*查看模拟试题 的错题*/
    public static Observable<BaseJson<ArrayList<Topic>>> lookErrorTopic(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(TopicPath.class)
                .lookErrorTopic(pams)
                .compose(HttpServer.<BaseJson<ArrayList<Topic>>>compatApplySchedulers());
    }

    /*查看 我的历史成绩*/
    public static Observable<BaseJson<ArrayList<MockHistoryGrade>>> findHistoryGrade(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(TopicPath.class)
                .findHistoryGrade(pams)
                .compose(HttpServer.<BaseJson<ArrayList<MockHistoryGrade>>>compatApplySchedulers());
    }

    /*查看 我的历史成绩 最大值*/
    public static Observable<BaseJson<MockHistoryGradeMax>> getHistoryGradeMax(Context context, String pams) {
        return HttpServer.getInstance()
                .build(context, Api.app_domain)
                .create(TopicPath.class)
                .getHistoryGradeMax(pams)
                .compose(HttpServer.<BaseJson<MockHistoryGradeMax>>compatApplySchedulers());
    }
}

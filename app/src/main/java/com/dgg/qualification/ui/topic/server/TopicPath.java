package com.dgg.qualification.ui.topic.server;


import com.dgg.baselibrary.db.been.Topic;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.qualification.ui.topic.been.Area;
import com.dgg.qualification.ui.topic.been.Countdown;

import exam.e8net.com.exam.been.Mock;

import com.dgg.qualification.ui.topic.been.Question;

import java.util.ArrayList;

import exam.e8net.com.exam.been.MockHistoryGrade;
import exam.e8net.com.exam.been.MockHistoryGradeMax;
import exam.e8net.com.exam.been.MockLookErrorTopic;
import exam.e8net.com.exam.been.MockResult;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by qiqi on 17/6/22.
 */

public interface TopicPath {
    /**
     * 获取地区信息
     *
     * @param params openapi/findAllProvince/v1
     * @return appAuth/findAllProvince/v1
     */
    @FormUrlEncoded
    @POST("openapi/findAllProvince/v1")
    Observable<BaseJson<ArrayList<Area>>> getRegin(@Field("param") String params);

    /**
     * 获取考题数据
     * appAuth/findAllQuestion/v1(获取全部试题)
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("appAuth/findQuestionByAreaIdAndExamTypeId/v1")
    Observable<BaseJson<ArrayList<Topic>>> getTopic(@Field("param") String params);

    /**
     * 获取倒计时数据
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("appAuth/findAllExam/v1")
    Observable<BaseJson<ArrayList<Countdown>>> getCountDownData(@Field("param") String params);

    /**
     * 获取 题库数据*
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("appAuth/findExamTypeList/v1")
    Observable<BaseJson<ArrayList<Question>>> getQuestionData(@Field("param") String params);

    /**
     * 提交 用户调查数据
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("appAuth/insertQuestionSurvey/v1")
    Observable<BaseJson> sendSurveyData(@Field("param") String params);

    /**
     * 检查 题库版本更新
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("openapi/findQuestionNewVersion/v1")
    Observable<BaseJson<Integer>> checkUpdataTopic(@Field("param") String params);

    /**
     * 获取 模拟试题信息
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("/appAuth/findAllPaper/v1")
    Observable<BaseJson<ArrayList<Mock>>> getMock(@Field("param") String params);

    /**
     * 获取 模拟试题信息(具体的试题)
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("/appAuth/findPaperQuestionByPaperId/v1")
    Observable<BaseJson<ArrayList<Topic>>> getMockInfo(@Field("param") String params);

    /**
     * 上传模拟试题成绩
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("/appAuth/insertPaperResult/v1")
    Observable<BaseJson<MockResult>> upMockGrade(@Field("param") String params);

    /**
     * 查看模拟试题 的错题
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("/appAuth/lookErrorQuestion/v1")
    Observable<BaseJson<ArrayList<Topic>>> lookErrorTopic(@Field("param") String params);

    /**
     * 查看我的历史 成绩
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("/appAuth/findHistoryGrade/v1")
    Observable<BaseJson<ArrayList<MockHistoryGrade>>> findHistoryGrade(@Field("param") String params);

    /**
     * 查看我的历史 成绩
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("/appAuth/findBestScore/v1")
    Observable<BaseJson<MockHistoryGradeMax>> getHistoryGradeMax(@Field("param") String params);
}

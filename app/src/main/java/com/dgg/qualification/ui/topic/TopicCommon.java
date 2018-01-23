package com.dgg.qualification.ui.topic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.ui.topic.activity.GradeActivity;
import com.dgg.qualification.ui.topic.server.TopicServer;

import java.util.HashMap;

import exam.e8net.com.exam.Question;
import exam.e8net.com.exam.QuestionActivity;
import exam.e8net.com.exam.been.MockHistoryGradeMax;
import exam.e8net.com.exam.been.MockLookErrorTopic;

/**
 * Created by qiqi on 17/9/15.
 */

public class TopicCommon {

    /*获取模拟试题最高的成绩*/
    public static void getHisteryGradeMax(Context context, final TextView view, final String tt) {
        TopicServer.getHistoryGradeMax(context, CommonUtils.encryptDES(Api.getCommonData()))
                .subscribe(new DefaultSubscriber<BaseJson<MockHistoryGradeMax>>(DefaultSubscriber.SILENCE) {
                    @Override
                    public void onNext(BaseJson<MockHistoryGradeMax> result) {
                        if (result != null && result.data != null)
                            view.setText(tt + result.data.score + "分");
                    }
                });
    }


}

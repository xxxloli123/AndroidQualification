package com.dgg.qualification.common;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.dgg.baselibrary.db.User;
import com.dgg.baselibrary.db.UserDao;
import com.dgg.baselibrary.db.been.TopicAction;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.dgg.qualification.ui.login.LoginActivity;
import com.dgg.qualification.ui.main.fragment.TopicFragment;
import com.dgg.qualification.ui.topic.activity.QuestionBankSelectActivity;
import com.dgg.qualification.ui.topic.activity.ReginChooseActivity;
import com.dgg.qualification.ui.topic.activity.RegionAssessmentActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import exam.e8net.com.exam.DBManager;

/**
 * Created by qiqi on 17/7/27.
 */

public class AppCommon extends CommonUtils {

    /*检查是否登录*/
    public static boolean chechIsLogin(Context context, boolean isArea) {
        boolean isLogin = true;
        User crentUser = DBManager.getInstance(context).getCrrentUser();

        if (crentUser != null && !crentUser.isLogin) {
            isLogin = false;
            context.startActivity(new Intent(context, LoginActivity.class));
        } else if (crentUser == null) {
            isLogin = false;
            context.startActivity(new Intent(context, LoginActivity.class));
        } else if (crentUser != null && crentUser.areaId == 0 && isArea) {
            isLogin = false;
            ToastUtils.showToast(context, "请选择地区");
            context.startActivity(new Intent(context, ReginChooseActivity.class));
        } else if (crentUser != null && crentUser.questionId == 0 && isArea) {
            isLogin = false;
            ToastUtils.showToast(context, "请选择考试类别");
            context.startActivity(new Intent(context, QuestionBankSelectActivity.class));
        } else if ((boolean) SharedPreferencesUtils.getParam(context, TopicFragment.TOPIC_UPDATING, false)) {
//            isLogin = false;
//            ToastUtils.showToast(context, "亲，题库玩命加载中请稍后！！");
        }

        return isLogin;
    }

    public static boolean cleanTopic(Context context) {
        User crentUser = DBManager.getInstance(context).getCrrentUser();
        UserDao userDao = new UserDao(context);
        boolean isClean = true;

        if (crentUser == null) {
            isClean = false;
            return isClean;
        }
        ArrayList<TopicAction> topicData = CommonUtils.getGson().fromJson(crentUser.TopicData, new TypeToken<ArrayList<TopicAction>>() {
        }.getType());
        for (TopicAction ele : topicData) {
            ele.completeError = 0;
//            ele.completeCorrect = 0;
//            ele.complete = 0;
        }
        crentUser.TopicData = CommonUtils.getGson().toJson(topicData);
        userDao.refreshUser(crentUser);
        return isClean;
    }
}

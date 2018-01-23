package com.dgg.qualification.common;

import com.dgg.qualification.BuildConfig;
import com.dgg.qualification.app.MyApplication;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by qiqi on 17/4/25.
 * 友盟分析 统计点击事件
 */

public class UmengAnalytics {

    public static final String home_message = "home_message";                         //首页快捷消息
    public static final String home_banner = "home_banner";                           //首页banner
    public static final String home_menu = "home_menu";                               //首页菜单
    public static final String home_news = "home_news";                               //首页资讯列表
    public static final String topic_collect = "topic_collect";                       //题库收藏
    public static final String topic_exam = "topic_exam";                             //题库考试类别
    public static final String topic_area = "topic_area";                             //题库地区
    public static final String topic_practise = "topic_practise";                     //题库专题练习
    public static final String topic_error_book = "topic_error_book";                 //题库错题本
    public static final String topic_no_practise = "topic_no_practise";               //题库未做习题
    public static final String topic_assess = "topic_assess";                         //题库练习评估
    public static final String topic_countdown = "topic_countdown";                   //题库考试倒计时
    public static final String mine_sting = "mine_sting";                             //我的设置
    public static final String mine_message = "mine_message";                         //我的消息
    public static final String mine_collect = "mine_collect";                         //我的收藏
    public static final String mine_error_book = "mine_error_book";                   //我的错题本
    public static final String mine_no_practise = "mine_no_practise";                 //我的未做习题
    public static final String mine_grade = "mine_grade";                             //我的成绩查询
    public static final String mine_share = "mine_share";                             //我的分享
    public static final String mine_personal_information = "mine_personal_information";      //我的个人资料
//    public static final String mine_wechat_friend = "mine_wechat_friend";             //我的分享到微信好友
//    public static final String mine_wechat_circle = "mine_wechat_circle";             //我的分享到微信朋友圈
//    public static final String mine_wechat_qq = "mine_wechat_qq";                     //mine_wechat_qq

    public static void umengSend(String eventId) {
        umengSend(eventId, null);
    }

    public static void umengSend(String eventId, HashMap<String, String> mapCustom) {
        MyApplication app = MyApplication.getInstance();
        if (BuildConfig.DEBUG) {
            return;
        }
//        String time = TimeUtils.millis2String(System.currentTimeMillis());
//        String userId = DataHelper.getStringSF(app, CURRENT_USER_ID);
//        HashMap<String, String> map = new HashMap<>();
//        map.put(app.getResources().getString(R.string.actionTime), time);//点击时间
//        map.put(app.getResources().getString(R.string.userId), userId);//用户Id
//        if (mapCustom != null)
//            map.putAll(mapCustom);
        MobclickAgent.onEvent(app, eventId);
    }
}

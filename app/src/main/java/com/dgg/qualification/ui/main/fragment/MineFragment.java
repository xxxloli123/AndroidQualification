package com.dgg.qualification.ui.main.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgg.baselibrary.KtBaseFragment;
import com.dgg.baselibrary.db.TopicDao;
import com.dgg.baselibrary.db.User;
import com.dgg.baselibrary.db.UserDao;
import com.dgg.baselibrary.db.been.Topic;
import com.dgg.baselibrary.db.been.TopicAction;
import com.dgg.baselibrary.html5.Html5Activity;
import com.dgg.baselibrary.numberrun.NumberRunningTextView;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.LogUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.dgg.qualification.R;
import com.dgg.qualification.common.AppCommon;
import com.dgg.qualification.common.UmengAnalytics;
import com.dgg.qualification.ui.login.LoginActivity;
import com.dgg.qualification.ui.mine.MessageActivity;
import com.dgg.qualification.ui.mine.PersonInfoActivity;
import com.dgg.qualification.ui.mine.SetActivity;
import com.dgg.qualification.ui.topic.activity.ErrorTopicBookActivity;
import com.dgg.qualification.ui.topic.activity.SurveyActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jingewenku.abrahamcaijin.commonutil.AppBigDecimal;
import com.jingewenku.abrahamcaijin.commonutil.GlideUtils;

import org.jetbrains.annotations.NotNull;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import exam.e8net.com.exam.DBManager;
import exam.e8net.com.exam.Question;
import exam.e8net.com.exam.QuestionActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by qiqi on 17/7/4.
 */

public class MineFragment extends KtBaseFragment implements View.OnClickListener {

    private LinearLayout set, message, personInfo, results, certificate;
    private ImageView headImg;
    private TextView name, collection, errorTopic, noCompleteTopic, editInfo;
    private NumberRunningTextView completeNumber, insistDayNumber;
    private ArrayList<Topic> errorData = new ArrayList<>();
    public static ArrayList<Topic> collectionData = new ArrayList<>();//收藏的数据
    public static ArrayList<Topic> completeData = new ArrayList<>();//没有做的数据


    @NotNull
    @Override
    protected View initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_mine, null, false);
        set = (LinearLayout) view.findViewById(R.id.set);
        message = (LinearLayout) view.findViewById(R.id.message);
        personInfo = (LinearLayout) view.findViewById(R.id.personInfo);
        headImg = (ImageView) view.findViewById(R.id.headImg);
        name = (TextView) view.findViewById(R.id.name);
        collection = (TextView) view.findViewById(R.id.collection);
        errorTopic = (TextView) view.findViewById(R.id.errorTopic);
        noCompleteTopic = (TextView) view.findViewById(R.id.noCompleteTopic);
        results = (LinearLayout) view.findViewById(R.id.results);
        certificate = (LinearLayout) view.findViewById(R.id.certificate);
        completeNumber = (NumberRunningTextView) view.findViewById(R.id.completeNumber);
        insistDayNumber = (NumberRunningTextView) view.findViewById(R.id.insistDayNumber);
        editInfo = (TextView) view.findViewById(R.id.editInfo);
        return view;
    }

    @Override
    protected void initData() {
        set.setOnClickListener(this);
        CommonUtils.setOnTouch(set);
        message.setOnClickListener(this);
        CommonUtils.setOnTouch(message);
        personInfo.setOnClickListener(this);
        CommonUtils.setOnTouch(personInfo);
        collection.setOnClickListener(this);
        CommonUtils.setOnTouch(collection);
        errorTopic.setOnClickListener(this);
        CommonUtils.setOnTouch(errorTopic);
        noCompleteTopic.setOnClickListener(this);
        CommonUtils.setOnTouch(noCompleteTopic);
        results.setOnClickListener(this);
        CommonUtils.setOnTouch(results);
        certificate.setOnClickListener(this);
        CommonUtils.setOnTouch(certificate);
        updateTopicData();
    }

    @Subscriber(tag = PersonInfoActivity.EDIT_HEAD)
    public void editHead(String path) {
        GlideUtils.getInstance().LoadContextCircleBitmap(getContext(), path, headImg);
    }

    @Subscriber(tag = PersonInfoActivity.EDIT_NAME)
    public void editName(String namett) {
        name.setText(namett);
    }

    /*刷新数据*/
    @org.simple.eventbus.Subscriber(tag = QuestionActivity.UP_TOPIC_DATA)
    public void upNOCompletion(String number) {
        updateTopicData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set:
                getActivity().startActivity(new Intent(getActivity(), SetActivity.class));
                UmengAnalytics.umengSend(UmengAnalytics.mine_sting);
                break;
            case R.id.message:
                getActivity().startActivity(new Intent(getActivity(), MessageActivity.class));
                UmengAnalytics.umengSend(UmengAnalytics.mine_message);
                break;
            case R.id.personInfo:
                if (!AppCommon.chechIsLogin(getActivity(), false)) {
                    return;
                }
                getActivity().startActivity(new Intent(getActivity(), PersonInfoActivity.class));
                UmengAnalytics.umengSend(UmengAnalytics.mine_personal_information);
                break;
            case R.id.collection:
                if (!AppCommon.chechIsLogin(getActivity(), true)) {
                    return;
                }
                Question.setResult(collectionData);
                Intent intentcollection = new Intent(getActivity(), QuestionActivity.class);
                if (collectionData.size() > 0) {
//                    intentcollection.putExtra(QuestionActivity.IS_EXAM, false);
//                    intentcollection.putExtra(QuestionActivity.IS_RECITE, true);
                    intentcollection.putExtra(QuestionActivity.TOPIC_MODLE, QuestionActivity.COLLECT);
                    startActivity(intentcollection);
                } else {
                    ToastUtils.showToast(getActivity(), "暂无收藏");
                }
                UmengAnalytics.umengSend(UmengAnalytics.mine_collect);
                break;
            case R.id.errorTopic:
                if (!AppCommon.chechIsLogin(getActivity(), true)) {
                    return;
                }
                Question.setResult(errorData);
                Intent intentErrorTopic = new Intent(getActivity(), QuestionActivity.class);
                if (errorData.size() > 0) {
//                    intentErrorTopic.putExtra(QuestionActivity.IS_CLEAN_ERROR, (Boolean) SharedPreferencesUtils.getParam(getContext(), ErrorTopicBookActivity.IS_CLEAN_ERROR, false));
                    intentErrorTopic.putExtra(QuestionActivity.TOPIC_MODLE, QuestionActivity.PRACTICE_ERROR);
                    startActivity(intentErrorTopic);
                } else {
                    ToastUtils.showToast(getActivity(), "暂无错题");
                }
                UmengAnalytics.umengSend(UmengAnalytics.mine_error_book);
                break;
            case R.id.noCompleteTopic:
                if (!AppCommon.chechIsLogin(getActivity(), true)) {
                    return;
                }

                Intent intentnoCompleteTopic = new Intent(getActivity(), QuestionActivity.class);
                if (completeData.size() > 0) {
                    Question.setResult(completeData);
                    intentnoCompleteTopic.putExtra(QuestionActivity.TOPIC_MODLE, QuestionActivity.PRACTICE_CORRECT);
                    startActivity(intentnoCompleteTopic);
                } else {
                    ToastUtils.showToast(getActivity(), "暂无未做习题" + completeData.size());
                }
                UmengAnalytics.umengSend(UmengAnalytics.mine_no_practise);
                break;
            case R.id.results:
                Intent intent = new Intent(getContext(), Html5Activity.class);
                intent.putExtra(Html5Activity.WEB_PATH, "http://www.hbsrsksy.cn");
                intent.putExtra(Html5Activity.IS_SUPPORT, true);
                startActivity(intent);
                UmengAnalytics.umengSend(UmengAnalytics.mine_grade);
                break;
            case R.id.certificate:
                showShare();
                UmengAnalytics.umengSend(UmengAnalytics.mine_share);
                break;
        }
    }

    private void showShare() {
        String path = saveLocal();
//        一键分享？
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题
        oks.setTitle("顶呱呱学院APP下载");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://college.dgg.net/fenxiao/shareApp.html");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("顶呱呱学院主要针对于需要考取建筑资质的用户，我们拥有海量题库，提供答题、背题2种模式，多种灵活的学习方式。");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(path);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://college.dgg.net/fenxiao/shareApp.html");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://college.dgg.net/fenxiao/shareApp.html");// https://zzappyfb.dggweb.com/fenxiang/shareApp.html

        // 启动分享GUI
        oks.show(getContext());
//        oks.setCallback(new PlatformActionListener() {
//            @Override
//            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                LogUtils.d("分享完成");
//            }
//
//            @Override
//            public void onError(Platform platform, int i, Throwable throwable) {
//                LogUtils.d("分享错误");
//            }
//
//            @Override
//            public void onCancel(Platform platform, int i) {
//                LogUtils.d("分享取消");
//            }
//        });
    }

    /*保存分享链接的图片*/
    private String saveLocal() {
        Resources res = this.getResources();
        BitmapDrawable d = (BitmapDrawable) res.getDrawable(R.mipmap.logo_icon);
        Bitmap img = d.getBitmap();

        String fn = "logo_icon.png";
        String path = getContext().getFilesDir() + File.separator + fn;
        try {
            if (!(new File(path)).exists()) {
                OutputStream os = new FileOutputStream(path);
                img.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.close();
            }
        } catch (Exception e) {
            Log.e("TAG", "", e);
        }
        return path;
    }

    private void updateTopicData() {
        Observable.create(new Observable.OnSubscribe<MineHolder>() {
            @Override
            public void call(rx.Subscriber<? super MineHolder> subscriber) {
                try {
                    User crentUser = DBManager.getInstance(getContext()).getCrrentUser();
                    errorData.clear();
                    collectionData.clear();
                    completeData.clear();
                    if (crentUser == null) {
                        subscriber.onNext(new MineHolder(0, 0, 0, 0, 0, crentUser));
                        subscriber.onCompleted();
                        return;
                    }
                    ArrayList<TopicAction> newData = CommonUtils.getGson().fromJson(crentUser.TopicData, new TypeToken<ArrayList<TopicAction>>() {
                    }.getType());
                    /*坚持天数*/
                    HashSet<String> insistDay = new Gson().fromJson(crentUser.insistDay, new TypeToken<HashSet<String>>() {
                    }.getType());
                    if (newData == null) {
                        subscriber.onNext(new MineHolder(0, 0, 0, 0, 0, crentUser));
                        subscriber.onCompleted();
                        return;
                    }
                    long completeIndex = 0;//完成次数
                    long completeErrorNumber = 0;//完成错误次数
                    long collectionNumber = 0;//试题是收藏 数
                    TopicDao td = new TopicDao(getContext());
                    ArrayList<Topic> tdAll = td.queryAllData();

                    completeData.addAll(tdAll);
                    for (TopicAction ele : newData) {
                        Topic topic = td.queryById(ele.id);
                        if (topic != null) {
                            if (ele.completeError != 0 && !ele.isCorrect) {
                                completeErrorNumber++;
                                errorData.add(topic);
                            }
                            if (ele.isCollection) {
                                collectionData.add(topic);
                                collectionNumber++;
                            }
                            if (ele.complete != 0)
                                completeIndex++;
//                        completeData.clear();
                            for (Topic tdEle : tdAll) {
                                if (ele.id == tdEle.id) {//&& ele.complete != 0
                                    completeData.remove(tdEle);
                                }
                            }
                        }
                    }
//                    LogUtils.d("客户获得数据----" + CommonUtils.getGson().toJson(td.queryById(104435)));
                    subscriber.onNext(new MineHolder(completeIndex
                            , completeErrorNumber
                            , collectionNumber,
                            (insistDay == null ? 0 : insistDay.size())
                            , newData.size()
                            , crentUser));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MineHolder>() {
                    @Override
                    public void call(MineHolder o) {
                        User user = o.user;
                        if (user != null && user.isLogin) {
                            TopicDao td = new TopicDao(getContext());
                            errorTopic.setText("错题本(" + o.completeErrorNumber + ")");
                            if (o.completeInfo == 0) {
                                noCompleteTopic.setText("未做习题(" + 0 + ")");
                            } else {
                                noCompleteTopic.setText("未做习题(" + ((td.getTopicCount() - o.completeInfo) > 0 ? (td.getTopicCount() - o.completeInfo) : 0) + ")");
                            }
                            collection.setText("收藏夹(" + o.collectionNumber + ")");
                            completeNumber.setContent(o.completeIndex + "");
                            insistDayNumber.setContent(o.insistDay + "");
                            EventBus.getDefault().post(o.completeIndex + "", QuestionActivity.UP_PERCENTAGE);//完成率
                            EventBus.getDefault().post(o.collectionNumber + "", QuestionActivity.UP_COLLECTIONNUMBER);//收藏

                            if (!TextUtils.isEmpty(user.head))
                                GlideUtils.getInstance().LoadContextCircleBitmap(getContext(), user.head, headImg);
                            if (!TextUtils.isEmpty(user.getName())) {
                                name.setText(user.getName());
                            } else {
                                name.setText("还没有设置真实姓名");
                            }
                            editInfo.setVisibility(user.isLogin ? View.VISIBLE : View.GONE);
                        } else {
                            editInfo.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public void reLoadingData() {

    }

    class MineHolder {
        long completeIndex = 0;//完成次数
        long completeErrorNumber = 0;//完成错误次数
        long collectionNumber = 0;//试题是收藏 数
        long insistDay = 0;//坚持天数
        int completeInfo = 0;//完成数据(暂时不用0808)
        User user;

        public MineHolder(long completeIndex, long completeErrorNumber, long collectionNumber,
                          long insistDay, int completeInfo, User user) {
            this.completeIndex = completeIndex;
            this.completeErrorNumber = completeErrorNumber;
            this.collectionNumber = collectionNumber;
            this.insistDay = insistDay;
            this.completeInfo = completeInfo;
            this.user = user;
        }
    }
}

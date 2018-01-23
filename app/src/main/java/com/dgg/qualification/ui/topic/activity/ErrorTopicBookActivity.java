package com.dgg.qualification.ui.topic.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgg.baselibrary.db.TopicDao;
import com.dgg.baselibrary.db.User;
import com.dgg.baselibrary.db.been.Topic;
import com.dgg.baselibrary.db.been.TopicAction;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.LogUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.dgg.baselibrary.tools.UIUtils;
import com.dgg.baselibrary.widget.refresh.adapter.SWRecyclerAdapter;
import com.dgg.baselibrary.widget.refresh.adapter.SWViewHolder;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.R;
import com.dgg.qualification.common.AppCommon;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import exam.e8net.com.exam.DBManager;
import exam.e8net.com.exam.Question;
import exam.e8net.com.exam.QuestionActivity;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by qiqi on 17/7/11.
 */

public class ErrorTopicBookActivity extends KtBaseActivity implements View.OnClickListener {

    public static final String IS_CLEAN_ERROR = "is_clean_error";
    private ArrayList<Topic> errorData = new ArrayList<>();
    private TextView errorTopic1, errorTopic2, errorNumber, cleanError;
    private RecyclerView listView;
    private long completeErrorNumber = 0;//完成错误次数
    private ArrayList<ItemData> listData = new ArrayList<>();

    @Override
    protected int contentLayoutId() {
        return R.layout.activity_error_topic_book;
    }

    @Override
    protected void initData() {
        initTitle("错题本");
        errorTopic1 = (TextView) findViewById(R.id.errorTopic1);
        errorTopic1.setOnClickListener(this);
        CommonUtils.setOnTouch(errorTopic1);
        errorTopic2 = (TextView) findViewById(R.id.errorTopic2);
        errorTopic2.setOnClickListener(this);
        CommonUtils.setOnTouch(errorTopic2);
        errorNumber = (TextView) findViewById(R.id.errorNumber);
        cleanError = (TextView) findViewById(R.id.cleanError);
        cleanError.setOnClickListener(this);
        CommonUtils.setOnTouch(cleanError);
        listView = (RecyclerView) findViewById(R.id.listView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initErrorTopicData();
    }

    private void initErrorTopicData() {
        listData.clear();
        completeErrorNumber = 0;
        errorData.clear();
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                User crentUser = DBManager.getInstance(ErrorTopicBookActivity.this).getCrrentUser();
                if (crentUser == null || TextUtils.isEmpty(crentUser.TopicData)) {
                    return;
                }
                TopicDao td = new TopicDao(ErrorTopicBookActivity.this);
                ArrayList<TopicAction> newData = new Gson().fromJson(crentUser.TopicData, new TypeToken<ArrayList<TopicAction>>() {
                }.getType());
//                long completeIndex = newData.size();//完成次数
////                long completeErrorNumber = 0;//完成错误次数
//                long completeCorrectNumber = 0;//完成正确次数
//                long collectionNumber = 0;//试题是收藏 数

                for (TopicAction ele : newData) {
                    if (ele.completeError != 0 && !ele.isCorrect) {
                        Topic topic = td.queryById(ele.id);
                        if (topic != null) {
                            completeErrorNumber++;
                            errorData.add(topic);
                        }
                    }
                }
                creatListData(errorData, td);
                subscriber.onNext("");
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {

                    @Override
                    public void call(Object o) {
                        errorNumber.setText(completeErrorNumber + "");
                        ItemAdadpter adadpter = new ItemAdadpter(ErrorTopicBookActivity.this, listData);
                        listView.setLayoutManager(new LinearLayoutManager(ErrorTopicBookActivity.this));
                        listView.setAdapter(adadpter);
                        if (listData.size() > 5) {
                            listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.pix2dip(2000)));
                            LogUtils.d("HUANChu" + UIUtils.pix2dip(2000));
                        }
                    }
                });
    }

    private void creatListData(ArrayList<Topic> newData, TopicDao td) {
        Set<Long> set = new HashSet<>();
        for (Topic setEle : newData) {
//            if (setEle.completeError != 0)
            set.add(setEle.typeCode);
        }
        for (Long ele : set) {
            ItemData itemData = new ItemData();
            ArrayList<Topic> data = new ArrayList<>();
            for (Topic dataEle : newData) {
                if (ele == dataEle.typeCode) {
                    Topic topic = td.queryById(dataEle.id);
                    if (topic != null) {
                        itemData.id = topic.id;
                        itemData.title = topic.typeCodeName;
                        data.add(topic);
                        itemData.data = data;
                    }
                }
            }
            if (itemData.data != null)
                listData.add(itemData);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.errorTopic1:
                Intent intentTopic1 = new Intent(this, QuestionActivity.class);
                if (errorData.size() > 0) {
                    Question.setResult(errorData);
                    intentTopic1.putExtra(QuestionActivity.TOPIC_MODLE, QuestionActivity.PRACTICE_ERROR);
                    startActivity(intentTopic1);
                } else {
                    ToastUtils.showToast(ErrorTopicBookActivity.this, R.string.no_error_topic);
                }
                break;
            case R.id.errorTopic2:
                Intent intentTopic2 = new Intent(this, QuestionActivity.class);
                if (errorData.size() > 0) {
                    Question.setResult(errorData);
                    intentTopic2.putExtra(QuestionActivity.TOPIC_MODLE, QuestionActivity.COLLECT);
                    startActivity(intentTopic2);
                } else {
                    ToastUtils.showToast(ErrorTopicBookActivity.this, R.string.no_error_topic);
                }
                break;
            case R.id.cleanError:
                if (AppCommon.cleanTopic(ErrorTopicBookActivity.this)) {
                    errorData.clear();
                    listData.clear();
                    errorNumber.setText(0 + "");
                    ItemAdadpter adadpter = new ItemAdadpter(ErrorTopicBookActivity.this, listData);
                    listView.setLayoutManager(new LinearLayoutManager(ErrorTopicBookActivity.this));
                    listView.setAdapter(adadpter);
                    EventBus.getDefault().post("1", QuestionActivity.UP_TOPIC_DATA);//通知刷新数据
                    ToastUtils.showToast(this, "已清除");
                } else {
                    ToastUtils.showToast(this, "清除失败");
                }

                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void reLoadingData() {

    }

    class ItemData {
        public String title;
        public int id;//题的ID
        public ArrayList<Topic> data;

    }

    class ItemAdadpter extends SWRecyclerAdapter<ItemData> {

        public ItemAdadpter(Context context, ArrayList<ItemData> list) {
            super(context, list);
        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_error_topic;
        }

        @Override
        public void bindData(SWViewHolder holder, int position, final ItemData item) {
            TextView title = holder.getTextView(R.id.title);
            title.setText(item.title);
            holder.getTextView(R.id.number).setText(item.data.size() + "");
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentTopic = new Intent(ErrorTopicBookActivity.this, QuestionActivity.class);
                    if (item.data != null && item.data.size() > 0) {
                        Question.setResult(item.data);
                        intentTopic.putExtra(QuestionActivity.TOPIC_MODLE, QuestionActivity.PRACTICE_ERROR);
                        startActivity(intentTopic);
                    } else {
                        ToastUtils.showToast(ErrorTopicBookActivity.this, "你还没有错题哦");
                    }
                }
            });
        }
    }
}

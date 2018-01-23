package com.dgg.qualification.ui.topic.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgg.baselibrary.db.TopicDao;
import com.dgg.baselibrary.db.been.Topic;
import com.dgg.baselibrary.loading.ILoadingHelper;
import com.dgg.baselibrary.recycle.CommonAdapter;
import com.dgg.baselibrary.recycle.ViewHolder;
import com.dgg.baselibrary.tools.ToastUtils;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.R;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import exam.e8net.com.exam.Question;
import exam.e8net.com.exam.QuestionActivity;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by qiqi on 17/9/8.
 */

public class ChapterPracticeActivity extends KtBaseActivity {
    private RecyclerView list;
    private ILoadingHelper loading;

    @Override
    protected int contentLayoutId() {
        return R.layout.activity_chapter_practice;
    }

    @Override
    protected void initData() {
        initTitle("章节练习");
        list = (RecyclerView) findViewById(R.id.list);
        loading = initLoading(list);
        initListData();
        list.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void reLoadingData() {
        initListData();
    }

    private void initListData() {
        loading.showLoading();
        Observable.create(new Observable.OnSubscribe<ArrayList<ItemData>>() {

            @Override
            public void call(Subscriber<? super ArrayList<ItemData>> subscriber) {
                try {
                    ArrayList<ItemData> listData = new ArrayList<>();
                    TopicDao td = new TopicDao(ChapterPracticeActivity.this);
                    ArrayList<Topic> allTd = td.queryAllData();
                    if (allTd.size() == 0)
                        loading.showEmpty();
                    Set<Long> set = new HashSet<>();
                    for (Topic ele : allTd) {
                        if (set.add(ele.typeCode)) {
                            ItemData ID = new ItemData();
                            ID.typeCode = ele.typeCode;
                            ID.typeCodeName = ele.typeCodeName;
                            ID.itemList.add(ele);
                            listData.add(ID);
                        } else {
                            for (ItemData ele2 : listData) {
                                if (ele2.typeCode == ele.typeCode) {
                                    ele2.itemList.add(ele);
                                }
                            }
                        }
                    }
                    subscriber.onNext(listData);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<ItemData>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        loading.setErrorMessage("数据加载错误");
                        loading.showError();
                    }

                    @Override
                    public void onNext(ArrayList<ItemData> o) {
                        ItemAdapter adapter = new ItemAdapter(ChapterPracticeActivity.this, R.layout.item_chapter_practice, o);
                        list.setAdapter(adapter);
                        loading.restore();
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post("1", QuestionActivity.UP_TOPIC_DATA);
    }

    class ItemAdapter extends CommonAdapter<ItemData> {

        public ItemAdapter(Context context, int layoutId, ArrayList<ItemData> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(ViewHolder holder, final ItemData s, int position) {
            TextView index = holder.getView(R.id.index);
            index.setText("第" + (position + 1) + "章");
            TextView name = holder.getView(R.id.name);
            name.setText(s.typeCodeName);
            TextView number = holder.getView(R.id.number);
            number.setText(s.itemList.size() + "题");
            LinearLayout root = holder.getView(R.id.root);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentTopic1 = new Intent(ChapterPracticeActivity.this, QuestionActivity.class);
                    if (s.itemList.size() > 0) {
                        Question.setResult(s.itemList);
                        intentTopic1.putExtra(QuestionActivity.TOPIC_MODLE, QuestionActivity.PRACTICE_CHAPTERS);
                        startActivity(intentTopic1);
                    } else {
                        ToastUtils.showToast(ChapterPracticeActivity.this, "暂无数据");
                    }
                }
            });
        }
    }

    class ItemData {
        public long typeCode;
        public String typeCodeName;
        public ArrayList<Topic> itemList = new ArrayList<>();

    }

}

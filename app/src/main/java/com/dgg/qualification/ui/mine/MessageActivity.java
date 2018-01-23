package com.dgg.qualification.ui.mine;

import android.support.v7.widget.LinearLayoutManager;

import com.dgg.baselibrary.loading.ILoadingHelper;
import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.ToastUtils;
import com.dgg.baselibrary.widget.refresh.interfaces.OnTouchUpListener;
import com.dgg.baselibrary.widget.refresh.layout.SWPullRecyclerLayout;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.R;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.ui.mine.adapter.MessageAdapter;
import com.dgg.qualification.ui.mine.been.Message;
import com.dgg.qualification.ui.mine.server.MineServer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by qiqi on 17/7/8.
 */

public class MessageActivity extends KtBaseActivity implements OnTouchUpListener {

    private SWPullRecyclerLayout list;
    private ArrayList<Message> listData = new ArrayList<>();
    private MessageAdapter adapter;
    private int page = 1;
    private boolean isRefersh = true;//是否下拉刷新
    private ILoadingHelper loading;

    @Override
    protected int contentLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected void initData() {
        initTitle("消息");
        list = (SWPullRecyclerLayout) findViewById(R.id.list);
        list.addOnTouchUpListener(this);
        adapter = new MessageAdapter(MessageActivity.this, listData);
        list.setMyRecyclerView(new LinearLayoutManager(MessageActivity.this), adapter);
        loading = initLoading(list);
        loading.showLoading();
        getMessage(1);
    }

    private void initAdapter() {
        adapter.setData(listData);
        list.setIsScrollRefresh(false);
        list.setIsScrollLoad(false);
        list.setScrollTo(list.getTotal(), 0);
        if (listData.size() <= 0) {
            loading.showEmpty();
        } else {
            loading.restore();
        }
    }

    private void getMessage(int page) {
        HashMap<String, Object> map = Api.getCommonData();
        map.put("page", page);
        map.put("pageSize", 10);

        MineServer.getMessageList(MessageActivity.this, CommonUtils.encryptDES(map))
                .subscribe(new DefaultSubscriber<BaseJson<ArrayList<Message>>>(loading) {
                    @Override
                    public void onNext(BaseJson<ArrayList<Message>> baseJson) {
                        if (isRefersh) {
                            listData = baseJson.data;
                        } else {
                            listData.addAll(baseJson.data);
                            if (baseJson.data.size() == 0) {
                                ToastUtils.showToast(MessageActivity.this, "没有更多数据");
                            }
                        }
                        initAdapter();
                    }
                });

    }


    @Override
    public void OnRefreshing() {
        page = 1;
        isRefersh = true;
        getMessage(page);
    }

    @Override
    public void OnLoading() {
        page++;
        isRefersh = false;
        getMessage(page);
    }

    @Override
    public void reLoadingData() {
        loading.showLoading();
        OnRefreshing();
    }
}

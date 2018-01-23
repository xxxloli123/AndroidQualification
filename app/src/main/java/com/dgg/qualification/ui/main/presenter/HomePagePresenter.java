package com.dgg.qualification.ui.main.presenter;

import android.content.Context;

import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.ui.main.contract.HomePageContract;
import com.dgg.qualification.ui.main.model.HomePageModel;
import com.dgg.qualification.ui.main.server.HomePage;
import com.dgg.qualification.ui.main.server.HomePageItem;
import com.dgg.qualification.ui.main.server.ManPageServer;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by qiqi on 17/7/4.
 */

public class HomePagePresenter implements HomePageContract.Presenter {
    private Context context;
    private int page = 0;
    private HomePageContract.View mView;
    private HomePageContract.Model model;
    private boolean isRefresh = true;
    private ArrayList<HomePageItem> itemNew = new ArrayList<>();

    public HomePagePresenter(HomePageContract.View mView) {
        this.mView = mView;
        this.context = mView.getMyContext();
        this.model = new HomePageModel(this);
    }

    @Override
    public void getNewList() {
        HashMap<String, Object> map = Api.getCommonData();
        map.put("page", page);
        map.put("pageSize", 10);
        model.getNewList(context, map);

    }

    @Override
    public void refreshingNewList() {
        page = 1;
        isRefresh = true;
        getNewList();
    }

    @Override
    public void loadingMoreNewList() {
        page++;
        isRefresh = false;
        getNewList();
    }

    @Override
    public void getNewLisSuccess(ArrayList<HomePageItem> item) {
        if (isRefresh) {
            itemNew = item;
        } else {
            itemNew.addAll(item);
            if (item.size() == 0)
                mView.loadMoreComplete();
        }
        mView.refreshNewList(itemNew);
    }

    @Override
    public void loadListError() {
        mView.loadListError();
    }

}

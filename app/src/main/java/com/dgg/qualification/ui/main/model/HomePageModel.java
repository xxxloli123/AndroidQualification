package com.dgg.qualification.ui.main.model;

import android.content.Context;

import com.dgg.baselibrary.network.BaseJson;
import com.dgg.baselibrary.network.DefaultSubscriber;
import com.dgg.baselibrary.tools.CommonUtils;
import com.dgg.qualification.common.Api;
import com.dgg.qualification.ui.main.contract.HomePageContract;
import com.dgg.qualification.ui.main.server.HomePage;
import com.dgg.qualification.ui.main.server.HomePageItem;
import com.dgg.qualification.ui.main.server.ManPageServer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by qiqi on 17/7/4.
 */

public class HomePageModel implements HomePageContract.Model {

    private HomePageContract.Presenter mPresenter;

    public HomePageModel(HomePageContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public void getNewList(Context context, HashMap<String, Object> map) {
        ManPageServer.getNewList(context, CommonUtils.encryptDES(map))
                .subscribe(new DefaultSubscriber<BaseJson<ArrayList<HomePageItem>>>(context) {
                    @Override
                    public void onNext(BaseJson<ArrayList<HomePageItem>> baseJson) {
                        mPresenter.getNewLisSuccess(baseJson.data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mPresenter.loadListError();
                    }
                });

    }
}

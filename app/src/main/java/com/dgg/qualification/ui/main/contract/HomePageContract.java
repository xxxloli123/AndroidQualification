package com.dgg.qualification.ui.main.contract;

import android.content.Context;

import com.dgg.baselibrary.mvp.BaseContract;
import com.dgg.qualification.ui.main.server.HomePageItem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by qiqi on 17/7/4.
 */

public interface HomePageContract {
    interface Model {
        void getNewList(Context context, HashMap<String, Object> map);
    }

    interface View extends BaseContract.BaseView {
        void refreshNewList(ArrayList<HomePageItem> items);

        void loadMoreComplete();

        void loadListError();
    }

    interface Presenter {

        void getNewList();

        void refreshingNewList();

        void loadingMoreNewList();

        void getNewLisSuccess(ArrayList<HomePageItem> item);

        void loadListError();
    }
}

package com.dgg.baselibrary.mvp;

import android.content.Context;

import com.dgg.baselibrary.KtBaseView;

/**
 * Created by qiqi on 17/8/1.
 */

public interface BaseContract {
    interface BaseModel {
    }

    interface BaseView {
        Context getMyContext();

        void showToast(String msg);
    }

    interface BasePresenter {
    }
}

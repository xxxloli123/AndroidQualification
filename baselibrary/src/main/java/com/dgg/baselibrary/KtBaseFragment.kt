package com.dgg.baselibrary

import android.app.PendingIntent.getActivity
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dgg.baselibrary.loading.ILoadingHelper
import com.dgg.baselibrary.loading.LoadingHelper
import com.dgg.hdforeman.base.KtBaseActivity
import org.simple.eventbus.EventBus

/**
 * Created by qiqi on 17/6/29.
 */
public abstract class KtBaseFragment : Fragment() {

//    protected var mActivity = getActivity() as KtBaseActivity
    //绑定到butterknife
//    var mUnbinder = ButterKnife.bind(mActivity)


    override fun onCreateView(inflater: LayoutInflater?, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
//    mRootView = initView()
//        ButterKnife.bind(getActivity())
        return initView()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        EventBus.getDefault().register(this)//注册到事件主线
        initData()
    }

    /*此方法 子类去调用传入具体的布局*/
    protected fun initLoading(view: View): ILoadingHelper? {
        var loading: ILoadingHelper? = LoadingHelper.with(view).setRetryListener { loadingHelper ->
            /*此处重新获取数据 刷新数据*/
            reLoadingData()
        }.setLoginListener { loginListener ->
            /*此处跳转到登录页面*/

        }
        return loading
    }

    abstract fun reLoadingData()


    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    protected abstract fun initView(): View

    protected abstract fun initData()
}
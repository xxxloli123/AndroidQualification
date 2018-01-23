package com.dgg.hdforeman.base

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.dgg.baselibrary.BaseApplication
import com.dgg.baselibrary.R
import com.dgg.baselibrary.loading.ILoadingHelper
import com.dgg.baselibrary.loading.LoadingHelper
import com.dgg.baselibrary.tools.ToastUtils
import com.umeng.analytics.MobclickAgent
import org.simple.eventbus.EventBus

/*
* 此基类 的布局必须要有
* <include layout="@layout/include_title" />
* */

public abstract class KtBaseActivity : AppCompatActivity() {
    //internal 声明，在同一模块中的任何地方可见
//    private var loading: ILoadingHelper? = null
    var mUnbinder: Unbinder? = null

//    internal var myAPP = application as BaseApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentLayoutId())
        //绑定到butterknife
        mUnbinder = ButterKnife.bind(this)
        //如果要使用eventbus请将此方法返回 true
        if (useEventBus())
            EventBus.getDefault().register(this)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT// Color.TRANSPARENT = 0 表示#00000000即透明颜色
//        window.navigationBarColor = Color.TRANSPARENT
        findViewById(R.id.back).setOnClickListener { onBackPressed() }
        initData();
        synchronized(this) {
            (application as BaseApplication).activityList.add(this)
        }
    }

    protected fun initTitle(title: String) {
        var tt: TextView = findViewById(R.id.title) as TextView;
        tt.setText(title)
    }

    /*子类复写返回该页面的布局*/
    protected abstract fun contentLayoutId(): Int

    /*初始化页面数据*/
    protected abstract fun initData()

    protected fun showToast(msg: String) {
        ToastUtils.showToast(this, msg)
    }

    protected fun showToast(msg: Int) {
        ToastUtils.showToast(this, getString(msg))
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
    /**
     * 是否使用eventBus,默认为使用(true)，

     * @return
     */
    protected fun useEventBus(): Boolean {
        return true
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this);
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus())
        //如果要使用eventbus请将此方法返回true
            EventBus.getDefault().unregister(this)
//        if (mUnbinder !== Unbinder.EMPTY)
//            mUnbinder!!.unbind()

        synchronized(this) { (application as BaseApplication).getActivityList().remove(this) }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    /*退出所以的页面*/
    fun killAll() {
        for (item in (application as BaseApplication).getActivityList()) {
            item.finish()
        }
    }
}

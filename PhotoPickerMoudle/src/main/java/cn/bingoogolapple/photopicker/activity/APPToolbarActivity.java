package cn.bingoogolapple.photopicker.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ViewStubCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import cn.bingoogolapple.photopicker.R;
import cn.bingoogolapple.photopicker.widget.AutoToolbar;

/**
 * Created by kelvin on 2016/11/16.
 */
public abstract class APPToolbarActivity extends AppCompatActivity implements View.OnClickListener {
    protected String TAG;
    protected AutoToolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();

        initView(savedInstanceState);
        setListener();
        processLogic(savedInstanceState);
    }

    /**
     * 初始化布局以及View控件
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 给View控件添加事件监听器
     */
    protected abstract void setListener();

    /**
     * 处理业务逻辑，状态恢复等操作
     *
     * @param savedInstanceState
     */
    protected abstract void processLogic(Bundle savedInstanceState);

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.bga_pp_toolbar_viewstub);
        mToolbar = getViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewStubCompat viewStub = getViewById(R.id.viewStub);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewStub.getLayoutParams();
        lp.addRule(RelativeLayout.BELOW, R.id.toolbar);

        viewStub.setLayoutResource(layoutResID);
        viewStub.inflate();
    }

    public void setNoLinearContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.bga_pp_toolbar_viewstub);
        mToolbar = getViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewStubCompat viewStub = getViewById(R.id.viewStub);
        viewStub.setLayoutResource(layoutResID);
        viewStub.inflate();
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 需要处理点击事件时，重写该方法
     *
     * @param v
     */
    public void onClick(View v) {
    }

    @Override
    protected void onDestroy() {
        setContentView(new View(this));
        super.onDestroy();
    }

    /**
     * 设置点击事件
     *
     * @param id 控件的id
     */
    protected void setOnClickListener(@IdRes int id) {
        getViewById(id).setOnClickListener(this);
    }

    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) findViewById(id);
    }

}
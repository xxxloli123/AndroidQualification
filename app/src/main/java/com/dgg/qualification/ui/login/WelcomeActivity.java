package com.dgg.qualification.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgg.baselibrary.tools.SharedPreferencesUtils;
import com.dgg.hdforeman.base.KtBaseActivity;
import com.dgg.qualification.MainActivity;
import com.dgg.qualification.R;

//import com.loyo.oa.v2.R;
//import com.loyo.oa.v2.activityui.home.MainHomeActivity;
//import com.loyo.oa.v2.application.MainApp;
//import com.loyo.oa.v2.common.ExtraAndResult;
//import com.loyo.oa.v2.tool.SharedUtil;
//import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 启动
 * Created xnq 16/1/18.
 */
public class WelcomeActivity extends KtBaseActivity {

    public static final String IS_TO_VIEW = "is_to_view";

    @Override
    protected int contentLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initData() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.welcome_pager);
//        SharedUtil.putBoolean(getApplicationContext(), ExtraAndResult.WELCOM_KEY, true);
        viewPager.setAdapter(new PagerAdapter() {
            private final int[] IMAGE_RES = new int[]{
                    R.mipmap.welcome_01,
                    R.mipmap.welcome_02,
                    R.mipmap.welcome_03,
            };
            private final String[] TITLES = new String[]{"行业资讯", "海量题库", "掌上练习"};
            private final String[] CONTENTS = new String[]{"足不出户  快速掌握", "全国考题   一应俱全", "随时随地   考题练习"};

            @Override
            public int getCount() {
                return IMAGE_RES.length;
            }

            @Override
            public boolean isViewFromObject(final View view, final Object o) {
                return view == o;
            }

            @Override
            public void destroyItem(final ViewGroup container, final int position, final Object object) {
                container.removeView((View) object);
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_welcome, container, false);
                container.addView(view);
                ImageView imageView = (ImageView) view.findViewById(R.id.welcomeImage);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView content = (TextView) view.findViewById(R.id.content);
                imageView.setImageResource(IMAGE_RES[position]);
//TODO 引导页面暂时没有压缩处理 后期发版如果有oom则压缩处理
                AppCompatButton buttonOk = (AppCompatButton) view.findViewById(R.id.welcomeOkButton);
                title.setText(TITLES[position]);
                content.setText(CONTENTS[position]);
                if (position == getCount() - 1) {
                    buttonOk.setVisibility(View.VISIBLE);
                    buttonOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            Intent intent = new Intent();
                            intent.setClass(WelcomeActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                            finish();
                            SharedPreferencesUtils.setParam(WelcomeActivity.this, IS_TO_VIEW, true);
                        }
                    });
                }
                return view;
            }
        });
//        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.welcomePagerIndicator);
//        indicator.setViewPager(viewPager);

    }

    @Override
    public void reLoadingData() {

    }
}

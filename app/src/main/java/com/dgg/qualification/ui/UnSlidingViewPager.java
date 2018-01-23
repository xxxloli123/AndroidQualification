package com.dgg.qualification.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Created by Rex on 2016/10/20.
 */

public class UnSlidingViewPager extends ViewPager {
    private boolean mDisableSroll = true;

    public UnSlidingViewPager(Context context) {
        super(context);
    }

    public UnSlidingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDisableScroll(boolean bDisable) {
        mDisableSroll = bDisable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDisableSroll) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mDisableSroll) {
            return false;
        }
        return super.onTouchEvent(ev);
    }
}

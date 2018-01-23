package com.dgg.baselibrary.widget;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by qiqi on 17/7/8.
 */

public class WaveView2 extends View {

    private Paint mWavePaint;

    public WaveView2(Context context) {
        this(context, null);
    }

    public WaveView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        mWavePaint = new Paint();
        mWavePaint.setAntiAlias(true);//抗锯齿
    }

}

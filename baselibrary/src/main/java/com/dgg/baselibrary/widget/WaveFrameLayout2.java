package com.dgg.baselibrary.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;

import com.dgg.baselibrary.R;


/**
 * 功能描述:@TODO 填写功能描述
 * Created by LuoHaifeng on 2017/7/10.
 * Email:496349136@qq.com
 */

public class WaveFrameLayout2 extends FrameLayout {
    float step = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 333, getResources().getDisplayMetrics());
    float amplitude = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 17, getResources().getDisplayMetrics());
    float startX = 0.0f;
    float startMaskX = 0.0f;
    Paint paintMask = null;
    Paint paint = null;
    Path path = new Path();
    Thread thread = null;

    boolean isPause = true;
    private Drawable bgDrawable;

    public WaveFrameLayout2(@NonNull Context context) {
        this(context, null);
    }

    public WaveFrameLayout2(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public WaveFrameLayout2(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paintMask = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintMask.setStyle(Paint.Style.FILL);
        paintMask.setColor(Color.parseColor("#55ffffff"));
        paintMask.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        bgDrawable = getResources().getDrawable(R.mipmap.login_bg);

        startAnimation();
    }

    private void startAnimation() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }

        //初始化线程
        thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (!isPause) {
                        startX -= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());//移动波浪层
                        startMaskX -= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()); //移动mask层 和波浪层速度不一致产生轻微的视差
                        if (Math.abs(startX) >= step * 2) { //如果超出步长 重新开始
                            startX = 0f;
                        }

                        if (Math.abs(startMaskX) >= step * 2) {//如果超出步长 重新开始
                            startMaskX = 0f;
                        }

                        postInvalidate();
                    }
                    try {
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
//        LogUtils.d("www开始绘制--dispatchDraw");
        canvas.drawColor(Color.parseColor("#f5f5fa"));
        canvas.saveLayer(0f, 0f, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        bgDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        bgDrawable.draw(canvas);
        super.dispatchDraw(canvas);
        drawWaveMask(canvas, startMaskX);
        drawWave(canvas, startX);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        LogUtils.d("www开始绘制--onDraw");
    }

    private void drawWaveMask(Canvas canvas, float startX) {
        float yUp = canvas.getHeight() - 2 * amplitude;
        float yDown = canvas.getHeight();
        float startY = canvas.getHeight() - amplitude;

        path.reset();
        path.moveTo(startX, startY);

        float tempX = startX;
        float tempCenterY = yUp;
        while (true) {
            float x0 = tempX + step / 2;
            float y0;
            if (tempCenterY == yDown) {
                tempCenterY = yUp;
                y0 = tempCenterY;
            } else {
                tempCenterY = yDown;
                y0 = tempCenterY;
            }

            float x1 = tempX + step;
            float y1 = startY;

            tempX = x1;

            path.quadTo(x0, y0, x1, y1);

            if (x1 > canvas.getWidth() - startX) {
                path.lineTo(x1, canvas.getHeight());
                path.lineTo(0f, canvas.getHeight());
                path.close();
                break;
            }
        }

        canvas.drawPath(path, paintMask);
    }

    private void drawWave(Canvas canvas, float startX) {
        float yUp = canvas.getHeight() - 2 * amplitude;
        float yDown = canvas.getHeight();
        float startY = canvas.getHeight() - amplitude;

        path.reset();
        path.moveTo(startX, startY);

        float tempX = startX;
        float tempCenterY = yDown;
        while (true) {
            float x0 = tempX + step / 2;
            float y0;
            if (tempCenterY == yDown) {
                tempCenterY = yUp;
                y0 = tempCenterY;
            } else {
                tempCenterY = yDown;
                y0 = tempCenterY;
            }

            float x1 = tempX + step;
            float y1 = startY;

            tempX = x1;

            path.quadTo(x0, y0, x1, y1);

            if (x1 > canvas.getWidth() - startX) {
                path.lineTo(x1, canvas.getHeight());
                path.lineTo(0f, canvas.getHeight());
                path.close();
                break;
            }
        }

        canvas.drawPath(path, paint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isPause = false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isPause = true;
    }
}

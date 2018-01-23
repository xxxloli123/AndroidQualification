package com.dgg.baselibrary.widget.progress;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.dgg.baselibrary.R;
import com.dgg.baselibrary.tools.LogUtils;

/**
 * Created by qiqi on 17/7/5.  半圆统计控件
 * --------自定义控件-------
 */
public class CustomProgressView extends View {


    //画笔
    private Paint paint;
    private RectF oval;


    //圆弧颜色
    private int roundColor;
    //进度颜色
    private int progressColor;
    //文字内容
    private boolean textIsShow;
    //字体大小
    private float textSize = 14;
    //文字颜色
    private int textColor;
    //最大进度
    private int max = 1000;
    //当前进度
    private int progress = 0;
    //圆弧宽度
    private int roundWidth = 30;

    private int viewWidth = 630; //宽度--控件所占区域

    private float nowPro = 1;//用于动画

    private ValueAnimator animator;

    public CustomProgressView(Context context) {
        this(context, null);
    }

    public CustomProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
        setWillNotDraw(false);
        LogUtils.d("第一次");
    }

    public CustomProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LogUtils.d("第二次");
        initAttrs(attrs, context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LogUtils.d("第三次");
        initAttrs(attrs, context);
    }


    private void initAttrs(AttributeSet attr, Context context) {
        this.setBackgroundColor(Color.TRANSPARENT);
        setWillNotDraw(false);
        TypedArray array = context.obtainStyledAttributes(attr, R.styleable.CustomProgressView);
        roundColor = array.getColor(R.styleable.CustomProgressView_roundColor, Color.BLACK);//环形颜色
        progressColor = array.getColor(R.styleable.CustomProgressView_progressColor, Color.RED);//进度颜色
        textIsShow = array.getBoolean(R.styleable.CustomProgressView_textIsShow, false);//文字
        textSize = array.getDimension(R.styleable.CustomProgressView_textSize2, 14);//文字大小
        textColor = array.getColor(R.styleable.CustomProgressView_textColor2, Color.BLACK);//文字颜色
        roundWidth = array.getInt(R.styleable.CustomProgressView_roundWidth, 30);//圆环宽度
        viewWidth = array.getInt(R.styleable.CustomProgressView_viewWidth, 630);//控件宽度
        array.recycle();

        //动画
        animator = ValueAnimator.ofFloat(1, progress);
        animator.setDuration(1800);
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                nowPro = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
//        LogUtils.d("动画开始" + getWidth());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST) {//可获得最大空间
            setMeasuredDimension(widthMeasureSpec, (widthSpecSize / 2) + (int) (Math.cos(20) * (widthSpecSize / 2)));
        } else if (widthMeasureSpec == MeasureSpec.EXACTLY) {//一般指精确值
            setMeasuredDimension(widthMeasureSpec, (widthSpecSize / 2) + (int) (Math.cos(20) * (widthSpecSize / 2)));
        } else {
            setMeasuredDimension(widthMeasureSpec, (viewWidth / 2) + (int) (Math.cos(20) * (viewWidth / 2)));
        }
//        LogUtils.d(widthMeasureSpec + "  布局测量  " + viewWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;//得到宽度以此来计算控件所占实际大小

        //计算画布所占区域
        oval = new RectF();
        oval.left = roundWidth + getPaddingLeft();
        oval.top = roundWidth + getPaddingTop();
        oval.right = viewWidth - roundWidth - getPaddingRight();
        oval.bottom = viewWidth - roundWidth - getPaddingBottom();
//        LogUtils.d("尺寸改变" + viewWidth);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

//        LogUtils.d("开始绘制--dispatchDraw");
        Paint paint = new Paint();
        paint.setAntiAlias(true);                        //设置画笔为无锯齿
        paint.setColor(roundColor);                     //设置画笔颜色
        paint.setStrokeWidth(roundWidth);                //线宽
        paint.setStrokeCap(Paint.Cap.ROUND);             // 定义线段断点形状为圆头
        paint.setStyle(Paint.Style.STROKE);              //空心
        canvas.drawArc(oval, 160, 220, false, paint);    //绘制圆弧

        //画进度层
        paint.setColor(progressColor);
        paint.setStrokeWidth(roundWidth + 1);
        canvas.drawArc(oval, 160, 220 * nowPro / max, false, paint);  //绘制圆弧


        if (textIsShow) {
            paint.setColor(textColor);
            paint.setStrokeWidth(0);
            paint.setTypeface(Typeface.DEFAULT);
            paint.setTextSize(textSize * 2);
            float textWidth = paint.measureText((int) ((nowPro / (float) max) * 100) + "%");
            canvas.drawText((int) ((nowPro / (float) max) * 100) + "%", viewWidth / 2 - textWidth / 2, (viewWidth) * 2 / 3, paint);
//            String tt = "已完成";
//            paint.setTextSize(textSize);
//            float ttWidth = paint.measureText(tt);
//            canvas.drawText(tt, viewWidth / 2 - ttWidth / 2, (viewWidth * 3 / 5)- UIUtils.pix2dip(200), paint);
        }
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        LogUtils.d("开始绘制--onDraw");
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);                        //设置画笔为无锯齿
//        paint.setColor(roundColor);                     //设置画笔颜色
//        paint.setStrokeWidth(roundWidth);                //线宽
//        paint.setStrokeCap(Paint.Cap.ROUND);             // 定义线段断点形状为圆头
//        paint.setStyle(Paint.Style.STROKE);              //空心
//        canvas.drawArc(oval, 160, 220, false, paint);    //绘制圆弧
//
//        //画进度层
//        paint.setColor(progressColor);
//        paint.setStrokeWidth(roundWidth + 1);
//        canvas.drawArc(oval, 160, 220 * nowPro / max, false, paint);  //绘制圆弧
//
//
//        if (textIsShow) {
//            paint.setColor(textColor);
//            paint.setStrokeWidth(0);
//            paint.setTypeface(Typeface.DEFAULT);
//            paint.setTextSize(textSize * 2);
//            float textWidth = paint.measureText((int) ((nowPro / (float) max) * 100) + "%");
//            canvas.drawText((int) ((nowPro / (float) max) * 100) + "%", viewWidth / 2 - textWidth / 2, viewWidth / 2, paint);
//        }

    }


    private int getDefaultHeight() {
        return 0;
    }

    private int getDefaultWidth() {
        return 0;
    }


    public int getRoundColor() {
        return roundColor;
    }

    public void setRoundColor(int roundColor) {
        this.roundColor = roundColor;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public boolean getText() {
        return textIsShow;
    }

    public void setText(boolean text) {
        this.textIsShow = text;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
//        this.progress = progress*100;
        //动画
        animator = ValueAnimator.ofFloat(0, progress * 10);
        animator.setDuration(1800);
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                nowPro = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
        invalidate();
//        postInvalidate();
//        requestLayout();
    }
}

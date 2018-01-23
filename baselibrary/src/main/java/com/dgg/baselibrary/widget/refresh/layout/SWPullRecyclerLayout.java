package com.dgg.baselibrary.widget.refresh.layout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.*;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.dgg.baselibrary.R;
import com.dgg.baselibrary.widget.refresh.adapter.SWRecyclerAdapter;
import com.dgg.baselibrary.widget.refresh.interfaces.OnTouchUpListener;

/**

 */
public class SWPullRecyclerLayout extends LinearLayout implements NestedScrollingParent , NestedScrollingChild {

    private Context context = null;
    private NestedScrollingParentHelper Phelper = null;
    private boolean IsRefresh = true;
    private boolean IsLoad = true;
    //move total
    private int totalY = 0;
    private LinearLayout headerLayout = null;
    private MyRecyclerView myRecyclerView = null;
    private LinearLayout footerLayout = null;
    private OnTouchUpListener onTouchUpListener = null;
    private boolean isfling = false;
    private int headerHeight = 0;
    private int footerHeight = 0;
    private NestedScrollingChildHelper Chelper;

    public SWPullRecyclerLayout(Context context) {
        super(context);
        this.context = context;
        inital();
    }

    public SWPullRecyclerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inital();
    }

    private void inital() {
        LayoutInflater inflater = LayoutInflater.from(context);
        Phelper = new NestedScrollingParentHelper(this);
        Chelper = new NestedScrollingChildHelper(this);
        headerLayout = new LinearLayout(context);
        myRecyclerView = new MyRecyclerView(context);
        footerLayout = new LinearLayout(context);

        setOrientation(VERTICAL);
        headerLayout.setOrientation(VERTICAL);
        footerLayout.setOrientation(VERTICAL);
        addView(this.headerLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(this.myRecyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(this.footerLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

/* 设置header footer */
        LinearLayout head = (LinearLayout) inflater.inflate(R.layout.refresh_header, null, false);
//        ImageView hearderImg = (ImageView) head.findViewById(com.dgg.baselibrary.R.id.header);
//        Glide.with(context)
//                .load(R.mipmap.loading2)
//                .into(hearderImg);
        addHeaderView(head, 90);

        LinearLayout footer = (LinearLayout) inflater.inflate(R.layout.refresh_footer, null, false);
//        AppCompatImageView footerImg = (AppCompatImageView) footer.findViewById(com.dgg.baselibrary.R.id.footer);
//        Glide.with(context)
//                .load(R.mipmap.loading2)
//                .into(footerImg);
        addFooterView(footer, 90);
    }

    public void setMyRecyclerView(RecyclerView.LayoutManager layoutManager, SWRecyclerAdapter adapter) {
        myRecyclerView.setMyLayoutManager(layoutManager);
        myRecyclerView.setAdapter(adapter);
    }

    public void setMyRecyclerView(RecyclerView.LayoutManager layoutManager, SWRecyclerAdapter adapter, boolean fixed) {
        myRecyclerView.setMyLayoutManager(layoutManager);
        myRecyclerView.setAdapter(adapter);
        myRecyclerView.setHasFixedSize(fixed);
    }

    //add headerview
    public void addHeaderView(View headerView, int headerHeight) {
        this.headerHeight = headerHeight;
        this.headerLayout.removeAllViews();
        this.headerLayout.addView(headerView);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, headerHeight);
        layoutParams.topMargin = -headerHeight;
        this.headerLayout.setLayoutParams(layoutParams);
    }

    //add footerview
    public void addFooterView(View footerView, int footerHeight) {
        this.footerHeight = footerHeight;
        this.footerLayout.removeAllViews();
        this.footerLayout.addView(footerView);
        this.footerLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, footerHeight));
    }

    public void setScrollTo(int fromY, int toY) {
        smoothScrollTo((float) fromY, (float) toY);
    }

    public void setItemDivider(RecyclerView.ItemDecoration itemDecoration) {
        myRecyclerView.addItemDecoration(itemDecoration);
    }

    public int getTotal() {
        return -totalY / 2;
    }


    public void setIsScrollLoad(boolean isScrollLoad) {
        myRecyclerView.isScrollLoad = isScrollLoad;
    }

    public boolean isScrollLoad() {
        return myRecyclerView.isScrollLoad;
    }

    public void setIsScrollRefresh(boolean isScrollRefresh) {
        myRecyclerView.isScrollRefresh = isScrollRefresh;
    }

    public boolean isScrollRefresh() {
        return myRecyclerView.isScrollRefresh;
    }

    public void setRecyclerViewScrollToPosition(int position) {
        myRecyclerView.scrollToPosition(position);
    }

    public void addOnTouchUpListener(OnTouchUpListener onTouchUpListener) {
        this.onTouchUpListener = onTouchUpListener;
    }
    @Override
    public boolean startNestedScroll(int axes) {
        return Chelper.startNestedScroll(axes);
    }

    // NestedScrollingParent
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }
    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        Phelper.onNestedScrollAccepted(child, target, axes);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
    }

    //parent view intercept child view scroll
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (totalY < 0 && myRecyclerView.isOrientation(0) || totalY > 0 && myRecyclerView.isOrientation(1)) {
            isfling = true;
        }
        if (IsRefresh) {
            if (dy > 0) {
                if (myRecyclerView.isOrientation(0)) {
                    totalY += dy;
                    if ((totalY / 2) <= 0) {
                        scrollTo(0, totalY / 2);
                        consumed[1] = dy;
                    } else {
                        scrollTo(0, 0);
                        consumed[1] = 0;
                    }
                }
                return;
            }
        }
        if (IsLoad) {
            if (dy < 0) {
                if (myRecyclerView.isOrientation(1)) {
                    totalY += dy;
                    if ((totalY / 2) >= 0) {
                        scrollTo(0, totalY / 2);
                        consumed[1] = dy;
                    } else {
                        scrollTo(0, 0);
                        consumed[1] = 0;
                    }
                }
                return;
            }
        }
    }

    //while child view move finish
    //dyUnconsumed < 0 move down
    //dyUnconsumed > 0 move up
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed != 0) {
            totalY += dyUnconsumed;
            scrollTo(0, totalY / 2);
        }
    }
    @Override
    public void onStopNestedScroll(View child) {
        Phelper.onStopNestedScroll(child);
        if (onTouchUpListener != null) {
            isfling = false;
            if (this.getTotal() >= headerHeight) {
                this.setScrollTo(this.getTotal(), headerHeight);
                if (!this.isScrollRefresh()) {
                    this.setIsScrollRefresh(true);
                    onTouchUpListener.OnRefreshing();
                }
            } else if (-this.getTotal() >= footerHeight) {
                this.setScrollTo(this.getTotal(), -footerHeight);
                if (!this.isScrollLoad()) {
                    this.setIsScrollLoad(true);
                    onTouchUpListener.OnLoading();
                }
            } else {
                this.setScrollTo(0, 0);
            }
        }
    }

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return isfling;
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return isfling;
    }

    public int getNestedScrollAxes() {
        return Phelper.getNestedScrollAxes();
    }

    private void smoothScrollTo(float fromY, float toY) {
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{fromY, toY});
        if (fromY == toY) {
            animator.setDuration(0);
        } else {
            animator.setDuration(300);
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int to = (int) (-((Float) animation.getAnimatedValue()).floatValue());
                scrollTo(0, to);
                totalY = to * 2;
            }
        });
        animator.start();
    }

    private class MyRecyclerView extends RecyclerView {
        private StaggeredGridLayoutManager staggeredGridLayoutManager = null;
        private LinearLayoutManager linearLayoutManager = null;
        private GridLayoutManager gridLayoutManager = null;
        private boolean isScrollLoad = false;
        private boolean isScrollRefresh = false;

        public MyRecyclerView(Context context) {
            super(context);
            setVerticalFadingEdgeEnabled(false);
            setHorizontalFadingEdgeEnabled(false);
            setVerticalScrollBarEnabled(false);
            setHorizontalScrollBarEnabled(false);
            setOverScrollMode(OVER_SCROLL_NEVER);
            setItemAnimator(new DefaultItemAnimator());
        }

        private void setMyLayoutManager(LayoutManager layoutManager) {
            if (layoutManager instanceof StaggeredGridLayoutManager) {
                staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            } else if (layoutManager instanceof GridLayoutManager) {
                gridLayoutManager = (GridLayoutManager) layoutManager;
            } else if (layoutManager instanceof LinearLayoutManager) {
                linearLayoutManager = (LinearLayoutManager) layoutManager;
            }
            setLayoutManager(layoutManager);
            if (!isVertical()) {
                throw new NullPointerException("vertical!");
            }
        }

        //orientation
        // 0 menas down
        // 1 means up
        private boolean isOrientation(int orientation) {
            if (orientation == 0)
                return isCanPullDown();
            else if (orientation == 1)
                return isCanPullUp();
            return false;
        }

        private boolean isCanPullDown() {
            return !canScrollVertically(-1);
        }

        private boolean isCanPullUp() {
            return !canScrollVertically(1);
        }

        private boolean isVertical() {
            if (staggeredGridLayoutManager != null)
                return staggeredGridLayoutManager.getOrientation() == StaggeredGridLayoutManager.VERTICAL;
            else if (linearLayoutManager != null)
                return linearLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL;
            else if (gridLayoutManager != null)
                return gridLayoutManager.getOrientation() == GridLayoutManager.VERTICAL;
            return false;
        }
    }

}
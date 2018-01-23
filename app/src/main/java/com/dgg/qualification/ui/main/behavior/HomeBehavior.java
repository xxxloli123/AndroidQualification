package com.dgg.qualification.ui.main.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.dgg.baselibrary.tools.LogUtils;
import com.dgg.qualification.R;

import java.util.List;

/**
 * Created by qiqi on 17/8/22.
 */

public class HomeBehavior extends CoordinatorLayout.Behavior<NestedScrollView> {

    public HomeBehavior() {
    }

    public HomeBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, NestedScrollView child, View dependency) {
        return dependency.getId() == R.id.root;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, NestedScrollView child, View dependency) {
        LogUtils.d(child.getNestedScrollAxes() + "滑动的变化：" + getFabTranslationYForSnackbar(parent, child));
        return false;
    }

    private float getFabTranslationYForSnackbar(CoordinatorLayout parent, NestedScrollView child) {
        float minOffset = 0;
//        final List<View> dependencies = parent.getDependencies(child);
//        for (int i = 0, z = dependencies.size(); i < z; i++) {
//            final View view = dependencies.get(i);
//            if (view instanceof RelativeLayout && parent.doViewsOverlap(child, view)) {
//                minOffset = Math.min(minOffset,
//                        ViewCompat.getTranslationY(view) - view.getHeight());
//            }
//        }

        return minOffset;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, NestedScrollView child, View directTargetChild, View target, int nestedScrollAxes) {
        LogUtils.d("onStartNestedScroll" + nestedScrollAxes);
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, NestedScrollView child, View target, int dx, int dy, int[] consumed) {
        LogUtils.d("onStartNestedScroll" + dy);
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, NestedScrollView child, View target, float velocityX, float velocityY) {
        LogUtils.d("onStartNestedScroll" + velocityY);
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }
}

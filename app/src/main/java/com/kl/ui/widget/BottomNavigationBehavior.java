package com.kl.ui.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;


public class BottomNavigationBehavior extends CoordinatorLayout.Behavior<View> {

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private static final int DURATION = 300;

    private int totalDy;
    private int childHeight;
    private boolean hidden;

    public BottomNavigationBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                                 @NonNull View child,
                                                 @NonNull View directTargetChild,
                                                 @NonNull View target,
                                                 int axes,
                                                 int type) {
        childHeight = child.getHeight();
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                            @NonNull View child,
                                            @NonNull View target,
                                            int dx,
                                            int dy,
                                            @NonNull int[] consumed,
                                            int type) {
        if (dy > 0 && totalDy < 0 || dy < 0 && totalDy > 0) {
            totalDy = 0;
        }

        totalDy += dy;
        if (!hidden && totalDy > child.getHeight()) {
            hide(child);
            hidden = true;
        } else if (hidden && totalDy < -child.getHeight()) {
            show(child);
            hidden = false;
        }
    }

    private void hide(final View child) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(child, "translationY", 0, childHeight);
        animator.setDuration(DURATION);
        animator.setInterpolator(INTERPOLATOR);
        animator.start();
    }

    private void show(final View child) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(child, "translationY", childHeight, 0);
        animator.setDuration(DURATION);
        animator.setInterpolator(INTERPOLATOR);
        animator.start();
    }
}
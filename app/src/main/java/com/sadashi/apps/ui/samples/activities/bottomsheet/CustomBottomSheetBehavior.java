/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sadashi.apps.ui.samples.activities.bottomsheet;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.support.v4.math.MathUtils;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;

import com.sadashi.apps.ui.samples.R;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class CustomBottomSheetBehavior<V extends View> extends Behavior<V> {
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_SETTLING = 2;
    public static final int STATE_EXPANDED = 3;
    public static final int STATE_COLLAPSED = 4;
    public static final int STATE_HIDDEN = 5;
    public static final int STATE_HALFWAY_EXPANDED = 6;
    public static final int PEEK_HEIGHT_AUTO = -1;
    private static final float HIDE_THRESHOLD = 0.5F;
    private static final float HIDE_FRICTION = 0.1F;
    private boolean fitToContents = true;
    private float maximumVelocity;
    private int peekHeight;
    private boolean peekHeightAuto;
    private int peekHeightMin;
    private int lastPeekHeight;
    int fitToContentsOffset;
    int halfwayExpandedOffset;
    int collapsedOffset;
    float halfwayExpandedRatio;
    boolean hideable;
    private boolean skipCollapsed;
    int state = STATE_COLLAPSED;
    ViewDragHelper viewDragHelper;
    private boolean ignoreEvents;
    private int lastNestedScrollDy;
    private boolean nestedScrolled;
    int parentHeight;
    WeakReference<V> viewRef;
    WeakReference<View> nestedScrollingChildRef;
    private CustomBottomSheetBehavior.BottomSheetCallback callback;
    private VelocityTracker velocityTracker;
    int activePointerId;
    private int initialY;
    boolean touchingScrollingChild;
    private Map<View, Integer> importantForAccessibilityMap;
    private final Callback dragCallback;

    class NamelessClass_1 extends Callback {
        NamelessClass_1() {
        }

        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            if (CustomBottomSheetBehavior.this.state == STATE_DRAGGING) {
                return false;
            } else if (CustomBottomSheetBehavior.this.touchingScrollingChild) {
                return false;
            } else {
                if (CustomBottomSheetBehavior.this.state == STATE_EXPANDED && CustomBottomSheetBehavior.this.activePointerId == pointerId) {
                    View scroll = CustomBottomSheetBehavior.this.nestedScrollingChildRef.get();
                    if (scroll != null && scroll.canScrollVertically(-1)) {
                        return false;
                    }
                }

                return CustomBottomSheetBehavior.this.viewRef != null && CustomBottomSheetBehavior.this.viewRef.get() == child;
            }
        }

        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            CustomBottomSheetBehavior.this.dispatchOnSlide(top);
        }

        public void onViewDragStateChanged(int state) {
            if (state == STATE_DRAGGING) {
                CustomBottomSheetBehavior.this.setStateInternal(STATE_DRAGGING);
            }

        }

        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            int top;
            byte targetState;
            int currentTop;
            if (yvel < 0.0F) {
                if (CustomBottomSheetBehavior.this.fitToContents) {
                    top = CustomBottomSheetBehavior.this.fitToContentsOffset;
                    targetState = STATE_EXPANDED;
                } else {
                    currentTop = releasedChild.getTop();
                    if (currentTop > CustomBottomSheetBehavior.this.halfwayExpandedOffset) {
                        top = CustomBottomSheetBehavior.this.halfwayExpandedOffset;
                        targetState = STATE_HALFWAY_EXPANDED;
                    } else {
                        top = 0;
                        targetState = STATE_EXPANDED;
                    }
                }
            } else if (!CustomBottomSheetBehavior.this.hideable || !CustomBottomSheetBehavior.this.shouldHide(releasedChild, yvel) || releasedChild.getTop() <= CustomBottomSheetBehavior.this.collapsedOffset && Math.abs(xvel) >= Math.abs(yvel)) {
                if (yvel != 0.0F && Math.abs(xvel) <= Math.abs(yvel)) {
                    top = CustomBottomSheetBehavior.this.collapsedOffset;
                    targetState = STATE_COLLAPSED;
                } else {
                    currentTop = releasedChild.getTop();
                    if (CustomBottomSheetBehavior.this.fitToContents) {
                        if (Math.abs(currentTop - CustomBottomSheetBehavior.this.fitToContentsOffset) < Math.abs(currentTop - CustomBottomSheetBehavior.this.collapsedOffset)) {
                            top = CustomBottomSheetBehavior.this.fitToContentsOffset;
                            targetState = STATE_EXPANDED;
                        } else {
                            top = CustomBottomSheetBehavior.this.collapsedOffset;
                            targetState = STATE_COLLAPSED;
                        }
                    } else if (currentTop < CustomBottomSheetBehavior.this.halfwayExpandedOffset) {
                        if (currentTop < Math.abs(currentTop - CustomBottomSheetBehavior.this.collapsedOffset)) {
                            top = 0;
                            targetState = STATE_EXPANDED;
                        } else {
                            top = CustomBottomSheetBehavior.this.halfwayExpandedOffset;
                            targetState = STATE_HALFWAY_EXPANDED;
                        }
                    } else if (Math.abs(currentTop - CustomBottomSheetBehavior.this.halfwayExpandedOffset) < Math.abs(currentTop - CustomBottomSheetBehavior.this.collapsedOffset)) {
                        top = CustomBottomSheetBehavior.this.halfwayExpandedOffset;
                        targetState = STATE_HALFWAY_EXPANDED;
                    } else {
                        top = CustomBottomSheetBehavior.this.collapsedOffset;
                        targetState = STATE_COLLAPSED;
                    }
                }
            } else {
                top = CustomBottomSheetBehavior.this.parentHeight;
                targetState = STATE_HIDDEN;
            }

            if (CustomBottomSheetBehavior.this.viewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top)) {
                CustomBottomSheetBehavior.this.setStateInternal(STATE_SETTLING);
                ViewCompat.postOnAnimation(releasedChild, CustomBottomSheetBehavior.this.new SettleRunnable(releasedChild, targetState));
            } else {
                CustomBottomSheetBehavior.this.setStateInternal(targetState);
            }

        }

        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return MathUtils.clamp(top, CustomBottomSheetBehavior.this.getExpandedOffset(), CustomBottomSheetBehavior.this.hideable ? CustomBottomSheetBehavior.this.parentHeight : CustomBottomSheetBehavior.this.collapsedOffset);
        }

        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            return child.getLeft();
        }

        public int getViewVerticalDragRange(@NonNull View child) {
            return CustomBottomSheetBehavior.this.hideable ? CustomBottomSheetBehavior.this.parentHeight : CustomBottomSheetBehavior.this.collapsedOffset;
        }
    }

    public CustomBottomSheetBehavior() {
        this.dragCallback = new NamelessClass_1();
    }

    public CustomBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);


        this.dragCallback = new NamelessClass_1();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomBottomSheetBehavior_Layout);
        TypedValue value = a.peekValue(R.styleable.CustomBottomSheetBehavior_Layout_behavior_customPeekHeight);
        if (value != null && value.data == PEEK_HEIGHT_AUTO) {
            this.setPeekHeight(value.data);
        } else {
            this.setPeekHeight(a.getDimensionPixelSize(R.styleable.CustomBottomSheetBehavior_Layout_behavior_customPeekHeight, PEEK_HEIGHT_AUTO));
        }

        this.setHideable(a.getBoolean(R.styleable.CustomBottomSheetBehavior_Layout_behavior_customHideable, false));
        this.setFitToContents(a.getBoolean(R.styleable.CustomBottomSheetBehavior_Layout_behavior_customFitToContents, true));
        this.setSkipCollapsed(a.getBoolean(R.styleable.CustomBottomSheetBehavior_Layout_behavior_customSkipCollapsed, false));
        this.setHalfwayExpandedRatio(a.getFloat(R.styleable.CustomBottomSheetBehavior_Layout_behavior_halfwayExpandRatio, 0.5F));
        a.recycle();
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.maximumVelocity = (float) configuration.getScaledMaximumFlingVelocity();
    }

    public Parcelable onSaveInstanceState(@NotNull CoordinatorLayout parent, @NotNull V child) {
        return new CustomBottomSheetBehavior.SavedState(super.onSaveInstanceState(parent, child), this.state);
    }

    public void onRestoreInstanceState(@NotNull CoordinatorLayout parent, @NotNull V child, @NotNull Parcelable state) {
        CustomBottomSheetBehavior.SavedState ss = (CustomBottomSheetBehavior.SavedState) state;
        super.onRestoreInstanceState(parent, child, ss.getSuperState());
        if (ss.state != STATE_DRAGGING && ss.state != STATE_SETTLING) {
            this.state = ss.state;
        } else {
            this.state = STATE_COLLAPSED;
        }

    }

    public boolean onLayoutChild(@NotNull CoordinatorLayout parent, @NotNull V child, int layoutDirection) {
        if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
            child.setFitsSystemWindows(true);
        }

        int savedTop = child.getTop();
        parent.onLayoutChild(child, layoutDirection);
        this.parentHeight = parent.getHeight();
        if (this.peekHeightAuto) {
            if (this.peekHeightMin == 0) {
                this.peekHeightMin = parent.getResources().getDimensionPixelSize(R.dimen.design_bottom_sheet_peek_height_min);
            }

            this.lastPeekHeight = Math.max(this.peekHeightMin, this.parentHeight - parent.getWidth() * 9 / 16);
        } else {
            this.lastPeekHeight = this.peekHeight;
        }

        this.fitToContentsOffset = Math.max(0, this.parentHeight - child.getHeight());
        this.halfwayExpandedOffset = Math.round(this.parentHeight * this.halfwayExpandedRatio);
        this.calculateCollapsedOffset();
        if (this.state == STATE_EXPANDED) {
            ViewCompat.offsetTopAndBottom(child, this.getExpandedOffset());
        } else if (this.state == STATE_HALFWAY_EXPANDED) {
            ViewCompat.offsetTopAndBottom(child, this.halfwayExpandedOffset);
        } else if (this.hideable && this.state == STATE_HIDDEN) {
            ViewCompat.offsetTopAndBottom(child, this.parentHeight);
        } else if (this.state == STATE_COLLAPSED) {
            ViewCompat.offsetTopAndBottom(child, this.collapsedOffset);
        } else if (this.state == STATE_DRAGGING || this.state == STATE_SETTLING) {
            ViewCompat.offsetTopAndBottom(child, savedTop - child.getTop());
        }

        if (this.viewDragHelper == null) {
            this.viewDragHelper = ViewDragHelper.create(parent, this.dragCallback);
        }

        this.viewRef = new WeakReference<>(child);
        this.nestedScrollingChildRef = new WeakReference<>(this.findScrollingChild(child));
        return true;
    }

    public boolean onInterceptTouchEvent(@NotNull CoordinatorLayout parent, V child, @NotNull MotionEvent event) {
        if (!child.isShown()) {
            this.ignoreEvents = true;
            return false;
        } else {
            int action = event.getActionMasked();
            if (action == 0) {
                this.reset();
            }

            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }

            this.velocityTracker.addMovement(event);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    int initialX = (int) event.getX();
                    this.initialY = (int) event.getY();
                    View scroll = this.nestedScrollingChildRef != null ? this.nestedScrollingChildRef.get() : null;
                    if (scroll != null && parent.isPointInChildBounds(scroll, initialX, this.initialY)) {
                        this.activePointerId = event.getPointerId(event.getActionIndex());
                        this.touchingScrollingChild = true;
                    }

                    this.ignoreEvents = this.activePointerId == MotionEvent.INVALID_POINTER_ID && !parent.isPointInChildBounds(child, initialX, this.initialY);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    this.touchingScrollingChild = false;
                    this.activePointerId = MotionEvent.INVALID_POINTER_ID;
                    if (this.ignoreEvents) {
                        this.ignoreEvents = false;
                        return false;
                    }
                default:
            }

            if (!this.ignoreEvents && this.viewDragHelper != null && this.viewDragHelper.shouldInterceptTouchEvent(event)) {
                return true;
            } else {
                View scroll = this.nestedScrollingChildRef != null ? this.nestedScrollingChildRef.get() : null;
                return action == MotionEvent.ACTION_MOVE && scroll != null && !this.ignoreEvents && this.state != STATE_DRAGGING && !parent.isPointInChildBounds(scroll, (int) event.getX(), (int) event.getY()) && this.viewDragHelper != null && Math.abs((float) this.initialY - event.getY()) > (float) this.viewDragHelper.getTouchSlop();
            }
        }
    }

    public boolean onTouchEvent(@NotNull CoordinatorLayout parent, V child, @NotNull MotionEvent event) {
        if (!child.isShown()) {
            return false;
        } else {
            int action = event.getActionMasked();
            if (this.state == STATE_DRAGGING && action == MotionEvent.ACTION_DOWN) {
                return true;
            } else {
                if (this.viewDragHelper != null) {
                    this.viewDragHelper.processTouchEvent(event);
                }

                if (action == MotionEvent.ACTION_DOWN) {
                    this.reset();
                }

                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }

                this.velocityTracker.addMovement(event);
                if (action == MotionEvent.ACTION_MOVE && !this.ignoreEvents && Math.abs((float) this.initialY - event.getY()) > (float) this.viewDragHelper.getTouchSlop()) {
                    this.viewDragHelper.captureChildView(child, event.getPointerId(event.getActionIndex()));
                }

                return !this.ignoreEvents;
            }
        }
    }

    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        this.lastNestedScrollDy = 0;
        this.nestedScrolled = false;
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (type != 1) {
            View scrollingChild = this.nestedScrollingChildRef.get();
            if (target == scrollingChild) {
                int currentTop = child.getTop();
                int newTop = currentTop - dy;
                if (dy > 0) {
                    if (newTop < this.getExpandedOffset()) {
                        consumed[1] = currentTop - this.getExpandedOffset();
                        ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                        this.setStateInternal(STATE_EXPANDED);
                    } else {
                        consumed[1] = dy;
                        ViewCompat.offsetTopAndBottom(child, -dy);
                        this.setStateInternal(STATE_DRAGGING);
                    }
                } else if (dy < 0 && !target.canScrollVertically(-1)) {
                    if (newTop > this.collapsedOffset && !this.hideable) {
                        consumed[1] = currentTop - this.collapsedOffset;
                        ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                        this.setStateInternal(STATE_COLLAPSED);
                    } else {
                        consumed[1] = dy;
                        ViewCompat.offsetTopAndBottom(child, -dy);
                        this.setStateInternal(STATE_DRAGGING);
                    }
                }

                this.dispatchOnSlide(child.getTop());
                this.lastNestedScrollDy = dy;
                this.nestedScrolled = true;
            }
        }
    }

    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int type) {
        if (child.getTop() == this.getExpandedOffset()) {
            this.setStateInternal(STATE_EXPANDED);
        } else if (target == this.nestedScrollingChildRef.get() && this.nestedScrolled) {
            int top;
            byte targetState;
            if (this.lastNestedScrollDy > 0) {
                top = this.getExpandedOffset();
                targetState = STATE_EXPANDED;
            } else if (this.hideable && this.shouldHide(child, this.getYVelocity())) {
                top = this.parentHeight;
                targetState = STATE_HIDDEN;
            } else if (this.lastNestedScrollDy == 0) {
                int currentTop = child.getTop();
                if (this.fitToContents) {
                    if (Math.abs(currentTop - this.fitToContentsOffset) < Math.abs(currentTop - this.collapsedOffset)) {
                        top = this.fitToContentsOffset;
                        targetState = STATE_EXPANDED;
                    } else {
                        top = this.collapsedOffset;
                        targetState = STATE_COLLAPSED;
                    }
                } else if (currentTop < this.halfwayExpandedOffset) {
                    if (currentTop < Math.abs(currentTop - this.collapsedOffset)) {
                        top = 0;
                        targetState = STATE_EXPANDED;
                    } else {
                        top = this.halfwayExpandedOffset;
                        targetState = STATE_HALFWAY_EXPANDED;
                    }
                } else if (Math.abs(currentTop - this.halfwayExpandedOffset) < Math.abs(currentTop - this.collapsedOffset)) {
                    top = this.halfwayExpandedOffset;
                    targetState = STATE_HALFWAY_EXPANDED;
                } else {
                    top = this.collapsedOffset;
                    targetState = STATE_COLLAPSED;
                }
            } else {
                top = this.collapsedOffset;
                targetState = STATE_COLLAPSED;
            }

            if (this.viewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
                this.setStateInternal(STATE_SETTLING);
                ViewCompat.postOnAnimation(child, new CustomBottomSheetBehavior.SettleRunnable(child, targetState));
            } else {
                this.setStateInternal(targetState);
            }

            this.nestedScrolled = false;
        }
    }

    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, float velocityX, float velocityY) {
        return target == this.nestedScrollingChildRef.get() && (this.state != STATE_EXPANDED || super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY));
    }

    public boolean isFitToContents() {
        return this.fitToContents;
    }

    public void setFitToContents(boolean fitToContents) {
        if (this.fitToContents != fitToContents) {
            this.fitToContents = fitToContents;
            if (this.viewRef != null) {
                this.calculateCollapsedOffset();
            }

            this.setStateInternal(this.fitToContents && this.state == STATE_HALFWAY_EXPANDED ? STATE_EXPANDED : this.state);
        }
    }

    public final void setPeekHeight(int peekHeight) {
        boolean layout = false;
        if (peekHeight == PEEK_HEIGHT_AUTO) {
            if (!this.peekHeightAuto) {
                this.peekHeightAuto = true;
                layout = true;
            }
        } else if (this.peekHeightAuto || this.peekHeight != peekHeight) {
            this.peekHeightAuto = false;
            this.peekHeight = Math.max(0, peekHeight);
            this.collapsedOffset = this.parentHeight - peekHeight;
            layout = true;
        }

        if (layout && this.state == STATE_COLLAPSED && this.viewRef != null) {
            V view = this.viewRef.get();
            if (view != null) {
                view.requestLayout();
            }
        }

    }

    public final int getPeekHeight() {
        return this.peekHeightAuto ? PEEK_HEIGHT_AUTO : this.peekHeight;
    }

    public void setHideable(boolean hideable) {
        this.hideable = hideable;
    }

    public boolean isHideable() {
        return this.hideable;
    }

    public void setSkipCollapsed(boolean skipCollapsed) {
        this.skipCollapsed = skipCollapsed;
    }

    public boolean getSkipCollapsed() {
        return this.skipCollapsed;
    }

    public void setHalfwayExpandedRatio(float ratio) {
        this.halfwayExpandedRatio = ratio;
        this.halfwayExpandedOffset = Math.round(this.parentHeight * this.halfwayExpandedRatio);
    }

    public float getHalfwayExpandedRatio() {
        return this.halfwayExpandedRatio;
    }

    public void setBottomSheetCallback(CustomBottomSheetBehavior.BottomSheetCallback callback) {
        this.callback = callback;
    }

    public final void setState(final int state) {
        if (state != this.state) {
            if (this.viewRef == null) {
                if (state == STATE_COLLAPSED || state == STATE_EXPANDED || state == STATE_HALFWAY_EXPANDED || this.hideable && state == STATE_HIDDEN) {
                    this.state = state;
                }

            } else {
                final V child = this.viewRef.get();
                if (child != null) {
                    ViewParent parent = child.getParent();
                    if (parent != null && parent.isLayoutRequested() && ViewCompat.isAttachedToWindow(child)) {
                        child.post(() -> CustomBottomSheetBehavior.this.startSettlingAnimation(child, state));
                    } else {
                        this.startSettlingAnimation(child, state);
                    }

                }
            }
        }
    }

    public final int getState() {
        return this.state;
    }

    void setStateInternal(int state) {
        if (this.state != state) {
            this.state = state;
            if (state != STATE_HALFWAY_EXPANDED && state != STATE_EXPANDED) {
                if (state == STATE_HIDDEN || state == STATE_COLLAPSED) {
                    this.updateImportantForAccessibility(false);
                }
            } else {
                this.updateImportantForAccessibility(true);
            }

            View bottomSheet = this.viewRef.get();
            if (bottomSheet != null && this.callback != null) {
                this.callback.onStateChanged(bottomSheet, state);
            }

        }
    }

    private void calculateCollapsedOffset() {
        if (this.fitToContents) {
            this.collapsedOffset = Math.max(this.parentHeight - this.lastPeekHeight, this.fitToContentsOffset);
        } else {
            this.collapsedOffset = this.parentHeight - this.lastPeekHeight;
        }

    }

    private void reset() {
        this.activePointerId = MotionEvent.INVALID_POINTER_ID;
        if (this.velocityTracker != null) {
            this.velocityTracker.recycle();
            this.velocityTracker = null;
        }

    }

    boolean shouldHide(View child, float yvel) {
        if (this.skipCollapsed) {
            return true;
        } else if (child.getTop() < this.collapsedOffset) {
            return false;
        } else {
            float newTop = (float) child.getTop() + yvel * HIDE_FRICTION;
            return Math.abs(newTop - (float) this.collapsedOffset) / (float) this.peekHeight > HIDE_THRESHOLD;
        }
    }

    @VisibleForTesting
    View findScrollingChild(View view) {
        if (ViewCompat.isNestedScrollingEnabled(view)) {
            return view;
        } else {
            if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) view;
                int i = 0;

                for (int count = group.getChildCount(); i < count; ++i) {
                    View scrollingChild = this.findScrollingChild(group.getChildAt(i));
                    if (scrollingChild != null) {
                        return scrollingChild;
                    }
                }
            }

            return null;
        }
    }

    private float getYVelocity() {
        if (this.velocityTracker == null) {
            return 0.0F;
        } else {
            this.velocityTracker.computeCurrentVelocity(1000, this.maximumVelocity);
            return this.velocityTracker.getYVelocity(this.activePointerId);
        }
    }

    private int getExpandedOffset() {
        return this.fitToContents ? this.fitToContentsOffset : 0;
    }

    void startSettlingAnimation(View child, int state) {
        int top;
        if (state == STATE_COLLAPSED) {
            top = this.collapsedOffset;
        } else if (state == STATE_HALFWAY_EXPANDED) {
            top = this.halfwayExpandedOffset;
            if (this.fitToContents && top <= this.fitToContentsOffset) {
                state = STATE_EXPANDED;
                top = this.fitToContentsOffset;
            }
        } else if (state == STATE_EXPANDED) {
            top = this.getExpandedOffset();
        } else {
            if (!this.hideable || state != STATE_HIDDEN) {
                throw new IllegalArgumentException("Illegal state argument: " + state);
            }

            top = this.parentHeight;
        }

        if (this.viewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
            this.setStateInternal(STATE_SETTLING);
            ViewCompat.postOnAnimation(child, new CustomBottomSheetBehavior.SettleRunnable(child, state));
        } else {
            this.setStateInternal(state);
        }

    }

    void dispatchOnSlide(int top) {
        View bottomSheet = this.viewRef.get();
        if (bottomSheet != null && this.callback != null) {
            if (top > this.collapsedOffset) {
                this.callback.onSlide(bottomSheet, (float) (this.collapsedOffset - top) / (float) (this.parentHeight - this.collapsedOffset));
            } else {
                this.callback.onSlide(bottomSheet, (float) (this.collapsedOffset - top) / (float) (this.collapsedOffset - this.getExpandedOffset()));
            }
        }

    }

    @VisibleForTesting
    int getPeekHeightMin() {
        return this.peekHeightMin;
    }

    public static <V extends View> CustomBottomSheetBehavior<V> from(V view) {
        LayoutParams params = view.getLayoutParams();
        if (!(params instanceof android.support.design.widget.CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        } else {
            Behavior behavior = ((android.support.design.widget.CoordinatorLayout.LayoutParams) params).getBehavior();
            if (!(behavior instanceof CustomBottomSheetBehavior)) {
                throw new IllegalArgumentException("The view is not associated with CustomBottomSheetBehavior");
            }
            return (CustomBottomSheetBehavior) behavior;
        }
    }

    private void updateImportantForAccessibility(boolean expanded) {
        if (this.viewRef != null) {
            ViewParent viewParent = (this.viewRef.get()).getParent();
            if (viewParent instanceof CoordinatorLayout) {
                CoordinatorLayout parent = (CoordinatorLayout) viewParent;
                int childCount = parent.getChildCount();
                if (expanded) {
                    if (this.importantForAccessibilityMap != null) {
                        return;
                    }

                    this.importantForAccessibilityMap = new HashMap<>(childCount);
                }

                for (int i = 0; i < childCount; ++i) {
                    View child = parent.getChildAt(i);
                    if (child != this.viewRef.get()) {
                        if (!expanded) {
                            if (this.importantForAccessibilityMap != null && this.importantForAccessibilityMap.containsKey(child)) {
                                ViewCompat.setImportantForAccessibility(child, this.importantForAccessibilityMap.get(child));
                            }
                        } else {
                            this.importantForAccessibilityMap.put(child, child.getImportantForAccessibility());
                            ViewCompat.setImportantForAccessibility(child, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
                        }
                    }
                }

                if (!expanded) {
                    this.importantForAccessibilityMap = null;
                }

            }
        }
    }

    protected static class SavedState extends AbsSavedState {
        final int state;
        public static final Creator<CustomBottomSheetBehavior.SavedState> CREATOR = new ClassLoaderCreator<CustomBottomSheetBehavior.SavedState>() {
            public CustomBottomSheetBehavior.SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new CustomBottomSheetBehavior.SavedState(in, loader);
            }

            public CustomBottomSheetBehavior.SavedState createFromParcel(Parcel in) {
                return new CustomBottomSheetBehavior.SavedState(in, null);
            }

            public CustomBottomSheetBehavior.SavedState[] newArray(int size) {
                return new CustomBottomSheetBehavior.SavedState[size];
            }
        };

        public SavedState(Parcel source) {
            this(source, null);
        }

        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            this.state = source.readInt();
        }

        public SavedState(Parcelable superState, int state) {
            super(superState);
            this.state = state;
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.state);
        }
    }

    private class SettleRunnable implements Runnable {
        private final View view;
        private final int targetState;

        SettleRunnable(View view, int targetState) {
            this.view = view;
            this.targetState = targetState;
        }

        public void run() {
            if (CustomBottomSheetBehavior.this.viewDragHelper != null && CustomBottomSheetBehavior.this.viewDragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(this.view, this);
            } else {
                CustomBottomSheetBehavior.this.setStateInternal(this.targetState);
            }

        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({Scope.LIBRARY_GROUP})
    public @interface State {
    }

    public abstract static class BottomSheetCallback {
        public BottomSheetCallback() {
        }

        public abstract void onStateChanged(@NonNull View var1, int var2);

        public abstract void onSlide(@NonNull View var1, float var2);
    }
}

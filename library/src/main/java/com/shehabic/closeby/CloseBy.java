package com.shehabic.closeby;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;

public class CloseBy {
    // Position of icon relative to 
    public static final int POSITION_TOP = 1;
    public static final int POSITION_RIGHT = 2;
    public static final int POSITION_TOP_RIGHT = 3;
    public static final int POSITION_BOTTOM = 4;
    public static final int POSITION_BOTTOM_RIGHT = 6;
    public static final int POSITION_TOP_LEFT = 9;
    public static final int POSITION_LEFT = 8;
    public static final int POSITION_BOTTOM_LEFT = 12;

    public static final int ALIGN_HORIZONTAL_LEFT = 1;
    public static final int ALIGN_HORIZONTAL_CENTER = 2;
    public static final int ALIGN_HORIZONTAL_RIGHT = 4;

    public static final int ALIGN_VERTICAL_TOP = 1;
    public static final int ALIGN_VERTICAL_BOTTOM = 2;
    public static final int ALIGN_VERTICAL_CENTER = 4;

    public static final int MARGIN_TYPE_ABSOLUTE = 0;
    public static final int MARGIN_TYPE_RELATIVE_TO_PARENT = 1;
    public static final int MARGIN_TYPE_RELATIVE_TO_SELF = 2;

    public boolean isAnimating = false;


    protected AnimationHandlerInterface animationHandler;

    public Activity activity;
    protected View sourceView;
    protected int positionX;
    protected int positionY;
    protected int position;
    protected Item closeBy;
    protected int verticalAlignment = ALIGN_VERTICAL_CENTER;
    protected int horizontalAlignment = ALIGN_HORIZONTAL_CENTER;

    protected boolean added = false;

    public static class Item {
        public int x;
        public int y;
        public int width;
        public int height;
        public int marginLeft = 0;
        public int marginRight = 0;
        public int marginTop = 0;
        public int marginBottom = 0;
        public float marginLeftRelative = 0f;
        public float marginRightRelative = 0f;
        public float marginTopRelative = 0f;
        public float marginBottomRelative = 0f;

        public int marginType = MARGIN_TYPE_ABSOLUTE;

        public View view;
        public View sourceView;

        public Item(View view, View sourceView, int width, int height) {
            this.sourceView = sourceView;
            this.view = view;
            this.width = width;
            this.height = height;
            x = 0;
            y = 0;
        }

        public void setMargin(int top, int right, int left, int bottom) {
            this.marginTop = top;
            this.marginRight = right;
            this.marginBottom = bottom;
            this.marginLeft = left;
        }

        public void setMargin(float top, float right, float left, float bottom) {
            this.marginTopRelative = top;
            this.marginRightRelative = right;
            this.marginBottomRelative = bottom;
            this.marginLeftRelative = left;
        }

        public int getMarginTop() {
            return marginType == MARGIN_TYPE_ABSOLUTE
                    ? marginTop
                    : Math.round(marginTopRelative * getRelativeView().getMeasuredHeight());
        }

        public int getMarginBottom() {
            return marginType == MARGIN_TYPE_ABSOLUTE
                    ? marginBottom
                    : Math.round(marginBottomRelative * getRelativeView().getMeasuredHeight());
        }

        public int getMarginLeft() {
            return marginType == MARGIN_TYPE_ABSOLUTE
                    ? marginLeft
                    : Math.round(marginLeftRelative * getRelativeView().getMeasuredWidth());
        }

        public int getMarginRight() {
            return marginType == MARGIN_TYPE_ABSOLUTE
                    ? marginRight
                    : Math.round(marginRightRelative * getRelativeView().getMeasuredWidth());
        }

        public View getRelativeView() {
            if (this.marginType == MARGIN_TYPE_RELATIVE_TO_PARENT) {
                return this.sourceView;
            }

            return this.view;
        }

        public void setMarginType(int marginType) {
            this.marginType = marginType;
        }
    }

    public CloseBy(Activity activity, View sourceView, Item closeBy, int position, int horizontalAlignment,
                   int verticalAlignment) {
        this.activity = activity;
        this.sourceView = sourceView;
        this.closeBy = closeBy;
        this.position = position;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
    }

    protected OnClickListener onclick;

    protected void setOnClick(OnClickListener onclick) {
        this.onclick = onclick;
    }

    public void setAnimationHandler(AnimationHandlerInterface animationHandler) {
        this.animationHandler = animationHandler;
    }

    public void detach(View v) {
        ((ViewGroup) getActivityContentView()).removeView(v);
        added = false;
    }

    public void detach() {
        detach(closeBy.view);
    }

    protected View getActivityContentView() {
        return ((Activity) sourceView.getContext()).getWindow().getDecorView().findViewById(android.R.id.content);
    }

    protected Point getScreenSize() {
        Point size = new Point();
        ((Activity) sourceView.getContext()).getWindowManager().getDefaultDisplay().getSize(size);
        return size;
    }

    protected Point getActionViewCoordinates() {
        int[] coords = new int[2];
        sourceView.getLocationOnScreen(coords);

        return new Point(coords[0], coords[1]);
    }

    public Point getActionViewCenter() {
        Point point = getActionViewCoordinates();
        point.x += sourceView.getMeasuredWidth() / 2;
        point.y += sourceView.getMeasuredHeight() / 2;

        return point;
    }

    public View getCloseBy() {
        return closeBy.view;
    }

    protected void calculateCloseByPosition() {
        Point center = getActionViewCenter();

        // Adjusting X (Left)
        if (isPositioned(position, POSITION_RIGHT)) {
            closeBy.x = center.x + (sourceView.getMeasuredWidth() / 2);
        } else if (isPositioned(position, POSITION_LEFT)) {
            closeBy.x = center.x - (sourceView.getMeasuredWidth() / 2) - (closeBy.width);
        } else {
            closeBy.x = center.x - (closeBy.width / 2);
        }

        // Adjusting Y (Top)
        if (isPositioned(position, POSITION_TOP)) {
            closeBy.y = center.y - closeBy.height - (sourceView.getMeasuredHeight() / 2);
        } else if (isPositioned(position, POSITION_BOTTOM)) {
            closeBy.y = center.y + (sourceView.getMeasuredHeight() / 2);
        } else {
            closeBy.y = center.y - (closeBy.height / 2);
        }

        // Special Cases based on alignment & position
        if (position == POSITION_TOP || position == POSITION_BOTTOM) {
            if (horizontalAlignment == ALIGN_HORIZONTAL_LEFT) {
                closeBy.x = center.x - (sourceView.getMeasuredWidth() / 2);
            } else if (horizontalAlignment == ALIGN_HORIZONTAL_RIGHT) {
                closeBy.x = center.x + (sourceView.getMeasuredWidth() / 2) - closeBy.width;
            }
        }

        if (position == POSITION_LEFT || position == POSITION_RIGHT) {
            if (verticalAlignment == ALIGN_VERTICAL_TOP) {
                closeBy.y = center.y - (sourceView.getMeasuredHeight() / 2);
            } else if (verticalAlignment == ALIGN_VERTICAL_BOTTOM) {
                closeBy.y = center.y + (sourceView.getMeasuredHeight() / 2) - closeBy.height;
            }
        }

        closeBy.y -= isPositioned(position, POSITION_TOP) ? closeBy.getMarginTop() : 0;
        closeBy.y += isPositioned(position, POSITION_BOTTOM) ? closeBy.getMarginBottom() : 0;
        closeBy.x -= isPositioned(position, POSITION_LEFT) ? closeBy.getMarginLeft() : 0;
        closeBy.x += isPositioned(position, POSITION_RIGHT) ? closeBy.getMarginRight() : 0;

    }

    protected boolean isPositioned(int value, int position) {
        return (value & position) == position;
    }

    public void show() {
        show(true);
    }

    public void show(boolean animated) {
        if (isAnimating) {
            return;
        }
        isAnimating = true;

        calculateCloseByPosition();
        addView();
        if (animated && animationHandler != null) {
            closeBy.view.setVisibility(View.GONE);
            animationHandler.show(closeBy.view, this);
        } else {
            closeBy.view.setVisibility(View.VISIBLE);
            isAnimating = false;
        }

        if (this.onclick != null) {
            closeBy.view.setOnClickListener(this.onclick);
        }
    }

    protected void addView() {
        if (added) {
            return;
        }
        Log.d("Adding", "Yes");
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(closeBy.width, closeBy.height, Gravity.TOP
                | Gravity.LEFT);
        params.setMargins(closeBy.x, closeBy.y, 0, 0);
        if (closeBy.view.getParent() != null) {
            ((ViewGroup) closeBy.view.getParent()).removeView(closeBy.view);
        }
        ((ViewGroup) getActivityContentView()).addView(closeBy.view, params);
        added = true;
    }

    public void hide(Boolean animated) {
        if (isAnimating) {
            return;
        }
        isAnimating = true;
        if (animated && animationHandler != null) {
            animationHandler.hide(closeBy.view, this);
        } else {
            detach();            isAnimating = false;
        }
    }

    public void hide() {
        hide(true);
    }

    public void updatePosition() {
        calculateCloseByPosition();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(closeBy.width, closeBy.height, Gravity.TOP
                | Gravity.LEFT);
        params.setMargins(closeBy.x, closeBy.y, 0, 0);
        getCloseBy().setLayoutParams(params);
    }

    @SuppressLint("NewApi")
    protected void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    protected ViewTreeObserver.OnGlobalLayoutListener onFirstRendered;

    public void setOnRendered(final ViewTreeObserver.OnGlobalLayoutListener onFirstRendered) {
        this.onFirstRendered = onFirstRendered;
        this.closeBy.view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeOnGlobalLayoutListener(getCloseBy(), this);
                if (onFirstRendered != null) {
                    onFirstRendered.onGlobalLayout();
                }
            }
        });
    }

    public static class Builder {
        protected View srcView;
        protected Item clsBy;
        protected int position;
        protected int positionX;
        protected int positionY;
        protected Activity activity;
        protected int horizontalAlignment = ALIGN_HORIZONTAL_CENTER;
        protected int verticalAlignment = ALIGN_VERTICAL_CENTER;
        protected OnClickListener onclick;
        protected AnimationHandlerInterface animationHandler;
        protected ViewTreeObserver.OnGlobalLayoutListener onFirstRendered;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setAnimationHandler(AnimationHandlerInterface animationHandler) {
            this.animationHandler = animationHandler;

            return this;
        }

        public Builder setSourceView(View srcView) {
            this.srcView = srcView;

            return this;
        }

        public Builder setPosition(int position) {
            this.position = position;

            return this;
        }

        public Builder setCloseBy(View closeBy) {
            setCloseBy(closeBy, POSITION_TOP);

            return this;
        }

        public Builder setCloseBy(View closeBy, int positionX, int positionY) {
            this.clsBy = new Item(closeBy, this.srcView, closeBy.getMeasuredWidth(), closeBy.getMeasuredHeight());
            this.positionX = positionX;
            this.positionY = positionY;

            return this;
        }

        public Builder setCloseBy(View closeBy, int position) {
            this.clsBy = new Item(closeBy, this.srcView, closeBy.getMeasuredWidth(), closeBy.getMeasuredHeight());
            this.position = position;

            return this;
        }

        public Builder setCloseBy(int resId, Context context) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(resId, null, false);
            view.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            // Log.d("Measured W/H", view.getMeasuredWidth() + "," + view.getMeasuredHeight());
            this.clsBy = new Item(view, this.srcView, view.getMeasuredWidth(), view.getMeasuredHeight());

            return this;
        }

        public Builder setAlignment(int horizontal, int vertical) {
            this.horizontalAlignment = horizontal;
            this.verticalAlignment = vertical;

            return this;
        }

        public Builder setVerticalAlignment(int vertical) {
            this.verticalAlignment = vertical;

            return this;
        }

        public Builder setHorizontalAlignment(int horizontal) {
            this.horizontalAlignment = horizontal;

            return this;
        }

        public Builder setMarginRight(float val) {
            this.clsBy.marginRightRelative = val;

            return this;
        }

        public Builder setMarginTop(float val) {
            this.clsBy.marginTopRelative = val;

            return this;
        }

        public Builder setMarginLeft(float val) {
            this.clsBy.marginLeftRelative = val;

            return this;
        }

        public Builder setMarginBottom(float val) {
            this.clsBy.marginBottomRelative = val;

            return this;
        }

        public Builder setMarginRight(int val) {
            this.clsBy.marginRight = val;

            return this;
        }

        public Builder setMarginTop(int val) {
            this.clsBy.marginTop = val;

            return this;
        }

        public Builder setMarginLeft(int val) {
            this.clsBy.marginLeft = val;

            return this;
        }

        public Builder setMarginBottom(int val) {
            this.clsBy.marginBottom = val;

            return this;
        }

        public Builder setMargin(int top, int right, int bottom, int left) {
            this.clsBy.setMargin(top, right, left, bottom);

            return this;
        }

        public Builder setMargin(float top, float right, float bottom, float left) {
            this.clsBy.setMargin(top, right, left, bottom);

            return this;
        }


        public Builder setOnClick(OnClickListener onclick) {
            this.onclick = onclick;

            return this;
        }

        public Builder setOnRendered(ViewTreeObserver.OnGlobalLayoutListener onFirstRendered) {
            this.onFirstRendered = onFirstRendered;

            return this;
        }

        public Builder setMarginType(int marginType) {
            this.clsBy.setMarginType(marginType);

            return this;
        }

        public CloseBy build() {
            this.clsBy.sourceView = this.srcView;            CloseBy closeBy = new CloseBy(this.activity, this.srcView, this.clsBy, this.position, this.horizontalAlignment,
                    this.verticalAlignment);
            if (this.onclick != null) {
                closeBy.setOnClick(this.onclick);
            }

            if (this.onFirstRendered != null) {
                closeBy.setOnRendered(this.onFirstRendered);
            }

            if (this.animationHandler != null) {
                closeBy.setAnimationHandler(this.animationHandler);
            }

            return closeBy;
        }
    }
}

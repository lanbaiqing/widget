package com.lbq.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by lbq on 2018/5/20.
 */

public class DampView extends ViewGroup
{
    private View childView;
    private float x1,x2,y1,y2;
    private int mScroll_child;
    private boolean mScrollLeftEnabled;
    private boolean mScrollRightEnabled;
    private boolean mScrollTopEnabled;
    private boolean mScrollBottomEnabled;
    private final Scroller mScroller = new Scroller(getContext());
    public void setChildView(View childView)
    {
        this.childView = childView;
    }
    public void setScrollLeftEnabled(boolean flag)
    {
        this.mScrollLeftEnabled = flag;
    }
    public void setScrollRightEnabled(boolean flag)
    {
        this.mScrollRightEnabled = flag;
    }
    public void setScrollTopEnabled(boolean flag)
    {
        this.mScrollTopEnabled = flag;
    }
    public void setScrollBottomEnabled(boolean flag)
    {
        this.mScrollBottomEnabled = flag;
    }
    public DampView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DampView);
        mScroll_child = array.getResourceId(R.styleable.DampView_scroll_child,View.NO_ID);
        mScrollLeftEnabled = array.getBoolean(R.styleable.DampView_scroll_left,false);
        mScrollTopEnabled = array.getBoolean(R.styleable.DampView_scroll_top,false);
        mScrollRightEnabled = array.getBoolean(R.styleable.DampView_scroll_right,false);
        mScrollBottomEnabled = array.getBoolean(R.styleable.DampView_scroll_bottom,false);
        array.recycle();
    }
    @Override
    public void addView(View child)
    {
        if (getChildCount() > 0)
            throw new IllegalStateException("DampView can host only one direct child");
        super.addView(child);
    }
    @Override
    public void addView(View child, int index)
    {
        if (getChildCount() > 0)
            throw new IllegalStateException("DampView can host only one direct child");
        super.addView(child, index);
    }
    @Override
    public void addView(View child, LayoutParams params)
    {
        if (getChildCount() > 0)
            throw new IllegalStateException("DampView can host only one direct child");
        super.addView(child, params);
    }
    @Override
    public void addView(View child, int index, LayoutParams params)
    {
        if (getChildCount() > 0)
            throw new IllegalStateException("DampView can host only one direct child");
        super.addView(child, index, params);
    }
    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        if (childView == null)
        {
            if (mScroll_child == View.NO_ID)
            {
                childView = getChildAt(0);
            }
            else
                childView = findViewById(mScroll_child);
            childView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }
    @Override
    protected void onLayout(boolean c, int l, int t, int r, int b)
    {
        if (c)
        {
            for (int i = 0; i < getChildCount(); i++)
            {
                View child = getChildAt(i);
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        }
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = ev.getRawX();
                y1 = ev.getRawY();
                mScroller.forceFinished(true);
                childView.scrollTo(0,0);
                break;
            case MotionEvent.ACTION_MOVE:
                x2 = ev.getRawX();
                y2 = ev.getRawY();
                int x3 = (int) (x2 - x1);
                int y3 = (int) (y2 - y1);
                if (Math.abs(x3) > Math.abs(y3))
                {
                    if (0 > x3 && mScrollLeftEnabled && !childView.canScrollHorizontally(1))
                        return true;
                    else if (0 < x3 && mScrollRightEnabled && !childView.canScrollHorizontally(-1))
                        return true;
                }
                else if (Math.abs(x3) < Math.abs(y3))
                {
                    if (0 > y3 && mScrollTopEnabled && !childView.canScrollVertically(1))
                        return true;
                    else if (0 < y3 && mScrollBottomEnabled && !childView.canScrollVertically(-1))
                        return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                int x3 = (int)(ev.getRawX() - x2);
                int y3 = (int)(ev.getRawY() - y2);
                int x4 = childView.getScrollX();
                int y4 = childView.getScrollY();
                if (mScrollTopEnabled && mScrollBottomEnabled)
                    childView.scrollBy(0,~y3/2);
                else
                {
                    if (mScrollTopEnabled)
                        setScrollTop(y3, y4);
                    else if (mScrollBottomEnabled)
                        setScrollBottom(y3, y4);
                }
                if (mScrollLeftEnabled && mScrollRightEnabled)
                    childView.scrollBy(~x3/2,0);
                else
                {
                    if (mScrollLeftEnabled)
                        setScrollLeft(x3, x4);
                    else if (mScrollRightEnabled)
                        setScrollRight(x3, x4);
                }
                x2 = ev.getRawX();
                y2 = ev.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                int startX = childView.getScrollX();
                int startY = childView.getScrollY();
                int dx = startX == 0 ? 0 : ~startX;
                int dy = startY == 0 ? 0 : ~startY;
                int xDuration = Math.abs(startX);
                int yDuration = Math.abs(startY);
                mScroller.startScroll(startX, startY, dx, dy, Math.max(xDuration, yDuration));
                invalidate();
                break;
        }
        return super.onTouchEvent(ev);
    }
    private void setScrollLeft(int x3, int x4)
    {
        if (!childView.canScrollHorizontally(1) && !mScrollRightEnabled)
        {
            if (0 >= x3 && 0 <= x4)
                childView.scrollBy(~x3/2,0);
            else if (~x3/2 + x4 < 0)
                childView.scrollTo(0,childView.getScrollY());
            else if (0 <= x3 && 0 <= x4)
                childView.scrollBy(~x3/2,0);
        }
    }
    private void setScrollRight(int x3, int x4)
    {
        if (!childView.canScrollHorizontally(-1))
        {
            if (0 <= x3 && 0 >= x4)
                childView.scrollBy(~x3/2,0);
            else if (~x3/2 + x4 > 0)
                childView.scrollTo(0,childView.getScrollY());
            else if (0 >= x3 && 0 >= x4)
                childView.scrollBy(~x3/2,0);
        }
    }
    private void setScrollTop(int y3, int y4)
    {
        if (!childView.canScrollVertically(1))
        {
            if (0 >= y3 && 0 <= y4)
                childView.scrollBy(0,~y3/2);
            else if (~y3/2 + y4 < 0)
                childView.scrollTo(childView.getScrollX(),0);
            else if (0 <= y3 && 0 <= y4)
                childView.scrollBy(0,~y3/2);
        }
    }
    private void setScrollBottom(int y3, int y4)
    {
        if (!childView.canScrollVertically(-1))
        {
            if (0 <= y3 && 0 >= y4)
                childView.scrollBy(0,~y3/2);
            else if (~y3/2 + y4 > 0)
                childView.scrollTo(childView.getScrollX(),0);
            else if (0 >= y3 && 0 >= y4)
                childView.scrollBy(0,~y3/2);
        }
    }
    @Override
    public void computeScroll()
    {
        super.computeScroll();
        if (mScroller.computeScrollOffset())
        {
            childView.scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            invalidate();
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0 ; i < getChildCount() ; i ++ )
        {
            measureChild(getChildAt(i),widthMeasureSpec,heightMeasureSpec);
        }
    }
}

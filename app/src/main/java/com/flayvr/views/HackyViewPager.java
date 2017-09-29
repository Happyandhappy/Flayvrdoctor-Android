package com.flayvr.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class HackyViewPager extends ViewPager
{

    public HackyViewPager(Context context)
    {
        super(context);
    }

    public HackyViewPager(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionevent)
    {
        boolean flag;
        try
        {
            flag = super.onInterceptTouchEvent(motionevent);
        }
        catch(Exception exception)
        {
            Log.e("hacky_view_pager", "Error on touch", exception);
            return false;
        }
        return flag;
    }
}

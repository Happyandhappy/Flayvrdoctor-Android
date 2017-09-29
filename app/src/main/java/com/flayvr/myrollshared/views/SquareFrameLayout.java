package com.flayvr.myrollshared.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class SquareFrameLayout extends FrameLayout
{

    public SquareFrameLayout(Context context)
    {
        super(context);
    }

    public SquareFrameLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public SquareFrameLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public SquareFrameLayout(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
    }

    protected void onMeasure(int i, int j)
    {
        super.onMeasure(i, i);
    }
}

package com.flayvr.myrollshared.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SquareRelativeLayout extends RelativeLayout
{

    public SquareRelativeLayout(Context context)
    {
        super(context);
    }

    public SquareRelativeLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public SquareRelativeLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public SquareRelativeLayout(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
    }

    protected void onMeasure(int i, int j)
    {
        super.onMeasure(i, i);
    }
}

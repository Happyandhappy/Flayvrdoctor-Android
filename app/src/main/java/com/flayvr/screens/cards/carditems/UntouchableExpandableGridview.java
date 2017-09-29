package com.flayvr.screens.cards.carditems;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.flayvr.views.ExpandableHeightGridView;

public class UntouchableExpandableGridview extends ExpandableHeightGridView
{

    public UntouchableExpandableGridview(Context context)
    {
        super(context);
    }

    public UntouchableExpandableGridview(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public UntouchableExpandableGridview(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public boolean dispatchTouchEvent(MotionEvent motionevent)
    {
        return false;
    }
}

package com.flayvr.myrollshared.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.flayvr.doctor.R;

public class ProportionalFrameLayoutByHeight extends FrameLayout
{

    private Float prop;

    public ProportionalFrameLayoutByHeight(Context context)
    {
        super(context);
    }

    public ProportionalFrameLayoutByHeight(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public ProportionalFrameLayoutByHeight(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        TypedArray typedarray;
        typedarray = context.getTheme().obtainStyledAttributes(attributeset, R.styleable.ThumbnailItemView, 0, 0);
        prop = Float.valueOf(1.0F / typedarray.getFloat(R.styleable.ThumbnailItemView_propoprtion, -1F));
        typedarray.recycle();
    }

    protected void onMeasure(int i, int j)
    {
        if(prop == null || prop.floatValue() < 0.0F)
        {
            super.onMeasure(i, j);
        } else {
            int k = android.view.View.MeasureSpec.getSize(j) - getPaddingTop() - getPaddingBottom();
            int l = (int)((float)k * prop.floatValue());
            setMeasuredDimension(resolveSize(l, i), k);
            super.onMeasure(android.view.View.MeasureSpec.makeMeasureSpec(l, MeasureSpec.EXACTLY), j);
        }
    }

    public void setProp(Float float1)
    {
        prop = float1;
    }
}

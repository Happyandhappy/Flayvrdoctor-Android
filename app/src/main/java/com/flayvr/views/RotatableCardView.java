package com.flayvr.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import com.nineoldandroids.view.ViewHelper;

public class RotatableCardView extends CardView
{

    public RotatableCardView(Context context)
    {
        super(context);
    }

    public RotatableCardView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public RotatableCardView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public float getDegree()
    {
        return ViewHelper.getRotation(this);
    }

    public float getDx()
    {
        return ViewHelper.getTranslationX(this);
    }

    public float getDy()
    {
        return ViewHelper.getTranslationY(this);
    }

    public void setOffsets(float f, float f1, float f2)
    {
        ViewHelper.setTranslationX(this, f1);
        ViewHelper.setTranslationY(this, f2);
        ViewHelper.setRotation(this, f);
    }
}

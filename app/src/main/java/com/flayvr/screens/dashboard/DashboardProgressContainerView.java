package com.flayvr.screens.dashboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.flayvr.doctor.R;
import com.nineoldandroids.view.ViewHelper;

public class DashboardProgressContainerView extends RelativeLayout
{

    DashboardProgressView progress1;
    DashboardProgressView progress2;

    public DashboardProgressContainerView(Context context)
    {
        super(context);
    }

    public DashboardProgressContainerView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public DashboardProgressContainerView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public DashboardProgressContainerView(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
    }

    protected void onFinishInflate()
    {
        super.onFinishInflate();
        progress1 = (DashboardProgressView)findViewById(R.id.progress_view);
        progress2 = (DashboardProgressView)findViewById(R.id.progress_view2);
    }

    public void setImageResource(int i)
    {
        progress1.setImageResource(i);
        progress2.setImageResource(i);
    }

    public void setProgress(float f)
    {
        if((double)f < 0.5D)
        {
            ViewHelper.setTranslationX(progress1, 0.0F);
            progress1.setProgress(f * 2.0F);
            ViewHelper.setTranslationX(progress2, -(getWidth() / 2 + progress1.getWidth() / 2));
        } else {
            float f1 = 2.0F * (f - 0.5F);
            ViewHelper.setTranslationX(progress1, f1 * (float)(getWidth() / 2 + progress1.getWidth() / 2));
            progress1.setProgress(1.0F);
            ViewHelper.setTranslationX(progress2, f1 * (float)(getWidth() / 2 + progress1.getWidth() / 2) - (float)(getWidth() / 2 + progress1.getWidth() / 2));
        }
    }
}

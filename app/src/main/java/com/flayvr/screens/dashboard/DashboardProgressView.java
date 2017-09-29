package com.flayvr.screens.dashboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.flayvr.doctor.R;

public class DashboardProgressView extends ImageView
{

    float progress;

    public DashboardProgressView(Context context)
    {
        super(context);
        progress = 0.0F;
    }

    public DashboardProgressView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        progress = 0.0F;
    }

    public DashboardProgressView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        progress = 0.0F;
    }

    public DashboardProgressView(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
        progress = 0.0F;
    }

    public void draw(Canvas canvas)
    {
        Paint paint = new Paint();
        if(isInEditMode())
        {
            progress = 0.66F;
        }
        paint.setColor(getResources().getColor(R.color.material_blue_grey_800));
        canvas.drawRect(new RectF(0.0F, 0.0F, getWidth(), getHeight()), paint);
        paint.setColor(getResources().getColor(R.color.md_yellow_200));
        canvas.drawRect(new RectF(0.0F, (float)getHeight() - progress * (float)getHeight(), getWidth(), getHeight()), paint);
        super.draw(canvas);
    }

    public void setProgress(float f)
    {
        progress = f;
        invalidate();
    }
}

package com.flayvr.screens.dashboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.utils.GeneralUtils;

public class PieChartView extends View
{

    private int color;
    private int emptyColor;
    private float percentage;
    private float width;

    public PieChartView(Context context)
    {
        super(context);
        color = getResources().getColor(R.color.md_yellow_400);
        emptyColor = getResources().getColor(R.color.md_blue_grey_400);
        width = GeneralUtils.pxFromDp(getContext(), 7F);
    }

    public PieChartView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        color = getResources().getColor(R.color.md_yellow_400);
        emptyColor = getResources().getColor(R.color.md_blue_grey_400);
        width = GeneralUtils.pxFromDp(getContext(), 7F);
    }

    public PieChartView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        color = getResources().getColor(R.color.md_yellow_400);
        emptyColor = getResources().getColor(R.color.md_blue_grey_400);
        width = GeneralUtils.pxFromDp(getContext(), 7F);
    }

    public PieChartView(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
        color = getResources().getColor(R.color.md_yellow_400);
        emptyColor = getResources().getColor(R.color.md_blue_grey_400);
        width = GeneralUtils.pxFromDp(getContext(), 7F);
    }

    public float getPercentage()
    {
        return percentage;
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(isInEditMode())
        {
            percentage = 0.37F;
            color = R.color.md_grey_100;
            width = GeneralUtils.pxFromDp(getContext(), 7F);
        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.white));
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, paint);
        paint.clearShadowLayer();
        paint.setColor(getResources().getColor(R.color.md_grey_700));
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (float)(getWidth() / 2) - width, paint);
        paint.setStyle(android.graphics.Paint.Style.FILL);
        RectF rectf = new RectF(width, width, (float)getWidth() - width, (float)getHeight() - width);
        paint.setColor(getResources().getColor(R.color.md_light_blue_500));
        canvas.drawArc(rectf, -90F, 360F * percentage, true, paint);
    }

    public void setColor(int i)
    {
        color = i;
    }

    public void setEmptyColor(int i)
    {
        emptyColor = i;
    }

    public void setPercentage(float f)
    {
        percentage = f;
        invalidate();
    }

    public void setWidth(float f)
    {
        width = f;
    }
}

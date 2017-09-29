package com.flayvr.screens.dashboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.utils.GeneralUtils;

public class DialChartView extends View
{

    private int color;
    private int emptyColor;
    private float percentage;
    private float width;

    public DialChartView(Context context)
    {
        super(context);
        color = getResources().getColor(R.color.md_yellow_400);
        emptyColor = getResources().getColor(R.color.md_blue_grey_400);
        width = GeneralUtils.pxFromDp(getContext(), 7F);
    }

    public DialChartView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        color = getResources().getColor(R.color.md_yellow_400);
        emptyColor = getResources().getColor(R.color.md_blue_grey_400);
        width = GeneralUtils.pxFromDp(getContext(), 7F);
    }

    public DialChartView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        color = getResources().getColor(R.color.md_yellow_400);
        emptyColor = getResources().getColor(R.color.md_blue_grey_400);
        width = GeneralUtils.pxFromDp(getContext(), 7F);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DialChartView(Context context, AttributeSet attributeset, int i, int j)
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
            percentage = 0.75F;
            color = getResources().getColor(R.color.md_grey_100);
            width = 20F;
        }
        RectF rectf = new RectF(width, width, (float)getWidth() - width, (float)getHeight() - width);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(android.graphics.Paint.Style.STROKE);
        paint.setStrokeWidth(width);
        paint.setColor(emptyColor);
        canvas.drawArc(rectf, -225F, 270F, false, paint);
        paint.setColor(color);
        canvas.drawArc(rectf, -225F, 270F * percentage, false, paint);
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

package com.flayvr.screens.dashboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.flayvr.doctor.R;

public class DistributionChartView extends View
{

    private int colors[];
    private float values[];

    public DistributionChartView(Context context)
    {
        super(context);
    }

    public DistributionChartView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public DistributionChartView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public DistributionChartView(Context context, AttributeSet attributeset, int i, int j)
    {
        super(context, attributeset, i, j);
    }

    public float[] getDistributions()
    {
        return values;
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Paint paint = new Paint();
        if(isInEditMode())
        {
            values = (new float[] {
                20F, 20F, 80F
            });
            colors = (new int[] {
                R.color.md_red_400, R.color.md_green_400, R.color.md_blue_400, R.color.md_yellow_800
            });
        }
        if(values != null)
        {
            int i = 0;
            float f;
            float f4;
            for(f = 0.0F; i < values.length; f = f4)
            {
                f4 = f + values[i];
                i++;
            }

            int j = 0;
            float f3;
            for(float f1 = 0.0F; j < values.length; f1 = f3)
            {
                paint.setColor(getResources().getColor(colors[j]));
                float f2 = (values[j] / f) * (float)getWidth();
                canvas.drawRect(f1, 0.0F, f1 + f2, getHeight(), paint);
                f3 = f1 + f2;
                j++;
            }

        }
    }

    public void setColors(int ai[])
    {
        colors = ai;
        invalidate();
    }

    public void setDistributions(float af[])
    {
        values = af;
        invalidate();
    }
}

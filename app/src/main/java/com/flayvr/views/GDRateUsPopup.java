package com.flayvr.views;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.views.RateUsPopup;
import com.flayvr.util.GalleryDoctorUtils;

public class GDRateUsPopup extends RateUsPopup
{

    public GDRateUsPopup(Context context)
    {
        super(context);
    }

    public GDRateUsPopup(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public GDRateUsPopup(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    protected TextView getAction()
    {
        return (TextView)findViewById(R.id.action);
    }

    protected TextView getResultText()
    {
        return (TextView)findViewById(R.id.result_text);
    }

    protected LinearLayout getStars()
    {
        return (LinearLayout)findViewById(R.id.stars);
    }

    protected Toolbar getToolbar()
    {
        return (Toolbar)findViewById(R.id.toolbar);
    }

    protected void sendFeedback()
    {
        GalleryDoctorUtils.sendFeedback(getContext());
    }
}

package com.flayvr.screens.cards.carditems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.application.FlayvrApplication;

public abstract class GalleryDoctorBaseCard extends FrameLayout
{

    public static final int BAD_PHOTOS = 0;
    public static final int CROSS_PROMOTION_CARD = 3;
    public static final int DUPLICATE_PHOTOS = 2;
    public static final int PHOTOS_FOR_REVIEW = 1;
    public static final int SCREENSHOTS_CARD = 5;
    public static final int VIDEOS_CARD = 4;
    public static final int WHATSAPP_CARD = 6;
    static LayoutInflater inflater = (LayoutInflater)FlayvrApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    protected TextView action;
    private View bottom;
    protected TextView subtitle;
    protected TextView title;
    private View top;
    protected View view;

    public GalleryDoctorBaseCard(Context context)
    {
        super(context);
        init();
    }

    public abstract void bindView();

    abstract int getLayout();

    public abstract int getType();

    protected void init()
    {
        setLayoutParams(new android.view.ViewGroup.LayoutParams(-2, -2));
        if(inflater != null)
        {
            view = inflater.inflate(getLayout(), this);
            title = (TextView)view.findViewById(R.id.title);
            subtitle = (TextView)view.findViewById(R.id.subtitle);
            action = (TextView)view.findViewById(R.id.bottom);
        }
        top = findViewById(R.id.top);
        bottom = findViewById(R.id.bottom);
    }

    public abstract int numberOfItems();

    public abstract void refreshCard(com.flayvr.util.GalleryDoctorStatsUtils.MediaItemStat mediaitemstat);

    public void setAction(android.view.View.OnClickListener onclicklistener)
    {
        if(top != null)
        {
            top.setOnClickListener(onclicklistener);
        }
        if(bottom != null)
        {
            bottom.setOnClickListener(onclicklistener);
        }
    }

}

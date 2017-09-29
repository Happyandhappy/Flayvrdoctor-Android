package com.flayvr.screens.cards.carditems;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.views.itemview.MediaItemView;
import com.nineoldandroids.view.ViewHelper;

public class GalleryDoctorCardMediaItemView extends MediaItemView
{

    protected View overlay;
    private boolean shouldShowOverlay;

    public GalleryDoctorCardMediaItemView(Context context)
    {
        super(context);
        shouldShowOverlay = false;
    }

    public GalleryDoctorCardMediaItemView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        shouldShowOverlay = false;
    }

    public GalleryDoctorCardMediaItemView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        shouldShowOverlay = false;
    }

    public GalleryDoctorCardMediaItemView(Context context, MediaItem mediaitem)
    {
        super(context, mediaitem);
        shouldShowOverlay = false;
    }

    protected int getLayout()
    {
        return R.layout.gallery_doctor_card_media_item_view;
    }

    protected void init(Context context)
    {
        super.init(context);
        overlay = view.findViewById(R.id.overlay);
        overlay.setVisibility(View.GONE);
        ViewHelper.setScaleX(videoFrame, 0.7F);
        ViewHelper.setScaleY(videoFrame, 0.7F);
    }

    public boolean isShouldShowOverlay()
    {
        return shouldShowOverlay;
    }

    public void setOverlayExtraPhotos(int i)
    {
        ((TextView)findViewById(R.id.extraPhotos)).setText((new StringBuilder()).append("+").append(i).toString());
    }

    public void setShouldShowOverlay(boolean flag)
    {
        shouldShowOverlay = flag;
        if(flag)
        {
            overlay.setVisibility(View.VISIBLE);
            return;
        } else
        {
            overlay.setVisibility(View.GONE);
            return;
        }
    }
}

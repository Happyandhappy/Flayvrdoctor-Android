package com.flayvr.screens.cards.carditems;

import android.content.Context;
import android.widget.FrameLayout;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.views.itemview.MediaItemView;

public abstract class ItemReviewCard extends GalleryDoctorBaseCard
{

    protected final MediaItemView img1 = (MediaItemView)findViewById(R.id.img1);
    protected final MediaItemView img2 = (MediaItemView)findViewById(R.id.img2);

    public ItemReviewCard(Context context)
    {
        super(context);
        img1.setProp(Float.valueOf(1.0F));
        img2.setProp(Float.valueOf(1.0F));
        FrameLayout framelayout = (FrameLayout)img1.findViewById(R.id.frame);
        FrameLayout framelayout1 = (FrameLayout)img2.findViewById(R.id.frame);
        framelayout.setForeground(null);
        framelayout1.setForeground(null);
    }

    int getLayout()
    {
        return R.layout.gallery_doctor_card_review_photos;
    }
}

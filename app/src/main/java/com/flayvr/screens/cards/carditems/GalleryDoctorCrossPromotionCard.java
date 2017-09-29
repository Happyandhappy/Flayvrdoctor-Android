package com.flayvr.screens.cards.carditems;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import com.flayvr.application.PreferencesManager;
import com.flayvr.doctor.R;

public class GalleryDoctorCrossPromotionCard extends GalleryDoctorBaseCard
{

    public GalleryDoctorCrossPromotionCard(Context context, com.flayvr.util.GalleryDoctorStatsUtils.MediaItemStat mediaitemstat)
    {
        super(context);
        findViewById(R.id.download).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try
                {
                    getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.flayvr.flayvr&referrer=utm_source=gd%26utm_medium=myroll_promotion")));
                }
                catch(ActivityNotFoundException activitynotfoundexception)
                {
                    getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.flayvr.flayvr&referrer=utm_source=gd%26utm_medium=myroll_promotion")));
                }
                PreferencesManager.setCrossPromotionCardShown();
                ((ViewGroup)getParent()).removeView(GalleryDoctorCrossPromotionCard.this);
            }
        });
        findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                PreferencesManager.setCrossPromotionCardShown();
                ((ViewGroup)getParent()).removeView(GalleryDoctorCrossPromotionCard.this);
            }
        });
    }

    public void bindView()
    {
    }

    int getLayout()
    {
        return R.layout.gallery_doctor_still_in_beta_card;
    }

    public int getType()
    {
        return 3;
    }

    public int numberOfItems()
    {
        return 0x7fffffff;
    }

    public void refreshCard(com.flayvr.util.GalleryDoctorStatsUtils.MediaItemStat mediaitemstat)
    {
    }
}

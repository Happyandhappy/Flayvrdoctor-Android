package com.flayvr.screens.whatsapp;

import com.flayvr.screens.ForReviewActivity;
import com.flayvr.screens.ForReviewFragment;

public class WhatsappReviewActivity extends ForReviewActivity
{

    public WhatsappReviewActivity()
    {
    }

    protected com.flayvr.util.GalleryDoctorAnalyticsSender.DoctorActionType getAction()
    {
        return com.flayvr.util.GalleryDoctorAnalyticsSender.DoctorActionType.CLEAN_WHATSAPP_FOR_REVIEW;
    }

    protected ForReviewFragment getForReviewFragment(int i)
    {
        return WhatsappReviewFragment.newInstance(i);
    }

    protected String getName()
    {
        return "whatsapp review";
    }
}

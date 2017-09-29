package com.flayvr.screens.review;

import com.flayvr.screens.ForReviewActivity;
import com.flayvr.screens.ForReviewFragment;

public class PhotosForReviewActivity extends ForReviewActivity
{

    public PhotosForReviewActivity()
    {
    }

    protected com.flayvr.util.GalleryDoctorAnalyticsSender.DoctorActionType getAction()
    {
        return com.flayvr.util.GalleryDoctorAnalyticsSender.DoctorActionType.CLEAN_PHOTOS_FOR_REVIEW;
    }

    protected ForReviewFragment getForReviewFragment(int i)
    {
        return PhotosForReviewFragment.newInstance(i);
    }

    protected String getName()
    {
        return "photos for review";
    }
}

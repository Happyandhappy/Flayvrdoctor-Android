package com.flayvr.screens.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.utils.GeneralUtils;
import com.flayvr.screens.cards.GalleryDoctorCardsActivity;
import com.flayvr.views.StyledProgressBarView;

public class DashboardBarsView extends FrameLayout
{

    private StyledProgressBarView badPhotos;
    private StyledProgressBarView photosForReview;
    private StyledProgressBarView screenshots;
    private StyledProgressBarView similarPhotos;
    private StyledProgressBarView videos;
    private StyledProgressBarView whatsapp;

    public DashboardBarsView(Context context)
    {
        super(context);
    }

    public DashboardBarsView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public DashboardBarsView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    protected void onFinishInflate()
    {
        super.onFinishInflate();
        badPhotos = (StyledProgressBarView)findViewById(R.id.bad_photos);
        similarPhotos = (StyledProgressBarView)findViewById(R.id.similar_photos);
        photosForReview = (StyledProgressBarView)findViewById(R.id.photos_for_review);
        whatsapp = (StyledProgressBarView)findViewById(R.id.whatsapp_photos);
        screenshots = (StyledProgressBarView)findViewById(R.id.screenshots);
        videos = (StyledProgressBarView)findViewById(R.id.long_videos);
    }

    public void setupBar(StyledProgressBarView styledprogressbarview, final com.flayvr.screens.cards.GalleryDoctorCardsActivity.CARDS_TYPE type, long l, int i, long l1, 
            int j, int k, float f)
    {
        if(l > 0L)
        {
            styledprogressbarview.setDone(false);
            StringBuilder stringbuilder = new StringBuilder();
            String s = getResources().getString(j);
            Object aobj[] = new Object[1];
            aobj[0] = GeneralUtils.humanReadableNumber(l);
            styledprogressbarview.setText(stringbuilder.append(String.format(s, aobj)).append(" | ").append(GeneralUtils.humanReadableByteCount(l1, false)).toString());
            styledprogressbarview.animateProgress(Math.min(Math.max((1.0F * (float)l1) / f, 0.1F), 1.0F));
            final Intent cards = new Intent(getContext(), GalleryDoctorCardsActivity.class);
            cards.putExtra("ITEMS_SOURCE", i);
            cards.putExtra("CARD_SELECTED", type);
            styledprogressbarview.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    getContext().startActivity(cards);
                }
            });
            return;
        } else
        {
            styledprogressbarview.setDone(true);
            styledprogressbarview.setDoneText(getResources().getString(k));
            styledprogressbarview.animateProgress(0.0F);
            styledprogressbarview.setOnClickListener(null);
            return;
        }
    }

    public void updateValues(com.flayvr.util.GalleryDoctorStatsUtils.MediaItemStat mediaitemstat)
    {
        if(!isInEditMode())
        {
            float f = mediaitemstat.getTotalPhotosSizeToClean();
            setupBar(badPhotos, com.flayvr.screens.cards.GalleryDoctorCardsActivity.CARDS_TYPE.BAD, mediaitemstat.getBadPhotoCount(), mediaitemstat.getSource(), mediaitemstat.getBadPhotoSize(), R.string.gallery_doctor_bad_photos_num, R.string.bad_photos_bar_done, f);
            setupBar(similarPhotos, com.flayvr.screens.cards.GalleryDoctorCardsActivity.CARDS_TYPE.DUPLICATE, mediaitemstat.getDuplicatePhotoCount(), mediaitemstat.getSource(), mediaitemstat.getDuplicatePhotoSize(), R.string.gallery_doctor_similar_photos, R.string.similar_photos_bar_done, f);
            setupBar(photosForReview, com.flayvr.screens.cards.GalleryDoctorCardsActivity.CARDS_TYPE.FOR_REVIEW, mediaitemstat.getForReviewCount(), mediaitemstat.getSource(), mediaitemstat.getForReviewSize(), R.string.gallery_doctor_dashboard_photos_for_review, R.string.for_review_bar_done, f);
            setupBar(whatsapp, com.flayvr.screens.cards.GalleryDoctorCardsActivity.CARDS_TYPE.WHATSAPP, mediaitemstat.getWhatsappPhotosCount(), mediaitemstat.getSource(), mediaitemstat.getWhatsappPhotosSize(), R.string.whatsapp_review_card_heading, R.string.whatsapp_bar_done, f);
            setupBar(screenshots, com.flayvr.screens.cards.GalleryDoctorCardsActivity.CARDS_TYPE.SCREENSHOTS, mediaitemstat.getScreenshotsCount(), mediaitemstat.getSource(), mediaitemstat.getScreenshotsSize(), R.string.screenshots_card_heading, R.string.screenshots_bar_done, f);
            setupBar(videos, com.flayvr.screens.cards.GalleryDoctorCardsActivity.CARDS_TYPE.VIDEOS, mediaitemstat.getTotalVideos(), mediaitemstat.getSource(), mediaitemstat.getSizeOfVideos(), R.string.gallery_doctor_videos_card_heading, R.string.long_videos_bar_done, f);
        }
    }
}

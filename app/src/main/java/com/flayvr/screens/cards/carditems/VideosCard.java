package com.flayvr.screens.cards.carditems;

import android.content.Context;
import android.content.res.Resources;
import android.widget.TextView;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.utils.GeneralUtils;
import com.flayvr.util.GalleryDoctorDBHelper;
import com.flayvr.views.ExpandableHeightGridView;

public class VideosCard extends ItemListCard
{

    private GridViewMediaItemAdapter adapter;
    private com.flayvr.util.GalleryDoctorStatsUtils.MediaItemStat stat;

    public VideosCard(Context context, com.flayvr.util.GalleryDoctorStatsUtils.MediaItemStat mediaitemstat)
    {
        super(context);
        refreshCard(mediaitemstat);
    }

    public void bindView()
    {
        TextView textview = title;
        String s = getResources().getString(R.string.gallery_doctor_videos_card_heading);
        Object aobj[] = new Object[1];
        aobj[0] = GeneralUtils.humanReadableNumber(stat.getTotalVideos());
        textview.setText(String.format(s, aobj));
        TextView textview1 = subtitle;
        String s1 = getResources().getString(R.string.gallery_doctor_bad_card_text);
        Object aobj1[] = new Object[1];
        aobj1[0] = GeneralUtils.humanReadableByteCount(stat.getSizeOfVideos(), true);
        textview1.setText(String.format(s1, aobj1));
        action.setText(getResources().getString(R.string.gallery_doctor_review_card_cta));
        mGrid.setAdapter(adapter);
    }

    public int getType()
    {
        return 4;
    }

    public int numberOfItems()
    {
        return (int)stat.getTotalVideos();
    }

    public void refreshCard(com.flayvr.util.GalleryDoctorStatsUtils.MediaItemStat mediaitemstat)
    {
        stat = mediaitemstat;
        java.util.List list = GalleryDoctorDBHelper.getFirstVideos(16, mediaitemstat.getSource());
        adapter = new GridViewMediaItemAdapter(getContext(), list, 15, (int)mediaitemstat.getTotalVideos());
    }
}

package com.flayvr.screens.cards.carditems;

import android.content.Context;
import android.content.res.Resources;
import android.widget.TextView;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.imageloading.AndroidImagesUtils;
import com.flayvr.myrollshared.utils.GeneralUtils;
import com.flayvr.util.GalleryDoctorDBHelper;
import java.util.List;

public class WhatsappReviewCard extends ItemReviewCard
{

    private com.flayvr.util.GalleryDoctorStatsUtils.MediaItemStat mediaItemStat;
    private MediaItem photo1;
    private MediaItem photo2;

    public WhatsappReviewCard(Context context, com.flayvr.util.GalleryDoctorStatsUtils.MediaItemStat mediaitemstat)
    {
        super(context);
        refreshCard(mediaitemstat);
    }

    public void bindView()
    {
        TextView textview = title;
        String s = getResources().getString(R.string.whatsapp_review_card_heading);
        Object aobj[] = new Object[1];
        aobj[0] = GeneralUtils.humanReadableNumber(mediaItemStat.getWhatsappPhotosCount());
        textview.setText(String.format(s, aobj));
        TextView textview1 = subtitle;
        String s1 = getResources().getString(R.string.gallery_doctor_review_card_text);
        Object aobj1[] = new Object[1];
        aobj1[0] = GeneralUtils.humanReadableByteCount(mediaItemStat.getWhatsappPhotosSize(), true);
        textview1.setText(String.format(s1, aobj1));
        action.setText(getResources().getString(R.string.gallery_doctor_review_card_cta));
        AndroidImagesUtils.getBitmapForItem(img1, photo1);
        AndroidImagesUtils.getBitmapForItem(img2, photo2);
    }

    public int getType()
    {
        return 6;
    }

    public int numberOfItems()
    {
        return (int)mediaItemStat.getWhatsappPhotosCount();
    }

    public void refreshCard(com.flayvr.util.GalleryDoctorStatsUtils.MediaItemStat mediaitemstat)
    {
        mediaItemStat = mediaitemstat;
        List list = GalleryDoctorDBHelper.getFirstWhatsappPhotos(2, mediaitemstat.getSource());
        if(list.size() > 0)
        {
            photo1 = (MediaItem)list.get(0);
            if(list.size() > 1)
            {
                photo2 = (MediaItem)list.get(1);
            }
        }
    }
}

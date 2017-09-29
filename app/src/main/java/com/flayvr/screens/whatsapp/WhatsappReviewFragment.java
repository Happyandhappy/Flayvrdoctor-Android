package com.flayvr.screens.whatsapp;

import android.os.Bundle;
import com.flayvr.screens.ForReviewFragment;
import com.flayvr.util.GalleryDoctorDBHelper;
import java.util.List;

public class WhatsappReviewFragment extends ForReviewFragment
{

    public WhatsappReviewFragment()
    {
    }

    public static ForReviewFragment newInstance(int i)
    {
        WhatsappReviewFragment whatsappreviewfragment = new WhatsappReviewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("SOURCE", i);
        whatsappreviewfragment.setArguments(bundle);
        return whatsappreviewfragment;
    }

    protected long getItemCount(int i)
    {
        return GalleryDoctorDBHelper.getWhatsappPhotosCount(i);
    }

    protected List getItemList(int i)
    {
        return GalleryDoctorDBHelper.getWhatsappPhotosLazy(i);
    }
}

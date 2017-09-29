package com.flayvr.services;

import android.app.IntentService;
import android.app.Service;
import android.content.res.Resources;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.services.AlgorithmsServiceProgress;
import com.flayvr.screens.dashboard.GalleryDoctorDashboardActivity;

public class GDServiceProgress extends AlgorithmsServiceProgress
{

    public GDServiceProgress(IntentService intentservice, int i)
    {
        super(intentservice, i);
    }

    public Class destinationActivity()
    {
        return GalleryDoctorDashboardActivity.class;
    }

    public String getContentText()
    {
        return service.getResources().getString(R.string.gallery_doctor_analyzing_gallery);
    }

    public String getContentTitle()
    {
        Resources resources = service.getResources();
        int i;
        if(source == 1)
        {
            i = R.string.gallery_doctor_name;
        } else
        {
            i = R.string.analyzing_cloud_title;
        }
        return resources.getString(i);
    }

    public int getSmallIcon()
    {
        return R.drawable.notif_app_icon;
    }
}

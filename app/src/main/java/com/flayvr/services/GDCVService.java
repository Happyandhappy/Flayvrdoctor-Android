package com.flayvr.services;

import android.content.Intent;
import com.flayvr.application.PreferencesManager;
import com.flayvr.myrollshared.cloud.PicasaSessionManager;
import com.flayvr.myrollshared.managers.GalleryDoctorServicesProgress;
import com.flayvr.myrollshared.services.CVService;
import com.flayvr.myrollshared.utils.IntentActions;
import java.util.ArrayList;
import java.util.List;

public class GDCVService extends CVService
{

    private GDServiceProgress serviceProgress;

    public GDCVService()
    {
        serviceProgress = new GDServiceProgress(this, getSource());
    }

    public static List getIntentFilter()
    {
        ArrayList arraylist = new ArrayList();
        arraylist.add(IntentActions.ACTION_GALLERY_BUILDER_NO_CHANGE);
        arraylist.add(IntentActions.ACTION_NEW_MEDIA);
        return arraylist;
    }

    private int getSource()
    {
        return !PicasaSessionManager.getInstance().hasUser() ? 1 : 2;
    }

    public void onCVFailed()
    {
        GalleryDoctorServicesProgress.cvServiceFinished(getSource(), true);
        sendBroadcast(new Intent(IntentActions.ACTION_CV_FINISHED));
    }

    public void onFinish()
    {
        GalleryDoctorServicesProgress.cvServiceFinished(getSource(), true);
        serviceProgress.stopNotification();
    }

    public void onStart()
    {
        if(PreferencesManager.getAnalysisStartTime() == -1L)
        {
            PreferencesManager.setAnalysisStartTime(System.currentTimeMillis());
        }
        if(!GalleryDoctorServicesProgress.didCVServiceFinish(getSource()))
        {
            serviceProgress.startWithNotification(true);
        }
    }

    public void onUpdate(float f)
    {
        GalleryDoctorServicesProgress.setCvServiceProgress(getSource(), f);
        if(!GalleryDoctorServicesProgress.didCVServiceFinish(getSource()))
        {
            serviceProgress.updateProgress();
        }
    }

    protected boolean shouldDownloadRemoteItems()
    {
        return true;
    }
}

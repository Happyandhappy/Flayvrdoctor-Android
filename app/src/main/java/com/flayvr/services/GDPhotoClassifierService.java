package com.flayvr.services;

import com.flayvr.application.PreferencesManager;
import com.flayvr.myrollshared.cloud.PicasaSessionManager;
import com.flayvr.myrollshared.managers.GalleryDoctorServicesProgress;
import com.flayvr.myrollshared.services.PhotoClassifierService;
import com.flayvr.util.GalleryDoctorAnalyticsSender;
import java.util.List;

public class GDPhotoClassifierService extends PhotoClassifierService
{

    private GDServiceProgress serviceProgress;

    public GDPhotoClassifierService()
    {
        serviceProgress = new GDServiceProgress(this, getSource());
    }

    private int getSource()
    {
        return !PicasaSessionManager.getInstance().hasUser() ? 1 : 2;
    }

    private void sendClassifiedPhotosData(List list)
    {
        if(!PreferencesManager.didSendClassifiedPhotosData())
        {
            PreferencesManager.sentClassifiedPhotosData();
            GalleryDoctorAnalyticsSender.sendClassifiedPhotosDataAsync(list);
        }
    }

    public void onFinish(List list)
    {
        GalleryDoctorServicesProgress.classifierServiceFinished(getSource(), true);
        sendClassifiedPhotosData(list);
        serviceProgress.stopNotification();
    }

    public void onStart()
    {
        if(!GalleryDoctorServicesProgress.didClassifierServiceFinish(getSource()))
        {
            serviceProgress.startWithNotification(true);
        }
    }

    public void onUpdate(int i, float f)
    {
        GalleryDoctorServicesProgress.setClassifierServiceProgress(i, f);
        if(!GalleryDoctorServicesProgress.didClassifierServiceFinish(i))
        {
            serviceProgress.updateProgress();
        }
    }

    public void waitIfNeeded()
    {
        if(serviceProgress.status == com.flayvr.myrollshared.services.ServiceProgress.STATUS.PAUSE)
        {
            synchronized(serviceProgress.pauseLock)
            {
                try {
                    serviceProgress.pauseLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

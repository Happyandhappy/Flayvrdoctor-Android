package com.flayvr.myrollshared.services;

import android.app.IntentService;
import com.flayvr.myrollshared.managers.GalleryDoctorServicesProgress;

public abstract class AlgorithmsServiceProgress extends ServiceProgress
{

    private static final int ALGORITHMS_NOTIFICATION_ID = 4000;
    protected final int source;

    public AlgorithmsServiceProgress(IntentService intentservice, int i)
    {
        super(intentservice, 4000);
        source = i;
        progress = GalleryDoctorServicesProgress.getTotalPrecentage(i);
    }

    public void updateProgress()
    {
        updateProgress(GalleryDoctorServicesProgress.getTotalPrecentage(source));
    }
}

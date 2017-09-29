package com.flayvr.myrollshared.managers;

import java.io.Serializable;

public abstract class AppSessionInfo
    implements Serializable
{

    public boolean firstSession;
    public int imageLoadingStatisticsItems;
    public int imageLoadingStatisticsTime;
    public int sessionTime;

    public AppSessionInfo()
    {
        sessionTime = 0;
        firstSession = false;
        imageLoadingStatisticsItems = 0;
        imageLoadingStatisticsTime = 0;
    }

    public abstract void sendData();
}

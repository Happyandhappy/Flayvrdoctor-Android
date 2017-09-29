package com.flayvr.managers;

import com.flayvr.myrollshared.managers.AppSessionInfo;
import com.flayvr.myrollshared.managers.AppSessionInfoManager;

public class GalleryDoctortSessionInfoManager extends AppSessionInfoManager
{

    public GalleryDoctortSessionInfoManager()
    {
    }

    protected GalleryDoctorAppSessionInfo createAppSessionInfo()
    {
        return new GalleryDoctorAppSessionInfo();
    }

    public void setSessionValues()
    {
    }
}

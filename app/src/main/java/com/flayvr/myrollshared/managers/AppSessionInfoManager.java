package com.flayvr.myrollshared.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.flayvr.myrollshared.application.AppConfiguration;
import com.flayvr.myrollshared.application.FlayvrApplication;
import java.util.Date;

public abstract class AppSessionInfoManager
{

    private static final String IS_FIRST_SESSION_KEY = "IS_FIRST_SESSION_KEY";
    private static final String TAG = AppSessionInfoManager.class.getSimpleName();
    protected AppSessionInfo sessionInfo;
    private Date sessionStart;

    public AppSessionInfoManager()
    {
        sessionInfo = createAppSessionInfo();
    }

    protected abstract AppSessionInfo createAppSessionInfo();

    public void endSession()
    {
        if(sessionStart != null)
        {
            sessionInfo.sessionTime = (int)(((new Date()).getTime() - sessionStart.getTime()) / 1000L);
        } else {
            Log.w(TAG, "sessionStart is null");
            sessionInfo.sessionTime = 0;
        }
    }

    public AppSessionInfo getSessionInfo()
    {
        return sessionInfo;
    }

    public void imageLoadingStatistics(int i)
    {
        AppSessionInfo appsessioninfo = sessionInfo;
        appsessioninfo.imageLoadingStatisticsItems = 1 + appsessioninfo.imageLoadingStatisticsItems;
        AppSessionInfo appsessioninfo1 = sessionInfo;
        appsessioninfo1.imageLoadingStatisticsTime = i + appsessioninfo1.imageLoadingStatisticsTime;
    }

    public void reset()
    {
        sessionInfo = createAppSessionInfo();
        sessionStart = null;
    }

    public abstract void setSessionValues();

    public void startSession()
    {
        if(sessionStart == null)
        {
            sessionInfo = createAppSessionInfo();
            sessionStart = new Date();
            SharedPreferences sharedpreferences = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0);
            if(!sharedpreferences.contains("IS_FIRST_SESSION_KEY"))
            {
                sessionInfo.firstSession = true;
                android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("IS_FIRST_SESSION_KEY", false);
                editor.commit();
            }
        }
    }
}

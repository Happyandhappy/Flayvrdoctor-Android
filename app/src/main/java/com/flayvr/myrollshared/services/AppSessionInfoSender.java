package com.flayvr.myrollshared.services;

import android.app.IntentService;
import android.content.Intent;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.managers.AppSessionInfo;
import com.flayvr.myrollshared.managers.AppSessionInfoManager;

public class AppSessionInfoSender extends IntentService
{

    public static final String SESSION_INFO_KEY = "SESSION_INFO_KEY";
    private static final String TAG = AppSessionInfoSender.class.getSimpleName();

    public AppSessionInfoSender()
    {
        super("AppSessionInfoSender");
    }

    protected void onHandleIntent(Intent intent)
    {
        if(intent != null && intent.hasExtra("SESSION_INFO_KEY"))
        {
            AppSessionInfo appsessioninfo = (AppSessionInfo)intent.getSerializableExtra("SESSION_INFO_KEY");
            if(appsessioninfo != null)
                appsessioninfo.sendData();
        } else {
            StringBuilder stringbuilder = (new StringBuilder()).append("intent null? ");
            boolean flag;
            if(intent == null)
                flag = true;
            else
                flag = false;
        }
        FlayvrApplication.getAppSessionInfoManager().reset();
    }

}

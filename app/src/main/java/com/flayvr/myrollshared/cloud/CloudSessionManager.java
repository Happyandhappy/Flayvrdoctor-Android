package com.flayvr.myrollshared.cloud;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.events.PicasaSessionChangedEvent;
import com.flayvr.myrollshared.utils.IntentActions;
import com.flayvr.myrollshared.utils.SharedPreferencesManager;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.gdata.client.photos.PicasawebService;
import de.greenrobot.event.EventBus;

public class CloudSessionManager
{

    private static final String TAG = CloudSessionManager.class.getSimpleName();
    private static CloudSessionManager instance;
    private PicasawebService picasaService;
    private String picasaUserId;

    private CloudSessionManager()
    {
    }

    private String createAppId()
    {
        int i = FlayvrApplication.getAppContext().getApplicationInfo().labelRes;
        String s = FlayvrApplication.getAppContext().getString(i);
        return (new StringBuilder()).append(s).append("-").append("1.0").toString();
    }

    public static CloudSessionManager getInstance()
    {
        if(instance == null)
        {
            instance = new CloudSessionManager();
        }
        return instance;
    }

    private String picasaGetUserId()
    {
        if(picasaUserId == null)
        {
            picasaUserId = SharedPreferencesManager.getPicasaUserAccount();
        }
        return picasaUserId;
    }

    private void picasaSetUserId(String s)
    {
        SharedPreferencesManager.setPicasaUserAccount(s);
        picasaUserId = s;
    }

    public PicasawebService getPicasaService()
    {
        return picasaService;
    }

    public String getPicasaUserId()
    {
        return picasaUserId;
    }

    public boolean picasaIsLogedin()
    {
        return picasaService != null;
    }

    public void picasaLogin(String s, String s1)
    {
        picasaService = new PicasawebService(createAppId());
        picasaService.setUserToken(s1);
        picasaSetUserId(s);
        EventBus.getDefault().post(new PicasaSessionChangedEvent());
        Intent intent = new Intent(IntentActions.ACTION_PICASA_SESSIOM_CHANGED);
        FlayvrApplication.getAppContext().sendBroadcast(intent);
    }

    public void picasaLogout()
    {
        picasaService = null;
        picasaSetUserId(null);
        EventBus.getDefault().post(new PicasaSessionChangedEvent());
        Intent intent = new Intent(IntentActions.ACTION_PICASA_SESSIOM_CHANGED);
        FlayvrApplication.getAppContext().sendBroadcast(intent);
    }

    public void picasaTryLogin()
    {
        String s1;
        try
        {
            String s = picasaGetUserId();
            if(s != null) {
                if((s1 = GoogleAuthUtil.getToken(FlayvrApplication.getAppContext(), s, "lh2", null)) != null) {
                    picasaLogin(s, s1);
                }
            }
        }
        catch(Exception exception)
        {
            Log.i(TAG, (new StringBuilder()).append("login to picasa failed: ").append(exception.getMessage()).toString());
        }
    }

}

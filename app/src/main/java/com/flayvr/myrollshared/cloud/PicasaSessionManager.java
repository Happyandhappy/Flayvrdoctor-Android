package com.flayvr.myrollshared.cloud;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.events.PicasaLogoutEvent;
import com.flayvr.myrollshared.events.PicasaSessionChangedEvent;
import com.flayvr.myrollshared.managers.GalleryDoctorServicesProgress;
import com.flayvr.myrollshared.utils.IntentActions;
import com.flayvr.myrollshared.utils.SharedPreferencesManager;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.gdata.client.photos.PicasawebService;
import de.greenrobot.event.EventBus;

public class PicasaSessionManager
{

    public static final String PICASA_API_PREFIX = "https://picasaweb.google.com/data/";
    public static final String PICASA_ENTRY_API_PREFIX = "https://picasaweb.google.com/data/entry/api/user";
    public static final String PICASA_FEED_API_PREFIX = "https://picasaweb.google.com/data/feed/api/user";
    private static final String TAG = PicasaSessionManager.class.getSimpleName();
    private static PicasaSessionManager instance;
    private PicasawebService picasaService;
    private String picasaUserId;

    private PicasaSessionManager()
    {
    }

    private String createAppId()
    {
        int i = FlayvrApplication.getAppContext().getApplicationInfo().labelRes;
        String s = FlayvrApplication.getAppContext().getString(i);
        return (new StringBuilder()).append(s).append("-").append("1.0").toString();
    }

    public static PicasaSessionManager getInstance()
    {
        if(instance == null)
        {
            instance = new PicasaSessionManager();
        }
        return instance;
    }

    private void setUserId(String s)
    {
        SharedPreferencesManager.setPicasaUserAccount(s);
        picasaUserId = s;
    }

    public PicasawebService getPicasaService()
    {
        return picasaService;
    }

    public String getUserId()
    {
        if(picasaUserId == null)
        {
            picasaUserId = SharedPreferencesManager.getPicasaUserAccount();
        }
        return picasaUserId;
    }

    public boolean hasUser()
    {
        return getUserId() != null;
    }

    public boolean isLogedin()
    {
        return picasaService != null;
    }

    public void login(String s, String s1)
    {
        picasaService = new PicasawebService(createAppId());
        picasaService.setUserToken(s1);
        setUserId(s);
    }

    public void logout()
    {
        picasaService = null;
        setUserId(null);
        EventBus.getDefault().post(new PicasaLogoutEvent());
        Intent intent = new Intent(IntentActions.ACTION_PICASA_SESSIOM_CHANGED);
        FlayvrApplication.getAppContext().sendBroadcast(intent);
        Intent intent1 = new Intent(IntentActions.ACTION_PICASA_CLEAN_DATA);
        FlayvrApplication.getAppContext().sendBroadcast(intent1);
        EventBus.getDefault().post(new PicasaSessionChangedEvent());
        GalleryDoctorServicesProgress.setCvServiceProgress(2, 0.0F);
        GalleryDoctorServicesProgress.setClassifierServiceProgress(2, 0.0F);
        GalleryDoctorServicesProgress.setDuplicatesServiceProgress(2, 0.0F);
        GalleryDoctorServicesProgress.cvServiceFinished(2, false);
        GalleryDoctorServicesProgress.classifierServiceFinished(2, false);
        GalleryDoctorServicesProgress.duplicatesServiceFinished(2, false);
    }

    public boolean tryLogin()
    {
        if(hasUser()) {
            String s = getUserId();
            String s1;
            try
            {
                s1 = GoogleAuthUtil.getToken(FlayvrApplication.getAppContext(), s, "lh2", null);
            }
            catch(Exception exception)
            {
                Log.i(TAG, (new StringBuilder()).append("login to picasa failed: ").append(exception.getMessage()).toString());
                return false;
            }
            if(s1 == null)
                return false;
            login(s, s1);
            return true;
        }
        return false;
    }
}

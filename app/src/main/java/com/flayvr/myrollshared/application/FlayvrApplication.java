package com.flayvr.myrollshared.application;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.flayvr.myrollshared.cloud.PicasaSessionManager;
import com.flayvr.myrollshared.events.PicasaSessionChangedEvent;
import com.flayvr.myrollshared.imageloading.*;
import com.flayvr.myrollshared.managers.AppSessionInfoManager;
import com.flayvr.myrollshared.managers.UserManager;
import com.flayvr.myrollshared.receivers.ContentRegisterReceiver;
import com.flayvr.myrollshared.utils.*;
import com.kissmetrics.sdk.KISSmetricsAPI;
import de.greenrobot.event.EventBus;
import java.util.HashMap;
import java.util.concurrent.*;

public abstract class FlayvrApplication extends MultiDexApplication
{

    public static final int DISK_CACHE_SIZE = 0x3200000;
    private static final String TAG = FlayvrApplication.class.getSimpleName();
    protected static ExecutorService actionsPool;
    protected static AppConfiguration configuration;
    protected static Context context;
    private static ImagesDiskCache diskCache;
    protected static boolean hasWear = false;
    private static ImagesCache imagesCache;
    protected static ExecutorService networkPool;
    private static String referrerSharingToken;
    protected static AppSessionInfoManager sessionManager;

    public FlayvrApplication()
    {
    }

    public static Context getAppContext()
    {
        return context;
    }

    public static AppSessionInfoManager getAppSessionInfoManager()
    {
        return sessionManager;
    }

    public static AppConfiguration getApplicationConfiguration()
    {
        return configuration;
    }

    public static ImagesDiskCache getDiskCache()
    {
        return diskCache;
    }

    public static ImagesCache getImagesCache()
    {
        return imagesCache;
    }

    public static String getReferrerSharingToken()
    {
        return referrerSharingToken;
    }

    public static boolean hasWear()
    {
        return hasWear;
    }

    public static Future runAction(Callable callable)
    {
        return actionsPool.submit(callable);
    }

    public static void runAction(Runnable runnable)
    {
        actionsPool.submit(runnable);
    }

    public static Future runNetwork(Callable callable)
    {
        return networkPool.submit(callable);
    }

    public static void runNetwork(Runnable runnable)
    {
        networkPool.submit(runnable);
    }

    public static void setHasWear(boolean flag)
    {
        hasWear = flag;
    }

    public static void setReferrerSharingToken(String s)
    {
        referrerSharingToken = s;
    }

    protected abstract AppSessionInfoManager createAppSessionInfoManager();

    protected abstract int getCacheSize(int i);

    protected abstract AppConfiguration getSpecificApplicationConfiguration();

    protected abstract void initKISSMetrics(Context context1);

    protected abstract void initParse();

    public abstract boolean isBatteryOptimizationOn();

    public void onCreate()
    {
        super.onCreate();
        if(AndroidUtils.isForbiddenCountry(getApplicationContext()))
        {
            System.exit(0);
            return;
        }
        configuration = getSpecificApplicationConfiguration();
        context = getApplicationContext();
        sessionManager = createAppSessionInfoManager();
        actionsPool = Executors.newFixedThreadPool(3, new BasicThreadFactory("actionThread"));
        networkPool = Executors.newFixedThreadPool(3, new BasicThreadFactory("networkThread"));
        initKISSMetrics(getAppContext());
        ActivityManager activitymanager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        Log.v(TAG, (new StringBuilder()).append("memory size: ").append(activitymanager.getMemoryClass()).append(" MB").toString());
        int i = (1024 * (1024 * activitymanager.getMemoryClass())) / 2;
        imagesCache = new ImagesCache(getCacheSize(i));
        diskCache = new ImagesDiskCache(0x3200000);
        AndroidImagesUtils.IMAGE_MAX_SIZE = Math.min(i / 16, 0x1c2000);
        Log.i(TAG, "sent app created");
        sendBroadcast(new Intent(IntentActions.ACTION_APP_CREATE));
        ContentRegisterReceiver.registerObserver(getAppContext());
        runAction(new Runnable() {
            @Override
            public void run() {
                setKissPropertiesValues();
                PicasaSessionManager.getInstance().tryLogin();
            }
        });
        try
        {
            KISSmetricsAPI.sharedAPI().identify(UserManager.getInstance().getUserId());
            HashMap hashmap = new HashMap();
            hashmap.put("uuid", UserManager.getInstance().getUserId());
            KISSmetricsAPI.sharedAPI().set(hashmap);
        }
        catch(Exception exception)
        {
            Log.e(TAG, exception.getMessage(), exception);
        }
        initParse();
    }

    public void onTrimMemory(int i)
    {
        if(android.os.Build.VERSION.SDK_INT >= 14)
        {
            super.onTrimMemory(i);
        }
        Log.v(TAG, (new StringBuilder()).append("onTrimMemory() with level=").append(i).toString());
        if(i >= 60) {
            Log.v(TAG, "evicting entire cache");
            imagesCache.evictAll();
        } else if(i >= 40) {
            Log.v(TAG, "evicting oldest half of cache");
            imagesCache.trimToSize(imagesCache.size() / 2);
        }
    }

    protected abstract void setKissPropertiesValues();

    protected void syncPicasa()
    {
        EventBus.getDefault().post(new PicasaSessionChangedEvent());
        Intent intent = new Intent(IntentActions.ACTION_PICASA_SESSIOM_CHANGED);
        getAppContext().sendBroadcast(intent);
    }
}

package com.flayvr.myrollshared.services;

import android.app.*;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.cloud.PicasaSessionManager;
import com.flayvr.myrollshared.events.NewAppLaunched;
import com.flayvr.myrollshared.events.PicasaSessionChangedEvent;
import com.flayvr.myrollshared.managers.AppSessionInfoManager;
import com.flayvr.myrollshared.managers.UserManager;
import com.flayvr.myrollshared.utils.IntentActions;
import com.flayvr.myrollshared.utils.SharedPreferencesManager;

import de.greenrobot.event.EventBus;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;

public class FocusWizardService extends Service
{

    private static final long APP_SESSION_INFO_DELAY = 60000L;
    protected static final String TAG = FocusWizardService.class.getSimpleName();
    private static Date lastUnfocus;
    private final IBinder mBinder = new LocalBinder();

    public FocusWizardService()
    {
    }

    private void onFocus()
    {
        Log.d(TAG, "app focus");
        PendingIntent pendingintent = PendingIntent.getService(this, 100, new Intent(this, AppSessionInfoSender.class), 0);
        ((AlarmManager)getSystemService(ALARM_SERVICE)).cancel(pendingintent);
        FlayvrApplication.getAppSessionInfoManager().startSession();
        if(lastUnfocus == null || DateUtils.addMinutes(lastUnfocus, 1).before(new Date()))
        {
            UserManager.getInstance().handleNewAppLaunch();
            EventBus.getDefault().post(new NewAppLaunched());
            FlayvrApplication.runAction(new Runnable() {
                @Override
                public void run() {
                    if(PicasaSessionManager.getInstance().isLogedin())
                    {
                        EventBus.getDefault().post(new PicasaSessionChangedEvent());
                        Intent intent1 = new Intent(IntentActions.ACTION_PICASA_SESSIOM_CHANGED);
                        FlayvrApplication.getAppContext().sendBroadcast(intent1);
                    }
                    Intent intent = new Intent(IntentActions.ACTION_MYROLL_CLOUD_SESSION_CHANGED);
                    FlayvrApplication.getAppContext().sendBroadcast(intent);
                }
            });
        }
    }

    private void onUnfocus()
    {
        Log.d(TAG, "app unfocus");
        lastUnfocus = new Date();
        FlayvrApplication.getAppSessionInfoManager().endSession();
        FlayvrApplication.runAction(new Runnable() {
            @Override
            public void run() {
                SharedPreferencesManager.setLastAppOpen(new Date());
                Intent intent = new Intent(FocusWizardService.this, AppSessionInfoSender.class);
                AppSessionInfoManager appsessioninfomanager = FlayvrApplication.getAppSessionInfoManager();
                appsessioninfomanager.setSessionValues();
                intent.putExtra("SESSION_INFO_KEY", appsessioninfomanager.getSessionInfo());
                PendingIntent pendingintent = PendingIntent.getService(FocusWizardService.this, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                ((AlarmManager)getSystemService(ALARM_SERVICE)).set(0, 60000L + System.currentTimeMillis(), pendingintent);
            }
        });
    }

    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    public void onCreate()
    {
        super.onCreate();
        onFocus();
    }

    public boolean onUnbind(Intent intent)
    {
        onUnfocus();
        return false;
    }


    private class LocalBinder extends Binder
    {
        FocusWizardService getService()
        {
            return FocusWizardService.this;
        }

        public LocalBinder()
        {
            super();
        }
    }
}

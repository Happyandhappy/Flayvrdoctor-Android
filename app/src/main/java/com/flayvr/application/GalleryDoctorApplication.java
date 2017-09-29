package com.flayvr.application;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;
import com.flayvr.managers.GalleryDoctortSessionInfoManager;
import com.flayvr.myrollshared.application.AppConfiguration;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.managers.UserManager;
import com.flayvr.myrollshared.server.ServerUrls;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.flayvr.myrollshared.utils.AndroidUtils;
import com.flayvr.myrollshared.utils.FlayvrHttpClient;
import com.flayvr.services.GalleryDoctorService;
import com.kissmetrics.sdk.KISSmetricsAPI;
import com.parse.*;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class GalleryDoctorApplication extends FlayvrApplication
{

    public static final boolean BUG_REPORT_ENABLE = false;
    public static final boolean CLOUD_INTEGRATION = false;
    private static final String FLAYVR_LOG_FILE = "flayvr.log";
    private static final String TAG = GalleryDoctorApplication.class.getSimpleName();

    public GalleryDoctorApplication()
    {
    }

    public static GalleryDoctortSessionInfoManager getAppSessionInfoManager()
    {
        return (GalleryDoctortSessionInfoManager)sessionManager;
    }

    public static String getLogFilePath()
    {
        return (new StringBuilder()).append(getAppContext().getFilesDir().getAbsolutePath()).append(File.separator).append(FLAYVR_LOG_FILE).toString();
    }

    private void setPushNotificationToken()
    {
        FlayvrApplication.runNetwork(new Runnable() {
            @Override
            public void run() {
                String s;
                if(UserManager.getInstance().getUserId() != null && !UserManager.getInstance().getUserId().equals(""))
                {
                    if((s = ParseInstallation.getCurrentInstallation().getString("deviceToken")) != null)
                    {
                        HashMap hashmap = new HashMap();
                        hashmap.put("push token", s);
                        KISSmetricsAPI.sharedAPI().set(hashmap);
                        FlayvrHttpClient flayvrhttpclient = new FlayvrHttpClient();
                        HttpPost httppost = new HttpPost(ServerUrls.SET_PUSH_NOTIFICATIONS_TOKEN_URL);
                        try
                        {
                            ArrayList arraylist = new ArrayList();
                            arraylist.add(new BasicNameValuePair("uuid", UserManager.getInstance().getUserId()));
                            arraylist.add(new BasicNameValuePair("token", s));
                            arraylist.add(new BasicNameValuePair("country_code", AndroidUtils.getCountryCode()));
                            arraylist.add(new BasicNameValuePair("device_type", AndroidUtils.getDeviceType()));
                            httppost.setEntity(new UrlEncodedFormEntity(arraylist, "UTF-8"));
                            flayvrhttpclient.executeWithRetries(httppost);
                            Log.i(GalleryDoctorApplication.TAG, "token for push notification of user updated");
                        } catch(IOException ioexception) {
                            Log.e(GalleryDoctorApplication.TAG, ioexception.getMessage(), ioexception);
                        }
                    }
                }
            }
        });
    }

    private void writeLogToFile()
    {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                Process process;
                BufferedReader bufferedreader;
                FileOutputStream fileoutputstream;
                try
                {
                    Log.d("flayvr_appllication", "starting reading logs");
                    try {
                        process = Runtime.getRuntime().exec("logcat -v time");
                        bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        fileoutputstream = openFileOutput(FLAYVR_LOG_FILE, 0);
                        while (true) {
                            String s = bufferedreader.readLine();
                            if (s == null)
                                break;
                            fileoutputstream.write((new StringBuilder()).append(s).append("\n").toString().getBytes());
                        }


                        process.waitFor();
                        fileoutputstream.close();
                        bufferedreader.close();
                    }catch(InterruptedException e) {
                    }
                    Log.d("flayvr_appllication", "closing log file");
                }
                catch(IOException ioexception)
                {
                    Log.e("flayvr_appllication", "failed", ioexception);
                }
            }
        })).start();
    }

    protected GalleryDoctortSessionInfoManager createAppSessionInfoManager()
    {
        return new GalleryDoctortSessionInfoManager();
    }

    public int getCacheSize(int i)
    {
        return i / 10;
    }

    public AppConfiguration getSpecificApplicationConfiguration()
    {
        return new BuildConfiguration();
    }

    protected void initKISSMetrics(Context context)
    {
        try
        {
            KISSmetricsAPI.sharedAPI(AppConfiguration.getConfiguration().getKissKey(), context);
            KISSmetricsAPI.sharedAPI().autoRecordInstalls();
            KISSmetricsAPI.sharedAPI().autoSetAppProperties();
            KISSmetricsAPI.sharedAPI().autoSetHardwareProperties();
            if(!PreferencesManager.wasSentAppInstalledEvent())
            {
                AnalyticsUtils.trackEventWithKISS("installed myroll");
                PreferencesManager.sentAppInstalledEvent();
            }
        }
        catch(Exception exception)
        {
            Log.e("KISS analytics", exception.getMessage(), exception);
        }
    }

    protected void initParse()
    {
        if(!PreferencesManager.isFirstSessionHandled() && PreferencesManager.getAppInstallDate() == null)
        {
            PreferencesManager.setAppInstallDate(new Date());
        }
        Parse.initialize(this, AppConfiguration.getConfiguration().getParseAppKey(), AppConfiguration.getConfiguration().getParseApiKey());
        if(!PreferencesManager.wasParseInstallSent())
        {
            ParseInstallation parseinstallation = ParseInstallation.getCurrentInstallation();
            parseinstallation.put("country", AndroidUtils.getCountryCode());
            parseinstallation.put("language", Locale.getDefault().getLanguage());
            parseinstallation.put("full_device_type", AndroidUtils.getDeviceType());
            parseinstallation.put("user_id", UserManager.getInstance().getUserId());
            if(!PreferencesManager.isFirstSessionHandled())
            {
                parseinstallation.put("install_date", PreferencesManager.getAppInstallDate());
                ParsePush.subscribeInBackground("uninstall");
            }
            parseinstallation.saveInBackground();
            PreferencesManager.setParseInstallSent();
        }
    }

    public boolean isBatteryOptimizationOn()
    {
        return false;
    }

    public void onCreate()
    {
        super.onCreate();
        if(AndroidUtils.isForbiddenCountry(getApplicationContext()))
        {
            System.exit(0);
        } else {
            runAction(new Runnable(){
                @Override
                public void run() {
                    GalleryDoctorService.addWeekendAlarm(FlayvrApplication.getAppContext());
                    PreferencesManager.setSessionsCount(1 + PreferencesManager.getSessionsCount());
                }
            });
            setPushNotificationToken();
        }
    }

    protected void setKissPropertiesValues()
    {
        HashMap hashmap = new HashMap();
        hashmap.put("os", "android");
        hashmap.put("country", AndroidUtils.getCountryCode());
        hashmap.put("language", Locale.getDefault().getLanguage());
        hashmap.put("full device type", AndroidUtils.getDeviceType());
        KISSmetricsAPI.sharedAPI().set(hashmap);
    }

    protected void syncPicasa()
    {
        super.syncPicasa();
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

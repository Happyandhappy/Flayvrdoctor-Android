package com.flayvr.myrollshared.server;

import android.content.Context;
import android.util.Log;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.managers.UserManager;
import com.flayvr.myrollshared.utils.AndroidUtils;
import com.flayvr.myrollshared.utils.FlayvrHttpClient;
import com.flayvr.myrollshared.utils.GeneralUtils;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.UnknownHostException;
import java.util.*;

public class BackendBasedSettings
    implements Serializable
{
    private static final String SETTINGS_FILENAME = "settings_file";
    private static final String TAG = "flayvr_app_settings";
    private static BackendBasedSettings instance;
    private static final long serialVersionUID = 0x76e31f4a268b64e4L;
    public Set calendarTitlesBlackList;
    public int daysUntilRemindRateus;
    public int galleryFullscreenTimeForRateus;
    public int momentsSharedForRateus;
    public int momentsWatchedForRateus;
    public long notificationWaitAfterAppSessionTime;
    public long notificationWaitAfterImageTakenTime;
    public long notificationWaitBetweenNotificationTime;
    public int notificationWeekendDay;
    public int notificationWeekendHour;
    public int photosSharedForRateus;
    public int photosWatchedForRateus;
    public int slideshowTimeForRateus;

    private BackendBasedSettings()
    {
        momentsWatchedForRateus = 4;
        momentsSharedForRateus = 1;
        slideshowTimeForRateus = 20;
        galleryFullscreenTimeForRateus = 40;
        photosWatchedForRateus = 7;
        photosSharedForRateus = 1;
        daysUntilRemindRateus = 7;
        notificationWaitBetweenNotificationTime = 0x7b98a00L;
        notificationWaitAfterAppSessionTime = 0L;
        notificationWaitAfterImageTakenTime = 0x36ee80L;
        notificationWeekendDay = 6;
        notificationWeekendHour = 17;
        calendarTitlesBlackList = new HashSet();
        calendarTitlesBlackList.add("(?i).*(available)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(call)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(reminder)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*\\d{5,}?.*");
        calendarTitlesBlackList.add("(?i).*(saved for)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(appointment)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(buy)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(saved)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(pay)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(bills)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(monthly)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(weekly)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(meeting)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(tentative)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(conference)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(conf)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(email)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(send)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(purchase)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(todo)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(fix)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(invitation)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(daily)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(drive)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(remind)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(out of office)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(notification)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(renew)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(search)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(canceled)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(buy)(\\s|$).*");
        calendarTitlesBlackList.add("(?i).*(invitation)(\\s|$).*");
    }

    public static BackendBasedSettings getInstance()
    {
        synchronized (BackendBasedSettings.class) {
            BackendBasedSettings backendbasedsettings;
            if(instance == null)
                instance = new BackendBasedSettings();
            backendbasedsettings = instance;
            return backendbasedsettings;
        }
    }

    private static BackendBasedSettings getInstanceFromFile()
    {
        try
        {
            Context context = FlayvrApplication.getAppContext();
            if(!context.getFileStreamPath("settings_file").exists())
                return null;
            ObjectInputStream objectinputstream = new ObjectInputStream(context.openFileInput("settings_file"));
            BackendBasedSettings backendbasedsettings = (BackendBasedSettings)objectinputstream.readObject();
            if(objectinputstream != null) {
                try {
                    objectinputstream.close();
                }catch(IOException ioexception){}
            }
            return backendbasedsettings;
        } catch(Exception exception) {
            return null;
        }
    }

    private void getSettingsFromServer()
    {
        FlayvrApplication.runNetwork(new Runnable() {
            @Override
            public void run() {
                FlayvrHttpClient flayvrhttpclient;
                HttpPost httppost;
                JSONObject jsonobject;
                JSONArray jsonarray;
                Log.i("flayvr_app_settings", "starting get settings from server");
                flayvrhttpclient = new FlayvrHttpClient();
                httppost = new HttpPost(ServerUrls.GET_APP_SETTINGS_URL);
                ArrayList arraylist;
                arraylist = new ArrayList();
                if(!Locale.getDefault().equals(Locale.SIMPLIFIED_CHINESE) && !Locale.getDefault().equals(Locale.TRADITIONAL_CHINESE))
                    arraylist.add(new BasicNameValuePair("locale", Locale.getDefault().getLanguage()));
                else
                    arraylist.add(new BasicNameValuePair("locale", Locale.getDefault().toString()));
                arraylist.add(new BasicNameValuePair("app_version", (new StringBuilder()).append("").append(AndroidUtils.getAppVersionCode()).toString()));
                arraylist.add(new BasicNameValuePair("uuid", UserManager.getInstance().getUserId()));
                try
                {
                    httppost.setEntity(new UrlEncodedFormEntity(arraylist, "UTF-8"));
                    InputStream inputstream = flayvrhttpclient.executeWithRetries(httppost).getEntity().getContent();
                    String s = GeneralUtils.convertStreamToString(inputstream);
                    inputstream.close();
                    jsonobject = new JSONObject(s);
                    jsonarray = jsonobject.getJSONArray("calendar_titles_black_list");
                    int i = 0;
                    while(i < jsonarray.length())
                    {
                        calendarTitlesBlackList.add(jsonarray.getString(i));
                        i++;
                    }
                    notificationWaitBetweenNotificationTime = jsonobject.getLong("notification_wait_between_notification_time");
                    notificationWaitAfterAppSessionTime = jsonobject.getLong("notification_wait_after_app_session_time");
                    notificationWaitAfterImageTakenTime = jsonobject.getLong("notification_wait_after_image_taken_time");
                    daysUntilRemindRateus = jsonobject.getInt("days_until_remind_rateus");
                    Log.i("flayvr_app_settings", "done getting settings from server");
                    saveSettingsToFile();
                }
                catch(JSONException jsonexception)
                {
                    Log.e("flayvr_app_settings", jsonexception.getMessage(), jsonexception);
                    httppost.abort();
                }
                catch(NullPointerException nullpointerexception)
                {
                    Log.e("flayvr_app_settings", nullpointerexception.getMessage(), nullpointerexception);
                    httppost.abort();
                }
                catch(UnknownHostException unknownhostexception)
                {
                    Log.e("flayvr_app_settings", unknownhostexception.getMessage(), unknownhostexception);
                    httppost.abort();
                }
                catch(Exception exception)
                {
                    Log.e("flayvr_app_settings", exception.getMessage(), exception);
                    httppost.abort();
                }
            }
        });
    }

    private void saveSettingsToFile()
    {
        Context context;
        ObjectOutputStream objectoutputstream;
        context = FlayvrApplication.getAppContext();
        objectoutputstream = null;
        try {
            ObjectOutputStream objectoutputstream1 = new ObjectOutputStream(context.openFileOutput("settings_file", 0));
            objectoutputstream1.writeObject(instance);
            if (objectoutputstream1 != null)
                objectoutputstream1.close();
        }catch(Exception e){}
    }
}

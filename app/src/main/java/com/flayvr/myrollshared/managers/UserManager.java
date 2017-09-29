package com.flayvr.myrollshared.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.flayvr.myrollshared.application.AppConfiguration;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.flayvr.myrollshared.utils.AndroidUtils;
import com.flayvr.myrollshared.utils.FlayvrHttpClient;
import com.flayvr.myrollshared.utils.GeneralUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class UserManager
{

    private static final String TAG = "flayvr_user_manager";
    private static final String USER_EMAIL_PREF_KEY = "USER_EMAIL";
    private static final String USER_ID_PREK_KEY = "USER_ID_WITH_LOCALE";
    private static UserManager instance;
    private String userEmail;
    private String userId;

    private UserManager()
    {
        Context context = FlayvrApplication.getAppContext();
        FlayvrApplication.getApplicationConfiguration();
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0);
        if(sharedpreferences.contains("USER_ID_WITH_LOCALE"))
        {
            userId = sharedpreferences.getString("USER_ID_WITH_LOCALE", null);
        } else
        {
            userId = generateUniqueUserIdentifier();
            createNewUser();
        }
        userEmail = sharedpreferences.getString("USER_EMAIL", "1@flayvr.com");
    }

    private void createAppSession()
    {
        HashMap hashmap = new HashMap();
        if(FlayvrApplication.hasWear())
        {
            hashmap.put("has wear", "yes");
        }
        AnalyticsUtils.trackEventWithKISS("opened app", hashmap, true);
        FlayvrApplication.runNetwork(new Runnable(){
            @Override
            public void run() {
                FlayvrHttpClient flayvrhttpclient = new FlayvrHttpClient();
                HttpPost httppost = new HttpPost(FlayvrApplication.getApplicationConfiguration().getCreateAppSessionUrl());
                try
                {
                    ArrayList arraylist = new ArrayList();
                    arraylist.add(new BasicNameValuePair("uuid", userId));
                    arraylist.add(new BasicNameValuePair("country_code", AndroidUtils.getCountryCode()));
                    arraylist.add(new BasicNameValuePair("app_version", AndroidUtils.getAppVersion()));
                    arraylist.add(new BasicNameValuePair("android_version", AndroidUtils.getAndroidVersion()));
                    arraylist.add(new BasicNameValuePair("device_type", AndroidUtils.getDeviceType()));
                    arraylist.add(new BasicNameValuePair("locale", Locale.getDefault().getLanguage()));
                    httppost.setEntity(new UrlEncodedFormEntity(arraylist, "UTF-8"));
                    flayvrhttpclient.executeWithRetries(httppost);
                    Log.i("flayvr_user_manager", "new app session created");
                } catch(IOException ioexception) {
                    Log.e("flayvr_user_manager", ioexception.getMessage(), ioexception);
                }
            }
        });
    }

    private void createNewUser()
    {
        FlayvrApplication.runNetwork(new Runnable() {
            @Override
            public void run() {
                Log.i("flayvr_user_manager", "creating new user");
                FlayvrHttpClient flayvrhttpclient = new FlayvrHttpClient();
                HttpPost httppost = new HttpPost(FlayvrApplication.getApplicationConfiguration().getCreateUserUrl());
                try
                {
                    ArrayList arraylist = new ArrayList();
                    arraylist.add(new BasicNameValuePair("uuid", userId));
                    arraylist.add(new BasicNameValuePair("country_code", AndroidUtils.getCountryCode()));
                    arraylist.add(new BasicNameValuePair("device_type", AndroidUtils.getDeviceType()));
                    arraylist.add(new BasicNameValuePair("locale", Locale.getDefault().getLanguage()));
                    httppost.setEntity(new UrlEncodedFormEntity(arraylist, "UTF-8"));
                    HttpResponse httpresponse = flayvrhttpclient.executeWithRetries(httppost);
                    Log.i("flayvr_user_manager", "new user created");
                    if(userId == null || userId.equals(""))
                    {
                        InputStream inputstream = httpresponse.getEntity().getContent();
                        String s = GeneralUtils.convertStreamToString(inputstream);
                        inputstream.close();
                        JSONObject jsonobject = new JSONObject(s);
                        userId = jsonobject.getString("uuid");
                    }
                    storeUserId();
                }
                catch(UnknownHostException unknownhostexception)
                {
                    Log.e("flayvr_user_manager", unknownhostexception.getMessage(), unknownhostexception);
                    httppost.abort();
                }
                catch(IOException ioexception)
                {
                    Log.e("flayvr_user_manager", ioexception.getMessage(), ioexception);
                }
                catch(JSONException jsonexception)
                {
                    Log.e("flayvr_user_manager", jsonexception.getMessage(), jsonexception);
                }
            }
        });
    }

    private String generateUniqueUserIdentifier()
    {
        String s;
        try {
            s = ((TelephonyManager) FlayvrApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        }catch(SecurityException e){
            s = null;
        }
        String s1 = android.provider.Settings.Secure.getString(FlayvrApplication.getAppContext().getContentResolver(), "android_id");
        if(s != null && s1 != null)
        {
            s = (new StringBuilder()).append(s).append("-").append(s1).toString();
        } else if(s == null) {
            if(s1 != null)
            {
                return s1;
            } else
            {
                return "";
            }
        }
        return s;
    }

    public static UserManager getInstance()
    {
        synchronized (UserManager.class) {
            UserManager usermanager;
            if(instance == null)
                instance = new UserManager();
            usermanager = instance;
            return usermanager;
        }
    }

    public String getUserEmail()
    {
        return userEmail;
    }

    public String getUserId()
    {
        return userId;
    }

    public void handleNewAppLaunch()
    {
        createAppSession();
    }

    public void setUserEmail(String s)
    {
        userEmail = s;
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(FlayvrApplication.getApplicationConfiguration().getSharedPrefFile(), 0).edit();
        editor.putString("USER_EMAIL", s);
        editor.commit();
    }

    public void storeUserId()
    {
        Context context = FlayvrApplication.getAppContext();
        FlayvrApplication.getApplicationConfiguration();
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putString("USER_ID_WITH_LOCALE", userId);
        editor.commit();
    }
}

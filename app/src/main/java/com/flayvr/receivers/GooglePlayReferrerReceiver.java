package com.flayvr.receivers;

import android.content.*;
import android.preference.PreferenceManager;

import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.managers.UserManager;
import com.flayvr.myrollshared.server.ServerUrls;
import com.flayvr.myrollshared.utils.AndroidUtils;
import com.flayvr.myrollshared.utils.FlayvrHttpClient;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class GooglePlayReferrerReceiver extends BroadcastReceiver
{

    public static final String REFERRER_KEY_URL = "referrer";
    private static final String TAG = "flayvr_google_play_receiver";

    public GooglePlayReferrerReceiver()
    {
    }

    private void setReferrerInDB(final String referrer)
    {
        FlayvrApplication.runNetwork(new Runnable() {
            @Override
            public void run() {
                if(UserManager.getInstance().getUserId() == null || UserManager.getInstance().getUserId().equals(""))
                    return;
                FlayvrHttpClient flayvrhttpclient = new FlayvrHttpClient();
                HttpPost httppost = new HttpPost(ServerUrls.SET_REFERRER_URL);
                try
                {
                    ArrayList arraylist = new ArrayList();
                    arraylist.add(new BasicNameValuePair("uuid", UserManager.getInstance().getUserId()));
                    arraylist.add(new BasicNameValuePair(REFERRER_KEY_URL, referrer));
                    arraylist.add(new BasicNameValuePair("country_code", AndroidUtils.getCountryCode()));
                    arraylist.add(new BasicNameValuePair("device_type", AndroidUtils.getDeviceType()));
                    httppost.setEntity(new UrlEncodedFormEntity(arraylist, "UTF-8"));
                    flayvrhttpclient.executeWithRetries(httppost);
                    //GCMIntentService.sentPushNotificationToken();
                }
                catch(IOException ioexception)
                {
                }
            }
        });
    }

    public void onReceive(Context context, Intent intent)
    {
        String s;
        s = intent.getStringExtra(REFERRER_KEY_URL);
        if(s == null) {
            return;
        }
        String s1;
        s1 = "";
        try {
            String s2 = URLDecoder.decode(s, "UTF-8");
            s1 = s2;
        }catch (UnsupportedEncodingException unsupportedencodingexception) {}
        PreferenceManager.getDefaultSharedPreferences(FlayvrApplication.getAppContext()).edit().putString(REFERRER_KEY_URL, s1).commit();
        setReferrerInDB(s1);
    }
}

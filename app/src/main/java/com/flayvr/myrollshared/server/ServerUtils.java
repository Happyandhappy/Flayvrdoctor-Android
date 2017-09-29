package com.flayvr.myrollshared.server;

import android.util.Log;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.managers.UserManager;
import com.flayvr.myrollshared.utils.AndroidUtils;
import com.flayvr.myrollshared.utils.FlayvrHttpClient;
import java.net.UnknownHostException;
import java.util.Locale;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

public class ServerUtils
{

    public ServerUtils()
    {
    }

    private static void createAppEvent(String s, String as[], String as1[])
    {
        Log.i("app events", "starting to send app event");
        JSONObject jsonobject;
        JSONObject jsonobject1;
        int i;
        int j;
        StringEntity stringentity;
        int k;
        HttpPost httppost = new HttpPost(ServerUrls.CREATE_APP_EVENT_URL);
        try
        {
            FlayvrHttpClient flayvrhttpclient = new FlayvrHttpClient();
            jsonobject = new JSONObject();
            jsonobject.put("uuid", UserManager.getInstance().getUserId());
            jsonobject1 = new JSONObject();
            jsonobject1.put("type", s);
            jsonobject1.put("app_version", AndroidUtils.getAppVersion());
            jsonobject1.put("android_version", AndroidUtils.getAndroidVersion());
            jsonobject1.put("locale", Locale.getDefault().getLanguage());
            if(as != null)
                jsonobject1.put("ab_version", as);
            if(as != null) {
                if (as.length > 0) {
                    jsonobject1.put("ab_version", as[0]);
                    k = 1;
                    while (k < as.length) {
                        jsonobject1.put((new StringBuilder()).append("ab_version").append(k).toString(), as[k]);
                        k++;
                    }
                }
            }
            i = as1.length;
            j = 0;
            if(i != 1) {
                while(j < as1.length)
                {
                    jsonobject1.put((new StringBuilder()).append("value").append(j + 1).toString(), as1[j]);
                    j++;
                }
            } else
                jsonobject1.put("value", as1[0]);
            jsonobject.put("event", jsonobject1);
            stringentity = new StringEntity(jsonobject.toString());
            stringentity.setContentType("application/json");
            httppost.setEntity(stringentity);
            flayvrhttpclient.executeWithRetries(httppost);
            Log.i("app events", "send app events done");
        } catch(Exception exception) {
            Log.e("app events", exception.getMessage(), exception);
            httppost.abort();
        }
    }

    public static void createAppEventAsync(final String type, final String value[])
    {
        FlayvrApplication.runNetwork(new Runnable() {
            @Override
            public void run() {
                ServerUtils.createAppEvent(type, null, value);
            }
        });
    }

    public static void createAppEventWithABVersionAsync(String s, String s1, String as[])
    {
        createAppEventWithABVersionAsync(s, new String[] {
            s1
        }, as);
    }

    public static void createAppEventWithABVersionAsync(final String type, final String abVersion[], final String value[])
    {
        FlayvrApplication.runNetwork(new Runnable() {
            @Override
            public void run() {
                ServerUtils.createAppEvent(type, abVersion, value);
            }
        });
    }
}

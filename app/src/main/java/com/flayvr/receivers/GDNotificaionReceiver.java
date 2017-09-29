package com.flayvr.receivers;

import android.content.*;
import android.util.Log;
import com.flayvr.myrollshared.managers.UserManager;
import com.flayvr.screens.dashboard.GalleryDoctorDashboardActivity;
import com.flayvr.screens.settings.SettingsFragment;
import com.parse.*;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

public class GDNotificaionReceiver extends ParsePushBroadcastReceiver
{

    private static final String NOTIFICATION_TYPE_KEY = "type";
    public static final String SILENT_PUSH = "silent";
    private static final String TAG = GDNotificaionReceiver.class.getSimpleName();
    public static final String UPDATE_PUSH = "update";

    public GDNotificaionReceiver()
    {
    }

    private void handleSilentNotification(JSONObject jsonobject)
    {
        try
        {
            Object obj = jsonobject.get("server_time");
            ParseInstallation parseinstallation = ParseInstallation.getCurrentInstallation();
            parseinstallation.put("last_keep_alive_server", obj);
            parseinstallation.put("last_keep_alive_client", new Date());
            parseinstallation.saveInBackground();
            ParseObject parseobject = new ParseObject("KeepAlive");
            parseobject.put("user_id", UserManager.getInstance().getUserId());
            parseobject.put("server_time", obj);
            parseobject.put("device_time", new Date());
            parseobject.saveInBackground();
        }
        catch(JSONException jsonexception)
        {
            Log.w(TAG, jsonexception.getMessage(), jsonexception);
        }
    }

    public void onPushOpen(Context context, Intent intent)
    {
        try
        {
            if(intent.hasExtra("type") && "update".equals(intent.getStringExtra("type")))
            {
                Intent intent2 = new Intent("android.intent.action.VIEW", SettingsFragment.getAppLink(context));
                intent2.setFlags(0x10000000);
                context.startActivity(intent2);
            } else {
                Intent intent1 = new Intent(context, GalleryDoctorDashboardActivity.class);
                intent1.putExtras(intent.getExtras());
                intent1.setFlags(0x10000000);
                context.startActivity(intent1);
            }
        }
        catch(ActivityNotFoundException activitynotfoundexception)
        {
        }
    }

    public void onReceive(Context context, Intent intent)
    {
        JSONObject jsonobject;
        boolean flag;
        String s;
        Log.i(TAG, (new StringBuilder()).append("Received notification: ").append(intent.toString()).toString());
        if(!intent.hasExtra("com.parse.Data"))
        {
            super.onReceive(context, intent);
            return;
        }
        try
        {
            jsonobject = new JSONObject(intent.getStringExtra("com.parse.Data"));
            Log.i(TAG, (new StringBuilder()).append("data: ").append(jsonobject).toString());
            flag = jsonobject.has("type");
            s = null;
            if(flag)
                s = jsonobject.getString("type");
            if("silent".equals(s))
            {
                handleSilentNotification(jsonobject);
                return;
            }
            if("update".equals(s))
            {
                intent.putExtra("type", "update");
            }
            super.onReceive(context, intent);
        }
        catch(JSONException jsonexception)
        {
            Log.w(TAG, jsonexception.getMessage(), jsonexception);
        }
    }
}

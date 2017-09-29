package com.flayvr.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.*;
import android.content.res.Resources;
import android.net.Uri;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.utils.*;
import java.io.File;
import java.util.*;

public class GalleryDoctorUtils
{

    public static final String FACEBOOK_APP_ID = "258031544305338";

    public GalleryDoctorUtils()
    {
    }

    public static boolean checkIfMyRollInstalled(PackageManager packagemanager)
    {
        try
        {
            AnalyticsUtils.trackEventWithKISSOnce("has myroll installed");
            packagemanager.getPackageInfo("com.flayvr.flayvr", 1);
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            AnalyticsUtils.trackEventWithKISSOnce("doesn't have myroll installed");
            return false;
        }
        return true;
    }

    public static ResolveInfo findFacebookClient(PackageManager packagemanager)
    {
        return findIntentForPackageName("com.facebook.katana", packagemanager);
    }

    protected static ResolveInfo findIntentForPackageName(String s, PackageManager packagemanager)
    {
        String as[] = {
            s
        };
        Intent intent = new Intent();
        intent.setType("text/plain");
        List list = packagemanager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        int i = 0;
        do
        {
            if(i >= as.length)
            {
                break;
            }
            for(Iterator iterator = list.iterator(); iterator.hasNext();)
            {
                ResolveInfo resolveinfo = (ResolveInfo)iterator.next();
                String s1 = resolveinfo.activityInfo.packageName;
                if(s1 != null && s1.startsWith(as[i]))
                {
                    return resolveinfo;
                }
            }

            i++;
        } while(true);
        return null;
    }

    public static ResolveInfo findTwitterClient(PackageManager packagemanager)
    {
        return findIntentForPackageName("com.twitter.android", packagemanager);
    }

    public static boolean sendDuplicateReport(Context context, Collection collection)
    {
        Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
        intent.setType("message/rfc822");
        intent.putExtra("android.intent.extra.SUBJECT", context.getResources().getString(R.string.gallery_doctor_similar_photos_mistake));
        intent.putExtra("android.intent.extra.EMAIL", new String[] {
            "helpdoc@myroll.com"
        });
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append((new StringBuilder()).append("App version: ").append(AndroidUtils.getAppVersion()).append("\n").toString());
        stringbuilder.append((new StringBuilder()).append("Android version: ").append(AndroidUtils.getAndroidVersion()).append("\n").toString());
        stringbuilder.append((new StringBuilder()).append("Device: ").append(AndroidUtils.getDeviceType()).append("\n").toString());
        stringbuilder.append("==================\n\n");
        ArrayList arraylist = new ArrayList();
        for(Iterator iterator = collection.iterator(); iterator.hasNext(); arraylist.add(Uri.fromFile(new File(((MediaItem)iterator.next()).getPath())))) { }
        intent.putParcelableArrayListExtra("android.intent.extra.STREAM", arraylist);
        intent.putExtra("android.intent.extra.TEXT", stringbuilder.toString());
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.send_mail_chooser_title)));
        return true;
    }

    public static boolean sendFeedback(Context context)
    {
        return GeneralUtils.sendFeedback(context, context.getResources().getString(R.string.gallery_doctor_feedback_mail_title), "helpdoc@myroll.com");
    }
}

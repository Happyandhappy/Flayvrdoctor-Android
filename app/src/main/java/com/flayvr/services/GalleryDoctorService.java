package com.flayvr.services;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import com.flayvr.application.PreferencesManager;
import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.managers.GalleryDoctorServicesProgress;
import com.flayvr.myrollshared.server.BackendBasedSettings;
import com.flayvr.myrollshared.utils.*;
import com.flayvr.screens.dashboard.GalleryDoctorDashboardActivity;
import com.flayvr.util.GalleryDoctorDBHelper;
import com.flayvr.util.GalleryDoctorStatsUtils;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class GalleryDoctorService extends IntentService
{

    static final int BAD_THRESHOLD = 3;
    static final long DELAY_ACTION_NOTIFICATION = 0x1b7740L;
    static final int DUPLICATE_THRESHOLD = 3;
    static final String GD_ACTION_EXTRA_KEY = "GD_ACTION_EXTRA_KEY";
    static final String GD_DELAY_NOTIFICATION = "GD_DELAY_NOTIFICATION";
    public static final int GD_NOTIFICATION_KEY = 100;
    static final String GD_WEEKEND_NOTIFICATION = "GD_WEEKEND_NOTIFICATION";
    static final int LARGE_NUMBER_THRESHOLD = 1000;
    private static final String TAG = GalleryDoctorService.class.getSimpleName();

    public GalleryDoctorService()
    {
        super("GalleryDoctorService");
    }

    public static void addWeekendAlarm(Context context)
    {
        AlarmManager alarmmanager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, GalleryDoctorService.class);
        intent.setAction("GD_WEEKEND_NOTIFICATION");
        BackendBasedSettings backendbasedsettings = BackendBasedSettings.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_WEEK, backendbasedsettings.notificationWeekendDay);
        calendar.set(Calendar.HOUR_OF_DAY, backendbasedsettings.notificationWeekendHour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if(Calendar.getInstance().after(calendar))
        {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }
        PendingIntent pendingintent = PendingIntent.getService(context, 100, intent, 0);
        alarmmanager.setInexactRepeating(0, calendar.getTimeInMillis(), 0x240c8400L, pendingintent);
    }

    private void checkAnalyticsFinished(int i)
    {
        if(GalleryDoctorServicesProgress.didClassifierServiceFinish(i) && GalleryDoctorServicesProgress.didDuplicatesServiceFinish(i))
        {
            com.flayvr.util.GalleryDoctorStatsUtils.MediaItemStat mediaitemstat = GalleryDoctorStatsUtils.getMediaItemStat(i);
            Resources resources = getResources();
            int j;
            String s;
            String s1;
            Object aobj[];
            boolean flag;
            List list;
            if(i == 1)
            {
                j = R.string.gallery_doctor_analysis_finished_notif_header;
            } else
            {
                j = R.string.analysis_cloud_finished_notif_header;
            }
            s = resources.getString(j);
            s1 = getResources().getString(R.string.gallery_doctor_analysis_finished_notif_text);
            aobj = new Object[1];
            aobj[0] = GeneralUtils.humanReadableByteCount(mediaitemstat.getDuplicatePhotoSize() + mediaitemstat.getBadPhotoSize() + mediaitemstat.getForReviewSize(), false);
            flag = notifyUser(s, String.format(s1, aobj), null, "analysis complete");
            list = GalleryDoctorDBHelper.getBadPhotos(i);
            for(Iterator iterator = list.iterator(); iterator.hasNext(); ((MediaItem)iterator.next()).setWasAnalyzedByGD(Boolean.valueOf(true))) { }
            List list1 = GalleryDoctorDBHelper.getNewDuplicates();
            for(Iterator iterator1 = list1.iterator(); iterator1.hasNext(); ((DuplicatesSet)iterator1.next()).setWasAnalyzedByGD(Boolean.valueOf(true))) { }
            DBManager.getInstance().getDaoSession().getDuplicatesSetDao().updateInTx(list1);
            DBManager.getInstance().getDaoSession().getMediaItemDao().updateInTx(list);
            PreferencesManager.setAnalysisCompleteNotificationShown(i);
            boolean flag1;
            if(i == 1)
            {
                flag1 = true;
            } else
            {
                flag1 = false;
            }
            if(flag1)
            {
                if(flag)
                {
                    HashMap hashmap = new HashMap();
                    hashmap.put("analysis complete notification hour received", (new StringBuilder()).append(getCurrentHour()).append("").toString());
                    AnalyticsUtils.trackEventWithKISS("received analysis complete notification", hashmap, true);
                    HashMap hashmap1 = new HashMap();
                    hashmap1.put("notification hour received", (new StringBuilder()).append(getCurrentHour()).append("").toString());
                    AnalyticsUtils.trackEventWithKISS("received notification", hashmap1, true);
                }
                long l = PreferencesManager.getAnalysisStartTime();
                long l1 = (System.currentTimeMillis() - l) / 1000L;
                HashMap hashmap2 = new HashMap();
                hashmap2.put("analysis duration", (new StringBuilder()).append(l1).append("").toString());
                hashmap2.put("total photos analyzed", (new StringBuilder()).append(mediaitemstat.getTotalPhotos()).append("").toString());
                AnalyticsUtils.trackEventWithKISS("finished analysis", hashmap2, true);
                Log.d(TAG, (new StringBuilder()).append("finished event: ").append(hashmap2.toString()).toString());
            }
        }
    }

    private void classifierNotification()
    {
        List list = GalleryDoctorDBHelper.getNewBadPhotos();
        if(list.size() >= 3)
        {
            Log.d(TAG, "classifier notification!");
            for(Iterator iterator = list.iterator(); iterator.hasNext(); ((MediaItem)iterator.next()).setWasAnalyzedByGD(Boolean.valueOf(true))) { }
            DBManager.getInstance().getDaoSession().getMediaItemDao().updateInTx(list);
            String s = getResources().getString(R.string.gallery_doctor_name);
            String s1 = getString(R.string.gallery_doctor_bad_photos_notification_header);
            String s2 = getString(R.string.gallery_doctor_bad_photos_notification_title);
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(list.size());
            if(notifyUser(s, s1, String.format(s2, aobj), "bad photos identified"))
            {
                HashMap hashmap = new HashMap();
                hashmap.put("bad photos identified notification hour received", (new StringBuilder()).append(getCurrentHour()).append("").toString());
                hashmap.put("bad photos identified notification number of bad photos identified", (new StringBuilder()).append(list.size()).append("").toString());
                AnalyticsUtils.trackEventWithKISS("received bad photos identified notification", hashmap, true);
                HashMap hashmap1 = new HashMap();
                hashmap1.put("notification hour received", (new StringBuilder()).append(getCurrentHour()).append("").toString());
                AnalyticsUtils.trackEventWithKISS("received notification", hashmap1, true);
            }
        }
    }

    private void duplicateNotification()
    {
        List list = GalleryDoctorDBHelper.getNewDuplicates();
        if(list.size() >= 3)
        {
            Log.d(TAG, "duplicate notification!");
            Iterator iterator = list.iterator();
            int i;
            DuplicatesSet duplicatesset;
            for(i = 0; iterator.hasNext(); i += duplicatesset.getDuplicatesSetPhotos().size())
            {
                duplicatesset = (DuplicatesSet)iterator.next();
                duplicatesset.setWasAnalyzedByGD(Boolean.valueOf(true));
            }

            DBManager.getInstance().getDaoSession().getDuplicatesSetDao().updateInTx(list);
            String s = getResources().getString(R.string.gallery_doctor_name);
            String s1 = getString(R.string.gallery_doctor_duplicate_photos_notification_header);
            String s2 = getString(R.string.gallery_doctor_duplicate_photos_notification_title);
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(i);
            if(notifyUser(s, s1, String.format(s2, aobj), "similar photos identified"))
            {
                HashMap hashmap = new HashMap();
                hashmap.put("similar photos identified notification hour received", (new StringBuilder()).append(getCurrentHour()).append("").toString());
                hashmap.put("similar photos identified notification number of similar photos identified", (new StringBuilder()).append(list.size()).append("").toString());
                AnalyticsUtils.trackEventWithKISS("received similar photos identified notification", hashmap, true);
                HashMap hashmap1 = new HashMap();
                hashmap1.put("notification hour received", (new StringBuilder()).append(getCurrentHour()).append("").toString());
                AnalyticsUtils.trackEventWithKISS("received notification", hashmap1, true);
            }
        }
    }

    private int getCurrentHour()
    {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public static List getIntentFilter()
    {
        ArrayList arraylist = new ArrayList();
        arraylist.add(IntentActions.ACTION_CLASSIFIER_FINISHED);
        arraylist.add(IntentActions.ACTION_DUPLICATES_FINISHED);
        arraylist.add(IntentActions.ACTION_NEW_MEDIA);
        return arraylist;
    }

    private void largeNumberOfPhotosNotfication()
    {
        com.flayvr.util.GalleryDoctorStatsUtils.MediaItemStat mediaitemstat = GalleryDoctorStatsUtils.getMediaItemStat(1);
        if(mediaitemstat.getTotalPhotos() >= 1000L)
        {
            if(PreferencesManager.getShouldShowLargeNumberOfPhotosReached())
            {
                Log.d(TAG, "large number of photos notification!");
                if(notifyUser(getResources().getString(R.string.gallery_doctor_name), getString(R.string.gallery_doctor_too_many_photos_notification_header), getString(R.string.gallery_doctor_too_many_photos_notification_title), "large number of photos"))
                {
                    PreferencesManager.setShouldShowLargeNumberOfPhotosReached(false);
                    HashMap hashmap = new HashMap();
                    hashmap.put("large number of photos notification hour received", (new StringBuilder()).append(getCurrentHour()).append("").toString());
                    hashmap.put("large number of photos notification number of photos", (new StringBuilder()).append(mediaitemstat.getTotalPhotos()).append("").toString());
                    AnalyticsUtils.trackEventWithKISS("received large number of photos notification", hashmap, true);
                    HashMap hashmap1 = new HashMap();
                    hashmap1.put("notification hour received", (new StringBuilder()).append(getCurrentHour()).append("").toString());
                    AnalyticsUtils.trackEventWithKISS("received notification", hashmap1, true);
                }
            }
        } else
        {
            PreferencesManager.setShouldShowLargeNumberOfPhotosReached(true);
        }
    }

    private void lowStorageNotification()
    {
        Log.d(TAG, "law storage notification!");
        try
        {
            com.flayvr.util.GalleryDoctorStatsUtils.DriveStat drivestat = (com.flayvr.util.GalleryDoctorStatsUtils.DriveStat)GalleryDoctorStatsUtils.getDriveStat(1).get();
            String s = getResources().getString(R.string.gallery_doctor_name);
            String s1 = getString(R.string.gallery_doctor_low_storage_text);
            Object aobj[] = new Object[1];
            aobj[0] = GeneralUtils.humanReadableByteCount(drivestat.getFreeSpace(), false);
            if(notifyUser(s, String.format(s1, aobj), getString(R.string.gallery_doctor_low_storage_notification_subtitle), "low space"))
            {
                HashMap hashmap = new HashMap();
                hashmap.put("low space notification hour received", (new StringBuilder()).append(getCurrentHour()).append("").toString());
                hashmap.put("low space notification free space on device", (new StringBuilder()).append(drivestat.getFreeSpace() / 1024L).append("").toString());
                AnalyticsUtils.trackEventWithKISS("received low space notification", hashmap, true);
                HashMap hashmap1 = new HashMap();
                hashmap1.put("notification hour received", (new StringBuilder()).append(getCurrentHour()).append("").toString());
                AnalyticsUtils.trackEventWithKISS("received notification", hashmap1, true);
            }
        }
        catch(Exception exception)
        {
            Log.e(TAG, exception.getMessage(), exception);
        }
    }

    public void delayAlarm(String s)
    {
        Log.d(TAG, (new StringBuilder()).append("delay alarm for action: ").append(s).toString());
        Intent intent = new Intent(this, GalleryDoctorService.class);
        intent.setAction("GD_DELAY_NOTIFICATION");
        intent.putExtra("GD_ACTION_EXTRA_KEY", s);
        PendingIntent pendingintent = PendingIntent.getService(this, 100, intent, 0);
        ((AlarmManager)getSystemService(ALARM_SERVICE)).set(0, 0x1b7740L + Calendar.getInstance().getTimeInMillis(), pendingintent);
    }

    public boolean notifyUser(String s, String s1, String s2, String s3)
    {
        long l = Calendar.getInstance().getTime().getTime() - PreferencesManager.getLastNotificationTime();
        if(l < BackendBasedSettings.getInstance().notificationWaitBetweenNotificationTime)
        {
            Log.d(TAG, (new StringBuilder()).append("Not enough time between notifications: ").append(TimeUnit.MILLISECONDS.toMinutes(l)).toString());
            return false;
        }
        PreferencesManager.setLastNotificationTime(Calendar.getInstance().getTime().getTime());
        android.support.v4.app.NotificationCompat.Builder builder = (new android.support.v4.app.NotificationCompat.Builder(this)).setSmallIcon(R.drawable.notif_app_icon).setContentTitle(s).setContentText(s1);
        if(s2 != null)
            builder.setSubText(s2);
        Intent intent = new Intent(this, GalleryDoctorDashboardActivity.class);
        intent.putExtra("NOTIFICATION_SOURCE", s3);
        builder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 1001, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(100, builder.build()); // wrong
        return true;
    }

    protected void onHandleIntent(Intent intent)
    {
        if(PreferencesManager.isAnalysisCompleteNotificationShown(1)) {
            String s;
            if(!PreferencesManager.isAnalysisCompleteNotificationShown(2))
            {
                checkAnalyticsFinished(2);
            }
            s = intent.getAction();
            Log.d(TAG, (new StringBuilder()).append("notification action: ").append(s).toString());
            if("android.intent.action.DEVICE_STORAGE_LOW".equals(s))
            {
                if(PreferencesManager.getLowDiskPhotosNotification())
                {
                    delayAlarm(s);
                }
            } else if(IntentActions.ACTION_CLASSIFIER_FINISHED.equals(s)) {
                if(PreferencesManager.getBadPhotosNotification() && GalleryDoctorDBHelper.getNewBadPhotos().size() >= 3)
                {
                    delayAlarm(s);
                }
            } else  if(IntentActions.ACTION_DUPLICATES_FINISHED.equals(s)) {
                if(PreferencesManager.getDuplicatePhotosNotification() && GalleryDoctorDBHelper.getNewDuplicates().size() >= 3)
                {
                    delayAlarm(s);
                }
            } else if(IntentActions.ACTION_NEW_MEDIA.equals(s)) {
                if(PreferencesManager.getLargeNumberOfPhotosNotification() && PreferencesManager.getShouldShowLargeNumberOfPhotosReached() && GalleryDoctorStatsUtils.getMediaItemStat(1).getTotalPhotos() >= 1000L)
                    delayAlarm(s);
            } else if(s.equals("GD_WEEKEND_NOTIFICATION")) {
                if(PreferencesManager.getWeekendNotification())
                {
                    Log.d(TAG, "weekend notification!");
                    if(notifyUser(getResources().getString(R.string.gallery_doctor_name), getString(R.string.gallery_doctor_cleanup_notification_header), getString(R.string.gallery_doctor_cleanup_notification_title), "weekend cleanup time"))
                    {
                        HashMap hashmap = new HashMap();
                        hashmap.put("weekend cleanup time notification hour received", (new StringBuilder()).append(getCurrentHour()).append("").toString());
                        AnalyticsUtils.trackEventWithKISS("received weekend cleanup time notification", hashmap, true);
                        HashMap hashmap1 = new HashMap();
                        hashmap1.put("notification hour received", (new StringBuilder()).append(getCurrentHour()).append("").toString());
                        AnalyticsUtils.trackEventWithKISS("received notification", hashmap1, true);
                    }
                }
            } else if(s.equals("GD_DELAY_NOTIFICATION")) {
                String s1 = intent.getStringExtra("GD_ACTION_EXTRA_KEY");
                Log.d(TAG, (new StringBuilder()).append("delay action: ").append(s1).toString());
                if ("android.intent.action.DEVICE_STORAGE_LOW".equals(s1)) {
                    lowStorageNotification();
                    return;
                }
                if (IntentActions.ACTION_CLASSIFIER_FINISHED.equals(s1)) {
                    classifierNotification();
                    return;
                }
                if (IntentActions.ACTION_DUPLICATES_FINISHED.equals(s1)) {
                    duplicateNotification();
                    return;
                }
                if (IntentActions.ACTION_NEW_MEDIA.equals(s1)) {
                    largeNumberOfPhotosNotfication();
                    return;
                }
            }
        } else {
            Log.d(TAG, "analytics not complete");
            checkAnalyticsFinished(1);
        }
    }
}

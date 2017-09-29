package com.flayvr.myrollshared.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.flayvr.myrollshared.application.AppConfiguration;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.Folder;
import java.util.Date;

public class SharedPreferencesManager
{

    private static final String DID_SEND_CONNECTED_TO_WEAR_EVENT = "DID_SEND_CONNECTED_TO_WEAR_EVENT";
    private static final String FOLDER_ANALYTICS_SENT_KEY = "FOLDER_ANALYTICS_SENT";
    private static final String GALLERY_BUILDER_RUN_KEY = "GALLERY_BUILDER_RUN";
    private static final String LAST_APP_OPENED_KEY = "LAST_APP_OPENED";
    private static final String LAST_ENRICHMENT_RUN_DATE_KEY = "LAST_ENRICHMENT_RUN_DATE_KEY";
    private static final String MIGRATED_DB_KEY33 = "MIGRATED_DB_3.3";
    private static final String MIN_IMAGE_FOR_DUPLICATE = "MIN_IMAGE_FOR_DUPLICATE";
    private static final String MYROLL_CLOUD_EMAIL = "MYROLL_CLOUD_EMAIL";
    private static final String PENDING_PICASA_CLEAN_REQUEST_KEY = "PENDING_PICASA_CLEAN_REQUEST";
    private static final String PICASA_FEED_LOADED_KEY = "ICASA_FEED_LOADED";
    private static final String PICASSA_USER_ACCOUNT_KEY = "PICASSA_USER_ACCOUNT_KEY";
    private static final String RATE_US_POP_UP_DONE = "RATE_US_POP_UP_DONE";
    private static final String RATE_US_POP_UP_FEEDBACK = "RATE_US_POP_UP_FEEDBACK";
    private static final String RATE_US_POP_UP_NEVER_SHOW = "RATE_US_POP_UP_NEVER_SHOW_2";
    private static final String RATE_US_POP_UP_REMIND_LATER = "RATE_US_POP_UP_REMIND_LATER_2";
    private static final String RATE_US_POP_UP_SHOWN = "RATE_US_POP_UP_SHOWN_2";
    private static final String SCORE_VERSION = "SCORE_VERSION";
    private static final String SENT_PHOTOS_COUNT_KEY = "SENT_PHOTOS_COUNT_";

    public SharedPreferencesManager()
    {
    }

    public static Boolean didSendConnectedToWearEvent()
    {
        return Boolean.valueOf(FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean("DID_SEND_CONNECTED_TO_WEAR_EVENT", false));
    }

    public static boolean galleryBuilderRun()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean("GALLERY_BUILDER_RUN", false);
    }

    public static String getBackupCloudEmail()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getString("MYROLL_CLOUD_EMAIL", null);
    }

    public static boolean getFolderAnalyticsSent()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean("FOLDER_ANALYTICS_SENT", false);
    }

    public static Date getLastAppOpen()
    {
        long l = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getLong("LAST_APP_OPENED", -1L);
        if(l == -1L)
        {
            return null;
        } else
        {
            return new Date(l);
        }
    }

    public static Date getLastEnrichmentRunDate()
    {
        long l = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getLong("LAST_ENRICHMENT_RUN_DATE_KEY", -1L);
        if(l == -1L)
        {
            return null;
        } else
        {
            return new Date(l);
        }
    }

    public static Integer getLastVersionOfScoreUpdate()
    {
        return Integer.valueOf(FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getInt("SCORE_VERSION", -1));
    }

    public static Integer getMinImageForDuplicate()
    {
        return Integer.valueOf(FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getInt("MIN_IMAGE_FOR_DUPLICATE", 2));
    }

    public static boolean getNeverShowRateUsPopup()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean("RATE_US_POP_UP_NEVER_SHOW_2", false);
    }

    public static String getPicasaUserAccount()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getString("PICASSA_USER_ACCOUNT_KEY", null);
    }

    public static boolean getRateUsPopupDone()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean("RATE_US_POP_UP_DONE", false);
    }

    public static boolean getRateUsPopupSentFeedback()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean("RATE_US_POP_UP_FEEDBACK", false);
    }

    public static boolean getRateUsPopupShown()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean("RATE_US_POP_UP_SHOWN_2", false);
    }

    public static boolean getRemindRateUsPopup()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean("RATE_US_POP_UP_REMIND_LATER_2", false);
    }

    public static boolean hasPendingPicasaCleanRequest()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean("PENDING_PICASA_CLEAN_REQUEST", false);
    }

    public static boolean hasPicasaFeedLoaded()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean("ICASA_FEED_LOADED", false);
    }

    public static boolean isMigratedDB33(Folder folder)
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean((new StringBuilder()).append("MIGRATED_DB_3.3").append(folder.getId()).toString(), false);
    }

    public static void setBackupCloudEmail(String s)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putString("MYROLL_CLOUD_EMAIL", s);
        editor.commit();
    }

    public static void setFolderAnalyticsSent()
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean("FOLDER_ANALYTICS_SENT", true);
        editor.commit();
    }

    public static void setGalleryBuilderRun()
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean("GALLERY_BUILDER_RUN", true);
        editor.commit();
    }

    public static void setLastAppOpen(Date date)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putLong("LAST_APP_OPENED", date.getTime());
        editor.commit();
    }

    public static void setLastEnrichmentRunDate(Date date)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putLong("LAST_ENRICHMENT_RUN_DATE_KEY", date.getTime());
        editor.commit();
    }

    public static void setLastVersionOfScoreUpdate(Integer integer)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putInt("SCORE_VERSION", integer.intValue());
        editor.commit();
    }

    public static void setMigratedDB33(Folder folder)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean((new StringBuilder()).append("MIGRATED_DB_3.3").append(folder.getId()).toString(), true);
        editor.commit();
    }

    public static void setMinImageForDuplicate(int i)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putInt("MIN_IMAGE_FOR_DUPLICATE", i);
        editor.commit();
    }

    public static void setNeverShowRateUsPopup()
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean("RATE_US_POP_UP_NEVER_SHOW_2", true);
        editor.commit();
    }

    public static void setPendingPicasaCleanRequest(boolean flag)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean("PENDING_PICASA_CLEAN_REQUEST", flag);
        editor.commit();
    }

    public static void setPicasaFeedLoaded(boolean flag)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean("ICASA_FEED_LOADED", flag);
        editor.commit();
    }

    public static void setPicasaUserAccount(String s)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putString("PICASSA_USER_ACCOUNT_KEY", s);
        editor.commit();
    }

    public static void setRateUsPopupDone()
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean("RATE_US_POP_UP_DONE", true);
        editor.commit();
    }

    public static void setRateUsPopupSentFeedback()
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean("RATE_US_POP_UP_FEEDBACK", true);
        editor.commit();
    }

    public static void setRateUsPopupShown()
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean("RATE_US_POP_UP_SHOWN_2", true);
        editor.commit();
    }

    public static void setRemindRateUsPopup()
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean("RATE_US_POP_UP_REMIND_LATER_2", true);
        editor.commit();
    }

    public static void setSentConnectedToWearEvent()
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean("DID_SEND_CONNECTED_TO_WEAR_EVENT", true);
        editor.commit();
    }

    public static void setSentPhotosCount(int i)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean((new StringBuilder()).append("SENT_PHOTOS_COUNT_").append(i).toString(), true);
        editor.commit();
    }

    public static boolean wasSentPhotosCount(int i)
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean((new StringBuilder()).append("SENT_PHOTOS_COUNT_").append(i).toString(), false);
    }
}

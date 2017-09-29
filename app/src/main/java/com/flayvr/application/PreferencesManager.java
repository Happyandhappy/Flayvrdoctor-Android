package com.flayvr.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.flayvr.myrollshared.application.AppConfiguration;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.*;

public class PreferencesManager
{

    private static final String ACCOMPLISHMENT_POP_UP_NUMBER_OF_TIMES_SHOWN = "ACCOMPLISHMENT_POP_UP_NUMBER_OF_TIMES_SHOWN";
    private static final String ANALYSIS_COMPLETE_NOTIDICATION_SHOWN_KEY = "ANALYSIS_COMPLETE_NOTIDICATION_SHOWN_KEY";
    private static final String APP_INSTALL_DATE_KEY = "APP_INSTALL_DATE";
    private static final String BACKUP_TEST_SHOWN = "BACKUP_TEST_SHOWN";
    private static final String BAD_PHOTOS_HINTS_SHOWN_KEY = "bad_photos_hints_shown_key";
    private static final String BAD_PHOTOS_NOTIF = "BAD_PHOTOS_NOTIF";
    private static final String CROSS_PROMOTION_CARD_SHOWN = "CROSS_PROMOTION_CARD_SHOWN";
    private static final String DUPLICATE_PHOTOS_HINTS_SHOWN_KEY = "duplicate_photos_hints_shown_key";
    private static final String DUPLICATE_PHOTOS_NOTIF = "DUPLICATE_PHOTOS_NOTIF";
    private static final String EXCLUDE_ALL_FOLDERS_KEY = "EXCLUDE_ALL_FOLDERS";
    private static final String FIRST_SESSION_HANDLED_KEY = "intro_displayed_3.3";
    private static final String LARGE_NUMBER_PHOTOS_NOTIF = "LARGE_NUMBER_PHOTOS_NOTIF";
    private static final String LAST_GD_NOTIFICAION_TIME = "LAST_GD_NOTIFICAION_TIME";
    private static final String LOW_SPACE_NOTIF = "LOW_SPACE_NOTIF";
    private static final String PHOTOS_FOR_REVIEW_DELETE_HINTS_SHOWN_KEY = "photos_for_review_delete_hints_shown_key";
    private static final String PHOTOS_FOR_REVIEW_HINTS_SHOWN_KEY = "photos_for_review_hints_shown_key";
    private static final String PHOTOS_FOR_REVIEW_LIKE_HINTS_SHOWN_KEY = "photos_for_review_like__hints_shown_key";
    private static final String SENT_APP_INTSALLED_EVENT_KEY = "APP_INSTALL_EVENT";
    private static final String SENT_CLASSIFIED_PHOTOS_DATA_KEY = "SENT_CLASSIFIED_PHOTOS_DATA";
    private static final String SESSIONS_COUNT_KEY = "SESSIONS_COUNT";
    private static final String SET_PARSE_INSTALATION_KEY = "SET_PARSE_INSTALATION_KEY";
    private static final String SHOULD_SHOW_LARGE_NUMBER_OF_PHOTOS_NOTIFICATION = "SHOULD_SHOW_LARGE_NUMBER_OF_PHOTOS_NOTIFICATION";
    private static final String START_ANALYSIS_TIME = "START_ANALYSIS_TIME";
    private static final String TAG = "flayvr_pref_manager";
    private static final String WEEKEND_NOTIF = "WEEKEND_NOTIF";
    private static final String WHATS_NEW_POPUP_SHOWED_KEY_3_2_6 = "whats_new_popup_showed_3_2_6";

    public PreferencesManager()
    {
    }

    public static boolean didSendClassifiedPhotosData()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean(SENT_CLASSIFIED_PHOTOS_DATA_KEY, false);
    }

    public static long getAnalysisStartTime()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getLong(START_ANALYSIS_TIME, -1L);
    }

    public static Date getAppInstallDate()
    {
        long l = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getLong(APP_INSTALL_DATE_KEY, -1L);
        if(l == -1L)
        {
            return null;
        } else
        {
            return new Date(l);
        }
    }

    public static boolean getBackupTestShown()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean(BACKUP_TEST_SHOWN, false);
    }

    public static boolean getBadPhotosNotification()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean(BAD_PHOTOS_NOTIF, true);
    }

    public static boolean getCrossPromotionCardShown()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean(CROSS_PROMOTION_CARD_SHOWN, false);
    }

    public static boolean getDuplicatePhotosNotification()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean(DUPLICATE_PHOTOS_NOTIF, true);
    }

    public static Set<Long> getExcludeAllFolder()
    {
        String s = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getString(EXCLUDE_ALL_FOLDERS_KEY, null);
        if(s != null)
        {
            return (Set)(new Gson()).fromJson(s, (new TypeToken<Set<Long>>(){}).getType());
        } else
        {
            return new HashSet();
        }
    }

    public static boolean getLargeNumberOfPhotosNotification()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean(LARGE_NUMBER_PHOTOS_NOTIF, true);
    }

    public static long getLastNotificationTime()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getLong(LAST_GD_NOTIFICAION_TIME, 0L);
    }

    public static boolean getLowDiskPhotosNotification()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean(LOW_SPACE_NOTIF, true);
    }

    public static int getNumberOfTimesAccomplishmentShown()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getInt(ACCOMPLISHMENT_POP_UP_NUMBER_OF_TIMES_SHOWN, 0);
    }

    public static int getSessionsCount()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getInt(SESSIONS_COUNT_KEY, 0);
    }

    public static boolean getShouldShowLargeNumberOfPhotosReached()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean(SHOULD_SHOW_LARGE_NUMBER_OF_PHOTOS_NOTIFICATION, true);
    }

    public static boolean getWeekendNotification()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean(WEEKEND_NOTIF, true);
    }

    public static boolean isAnalysisCompleteNotificationShown(int i)
    {
        SharedPreferences sharedpreferences = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0);
        if(i == 1)
        {
            return sharedpreferences.getBoolean(ANALYSIS_COMPLETE_NOTIDICATION_SHOWN_KEY, false);
        } else
        {
            return sharedpreferences.getBoolean((new StringBuilder()).append("ANALYSIS_COMPLETE_NOTIDICATION_SHOWN_KEY_").append(i).toString(), false);
        }
    }

    public static boolean isBadPhotosHintsShown()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).contains(BAD_PHOTOS_HINTS_SHOWN_KEY);
    }

    public static boolean isDuplicatePhotosHintsShown()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).contains(DUPLICATE_PHOTOS_HINTS_SHOWN_KEY);
    }

    public static boolean isFirstSessionHandled()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean(FIRST_SESSION_HANDLED_KEY, false);
    }

    public static boolean isPhotosForReviewDeleteHintsShown()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).contains(PHOTOS_FOR_REVIEW_DELETE_HINTS_SHOWN_KEY);
    }

    public static boolean isPhotosForReviewHintsShown()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).contains(PHOTOS_FOR_REVIEW_HINTS_SHOWN_KEY);
    }

    public static boolean isPhotosForReviewLikeHintsShown()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).contains(PHOTOS_FOR_REVIEW_LIKE_HINTS_SHOWN_KEY);
    }

    public static boolean isWhatsNewPoped()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).contains(WHATS_NEW_POPUP_SHOWED_KEY_3_2_6);
    }

    public static void sentAppInstalledEvent()
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean(SENT_APP_INTSALLED_EVENT_KEY, true);
        editor.commit();
    }

    public static void sentClassifiedPhotosData()
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean(SENT_CLASSIFIED_PHOTOS_DATA_KEY, true);
        editor.commit();
    }

    public static void setAnalysisCompleteNotificationShown(int i)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        if(i == 1)
        {
            editor.putBoolean(ANALYSIS_COMPLETE_NOTIDICATION_SHOWN_KEY, true);
        } else
        {
            editor.putBoolean((new StringBuilder()).append("ANALYSIS_COMPLETE_NOTIDICATION_SHOWN_KEY_").append(i).toString(), true);
        }
        editor.commit();
    }

    public static void setAnalysisStartTime(long l)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putLong(START_ANALYSIS_TIME, l);
        editor.commit();
    }

    public static void setAppInstallDate(Date date)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putLong(APP_INSTALL_DATE_KEY, date.getTime());
        editor.commit();
    }

    public static void setBackupTestShown()
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean(BACKUP_TEST_SHOWN, true);
        editor.commit();
    }

    public static void setBadPhotosNotification(boolean flag)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean(BAD_PHOTOS_NOTIF, flag);
        editor.commit();
    }

    public static void setCrossPromotionCardShown()
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean(CROSS_PROMOTION_CARD_SHOWN, true);
        editor.commit();
    }

    public static void setDuplicatePhotosNotification(boolean flag)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean(DUPLICATE_PHOTOS_NOTIF, flag);
        editor.commit();
    }

    public static void setExcludeAllFolder(Set<Long> set)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putString(EXCLUDE_ALL_FOLDERS_KEY, (new Gson()).toJson(set));
        editor.commit();
    }

    public static void setFirstSessionHandled()
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean(FIRST_SESSION_HANDLED_KEY, true);
        editor.commit();
    }

    public static void setIsBadPhotosHintsShown()
    {
        try
        {
            Context context = FlayvrApplication.getAppContext();
            int i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            android.content.SharedPreferences.Editor editor = context.getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
            editor.putInt(BAD_PHOTOS_HINTS_SHOWN_KEY, i);
            editor.commit();
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            Log.e(TAG, namenotfoundexception.getMessage(), namenotfoundexception);
        }
    }

    public static void setIsDuplicatePhotosHintsShown()
    {
        try
        {
            Context context = FlayvrApplication.getAppContext();
            int i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            android.content.SharedPreferences.Editor editor = context.getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
            editor.putInt(DUPLICATE_PHOTOS_HINTS_SHOWN_KEY, i);
            editor.commit();
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            Log.e(TAG, namenotfoundexception.getMessage(), namenotfoundexception);
        }
    }

    public static void setIsPhotosForReviewDeleteHintsShown()
    {
        try
        {
            Context context = FlayvrApplication.getAppContext();
            int i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            android.content.SharedPreferences.Editor editor = context.getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
            editor.putInt(PHOTOS_FOR_REVIEW_DELETE_HINTS_SHOWN_KEY, i);
            editor.commit();
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            Log.e(TAG, namenotfoundexception.getMessage(), namenotfoundexception);
        }
    }

    public static void setIsPhotosForReviewHintsShown()
    {
        try
        {
            Context context = FlayvrApplication.getAppContext();
            int i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            android.content.SharedPreferences.Editor editor = context.getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
            editor.putInt(PHOTOS_FOR_REVIEW_HINTS_SHOWN_KEY, i);
            editor.commit();
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            Log.e(TAG, namenotfoundexception.getMessage(), namenotfoundexception);
        }
    }

    public static void setIsPhotosForReviewLikeHintsShown()
    {
        try
        {
            Context context = FlayvrApplication.getAppContext();
            int i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            android.content.SharedPreferences.Editor editor = context.getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
            editor.putInt(PHOTOS_FOR_REVIEW_LIKE_HINTS_SHOWN_KEY, i);
            editor.commit();
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            Log.e(TAG, namenotfoundexception.getMessage(), namenotfoundexception);
        }
    }

    public static void setLargeNumberOfPhotosNotification(boolean flag)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean("LARGE_NUMBER_PHOTOS_NOTIF", flag);
        editor.commit();
    }

    public static void setLastNotificationTime(long l)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putLong("LAST_GD_NOTIFICAION_TIME", l);
        editor.commit();
    }

    public static void setLowDiskPhotosNotification(boolean flag)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean("LOW_SPACE_NOTIF", flag);
        editor.commit();
    }

    public static void setNumberOfTimesAccomplishmentShown(int i)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putInt("ACCOMPLISHMENT_POP_UP_NUMBER_OF_TIMES_SHOWN", i);
        editor.commit();
    }

    public static void setParseInstallSent()
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean(SET_PARSE_INSTALATION_KEY, true);
        editor.commit();
    }

    public static void setSessionsCount(int i)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putInt(SESSIONS_COUNT_KEY, i);
        editor.commit();
    }

    public static void setShouldShowLargeNumberOfPhotosReached(boolean flag)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean(SHOULD_SHOW_LARGE_NUMBER_OF_PHOTOS_NOTIFICATION, flag);
        editor.commit();
    }

    public static void setWeekendNotification(boolean flag)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        editor.putBoolean(WEEKEND_NOTIF, flag);
        editor.commit();
    }

    public static void setWhatsNewPoped()
    {
        try
        {
            Context context = FlayvrApplication.getAppContext();
            int i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            android.content.SharedPreferences.Editor editor = context.getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
            editor.putInt(WHATS_NEW_POPUP_SHOWED_KEY_3_2_6, i);
            editor.commit();
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            Log.e(TAG, namenotfoundexception.getMessage(), namenotfoundexception);
        }
    }

    public static boolean wasParseInstallSent()
    {
        return FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).getBoolean(SET_PARSE_INSTALATION_KEY, false);
    }

    public static boolean wasSentAppInstalledEvent()
    {
        SharedPreferences sharedpreferences = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0);
        boolean flag1;
        if(sharedpreferences.contains(SENT_APP_INTSALLED_EVENT_KEY))
        {
            flag1 = sharedpreferences.getBoolean(SENT_APP_INTSALLED_EVENT_KEY, false);
        } else
        {
            boolean flag = sharedpreferences.contains(FIRST_SESSION_HANDLED_KEY);
            flag1 = false;
            if(flag)
            {
                android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
                boolean flag2 = sharedpreferences.getBoolean(FIRST_SESSION_HANDLED_KEY, false);
                editor.putBoolean(SENT_APP_INTSALLED_EVENT_KEY, flag2);
                editor.commit();
                return flag2;
            }
        }
        return flag1;
    }
}

package com.flayvr.myrollshared.managers;

import android.content.Context;
import android.content.SharedPreferences;
import com.flayvr.myrollshared.application.AppConfiguration;
import com.flayvr.myrollshared.application.FlayvrApplication;

public class GalleryDoctorServicesProgress
{

    private static final String CLASSIFIER_FINISHED_PREF_KEY = "CLASSIFIER_FINISHED_PREF";
    private static final String CLASSIFIER_PROGRESS_PREF_KEY = "CLASSIFIER_PROGRESS_PREF";
    private static final String CV_FINISHED_PREF_KEY = "CV_FINISHED_PREF";
    private static final String CV_PROGRESS_PREF_KEY = "CV_PROGRESS_PREF";
    private static final String DUPLICATES_FINISHED_PREF_KEY = "DUPLICATES_FINISHED_PREF";
    private static final String DUPLICATES_PROGRESS_PREF_KEY = "DUPLICATES_PROGRESS_PREF";
    private static float initialClassifierServiceProgress[];
    private static float initialCvServiceProgress[];
    private static float initialDuplicatesServiceProgress[];

    public GalleryDoctorServicesProgress()
    {
    }

    public static void classifierServiceFinished(int i, boolean flag)
    {
        setClassifierServiceProgress(i, 1.0F);
        setServiceFinished(i, "CLASSIFIER_FINISHED_PREF", flag);
    }

    public static void cvServiceFinished(int i, boolean flag)
    {
        setCvServiceProgress(i, 1.0F);
        setServiceFinished(i, "CV_FINISHED_PREF", flag);
    }

    public static boolean didCVServiceFinish(int i)
    {
        return didServiceFinish(i, "CV_FINISHED_PREF");
    }

    public static boolean didClassifierServiceFinish(int i)
    {
        return didServiceFinish(i, "CLASSIFIER_FINISHED_PREF");
    }

    public static boolean didDuplicatesServiceFinish(int i)
    {
        return didServiceFinish(i, "DUPLICATES_FINISHED_PREF");
    }

    private static boolean didServiceFinish(int i, String s)
    {
        SharedPreferences sharedpreferences = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0);
        if(i == 1)
        {
            return sharedpreferences.getBoolean(s, false);
        } else
        {
            return sharedpreferences.getBoolean((new StringBuilder()).append(s).append("_").append(i).toString(), false);
        }
    }

    public static void duplicatesServiceFinished(int i, boolean flag)
    {
        setDuplicatesServiceProgress(i, 1.0F);
        setServiceFinished(i, "DUPLICATES_FINISHED_PREF", flag);
    }

    public static float getCVServiceProgress(int i)
    {
        return getServiceProgress(i, "CV_PROGRESS_PREF");
    }

    public static float getClassifierServiceProgress(int i)
    {
        return getServiceProgress(i, "CLASSIFIER_PROGRESS_PREF");
    }

    public static float getDuplicatesServiceProgress(int i)
    {
        return getServiceProgress(i, "DUPLICATES_PROGRESS_PREF");
    }

    private static float getServiceProgress(int i, String s)
    {
        SharedPreferences sharedpreferences = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0);
        if(i == 1)
        {
            return sharedpreferences.getFloat(s, 0.0F);
        } else
        {
            return sharedpreferences.getFloat((new StringBuilder()).append(s).append("_").append(i).toString(), 0.0F);
        }
    }

    public static int getTotalPrecentage(int i)
    {
        return (int)(100D * (0.59999999999999998D * (double)getCVServiceProgress(i) + 0.34999999999999998D * (double)getDuplicatesServiceProgress(i) + 0.050000000000000003D * (double)getClassifierServiceProgress(i)));
    }

    public static void setClassifierServiceProgress(int i, float f)
    {
        setServiceProgress(i, "CLASSIFIER_PROGRESS_PREF", initialClassifierServiceProgress[i] + f * (1.0F - initialClassifierServiceProgress[i]));
    }

    public static void setCvServiceProgress(int i, float f)
    {
        setServiceProgress(i, "CV_PROGRESS_PREF", initialCvServiceProgress[i] + f * (1.0F - initialCvServiceProgress[i]));
    }

    public static void setDuplicatesServiceProgress(int i, float f)
    {
        setServiceProgress(i, "DUPLICATES_PROGRESS_PREF", initialDuplicatesServiceProgress[i] + f * (1.0F - initialDuplicatesServiceProgress[i]));
    }

    private static void setServiceFinished(int i, String s, boolean flag)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        if(i == 1)
        {
            editor.putBoolean(s, flag);
        } else
        {
            editor.putBoolean((new StringBuilder()).append(s).append("_").append(i).toString(), flag);
        }
        editor.commit();
    }

    private static void setServiceProgress(int i, String s, float f)
    {
        android.content.SharedPreferences.Editor editor = FlayvrApplication.getAppContext().getSharedPreferences(AppConfiguration.getConfiguration().getSharedPrefFile(), 0).edit();
        if(i == 1)
        {
            editor.putFloat(s, f);
        } else
        {
            editor.putFloat((new StringBuilder()).append(s).append("_").append(i).toString(), f);
        }
        editor.commit();
    }

    static 
    {
        float af[] = new float[3];
        af[0] = -1F;
        af[1] = getServiceProgress(1, "CV_PROGRESS_PREF");
        af[2] = getServiceProgress(2, "CV_PROGRESS_PREF");
        initialCvServiceProgress = af;
        float af1[] = new float[3];
        af1[0] = -1F;
        af1[1] = getServiceProgress(1, "DUPLICATES_PROGRESS_PREF");
        af1[2] = getServiceProgress(2, "DUPLICATES_PROGRESS_PREF");
        initialDuplicatesServiceProgress = af1;
        float af2[] = new float[3];
        af2[0] = -1F;
        af2[1] = getServiceProgress(1, "CLASSIFIER_PROGRESS_PREF");
        af2[2] = getServiceProgress(2, "CLASSIFIER_PROGRESS_PREF");
        initialClassifierServiceProgress = af2;
    }
}

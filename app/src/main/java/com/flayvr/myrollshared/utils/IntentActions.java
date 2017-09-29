package com.flayvr.myrollshared.utils;

import android.content.Context;
import com.flayvr.myrollshared.application.FlayvrApplication;

public class IntentActions
{
    public static final String packageName = FlayvrApplication.getAppContext().getPackageName()+".";
    public static final String ACTION_ANDROID_CONTENT_CHANGED = (new StringBuilder()).append(packageName).append("actions.ACTION_ANDROID_CONTENT_CHANGED").toString();
    public static final String ACTION_APP_CREATE = (new StringBuilder()).append(packageName).append("actions.ACTION_APP_CREATE").toString();
    public static final String ACTION_CLASSIFIER_FINISHED = (new StringBuilder()).append(packageName).append("actions.ACTION_CLASSIFIER_FINISHED").toString();
    public static final String ACTION_CLUSTERING_FINISHED = (new StringBuilder()).append(packageName).append("actions.ACTION_CLUSTERING_FINISHED").toString();
    public static final String ACTION_CONTENT_OBSERVER = (new StringBuilder()).append(packageName).append("actions.ACTION_CONTENT_OBSERVER").toString();
    public static final String ACTION_CV_FINISHED = (new StringBuilder()).append(packageName).append("actions.ACTION_CV_FINISHED").toString();
    public static final String ACTION_CV_FINISHED_ON_BATCH = (new StringBuilder()).append(packageName).append("actions.ACTION_CV_FINISHED_ON_BATCH").toString();
    public static final String ACTION_DELETE_FOLDERS = (new StringBuilder()).append(packageName).append("actions.ACTION_DELETE_FOLDERS").toString();
    public static final String ACTION_DELETE_ITEMS = (new StringBuilder()).append(packageName).append("actions.ACTION_DELETE_ITEMS").toString();
    public static final String ACTION_DUPLICATES_FINISHED = (new StringBuilder()).append(packageName).append("actions.ACTION_DUPLICATES_FINISHED").toString();
    public static final String ACTION_GALLERY_BUILDER_NO_CHANGE = (new StringBuilder()).append(packageName).append("actions.ACTION_GALLERY_BUILDER_NO_CHANGE").toString();
    public static final String ACTION_MYROLL_CLOUD_SESSION_CHANGED = (new StringBuilder()).append(packageName).append("actions.ACTION_MYROLL_CLOUD_SESSION_CHANGED").toString();
    public static final String ACTION_MYROLL_CLOUD_UPDATE_FOLDER = (new StringBuilder()).append(packageName).append("actions.ACTION_MYROLL_CLOUD_UPDATE_FOLDER").toString();
    public static final String ACTION_NEW_MEDIA = (new StringBuilder()).append(packageName).append("actions.ACTION_NEW_MEDIA").toString();
    public static final String ACTION_NEW_MOMENTS = (new StringBuilder()).append(packageName).append("actions.ACTION_NEW_MOMENT").toString();
    public static final String ACTION_PICASA_CLEAN_DATA = (new StringBuilder()).append(packageName).append("actions.ACTION_PICASA_CLEAN_DATA").toString();
    public static final String ACTION_PICASA_FETCH_DONE = (new StringBuilder()).append(packageName).append("actions.ACTION_PICASA_FETCH_DONE").toString();
    public static final String ACTION_PICASA_SESSIOM_CHANGED = (new StringBuilder()).append(packageName).append("actions.ACTION_PICASA_SESSIOM_CHANGED").toString();
    public static final String ACTION_PICASA_UPDATE_FOLDER = (new StringBuilder()).append(packageName).append("actions.ACTION_PICASA_UPDATE_FOLDER").toString();
    public static final String ACTION_SMART_MODE_READY = (new StringBuilder()).append(packageName).append("actions.ACTION_SMART_MODE_READY").toString();
    public static final String ACTION_USER_INFO_FETCHED = (new StringBuilder()).append(packageName).append("actions.USER_INFO_FETCHED").toString();
    public static final String EXTRA_FOLDER = (new StringBuilder()).append(packageName).append("extras.FOLDER").toString();
    public static final String EXTRA_FOLDERS_CHANGED = (new StringBuilder()).append(packageName).append("extras.FOLDERS_CHANGED").toString();
    public static final String EXTRA_FOLDERS_REMOVED = (new StringBuilder()).append(packageName).append("extras.ITEMS_REMOVED").toString();
    public static final String EXTRA_FOLDERS_WITH_ADDED = (new StringBuilder()).append(packageName).append("extras.FOLDERS_WITH_ADDED").toString();
    public static final String EXTRA_HOME_CHANGED = (new StringBuilder()).append(packageName).append("extras.HOME_CHANGED").toString();
    public static final String EXTRA_ITEMS_ADDED = (new StringBuilder()).append(packageName).append("extras.ITEMS_ADDED").toString();
    public static final String EXTRA_ITEMS_REMOVED = (new StringBuilder()).append(packageName).append("extras.ITEMS_REMOVED").toString();
    public static final String EXTRA_MOMENTS_ADDED = (new StringBuilder()).append(packageName).append("extras.MOMENTS_ADDED").toString();
    public static final String EXTRA_MOMENTS_REMOVED = (new StringBuilder()).append(packageName).append("extras.MOMENTS_REMOVED").toString();
    public static final String EXTRA_URI = (new StringBuilder()).append(packageName).append("extras.URI").toString();
}

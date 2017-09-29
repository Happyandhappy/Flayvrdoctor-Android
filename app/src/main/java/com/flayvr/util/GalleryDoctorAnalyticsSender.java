package com.flayvr.util;

import android.util.Log;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.learning.PhotoFeatures;
import com.flayvr.myrollshared.managers.UserManager;
import com.flayvr.myrollshared.server.ServerUrls;
import com.flayvr.myrollshared.utils.FlayvrHttpClient;
import java.io.File;
import java.util.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

public class GalleryDoctorAnalyticsSender
{

    private static final String TAG = GalleryDoctorAnalyticsSender.class.getSimpleName();

    public GalleryDoctorAnalyticsSender()
    {
    }

    private static void addClassifiedPhotoData(MediaItem mediaitem, JSONObject jsonobject)
    {
        try {
            jsonobject.put("photo_id", mediaitem.getAndroidId());
            jsonobject.put("is_bad", mediaitem.getIsBad());
            jsonobject.put("is_for_review", mediaitem.getIsForReview());
            List list = mediaitem.getPhotoClassifierRules();
            User user = User.getMyRollUser();
            String s = null;
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    s = "";
                }
                s = (new StringBuilder()).append(s).append(((ClassifierRulesToPhotos) list.get(i)).getClassifierRule().getRuleType()).toString();
                if (i < -1 + list.size()) {
                    s = (new StringBuilder()).append(s).append(",").toString();
                }
            }

            jsonobject.put("rules", s);
            jsonobject.put("taken_at", mediaitem.getDate());
            jsonobject.put("latitude", mediaitem.getLatitude());
            jsonobject.put("longitude", mediaitem.getLongitude());
            String s1 = mediaitem.getPath();
            jsonobject.put("file_size", (double) mediaitem.getFileSizeBytesSafe().longValue() / 1048576D);
            jsonobject.put("file_path", s1);

            int j = s1.lastIndexOf(File.separatorChar);
            jsonobject.put("file_name", s1.substring(j + 1));
            String s2 = s1.substring(0, j);
            jsonobject.put("folder_name", s2.substring(1 + s2.lastIndexOf(File.separatorChar)));
            int k = s1.indexOf(File.separatorChar, 1);
            jsonobject.put("disk_name", s1.substring(k + 1, s1.indexOf(File.separatorChar, k + 1)));

            jsonobject.put("score", mediaitem.getScore());
            jsonobject.put("blurry_score", mediaitem.getBlurry());
            jsonobject.put("dark_score", mediaitem.getDark());
            jsonobject.put("colorful_score", mediaitem.getColor());
            jsonobject.put("faces", mediaitem.getFacesCount());
            jsonobject.put("is_taken_at_work", PhotoFeatures.isTakenAtWork(mediaitem, user));
        } catch (IndexOutOfBoundsException indexoutofboundsexception) {
            Log.e(TAG, "bad path", indexoutofboundsexception);
        } catch(Exception e){}
    }

    private static void sendClassifiedPhotosData(Collection collection)
    {
        JSONObject jsonobject;
        JSONArray jsonarray;
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("uuid", UserManager.getInstance().getUserId());
            jsonarray = new JSONArray();
            JSONObject jsonobject1;
            for(Iterator iterator = collection.iterator(); iterator.hasNext(); jsonarray.put(jsonobject1))
            {
                MediaItem mediaitem = (MediaItem)iterator.next();
                jsonobject1 = new JSONObject();
                addClassifiedPhotoData(mediaitem, jsonobject1);
            }
            jsonobject.put("photos", jsonarray);
            FlayvrHttpClient flayvrhttpclient = new FlayvrHttpClient();
            HttpPost httppost = new HttpPost(ServerUrls.GD_CLASSIFIED_PHOTOS_DATA_URL);
            StringEntity stringentity = new StringEntity(jsonobject.toString(), "UTF-8");
            stringentity.setContentType("application/json");
            httppost.setEntity(stringentity);
            flayvrhttpclient.executeWithRetries(httppost);
        }
        catch(Exception exception)
        {
            Log.e(TAG, "error while sending folder info", exception);
        }
    }

    public static void sendClassifiedPhotosDataAsync(final Collection photos)
    {
        FlayvrApplication.runNetwork(new Runnable() {
            @Override
            public void run() {
                GalleryDoctorAnalyticsSender.sendClassifiedPhotosData(photos);
            }
        });
    }

    private static void sendDoctorUserAction(DoctorActionType doctoractiontype, int i, int j, long l)
    {
        try
        {
            JSONObject jsonobject = new JSONObject();
            jsonobject.put("uuid", UserManager.getInstance().getUserId());
            JSONObject jsonobject1 = new JSONObject();
            jsonobject1.put("action_type", doctoractiontype.name());
            jsonobject1.put("total_photos_deleted", i);
            jsonobject1.put("total_photos_kept", j);
            jsonobject1.put("total_space_deleted", (double)l / 1048576D);
            jsonobject.put("data", jsonobject1);
            FlayvrHttpClient flayvrhttpclient = new FlayvrHttpClient();
            HttpPost httppost = new HttpPost(ServerUrls.GD_DOCTOR_USER_ACTION_URL);
            StringEntity stringentity = new StringEntity(jsonobject.toString(), "UTF-8");
            stringentity.setContentType("application/json");
            httppost.setEntity(stringentity);
            flayvrhttpclient.executeWithRetries(httppost);
        }
        catch(Exception exception)
        {
            Log.e(TAG, "error while sending folder info", exception);
        }
    }

    public static void sendDoctorUserActionAsync(final DoctorActionType actionType, final int totalPhotosDeleted, final int totalPhotosKept, final long totalSpaceDeleted)
    {
        FlayvrApplication.runNetwork(new Runnable() {
            @Override
            public void run() {
                GalleryDoctorAnalyticsSender.sendDoctorUserAction(actionType, totalPhotosDeleted, totalPhotosKept, totalSpaceDeleted);
            }
        });
    }

    private static void sendDoctorUserData(long l, long l1, long l2, long l3, 
            long l4, long l5, float f, long l6, 
            long l7, long l8, long l9, long l10, long l11)
    {
        JSONObject jsonobject;
        JSONObject jsonobject1;
        ClassifierThreshold classifierthreshold;
        FlayvrHttpClient flayvrhttpclient;
        HttpPost httppost;
        StringEntity stringentity;
        try
        {
            jsonobject = new JSONObject();
            jsonobject.put("uuid", UserManager.getInstance().getUserId());
            jsonobject1 = new JSONObject();
            jsonobject1.put("total_photos", l);
            jsonobject1.put("total_videos", l1);
            jsonobject1.put("total_space", (double)l2 / 1048576D);
            jsonobject1.put("total_free_space", (double)l3 / 1048576D);
            jsonobject1.put("total_photos_space", (double)l4 / 1048576D);
            jsonobject1.put("total_videos_space", (double)l5 / 1048576D);
            jsonobject1.put("gallery_health", f);
            jsonobject1.put("total_bad_photos", l6);
            jsonobject1.put("total_bad_photos_space", (double)l7 / 1048576D);
            jsonobject1.put("total_similar_photos", l8);
            jsonobject1.put("total_similar_photos_space", (double)l9 / 1048576D);
            jsonobject1.put("total_photos_for_review", l10);
            jsonobject1.put("total_photos_for_review_space", (double)l11 / 1048576D);
            classifierthreshold = DaoHelper.getThreshold(1);
            if(classifierthreshold != null)
            {
                jsonobject1.put("dark_score_threshold", classifierthreshold.getBadDark());
                jsonobject1.put("blurry_score_percentile", 5);
                jsonobject1.put("blurry_score_threshold", classifierthreshold.getBadBlurry());
                jsonobject1.put("bad_score_percentile", 5);
                jsonobject1.put("bad_score_threshold", classifierthreshold.getBadScore());
                jsonobject1.put("for_review_score_percentile", 15);
                jsonobject1.put("for_review_score_threshold", classifierthreshold.getForReviewScore());
                jsonobject1.put("good_enough_score_percentile", 60);
                jsonobject1.put("good_enough_score_threshold", classifierthreshold.getGoodEnoughScore());
            }
            jsonobject.put("data", jsonobject1);
            flayvrhttpclient = new FlayvrHttpClient();
            httppost = new HttpPost(ServerUrls.GD_DOCTOR_USER_DATA_URL);
            stringentity = new StringEntity(jsonobject.toString(), "UTF-8");
            stringentity.setContentType("application/json");
            httppost.setEntity(stringentity);
            flayvrhttpclient.executeWithRetries(httppost);
        }
        catch(Exception exception)
        {
            Log.e(TAG, "error while sending folder info", exception);
        }
    }

    public static void sendDoctorUserDataAsync(final long totalPhotos, final long totalVideos, final long totalSpace, final long totalFreeSpace, 
            final long totalPhotosSpace, final long totalVideosSpace, final float galleryHealth, final long totalBadPhotos, 
            final long totalBadPhotosSpace, final long totalSimilarPhotos, final long totalSimilarPhotosSpace, final long totalPhotosForReview, final long totalPhotosForReviewSpace)
    {
        FlayvrApplication.runNetwork(new Runnable() {
            @Override
            public void run() {
                GalleryDoctorAnalyticsSender.sendDoctorUserData(totalPhotos, totalVideos, totalSpace, totalFreeSpace, totalPhotosSpace, totalVideosSpace, galleryHealth, totalBadPhotos, totalBadPhotosSpace, totalSimilarPhotos, totalSimilarPhotosSpace, totalPhotosForReview, totalPhotosForReviewSpace);
            }
        });
    }

    private static void sendLabeledPhotosData(Collection collection, boolean flag, boolean flag1)
    {
        JSONObject jsonobject;
        JSONArray jsonarray;
        Iterator iterator;
        MediaItem mediaitem;
        JSONObject jsonobject1;
        FlayvrHttpClient flayvrhttpclient;
        HttpPost httppost;
        StringEntity stringentity;
        boolean flag2;
        jsonobject = new JSONObject();
        try
        {
            jsonobject.put("uuid", UserManager.getInstance().getUserId());
            jsonarray = new JSONArray();
            iterator = collection.iterator();
            while(iterator.hasNext())
            {
                mediaitem = (MediaItem)iterator.next();
                jsonobject1 = new JSONObject();
                addClassifiedPhotoData(mediaitem, jsonobject1);
                jsonobject1.put("is_bad", flag);
                jsonobject1.put("is_for_review", flag1);
                if(mediaitem.getWasKeptByUser() != null && mediaitem.getWasKeptByUser().booleanValue())
                    flag2 = false;
                else
                    flag2 = true;
                jsonobject1.put("was_deleted", flag2);
                jsonarray.put(jsonobject1);
            }
            jsonobject.put("photos", jsonarray);
            flayvrhttpclient = new FlayvrHttpClient();
            httppost = new HttpPost(ServerUrls.GD_LABELED_PHOTOS_DATA_URL);
            stringentity = new StringEntity(jsonobject.toString(), "UTF-8");
            stringentity.setContentType("application/json");
            httppost.setEntity(stringentity);
            flayvrhttpclient.executeWithRetries(httppost);
        }
        catch(Exception exception)
        {
            Log.e(TAG, "error while sending folder info", exception);
        }
    }

    public static void sendLabeledPhotosDataAsync(final Collection photos, final boolean isBad, final boolean isForReview)
    {
        FlayvrApplication.runNetwork(new Runnable() {
            @Override
            public void run() {
                GalleryDoctorAnalyticsSender.sendLabeledPhotosData(photos, isBad, isForReview);
            }
        });
    }


    public enum DoctorActionType
    {
        CLEAN_BAD_PHOTOS("CLEAN_BAD_PHOTOS", 0),
        CLEAN_SIMILAR_SET("CLEAN_SIMILAR_SET", 1),
        KEEP_SIMILAR_SET("KEEP_SIMILAR_SET", 2),
        CLEAN_ALL_SIMILAR_SETS("CLEAN_ALL_SIMILAR_SETS", 3),
        CLEAN_PHOTOS_FOR_REVIEW("CLEAN_PHOTOS_FOR_REVIEW", 4),
        CLEAN_WHATSAPP_FOR_REVIEW("CLEAN_WHATSAPP_FOR_REVIEW", 5),
        CLEAN_VIDEOS("CLEAN_VIDEOS", 6),
        CLEAN_SCREENSHOTS("CLEAN_SCREENSHOTS", 7);

        private DoctorActionType(String s, int i)
        {
        }
    }
}

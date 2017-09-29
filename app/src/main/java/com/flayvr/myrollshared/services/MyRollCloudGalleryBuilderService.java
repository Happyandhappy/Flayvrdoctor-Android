package com.flayvr.myrollshared.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.managers.UserManager;
import com.flayvr.myrollshared.server.ServerUrls;
import com.flayvr.myrollshared.utils.*;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import org.apache.http.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.*;

public class MyRollCloudGalleryBuilderService extends IntentService
{

    private static final String TAG = MyRollCloudGalleryBuilderService.class.getSimpleName();

    public MyRollCloudGalleryBuilderService()
    {
        super(MyRollCloudGalleryBuilderService.class.getSimpleName());
    }

    public MyRollCloudGalleryBuilderService(String s)
    {
        super(s);
    }

    private MediaItem createMediaItemFromJson(JSONObject jsonobject, Folder folder)
    {
        MediaItem mediaitem = new MediaItem();
        mediaitem.setType(Integer.valueOf(1));
        try {
            mediaitem.setPath(jsonobject.getString("url"));
            mediaitem.setThumbnail(jsonobject.getString("thumb_url"));
            try {
                mediaitem.setDate(AndroidUtils.getServerDateFormat().parse(jsonobject.getString("taken_at")));
            } catch (ParseException parseexception) {
                Log.e(TAG, parseexception.getMessage(), parseexception);
            }
            mediaitem.setLatitude(Double.valueOf(jsonobject.getDouble("latitude")));
            mediaitem.setLongitude(Double.valueOf(jsonobject.getDouble("longitude")));
            mediaitem.setOrientation(Integer.valueOf(jsonobject.getInt("orientation")));
            mediaitem.setWidth(Integer.valueOf(jsonobject.getInt("width")));
            mediaitem.setHeight(Integer.valueOf(jsonobject.getInt("height")));
            if (mediaitem.getWidth() != null && mediaitem.getHeight() != null && mediaitem.getHeight().intValue() != 0) {
                mediaitem.setProp(Float.valueOf((1.0F * (float) mediaitem.getWidth().intValue()) / (float) mediaitem.getHeight().intValue()));
            }
            mediaitem.setFileSizeBytes(Long.valueOf(jsonobject.getLong("size")));
        }catch(JSONException e){}
        mediaitem.setSource(Integer.valueOf(3));
        mediaitem.setFolder(folder);
        mediaitem.setWasClustered(Boolean.valueOf(false));
        mediaitem.setWasMinimizedByUser(Boolean.valueOf(false));
        mediaitem.setInteractionScore(Double.valueOf(0.0D));
        return mediaitem;
    }

    private HashMap getCurrFolders()
    {
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getFolderDao().queryBuilder();
        querybuilder.where(com.flayvr.myrollshared.data.FolderDao.Properties.Source.eq(Integer.valueOf(3)), new WhereCondition[0]);
        HashMap hashmap = new HashMap();
        Folder folder;
        for(Iterator iterator = querybuilder.list().iterator(); iterator.hasNext(); hashmap.put(folder.getName(), folder))
        {
            folder = (Folder)iterator.next();
        }

        return hashmap;
    }

    private HashMap getCurrItemsForFolder(Folder folder)
    {
        HashMap hashmap = new HashMap();
        if(folder.getId() == null)
        {
            return hashmap;
        }
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getMediaItemDao().queryBuilder();
        querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.Source.eq(Integer.valueOf(3)), new WhereCondition[0]);
        querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.eq(folder.getId()), new WhereCondition[0]);
        querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.isNull(), new WhereCondition[0]);
        MediaItem mediaitem;
        for(Iterator iterator = querybuilder.list().iterator(); iterator.hasNext(); hashmap.put(mediaitem.getPath(), mediaitem))
        {
            mediaitem = (MediaItem)iterator.next();
        }

        return hashmap;
    }

    public static List getIntentFilter()
    {
        ArrayList arraylist = new ArrayList();
        arraylist.add(IntentActions.ACTION_MYROLL_CLOUD_SESSION_CHANGED);
        arraylist.add(IntentActions.ACTION_MYROLL_CLOUD_UPDATE_FOLDER);
        return arraylist;
    }

    private Map loadFolders()
    {
        int i;
        HashMap hashmap;
        i = 0;
        hashmap = new HashMap();
        JSONArray jsonarray;
        FlayvrHttpClient flayvrhttpclient = new FlayvrHttpClient();
        HttpPost httppost = new HttpPost(ServerUrls.MYROLL_CLOUD_GET_ALL_FOLDERS);
        JSONObject jsonobject = new JSONObject();
        try
        {
            jsonobject.put("uuid", UserManager.getInstance().getUserId());
            jsonobject.put("subscription_email", SharedPreferencesManager.getBackupCloudEmail());
            StringEntity stringentity = new StringEntity(jsonobject.toString(), "UTF-8");
            stringentity.setContentType("application/json");
            httppost.setEntity(stringentity);
            HttpResponse httpresponse = flayvrhttpclient.executeWithRetries(httppost);
            if(httpresponse.getStatusLine().getStatusCode() != 200)
            {
                Log.e(TAG, "Error while loading myroll cloud folders");
            }else {
                InputStream inputstream = httpresponse.getEntity().getContent();
                String s = GeneralUtils.convertStreamToString(inputstream);
                inputstream.close();
                jsonarray = (new JSONObject(s)).getJSONArray("folders");
                while (i < jsonarray.length()) {
                    JSONObject jsonobject1 = jsonarray.getJSONObject(i);
                    Folder folder = new Folder();
                    folder.setName(jsonobject1.getString("folder_name"));
                    folder.setSource(Integer.valueOf(3));
                    folder.setIsCamera(Boolean.valueOf(false));
                    hashmap.put(folder.getName(), folder);
                    i++;
                }
            }
        }
        catch(IOException ioexception)
        {
            Log.e(TAG, ioexception.getMessage(), ioexception);
            return hashmap;
        }
        catch(JSONException jsonexception)
        {
            Log.e(TAG, jsonexception.getMessage(), jsonexception);
            return hashmap;
        }
        return hashmap;
    }

    private Map loadItemsForFolder(Folder folder)
    {
        HashMap hashmap = new HashMap();
        JSONArray jsonarray;
        FlayvrHttpClient flayvrhttpclient = new FlayvrHttpClient();
        HttpPost httppost = new HttpPost(ServerUrls.MYROLL_CLOUD_GET_PHOTOS_OF_FOLDER);
        JSONObject jsonobject = new JSONObject();
        try
        {
            jsonobject.put("uuid", UserManager.getInstance().getUserId());
            jsonobject.put("subscription_email", SharedPreferencesManager.getBackupCloudEmail());
            jsonobject.put("folder_name", folder.getName());
            StringEntity stringentity = new StringEntity(jsonobject.toString(), "UTF-8");
            stringentity.setContentType("application/json");
            httppost.setEntity(stringentity);
            HttpResponse httpresponse = flayvrhttpclient.executeWithRetries(httppost);
            if(httpresponse.getStatusLine().getStatusCode() != 200)
            {
                Log.e(TAG, (new StringBuilder()).append("Error while loading photos for folder ").append(folder.getName()).append(" from myroll cloud").toString());
            }else {
                InputStream inputstream = httpresponse.getEntity().getContent();
                String s = GeneralUtils.convertStreamToString(inputstream);
                inputstream.close();
                jsonarray = (new JSONObject(s)).getJSONArray("photos");
                int i = 0;
                while (i < jsonarray.length()) {
                    MediaItem mediaitem = createMediaItemFromJson(jsonarray.getJSONObject(i), folder);
                    hashmap.put(mediaitem.getPath(), mediaitem);
                    i++;
                }
            }
        }
        catch(IOException ioexception)
        {
            Log.e(TAG, ioexception.getMessage(), ioexception);
        }
        catch(JSONException jsonexception)
        {
            Log.e(TAG, jsonexception.getMessage(), jsonexception);
        }
        return hashmap;
    }

    protected void onHandleIntent(Intent intent)
    {
        Log.d(TAG, (new StringBuilder()).append("start building myroll cloud gallery service ").append(intent.getAction()).toString());
        if(IntentActions.ACTION_MYROLL_CLOUD_SESSION_CHANGED.equals(intent.getAction()))
        {
            Map map1 = loadFolders();
            if(map1 == null)
            {
                Log.d(TAG, "error while getting folders for myroll cloud");
                return;
            }
            Log.d(TAG, (new StringBuilder()).append("received ").append(map1.size()).append(" folders for myroll cloud").toString());
            HashMap hashmap1 = getCurrFolders();
            map1.keySet().removeAll(hashmap1.keySet());
            Log.d(TAG, (new StringBuilder()).append(map1.size()).append(" new folders for myroll cloud").toString());
            DBManager.getInstance().getDaoSession().getFolderDao().insertInTx(map1.values());
            for(Iterator iterator1 = map1.values().iterator(); iterator1.hasNext();)
            {
                Folder folder1 = (Folder)iterator1.next();
                Map map2 = loadItemsForFolder(folder1);
                if(map2 == null)
                {
                    Log.d(TAG, (new StringBuilder()).append("error while getting items for folder ").append(folder1.getName()).append(" for myroll cloud").toString());
                } else
                {
                    Log.d(TAG, (new StringBuilder()).append("received ").append(map2.size()).append(" for folder ").append(folder1.getName()).append(" for myroll cloud").toString());
                    DBManager.getInstance().getDaoSession().getMediaItemDao().insertInTx(map2.values());
                }
            }

        } else
        if(IntentActions.ACTION_MYROLL_CLOUD_UPDATE_FOLDER.equals(intent.getAction()))
        {
            HashSet hashset = (HashSet)intent.getSerializableExtra(IntentActions.EXTRA_FOLDER);
            QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getFolderDao().queryBuilder();
            querybuilder.where(com.flayvr.myrollshared.data.FolderDao.Properties.Id.in(hashset), new WhereCondition[0]);
            Map map;
            for(Iterator iterator = querybuilder.build().list().iterator(); iterator.hasNext(); DBManager.getInstance().getDaoSession().getMediaItemDao().insertInTx(map.values()))
            {
                Folder folder = (Folder)iterator.next();
                HashMap hashmap = getCurrItemsForFolder(folder);
                map = loadItemsForFolder(folder);
                Log.d(TAG, (new StringBuilder()).append("found ").append(map.size()).append(" items and ").append(hashmap.size()).append(" existing items for folder ").append(folder.getName()).toString());
                map.keySet().removeAll(hashmap.keySet());
                Log.d(TAG, (new StringBuilder()).append("total ").append(map.size()).append(" new items in folder ").append(folder.getName()).toString());
            }

        }
        Log.d(TAG, (new StringBuilder()).append("done building myroll cloud gallery service ").append(intent.getAction()).toString());
    }

}

package com.flayvr.myrollshared.services;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.cloud.PicasaSessionManager;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.imageloading.ImagesDiskCache;
import com.flayvr.myrollshared.managers.UserManager;
import com.flayvr.myrollshared.server.ServerUrls;
import com.flayvr.myrollshared.utils.*;
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.geo.Point;
import com.google.gdata.data.media.mediarss.MediaContent;
import com.google.gdata.data.media.mediarss.MediaThumbnail;
import com.google.gdata.data.photos.*;
import com.google.gdata.util.ServiceException;
import com.kissmetrics.sdk.KISSmetricsAPI;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PicasaGalleryBuilderService extends GalleryBuilderService
{

    public static final int MAX_BATCH_SIZE = 1000;
    public static final int MAX_FEED_INDEX = 10001;
    private static final String TAG = PicasaGalleryBuilderService.class.getSimpleName();
    private Map folderAnalytics;
    private final PicasaSessionManager sessionManager = PicasaSessionManager.getInstance();

    public PicasaGalleryBuilderService()
    {
        super(PicasaGalleryBuilderService.class.getSimpleName());
    }

    private void clearData(Intent intent)
    {
        FolderDao folderdao = DBManager.getInstance().getDaoSession().getFolderDao();
        QueryBuilder querybuilder = folderdao.queryBuilder();
        querybuilder.where(com.flayvr.myrollshared.data.FolderDao.Properties.Source.eq(Integer.valueOf(2)), new WhereCondition[0]);
        List list = querybuilder.build().list();
        HashSet hashset = new HashSet();
        for(Iterator iterator = list.iterator(); iterator.hasNext(); hashset.addAll(((Folder)iterator.next()).getMediaItemList())) { }
        MediaItemDao mediaitemdao = DBManager.getInstance().getDaoSession().getMediaItemDao();
        Log.d(TAG, (new StringBuilder()).append("deleting #").append(hashset.size()).append(" items").toString());
        if(hashset.size() > 0)
        {
            deleteReferencesToMediaItems(hashset);
            mediaitemdao.deleteInTx(hashset);
            sentBroadcastEvents(new LinkedList(), hashset, intent.getAction());
        }
        folderdao.deleteInTx(list);
        SharedPreferencesManager.setPendingPicasaCleanRequest(false);
    }

    private MediaItem createItemFromEntry(Folder folder, PhotoEntry photoentry)
    {
        Double double1;
        Double double2;
        Double double3;
        Double double4;
        MediaItem mediaitem = new MediaItem();
        mediaitem.setWasMinimizedByUser(Boolean.valueOf(false));
        mediaitem.setAndroidId(Long.valueOf(Long.parseLong(photoentry.getGphotoId())));
        setType(mediaitem, photoentry);
        mediaitem.setThumbnail(((MediaThumbnail)photoentry.getMediaThumbnails().get(0)).getUrl());
        try {
            Integer integer = photoentry.getRotation();
            Point point;
            Double double5;
            if (integer != null)
                mediaitem.setOrientation(integer);
            else
                mediaitem.setOrientation(Integer.valueOf(0));
            mediaitem.setDate(photoentry.getTimestamp());
            point = photoentry.getGeoLocation();
            double1 = Double.valueOf(0.0D);
            double2 = Double.valueOf(0.0D);
            if (point == null) {
                double3 = double1;
                double4 = double2;
            } else if (point.getLatitude() != null)
                double1 = point.getLatitude();
            if (point.getLongitude() == null) {
                double3 = double1;
                double4 = double2;
            } else {
                double5 = point.getLongitude();
                double3 = double1;
                double4 = double5;
            }
            mediaitem.setLatitude(double3);
            mediaitem.setLongitude(double4);
            mediaitem.setFolder(folder);
            mediaitem.setFileSizeBytes(Long.valueOf(photoentry.getSize().longValue()));
            mediaitem.setWasClustered(Boolean.valueOf(false));
            mediaitem.setInteractionScore(Double.valueOf(0.0D));
            mediaitem.setSource(Integer.valueOf(2));
            mediaitem.setProp(Float.valueOf((1.0F * (float) photoentry.getWidthExt().getValue().longValue()) / (float) photoentry.getHeightExt().getValue().longValue()));
        } catch(Exception e) {
        }
        return mediaitem;
    }

    private HashMap getCurrItems(Folder folder)
    {
        HashMap hashmap = new HashMap();
        if(folder.getId() == null)
        {
            return hashmap;
        }
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getMediaItemDao().queryBuilder();
        querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.Source.eq(Integer.valueOf(2)), new WhereCondition[0]);
        querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.eq(folder.getId()), new WhereCondition[0]);
        querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.isNull(), new WhereCondition[0]);
        MediaItem mediaitem;
        for(Iterator iterator = querybuilder.list().iterator(); iterator.hasNext(); hashmap.put(mediaitem.getAndroidId(), mediaitem))
        {
            mediaitem = (MediaItem)iterator.next();
        }

        return hashmap;
    }

    private Map getFolderItems(Folder folder, int i, int j)
    {
        HashMap hashmap = new HashMap();
        android.net.Uri.Builder builder = new android.net.Uri.Builder();
        builder.encodedPath("https://picasaweb.google.com/data/feed/api/user");
        builder.appendPath(sessionManager.getUserId());
        builder.appendPath("albumid");
        builder.appendPath((new StringBuilder()).append(folder.getFolderSourceId()).append("").toString());
        builder.appendQueryParameter("max-results", (new StringBuilder()).append(j).append("").toString());
        builder.appendQueryParameter("imgmax", "1600u");
        builder.appendQueryParameter("thumbsize", "512u");
        builder.appendQueryParameter("start-index", (new StringBuilder()).append(i).append("").toString());
        builder.appendQueryParameter("fields", "entry(category,gphoto:id,media:group(media:content,media:thumbnail),gphoto:timestamp,georss:where(gml:Point),gphoto:rotation,gphoto:size,gphoto:height,gphoto:width)");
        PhotoEntry photoentry;
        for(Iterator iterator = ((AlbumFeed)getPicasaFeed(builder.build().toString(), AlbumFeed.class)).getPhotoEntries().iterator(); iterator.hasNext(); hashmap.put(Long.valueOf(Long.parseLong(photoentry.getGphotoId())), photoentry))
        {
            photoentry = (PhotoEntry)iterator.next();
        }
        return hashmap;
    }

    public static List getIntentFilter()
    {
        ArrayList arraylist = new ArrayList();
        arraylist.add(IntentActions.ACTION_PICASA_SESSIOM_CHANGED);
        arraylist.add(IntentActions.ACTION_PICASA_CLEAN_DATA);
        arraylist.add(IntentActions.ACTION_PICASA_UPDATE_FOLDER);
        return arraylist;
    }

    private LinkedList getNewItems(Folder folder, int i, int j)
    {
        new HashMap();
        Map map;
        LinkedList linkedlist;
        try
        {
            map = getFolderItems(folder, i, j);
            linkedlist = new LinkedList();
            for(Iterator iterator = map.keySet().iterator(); iterator.hasNext();)
            {
                PhotoEntry photoentry = (PhotoEntry)map.get((Long)iterator.next());
                linkedlist.add(createItemFromEntry(folder, photoentry));
            }
            return linkedlist;
        }
        catch(Exception exception)
        {
            Log.e(TAG, (new StringBuilder()).append("error while getting picasa photos for album: ").append(folder.getName()).toString(), exception);
            return null;
        }
    }

    private Result loadFolderItems(Folder folder)
    {
        int i;
        HashMap hashmap;
        i = 1;
        hashmap = new HashMap();
        Map map;
        map = getFolderItems(folder, i, 1000);
        hashmap.putAll(map);
        Result result;
        Iterator iterator;
        try
        {
            do
            {
                if(map.size() != 1000 || i >= 10001)
                    break;
                i += 1000;
                map = getFolderItems(folder, i, 1000);
                hashmap.putAll(map);
            } while(true);
            result = new Result();
            HashMap hashmap1 = getCurrItems(folder);
            HashMap hashmap2 = new HashMap(hashmap1);
            hashmap2.keySet().removeAll(hashmap.keySet());
            result.toDelete = hashmap2.values();
            hashmap.keySet().removeAll(hashmap1.keySet());
            iterator = hashmap.keySet().iterator();
            PhotoEntry photoentry;
            while(iterator.hasNext())
            {
                photoentry = (PhotoEntry)hashmap.get((Long)iterator.next());
                MediaItem mediaitem;
                try {
                    MediaItem mediaitem1 = createItemFromEntry(folder, photoentry);
                    mediaitem = mediaitem1;
                }catch (Exception e){
                    mediaitem = null;
                }
                result.newItems.add(mediaitem);
            }
            return result;
        }
        catch(Exception exception)
        {
            Log.e(TAG, (new StringBuilder()).append("error while getting picasa photos for album: ").append(folder.getName()).toString(), exception);
            return null;
        }
    }

    private Result loadFolders()
    {
        int i;
        HashMap hashmap;
        i = 1;
        try
        {
            folderAnalytics = new HashMap();
            hashmap = new HashMap();
            Map map;
            map = loadFolders(i);
            hashmap.putAll(map);
            while(map.size() == 1000)
            {
                i += 1000;
                map = loadFolders(i);
                hashmap.putAll(map);
            }
            Result result = new Result();
            HashMap hashmap1 = getCurrFolders();
            HashMap hashmap2 = new HashMap(hashmap1);
            hashmap2.keySet().removeAll(hashmap.keySet());
            result.toDelete = hashmap2.values();
            hashmap.keySet().removeAll(hashmap1.keySet());
            AlbumEntry albumentry;
            Folder folder;
            for(Iterator iterator = hashmap.keySet().iterator(); iterator.hasNext(); folderAnalytics.put(folder, albumentry.getPhotosUsedExt().getValue()))
            {
                Long long1 = (Long)iterator.next();
                albumentry = (AlbumEntry)hashmap.get(long1);
                folder = new Folder();
                folder.setName(albumentry.getTitle().getPlainText());
                folder.setSource(Integer.valueOf(2));
                folder.setFolderSourceId(long1);
                folder.setIsCamera(Boolean.valueOf(false));
                result.newItems.add(folder);
            }
            return result;
        }
        catch(Exception exception)
        {
            Log.e(TAG, "error while getting picasa albums", exception);
            return null;
        }
    }

    private Map loadFolders(int i)
    {
        HashMap hashmap = new HashMap();
        ArrayList arraylist = new ArrayList();
        android.net.Uri.Builder builder = new android.net.Uri.Builder();
        builder.encodedPath("https://picasaweb.google.com/data/feed/api/user");
        builder.appendPath(sessionManager.getUserId());
        builder.appendQueryParameter("max-results", "1000");
        builder.appendQueryParameter("start-index", (new StringBuilder()).append(i).append("").toString());
        builder.appendQueryParameter("fields", "entry(category,gphoto:id,title,gphoto:numphotos)");
        for(Iterator iterator = ((UserFeed)getPicasaFeed(builder.build().toString(), UserFeed.class)).getAlbumEntries().iterator(); iterator.hasNext(); arraylist.add((AlbumEntry)iterator.next())) { }
        AlbumEntry albumentry;
        for(Iterator iterator1 = arraylist.iterator(); iterator1.hasNext(); hashmap.put(Long.valueOf(Long.parseLong(albumentry.getGphotoId())), albumentry))
        {
            albumentry = (AlbumEntry)iterator1.next();
        }

        return hashmap;
    }

    private void sendPicasaStatistics()
    {
        FlayvrApplication.runNetwork(new Runnable() {
            @Override
            public void run() {
                FlayvrHttpClient flayvrhttpclient;
                HttpPost httppost;
                LinkedList linkedlist;
                JSONObject jsonobject;
                StringEntity stringentity;
                try
                {
                    Log.d(PicasaGalleryBuilderService.TAG, "sending picasa analytics");
                    flayvrhttpclient = new FlayvrHttpClient();
                    httppost = new HttpPost(ServerUrls.PICASA_STATISTICS_URL);
                    QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getMediaItemDao().queryBuilder();
                    querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.eq(null), new WhereCondition[0]);
                    querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.isNull(), new WhereCondition[0]);
                    Property aproperty[] = new Property[1];
                    aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
                    querybuilder.orderDesc(aproperty);
                    Query query = querybuilder.build();
                    linkedlist = new LinkedList();
                    JSONObject jsonobject1;
                    for(Iterator iterator = folderAnalytics.keySet().iterator(); iterator.hasNext(); linkedlist.add(jsonobject1))
                    {
                        Folder folder = (Folder)iterator.next();
                        jsonobject1 = new JSONObject();
                        query.setParameter(0, folder.getId());
                        List list = query.list();
                        jsonobject1.put("name", folder.getName());
                        jsonobject1.put("total_photos", folderAnalytics.get(folder));
                        jsonobject1.put("total_photos_synced", list.size());
                        if(list.size() > 0)
                        {
                            jsonobject1.put("first_date", ((MediaItem)list.get(-1 + list.size())).getDate());
                            jsonobject1.put("last_date", ((MediaItem)list.get(0)).getDate());
                        }
                    }
                    jsonobject = new JSONObject();
                    jsonobject.put("folders", new JSONArray(linkedlist));
                    jsonobject.put("uuid", UserManager.getInstance().getUserId());
                    jsonobject.put("email", PicasaSessionManager.getInstance().getUserId());
                    stringentity = new StringEntity(jsonobject.toString(), "UTF-8");
                    stringentity.setContentType("application/json");
                    httppost.setEntity(stringentity);
                    flayvrhttpclient.executeWithRetries(httppost);
                }
                catch(IOException ioexception)
                {
                    Log.e(PicasaGalleryBuilderService.TAG, ioexception.getMessage(), ioexception);
                }
                catch(Exception exception)
                {
                    Log.e(PicasaGalleryBuilderService.TAG, exception.getMessage(), exception);
                }
            }
        });
    }

    private void setType(MediaItem mediaitem, PhotoEntry photoentry)
    {
        byte byte0;
        int i;
        String s;
        Iterator iterator;
        int j;
        byte byte1;
        int k;
        int l;
        byte0 = 1;
        i = 0;
        s = null;
        iterator = photoentry.getMediaContents().iterator();
        j = 0;
        byte1 = 0;
        while(iterator.hasNext())
        {
            MediaContent mediacontent = (MediaContent)iterator.next();
            if("video".equals(mediacontent.getMedium()))
            {
                s = mediacontent.getUrl();
                byte1 = byte0;
            }
            if(mediacontent.getWidth() <= j)
            {
                k = i;
                l = j;
            }else {
                j = mediacontent.getWidth();
                i = mediacontent.getHeight();
                if (byte1 != 0) {
                    k = i;
                    l = j;
                } else {
                    s = mediacontent.getUrl();
                    k = i;
                    l = j;
                }
            }
            j = l;
            i = k;
        }
        if(byte1 != 0)
            byte0 = 2;
        mediaitem.setType(Integer.valueOf(byte0));
        mediaitem.setPath(s);
        mediaitem.setWidth(Integer.valueOf(j));
        mediaitem.setHeight(Integer.valueOf(i));
    }

    public HashMap getCurrFolders()
    {
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getFolderDao().queryBuilder();
        querybuilder.where(com.flayvr.myrollshared.data.FolderDao.Properties.Source.eq(Integer.valueOf(2)), new WhereCondition[0]);
        HashMap hashmap = new HashMap();
        Folder folder;
        for(Iterator iterator = querybuilder.list().iterator(); iterator.hasNext(); hashmap.put(folder.getFolderSourceId(), folder))
        {
            folder = (Folder)iterator.next();
        }

        return hashmap;
    }

    public GphotoFeed getPicasaFeed(String s, Class class1)
    {
        try {
            return (GphotoFeed)sessionManager.getPicasaService().getFeed(new URL(s), class1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onHandleIntent(Intent intent)
    {
        Date date;
        Log.d(TAG, (new StringBuilder()).append("start building gallery service ").append(intent.getAction()).toString());
        date = new Date();
        if(SharedPreferencesManager.hasPendingPicasaCleanRequest())
        {
            clearData(intent);
        }
        if(!IntentActions.ACTION_PICASA_SESSIOM_CHANGED.equals(intent.getAction())) {
            if(IntentActions.ACTION_PICASA_UPDATE_FOLDER.equals(intent.getAction()))
            {
                if(sessionManager.isLogedin())
                {
                    LinkedList linkedlist = new LinkedList();
                    HashSet hashset = new HashSet();
                    FolderDao folderdao = DBManager.getInstance().getDaoSession().getFolderDao();
                    HashSet hashset1 = (HashSet)intent.getSerializableExtra(IntentActions.EXTRA_FOLDER);
                    QueryBuilder querybuilder = folderdao.queryBuilder();
                    querybuilder.where(com.flayvr.myrollshared.data.FolderDao.Properties.Id.in(hashset1), new WhereCondition[0]);
                    Iterator iterator = querybuilder.build().list().iterator();
                    do
                    {
                        if(!iterator.hasNext())
                        {
                            break;
                        }
                        Folder folder = (Folder)iterator.next();
                        Result result = loadFolderItems(folder);
                        if(result != null)
                        {
                            Log.d(TAG, (new StringBuilder()).append("folder: ").append(folder.getName()).append(" new: ").append(result.newItems.size()).append(" delete: ").append(result.toDelete.size()).toString());
                            linkedlist.addAll(result.newItems);
                            hashset.addAll(result.toDelete);
                        }
                    } while(true);
                    MediaItemDao mediaitemdao = DBManager.getInstance().getDaoSession().getMediaItemDao();
                    if(linkedlist.size() > 0)
                    {
                        mediaitemdao.insertOrReplaceInTx(linkedlist);
                    }
                    deleteItems(hashset, false);
                    sentBroadcastEvents(linkedlist, hashset, intent.getAction());
                }
            } else if(IntentActions.ACTION_PICASA_CLEAN_DATA.equals(intent.getAction())) {
                SharedPreferencesManager.setPicasaFeedLoaded(false);
                SharedPreferencesManager.setPendingPicasaCleanRequest(true);
                clearData(intent);
                FlayvrApplication.getDiskCache().evictAll();
            }
        }else {
            if (sessionManager.isLogedin()) {
                boolean flag;
                HashSet hashset2;
                Result result1;
                if (!SharedPreferencesManager.hasPicasaFeedLoaded()) {
                    flag = true;
                } else {
                    flag = false;
                }
                hashset2 = new HashSet();
                result1 = loadFolders();
                if (result1 == null) {
                    if (flag) {
                        AnalyticsUtils.trackEventWithKISS("failed syncing picasa folders");
                    }
                    return;
                }
                Log.d(TAG, (new StringBuilder()).append("foldes: new: ").append(result1.newItems.size()).append(" delete: ").append(result1.toDelete.size()).toString());
                for (Iterator iterator1 = result1.toDelete.iterator(); iterator1.hasNext(); hashset2.addAll(((Folder) iterator1.next()).getMediaItemList())) {
                }
                FolderDao folderdao1 = DBManager.getInstance().getDaoSession().getFolderDao();
                folderdao1.insertInTx(result1.newItems);
                folderdao1.deleteInTx(result1.toDelete);
                MediaItemDao mediaitemdao1 = DBManager.getInstance().getDaoSession().getMediaItemDao();
                LinkedList linkedlist1 = new LinkedList();
                int i = 1;
                int j = 1;
                while (!result1.newItems.isEmpty()) {
                    LinkedList linkedlist2 = new LinkedList();
                    Iterator iterator2 = result1.newItems.iterator();
                    do {
                        if (!iterator2.hasNext()) {
                            break;
                        }
                        Folder folder1 = (Folder) iterator2.next();
                        LinkedList linkedlist3 = getNewItems(folder1, i, j);
                        if (linkedlist3 != null) {
                            linkedlist1.addAll(linkedlist3);
                            Log.d(TAG, (new StringBuilder()).append("folder: ").append(folder1.getName()).append(" new: ").append(linkedlist3.size()).toString());
                        } else {
                            AnalyticsUtils.trackEventWithKISS("failed syncing picasa photos");
                        }
                        if (linkedlist3 == null || linkedlist3.size() < j || i + j > 10001) {
                            linkedlist2.add(folder1);
                        }
                    } while (true);
                    result1.newItems.removeAll(linkedlist2);
                    i += j;
                    int k;
                    if (j == 1) {
                        k = 9;
                    } else {
                        k = j * 10;
                    }
                    j = Math.min(k, 1000);
                }
                if (linkedlist1.size() > 0) {
                    mediaitemdao1.insertOrReplaceInTx(linkedlist1);
                }
                sentBroadcastEvents(linkedlist1, new LinkedList(), intent.getAction());
                SharedPreferencesManager.setPicasaFeedLoaded(true);
                HashMap hashmap = new HashMap();
                hashmap.put("total picasa photos", (new StringBuilder()).append(DaoHelper.getPhotos(2).count()).append("").toString());
                hashmap.put("total picasa videos", (new StringBuilder()).append(DaoHelper.getVideos(2).count()).append("").toString());
                hashmap.put("total picasa folders", (new StringBuilder()).append(DaoHelper.getFolders(2).count()).append("").toString());
                KISSmetricsAPI.sharedAPI().set(hashmap);
                if (flag) {
                    sendBroadcast(new Intent(IntentActions.ACTION_PICASA_FETCH_DONE));
                    sendPicasaStatistics();
                    hashmap.put("picasa photos sync duration", (new StringBuilder()).append(((new Date()).getTime() - date.getTime()) / 1000L).append("").toString());
                    AnalyticsUtils.trackEventWithKISS("finished syncing picasa photos", hashmap);
                }
            }
        }
        long l = (new Date()).getTime() - date.getTime();
        Log.i(TAG, (new StringBuilder()).append("timing: done ").append(l).toString());
    }

    private class Result
    {
        List newItems;
        Collection toDelete;

        public Result()
        {
            super();
            newItems = new LinkedList();
            toDelete = new HashSet();
        }
    }
}

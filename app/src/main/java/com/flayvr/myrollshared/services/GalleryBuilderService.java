package com.flayvr.myrollshared.services;

import android.app.IntentService;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import com.flayvr.events.FolderIdChangedEvent;
import com.flayvr.events.FoldersChanged;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.cloud.PicasaSessionManager;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.events.GalleryBuilderFinishedFirstTimeEvent;
import com.flayvr.myrollshared.utils.*;
import com.google.gdata.client.photos.PicasawebService;
import com.kissmetrics.sdk.KISSmetricsAPI;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.*;
import de.greenrobot.event.EventBus;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GalleryBuilderService extends IntentService
{

    public static final List<String> CAMERA_FOLDERS = Arrays.asList(new String[] {
        "camera", "100media", "100andro", "images", "instagram", "image", "pictures", "sdcard0", "img", "dcim", 
        "cymera", "pic", "extsdcard", "100aviary", "sdcard", "100lgdsc", "photo"
    });
    private static final Integer PHOTOS_BUCKETS[];
    private static final String TAG = GalleryBuilderService.class.getSimpleName();

    public GalleryBuilderService()
    {
        super(GalleryBuilderService.class.getSimpleName());
    }

    public GalleryBuilderService(String s)
    {
        super(s);
    }

    private MediaItem addPhoto(Cursor cursor, int i, int j, int k, int l, int i1, int j1, 
            int k1, int l1, int i2, int j2)
    {
        long l2 = cursor.getInt(i);
        MediaItem mediaitem = new MediaItem();
        mediaitem.setWasMinimizedByUser(Boolean.valueOf(false));
        mediaitem.setType(Integer.valueOf(1));
        mediaitem.setAndroidId(Long.valueOf(l2));
        mediaitem.setOrientation(Integer.valueOf(cursor.getInt(j1)));
        mediaitem.setPath(cursor.getString(j));
        mediaitem.setDate(new Date(cursor.getLong(k)));
        mediaitem.setLatitude(Double.valueOf(cursor.getDouble(l)));
        mediaitem.setLongitude(Double.valueOf(cursor.getDouble(i1)));
        mediaitem.setFolderId(Long.valueOf(cursor.getLong(k1)));
        long l3 = cursor.getLong(l1);
        if(l3 == 0L)
        {
            l3 = (new File(mediaitem.getPath())).length();
        }
        int k2 = cursor.getInt(i2);
        if(k2 > 0)
        {
            mediaitem.setWidth(Integer.valueOf(k2));
        }
        int i3 = cursor.getInt(j2);
        if(i3 > 0)
        {
            mediaitem.setHeight(Integer.valueOf(i3));
        }
        mediaitem.setFileSizeBytes(Long.valueOf(l3));
        mediaitem.setWasClustered(Boolean.valueOf(false));
        mediaitem.setInteractionScore(Double.valueOf(0.0D));
        mediaitem.setSource(Integer.valueOf(1));
        return mediaitem;
    }

    private MediaItem addVideo(Cursor cursor, int i, int j, int k, int l, int i1, int j1, 
            int k1, int l1)
    {
        long l2 = cursor.getInt(i);
        MediaItem mediaitem = new MediaItem();
        mediaitem.setWasMinimizedByUser(Boolean.valueOf(false));
        mediaitem.setAndroidId(Long.valueOf(l2));
        mediaitem.setType(Integer.valueOf(2));
        mediaitem.setPath(cursor.getString(j));
        mediaitem.setDate(new Date(cursor.getLong(k)));
        mediaitem.setLatitude(Double.valueOf(cursor.getDouble(l)));
        mediaitem.setLongitude(Double.valueOf(cursor.getDouble(i1)));
        mediaitem.setDuration(Long.valueOf(cursor.getLong(j1)));
        mediaitem.setFolderId(Long.valueOf(cursor.getLong(k1)));
        long l3 = cursor.getLong(l1);
        if(l3 == 0L && mediaitem.getPath() != null)
        {
            l3 = (new File(mediaitem.getPath())).length();
        }
        mediaitem.setFileSizeBytes(Long.valueOf(l3));
        mediaitem.setWasClustered(Boolean.valueOf(false));
        mediaitem.setInteractionScore(Double.valueOf(0.0D));
        mediaitem.setSource(Integer.valueOf(1));
        return mediaitem;
    }

    private void checkForDBAfterError()
    {
        MediaItemDao mediaitemdao;
        ContentResolver contentresolver;
        String as[];
        String s;
        int i;
        String s1;
        String s2;
        String s3;
        Cursor cursor1;
        int j;
        try
        {
            mediaitemdao = DBManager.getInstance().getDaoSession().getMediaItemDao();
            contentresolver = getApplicationContext().getContentResolver();
            as = (new String[] {
                    "_id", "_data", "bucket_id", "datetaken", "latitude", "longitude", "orientation", "bucket_display_name", "_data", "_size"
            });
            s = Environment.getExternalStorageState();
            if(!s.equals("mounted") && !s.equals("mounted_ro"))
            {
                return;
            }
            Cursor cursor = contentresolver.query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, as, null, null, "_id ASC");
            if(cursor == null)
                return;
            i = 0;
            while(cursor.moveToNext())
            {
                i++;
            }
            cursor.close();
            s1 = com.flayvr.myrollshared.data.MediaItemDao.Properties.Id.columnName;
            s2 = com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.columnName;
            s3 = com.flayvr.myrollshared.data.MediaItemDao.Properties.AndroidId.columnName;
            cursor1 = mediaitemdao.getDatabase().query(mediaitemdao.getTablename(), new String[] {
                    s1, s2, s3
            }, (new StringBuilder()).append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Type.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.columnName).append(" is null").toString(), new String[] {
                    "1"
            }, null, null, (new StringBuilder()).append(s3).append(" ASC").toString());
            j = 0;
            while(cursor1.moveToNext())
            {
                j++;
            }
            cursor1.close();
        }
        catch(Throwable throwable)
        {
        }
    }

    private void createPhotoThumbnail(long l)
    {
        MediaItemDao mediaitemdao = DBManager.getInstance().getDaoSession().getMediaItemDao();
        MediaItem mediaitem = (MediaItem)mediaitemdao.load(Long.valueOf(l));
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
        android.graphics.Bitmap bitmap = android.provider.MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(), mediaitem.getAndroidId().longValue(), 1, options);
        mediaitem.setCheckedThumbnail(Boolean.valueOf(true));
        if(bitmap != null)
        {
            Cursor cursor = android.provider.MediaStore.Images.Thumbnails.queryMiniThumbnail(getContentResolver(), mediaitem.getAndroidId().longValue(), 1, new String[] {
                "_data"
            });
            if(cursor != null && cursor.getCount() > 0)
            {
                Log.d(TAG, (new StringBuilder()).append("Setting image thumb for item: ").append(l).toString());
                cursor.moveToFirst();
                mediaitem.setThumbnail(cursor.getString(cursor.getColumnIndex("_data")));
            }
            cursor.close();
        }
        mediaitemdao.update(mediaitem);
    }

    private void createVideoThumbnail(long l)
    {
        MediaItemDao mediaitemdao = DBManager.getInstance().getDaoSession().getMediaItemDao();
        MediaItem mediaitem = (MediaItem)mediaitemdao.load(Long.valueOf(l));
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
        android.graphics.Bitmap bitmap = android.provider.MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), mediaitem.getAndroidId().longValue(), 1, options);
        mediaitem.setCheckedThumbnail(Boolean.valueOf(true));
        if(bitmap != null)
        {
            Cursor cursor = getContentResolver().query(android.provider.MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, new String[] {
                "_data"
            }, (new StringBuilder()).append("video_id = ").append(mediaitem.getAndroidId()).append(" AND ").append("kind").append(" = ").append(1).toString(), null, null);
            if(cursor != null && cursor.getCount() > 0)
            {
                Log.d(TAG, (new StringBuilder()).append("Setting video thumb for item: ").append(l).toString());
                cursor.moveToFirst();
                mediaitem.setThumbnail(cursor.getString(cursor.getColumnIndex("_data")));
            }
            cursor.close();
        }
        mediaitemdao.update(mediaitem);
    }

    public static void deleteFiles(String s)
    {
        String s1;
        Runtime runtime;
        try {
            if (!(new File(s)).exists())
                return;
            s1 = (new StringBuilder()).append("rm -r ").append(s).toString();
            runtime = Runtime.getRuntime();
            runtime.exec(s1);
        }catch(Exception e){}
    }

    private void deleteFolders(List list, boolean flag)
    {
        if(list.size() > 0)
        {
            List list1;
            for(Iterator iterator = list.iterator(); iterator.hasNext(); deleteItems(list1, flag))
            {
                Folder folder = (Folder)iterator.next();
                list1 = folder.getMediaItemList();
                DBManager.getInstance().getDaoSession().getFolderDao().deleteInTx(new Folder[] {
                    folder
                });
                EventBus.getDefault().post(new FoldersChanged());
            }

        }
    }

    public static void deleteFolders(Folder afolder[])
    {
        Long along[] = new Long[afolder.length];
        for(int i = 0; i < afolder.length; i++)
        {
            along[i] = afolder[i].getId();
        }

        deleteFolders(along);
    }

    public static void deleteFolders(Long along[])
    {
        Intent intent = new Intent(IntentActions.ACTION_DELETE_FOLDERS);
        intent.putExtra(IntentActions.EXTRA_FOLDERS_REMOVED, along);
        FlayvrApplication.getAppContext().sendBroadcast(intent);
    }

    public static void deleteItems(Collection<MediaItem> collection)
    {
        Long along[] = new Long[collection.size()];
        Iterator iterator = collection.iterator();
        for(int i = 0; i < collection.size(); i++)
        {
            along[i] = ((MediaItem)iterator.next()).getId();
        }

        deleteItems(along);
    }

    public static void deleteItems(Long along[])
    {
        Intent intent = new Intent(IntentActions.ACTION_DELETE_ITEMS);
        intent.putExtra(IntentActions.EXTRA_ITEMS_REMOVED, along);
        FlayvrApplication.getAppContext().sendBroadcast(intent);
    }

    private Collection<Long> fillPhotosThumbnails()
    {
        LinkedList linkedlist = new LinkedList();
        LinkedList linkedlist1 = new LinkedList();
        MediaItemDao mediaitemdao = DBManager.getInstance().getDaoSession().getMediaItemDao();
        ContentResolver contentresolver = getApplicationContext().getContentResolver();
        String as[] = {
            "image_id", "_data"
        };
        Cursor cursor = contentresolver.query(android.provider.MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, as, "kind = 1", null, "image_id ASC");
        if(cursor == null)
        {
            return linkedlist1;
        }
        Cursor cursor1 = getMissingThumbsCursor(1);
        String as1[] = new String[1];
        as1[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.AndroidId.columnName;
        IdsCursorJoiner idscursorjoiner = new IdsCursorJoiner(cursor1, as1, cursor, new String[] {
            "image_id"
        });
        int i = cursor.getColumnIndex("_data");
        int j = cursor1.getColumnIndex(com.flayvr.myrollshared.data.MediaItemDao.Properties.Id.columnName);
        Iterator iterator = idscursorjoiner.iterator();
        do
        {
            if(iterator.hasNext())
            {
                com.flayvr.myrollshared.utils.IdsCursorJoiner.Result result = (com.flayvr.myrollshared.utils.IdsCursorJoiner.Result)iterator.next();
                switch(result)
                {
                case LEFT: // '\002'
                    linkedlist1.add(Long.valueOf(cursor1.getLong(j)));
                    break;

                case BOTH: // '\003'
                    long l = cursor1.getLong(j);
                    String s = cursor.getString(i);
                    MediaItem mediaitem = (MediaItem)mediaitemdao.load(Long.valueOf(l));
                    mediaitem.setThumbnail(s);
                    linkedlist.add(mediaitem);
                    break;
                }
            } else
            {
                mediaitemdao.updateInTx(linkedlist);
                cursor1.close();
                cursor.close();
                return linkedlist1;
            }
        } while(true);
    }

    private Collection<Long> fillVideosThumbnails()
    {
        LinkedList linkedlist = new LinkedList();
        LinkedList linkedlist1 = new LinkedList();
        MediaItemDao mediaitemdao = DBManager.getInstance().getDaoSession().getMediaItemDao();
        ContentResolver contentresolver = getApplicationContext().getContentResolver();
        String as[] = {
            "video_id", "_data"
        };
        Cursor cursor = contentresolver.query(android.provider.MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, as, "kind = 1", null, "video_id ASC");
        if(cursor == null)
        {
            return linkedlist1;
        }
        Cursor cursor1 = getMissingThumbsCursor(2);
        String as1[] = new String[1];
        as1[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.AndroidId.columnName;
        IdsCursorJoiner idscursorjoiner = new IdsCursorJoiner(cursor1, as1, cursor, new String[] {
            "video_id"
        });
        cursor.getColumnIndex("video_id");
        int i = cursor.getColumnIndex("_data");
        int j = cursor1.getColumnIndex(com.flayvr.myrollshared.data.MediaItemDao.Properties.Id.columnName);
        Iterator iterator = idscursorjoiner.iterator();
        do
        {
            if(iterator.hasNext())
            {
                com.flayvr.myrollshared.utils.IdsCursorJoiner.Result result = (com.flayvr.myrollshared.utils.IdsCursorJoiner.Result)iterator.next();
                switch(result)
                {
                case LEFT: // '\002'
                    linkedlist1.add(Long.valueOf(cursor1.getLong(j)));
                    break;

                case BOTH: // '\003'
                    long l = cursor1.getLong(j);
                    String s = cursor.getString(i);
                    MediaItem mediaitem = (MediaItem)mediaitemdao.load(Long.valueOf(l));
                    mediaitem.setThumbnail(s);
                    linkedlist.add(mediaitem);
                    break;
                }
            } else
            {
                mediaitemdao.updateInTx(linkedlist);
                cursor1.close();
                cursor.close();
                return linkedlist1;
            }
        } while(true);
    }

    public static List<String> getIntentFilter()
    {
        ArrayList arraylist = new ArrayList();
        arraylist.add(IntentActions.ACTION_APP_CREATE);
        arraylist.add(IntentActions.ACTION_ANDROID_CONTENT_CHANGED);
        arraylist.add(IntentActions.ACTION_DELETE_ITEMS);
        arraylist.add(IntentActions.ACTION_DELETE_FOLDERS);
        arraylist.add(IntentActions.ACTION_CONTENT_OBSERVER);
        return arraylist;
    }

    private Cursor getMissingThumbsCursor(int i)
    {
        MediaItemDao mediaitemdao = DBManager.getInstance().getDaoSession().getMediaItemDao();
        String s = com.flayvr.myrollshared.data.MediaItemDao.Properties.Id.columnName;
        String s1 = com.flayvr.myrollshared.data.MediaItemDao.Properties.AndroidId.columnName;
        SQLiteDatabase sqlitedatabase = mediaitemdao.getDatabase();
        String s2 = mediaitemdao.getTablename();
        String as[] = {
            s1, s
        };
        String s3 = (new StringBuilder()).append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Type.columnName).append(" = ? and ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Thumbnail.columnName).append(" is null AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.CheckedThumbnail.columnName).append(" is null AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.columnName).append(" is null AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Source.columnName).append(" = ?").toString();
        String as1[] = new String[2];
        as1[0] = (new StringBuilder()).append(i).append("").toString();
        as1[1] = "1";
        return sqlitedatabase.query(s2, as, s3, as1, null, null, (new StringBuilder()).append(s1).append(" ASC").toString());
    }

    private void scanFolders(LoadingResult loadingresult, LoadingResult loadingresult1)
    {
        HashMap hashmap = new HashMap(loadingresult.addedFolders);
        hashmap.putAll(loadingresult1.addedFolders);
        LinkedList linkedlist = new LinkedList(loadingresult.deletedFolders);
        linkedlist.addAll(loadingresult1.deletedFolders);
        updateFolders(hashmap, linkedlist);
    }

    private LoadingResult scanPhotos()
    {
        LinkedList linkedlist = new LinkedList();
        LinkedList linkedlist1 = new LinkedList();
        HashMap hashmap = new HashMap();
        LinkedList linkedlist2 = new LinkedList();
        MediaItemDao mediaitemdao = DBManager.getInstance().getDaoSession().getMediaItemDao();
        ContentResolver contentresolver = getApplicationContext().getContentResolver();
        String as[] = {
            "_id", "_data", "bucket_id", "datetaken", "latitude", "longitude", "orientation", "bucket_display_name", "_data", "_size", 
            "width", "height"
        };
        Cursor cursor = contentresolver.query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, as, null, null, "_id ASC");
        if(cursor == null)
        {
            return new LoadingResult(linkedlist, linkedlist1, hashmap, linkedlist2);
        }
        String s = com.flayvr.myrollshared.data.MediaItemDao.Properties.Id.columnName;
        String s1 = com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.columnName;
        String s2 = com.flayvr.myrollshared.data.MediaItemDao.Properties.AndroidId.columnName;
        Cursor cursor1 = mediaitemdao.getDatabase().query(mediaitemdao.getTablename(), new String[] {
            s, s1, s2
        }, (new StringBuilder()).append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Type.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.columnName).append(" is null AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Source.columnName).append(" = ?").toString(), new String[] {
            "1", "1"
        }, null, null, (new StringBuilder()).append(s2).append(" ASC").toString());
        IdsCursorJoiner idscursorjoiner = new IdsCursorJoiner(cursor1, new String[] {
            s2
        }, cursor, new String[] {
            "_id"
        });
        int i = cursor.getColumnIndex("_id");
        int j = cursor.getColumnIndex("_data");
        int k = cursor.getColumnIndex("datetaken");
        int l = cursor.getColumnIndex("latitude");
        int i1 = cursor.getColumnIndex("longitude");
        int j1 = cursor.getColumnIndex("orientation");
        int k1 = cursor.getColumnIndex("bucket_id");
        int l1 = cursor.getColumnIndex("bucket_display_name");
        int i2 = cursor.getColumnIndex("_size");
        int j2 = cursor.getColumnIndex("width");
        int k2 = cursor.getColumnIndex("height");
        int l2 = cursor1.getColumnIndex(s);
        int i3 = cursor1.getColumnIndex(s1);
        Iterator iterator = idscursorjoiner.iterator();
        do
        {
            if(iterator.hasNext())
            {
                com.flayvr.myrollshared.utils.IdsCursorJoiner.Result result = (com.flayvr.myrollshared.utils.IdsCursorJoiner.Result)iterator.next();
                switch(result)
                {
                case RIGHT: // '\001'
                    MediaItem mediaitem = addPhoto(cursor, i, j, k, l, i1, j1, k1, i2, j2, k2);
                    linkedlist.add(mediaitem);
                    String s3 = (new File(mediaitem.getPath())).getParent();
                    if(s3 != null)
                    {
                        Long long1 = mediaitem.getFolderId();
                        FolderInfo folderinfo = new FolderInfo(cursor.getString(l1), s3);
                        hashmap.put(long1, folderinfo);
                    } else
                    {
                        hashmap.put(mediaitem.getFolderId(), new FolderInfo(cursor.getString(l1), "/"));
                    }
                    break;

                case LEFT: // '\002'
                    linkedlist1.add(mediaitemdao.load(Long.valueOf(cursor1.getLong(l2))));
                    linkedlist2.add(Long.valueOf(cursor1.getLong(i3)));
                    break;
                }
            } else
            {
                cursor.close();
                cursor1.close();
                return new LoadingResult(linkedlist, linkedlist1, hashmap, linkedlist2);
            }
        } while(true);
    }

    private LoadingResult scanPhotosWithRetry()
    {
        LoadingResult loadingresult1;
        LoadingResult loadingresult;
        try
        {
            loadingresult1 = scanPhotos();
        }
        catch(Exception exception)
        {
            try
            {
                loadingresult = scanPhotos();
            }
            catch(Exception exception1)
            {
                checkForDBAfterError();
                return new LoadingResult(new LinkedList(), new LinkedList(), new HashMap(), new LinkedList());
            }
            return loadingresult;
        }
        return loadingresult1;
    }

    private void scanThumbs()
    {
        try
        {
            Collection collection = fillPhotosThumbnails();
            Log.d(TAG, (new StringBuilder()).append("photo thumbs #").append(collection.size()).toString());
            Collection collection1 = fillVideosThumbnails();
            Log.d(TAG, (new StringBuilder()).append("video thumbs #").append(collection1.size()).toString());
        }
        catch(Exception exception)
        {
        }
    }

    private LoadingResult scanVideos()
    {
        LinkedList linkedlist = new LinkedList();
        LinkedList linkedlist1 = new LinkedList();
        HashMap hashmap = new HashMap();
        LinkedList linkedlist2 = new LinkedList();
        MediaItemDao mediaitemdao = DBManager.getInstance().getDaoSession().getMediaItemDao();
        ContentResolver contentresolver = getApplicationContext().getContentResolver();
        String as[] = {
            "_id", "_data", "bucket_id", "datetaken", "latitude", "longitude", "duration", "bucket_display_name", "date_modified", "_size"
        };
        Cursor cursor = contentresolver.query(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, as, null, null, "_id ASC");
        if(cursor == null)
        {
            return new LoadingResult(linkedlist, linkedlist1, hashmap, linkedlist2);
        }
        String s = com.flayvr.myrollshared.data.MediaItemDao.Properties.Id.columnName;
        String s1 = com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.columnName;
        String s2 = com.flayvr.myrollshared.data.MediaItemDao.Properties.AndroidId.columnName;
        Cursor cursor1 = mediaitemdao.getDatabase().query(mediaitemdao.getTablename(), new String[] {
            s, s1, s2
        }, (new StringBuilder()).append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Type.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.columnName).append(" is null AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Source.columnName).append(" = ?").toString(), new String[] {
            "2", "1"
        }, null, null, (new StringBuilder()).append(s2).append(" ASC").toString());
        IdsCursorJoiner idscursorjoiner = new IdsCursorJoiner(cursor1, new String[] {
            s2
        }, cursor, new String[] {
            "_id"
        });
        int i = cursor.getColumnIndexOrThrow("_id");
        int j = cursor.getColumnIndexOrThrow("_data");
        int k = cursor.getColumnIndexOrThrow("datetaken");
        int l = cursor.getColumnIndexOrThrow("duration");
        int i1 = cursor.getColumnIndexOrThrow("latitude");
        int j1 = cursor.getColumnIndex("longitude");
        int k1 = cursor.getColumnIndex("bucket_id");
        int l1 = cursor.getColumnIndex("bucket_display_name");
        int i2 = cursor.getColumnIndex("_size");
        int j2 = cursor1.getColumnIndex(s);
        int k2 = cursor1.getColumnIndex(s1);
        Iterator iterator = idscursorjoiner.iterator();
        do
        {
            if(iterator.hasNext())
            {
                com.flayvr.myrollshared.utils.IdsCursorJoiner.Result result = (com.flayvr.myrollshared.utils.IdsCursorJoiner.Result)iterator.next();
                switch(result)
                {
                case RIGHT: // '\001'
                    MediaItem mediaitem = addVideo(cursor, i, j, k, i1, j1, l, k1, i2);
                    linkedlist.add(mediaitem);
                    String s3 = (new File(mediaitem.getPath())).getParent();
                    if(s3 != null)
                    {
                        Long long1 = mediaitem.getFolderId();
                        FolderInfo folderinfo = new FolderInfo(cursor.getString(l1), s3);
                        hashmap.put(long1, folderinfo);
                    } else
                    {
                        hashmap.put(mediaitem.getFolderId(), new FolderInfo(cursor.getString(l1), "/"));
                    }
                    break;

                case LEFT: // '\002'
                    linkedlist1.add(mediaitemdao.load(Long.valueOf(cursor1.getLong(j2))));
                    linkedlist2.add(Long.valueOf(cursor1.getLong(k2)));
                    break;
                }
            } else {
                cursor.close();
                cursor1.close();
                return new LoadingResult(linkedlist, linkedlist1, hashmap, linkedlist2);
            }
        } while(true);
    }

    private LoadingResult scanVideosWithRetries()
    {
        LoadingResult loadingresult1;
        try
        {
            loadingresult1 = scanVideos();
        }
        catch(Exception exception)
        {
            LoadingResult loadingresult;
            try
            {
                loadingresult = scanVideos();
            }
            catch(Exception exception1)
            {
                checkForDBAfterError();
                return new LoadingResult(new LinkedList(), new LinkedList(), new HashMap(), new LinkedList());
            }
            return loadingresult;
        }
        return loadingresult1;
    }

    private void sendAssetsAnalyitcs(long l)
    {
        char c = '\uFFFF';
        if(l < 1000L) {
            if(l >= 500L)
            {
                c = '\u01F4';
            } else
            if(l >= 200L)
            {
                c = '\310';
            } else
            if(l >= 100L)
            {
                c = 'd';
            }
        } else
            c = '\u03E8';
        if(c > 0 && !SharedPreferencesManager.wasSentPhotosCount(c))
        {
            Object aobj[] = new Object[1];
            aobj[0] = Integer.valueOf(c);
            AnalyticsUtils.trackEventWithKISS(String.format("got more than %d photos", aobj));
            SharedPreferencesManager.setSentPhotosCount(c);
        }
    }

    private void updateFolders(Map<Long, GalleryBuilderService.FolderInfo> map, Collection<Long> collection)
    {
        FolderDao folderdao = DBManager.getInstance().getDaoSession().getFolderDao();
        LinkedList linkedlist = new LinkedList();
        LinkedList linkedlist1 = new LinkedList();
        LinkedList linkedlist2 = new LinkedList();
        Iterator iterator = collection.iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            Long long2 = (Long)iterator.next();
            if(map.containsKey(long2))
            {
                map.remove(long2);
            } else
            {
                Folder folder2 = (Folder)folderdao.load(long2);
                if(folder2 != null && folder2.getNotDeletedMediaItemCount().longValue() == 0L)
                {
                    linkedlist1.add(folder2);
                }
            }
        } while(true);
        List list = folderdao.queryBuilder().where(com.flayvr.myrollshared.data.FolderDao.Properties.FolderSourceId.in(map.keySet()), new WhereCondition[0]).build().list();
        HashSet hashset = new HashSet();
        for(Iterator iterator1 = list.iterator(); iterator1.hasNext(); hashset.add(((Folder)iterator1.next()).getId())) { }
        HashSet hashset1 = new HashSet();
        Iterator iterator2 = map.keySet().iterator();
        do
        {
            if(!iterator2.hasNext())
            {
                break;
            }
            Long long1 = (Long)iterator2.next();
            if(!hashset.contains(long1))
            {
                FolderInfo folderinfo = (FolderInfo)map.get(long1);
                QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getFolderDao().queryBuilder();
                querybuilder.where(querybuilder.and(com.flayvr.myrollshared.data.FolderDao.Properties.FolderPath.eq(folderinfo.path), com.flayvr.myrollshared.data.FolderDao.Properties.FolderSourceId.isNull(), new WhereCondition[0]), new WhereCondition[0]);
                List list1 = querybuilder.build().list();
                Folder folder = new Folder(long1);
                if(list1.size() > 0)
                {
                    folder.setIsUserCreated(Boolean.valueOf(true));
                    Folder folder1;
                    for(Iterator iterator4 = list1.iterator(); iterator4.hasNext(); EventBus.getDefault().post(new FolderIdChangedEvent(folder1.getId(), folder.getId())))
                    {
                        folder1 = (Folder)iterator4.next();
                        linkedlist1.add(folder1);
                    }

                }
                linkedlist.add(folder);
                String s1 = folderinfo.name;
                folder.setName(s1);
                folder.setFolderPath(folderinfo.path);
                folder.setFolderSourceId(long1);
                folder.setIsCamera(Boolean.valueOf(CAMERA_FOLDERS.contains(s1.toLowerCase())));
                folder.setSource(Integer.valueOf(1));
                folder.setFolderSourceId(long1);
                hashset1.add(s1);
            }
        } while(true);
        if(!SharedPreferencesManager.getFolderAnalyticsSent())
        {
            HashMap hashmap = new HashMap();
            StringBuilder stringbuilder = new StringBuilder();
            boolean flag;
            Iterator iterator3;
            if(hashset1.size() > 0)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            hashmap.put("found camera folder", stringbuilder.append(flag).append("").toString());
            hashmap.put("total camera folders found", (new StringBuilder()).append(hashset1.size()).append("").toString());
            iterator3 = hashset1.iterator();
            for(int i = 1; iterator3.hasNext(); i++)
            {
                String s = (String)iterator3.next();
                Object aobj[] = new Object[1];
                aobj[0] = Integer.valueOf(i);
                hashmap.put(String.format("camera folder %d name", aobj), s);
            }

            KISSmetricsAPI.sharedAPI().set(hashmap);
            SharedPreferencesManager.setFolderAnalyticsSent();
        }
        if(linkedlist2.size() > 0)
        {
            Log.d(TAG, (new StringBuilder()).append("updating ").append(linkedlist2.size()).append(" folders").toString());
            folderdao.updateInTx(linkedlist2);
        }
        if(linkedlist.size() > 0)
        {
            Log.d(TAG, (new StringBuilder()).append("adding ").append(linkedlist.size()).append(" folders").toString());
            folderdao.insertInTx(linkedlist);
        }
        if(linkedlist1.size() > 0)
        {
            Log.d(TAG, (new StringBuilder()).append("deleting ").append(linkedlist1.size()).append(" folders").toString());
            folderdao.deleteInTx(linkedlist1);
        }
    }

    protected void deleteItems(Collection<MediaItem> collection, boolean flag)
    {
        MediaItemDao mediaitemdao = DBManager.getInstance().getDaoSession().getMediaItemDao();
        if(collection.size() > 0)
        {
            Iterator iterator = collection.iterator();
            while(iterator.hasNext())
            {
                MediaItem mediaitem = (MediaItem)iterator.next();
                mediaitem.setWasDeleted(Boolean.valueOf(true));
                if(flag)
                {
                    if(mediaitem.getSource().intValue() == 1)
                    {
                        (new File(mediaitem.getPath())).delete();
                        if(mediaitem.getAndroidId().longValue() != -1L)
                        {
                            android.net.Uri uri;
                            ContentResolver contentresolver;
                            if(mediaitem.getType().intValue() == 1)
                            {
                                uri = ContentUris.withAppendedId(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mediaitem.getAndroidId().longValue());
                            } else
                            {
                                uri = ContentUris.withAppendedId(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaitem.getAndroidId().longValue());
                            }
                            contentresolver = FlayvrApplication.getAppContext().getContentResolver();
                            if(android.os.Build.VERSION.SDK_INT >= 11)
                            {
                                ContentValues contentvalues = new ContentValues();
                                contentvalues.put("media_type", Integer.valueOf(4));
                                contentresolver.update(uri, contentvalues, null, null);
                            }
                            contentresolver.delete(uri, null, null);
                        }
                    } else
                    if(mediaitem.getSource().intValue() == 2)
                    {
                        PicasaSessionManager picasasessionmanager = PicasaSessionManager.getInstance();
                        android.net.Uri.Builder builder = new android.net.Uri.Builder();
                        builder.encodedPath("https://picasaweb.google.com/data/entry/api/user");
                        builder.appendPath(picasasessionmanager.getUserId());
                        builder.appendPath("albumid");
                        builder.appendPath((new StringBuilder()).append(mediaitem.getFolder().getFolderSourceId()).append("").toString());
                        builder.appendPath("photoid");
                        builder.appendPath((new StringBuilder()).append(mediaitem.getAndroidId()).append("").toString());
                        try
                        {
                            picasasessionmanager.getPicasaService().delete(new URL(builder.toString()), "*");
                        }
                        catch(Exception exception)
                        {
                            Log.w(TAG, (new StringBuilder()).append("error while deleting item from server: ").append(mediaitem.getPath()).toString(), exception);
                        }
                    }
                }
            }
            mediaitemdao.updateInTx(collection);
            deleteReferencesToMediaItems(collection);
        }
    }

    protected void deleteReferencesToMediaItems(Collection<MediaItem> collection)
    {
        DuplicatesService.deleteSetsForDeletedPhotos(collection);
        sendMomentDeletedIntent(ClusteringService.cleanDeletedMoments(collection));
    }

    protected void onHandleIntent(Intent intent)
    {
        long l;
        Integer ainteger[];
        int i;
        int j;
        Integer integer;
        if(intent.getAction().equals("android.intent.action.ACTION_POWER_CONNECTED") && !((FlayvrApplication)getApplication()).isBatteryOptimizationOn())
            return;
        Log.d(TAG, (new StringBuilder()).append("start building gallery service ").append(intent.getAction()).toString());
        Date date = new Date();
        HashMap hashmap;
        long l1;
        if(IntentActions.ACTION_DELETE_ITEMS.equals(intent.getAction()))
        {
            Object[] serializable1 = (Object[])intent.getExtras().get(IntentActions.EXTRA_ITEMS_REMOVED);
            try
            {
                Collection<MediaItem> items = DBManager.getInstance().getDaoSession().getMediaItemDao().queryBuilder().where(MediaItemDao.Properties.Id.in(serializable1), new WhereCondition[0]).list();
                deleteItems(items, true);
                Intent intent2 = new Intent(IntentActions.ACTION_NEW_MEDIA);
                intent2.putExtra(IntentActions.EXTRA_ITEMS_ADDED, new Long[0]);
                intent2.putExtra(IntentActions.EXTRA_ITEMS_REMOVED, serializable1);
                intent2.putExtra(IntentActions.EXTRA_FOLDERS_WITH_ADDED, new Long[0]);
                sendBroadcast(intent2);
            }
            catch(NullPointerException nullpointerexception)
            {
            }
            catch(IllegalArgumentException illegalargumentexception)
            {
            }catch(DaoException exception) {
                exception.printStackTrace();
            }
        } else if(IntentActions.ACTION_DELETE_FOLDERS.equals(intent.getAction())) {
            Object[] serializable = (Object[])intent.getExtras().get(IntentActions.EXTRA_FOLDERS_REMOVED);
            deleteFolders(DBManager.getInstance().getDaoSession().getFolderDao().queryBuilder().where(com.flayvr.myrollshared.data.FolderDao.Properties.Id.in(serializable), new WhereCondition[0]).list(), true);
            Intent intent1 = new Intent(IntentActions.ACTION_NEW_MEDIA);
            intent1.putExtra(IntentActions.EXTRA_ITEMS_ADDED, new Long[0]);
            intent1.putExtra(IntentActions.EXTRA_ITEMS_REMOVED, serializable);
            intent1.putExtra(IntentActions.EXTRA_FOLDERS_WITH_ADDED, new Long[0]);
            sendBroadcast(intent1);
        } else {
            LoadingResult loadingresult = scanPhotosWithRetry();
            Log.d(TAG, (new StringBuilder()).append("scan photos #").append(loadingresult.added.size()).append(" ").append(loadingresult.deleted.size()).toString());
            LoadingResult loadingresult1 = scanVideosWithRetries();
            Log.d(TAG, (new StringBuilder()).append("scan videos #").append(loadingresult1.added.size()).append(" ").append(loadingresult1.deleted.size()).toString());
            MediaItemDao mediaitemdao = DBManager.getInstance().getDaoSession().getMediaItemDao();
            LinkedList linkedlist = new LinkedList(loadingresult.added);
            linkedlist.addAll(loadingresult1.added);
            if(linkedlist.size() > 0)
            {
                mediaitemdao.insertOrReplaceInTx(linkedlist);
            }
            LinkedList linkedlist1 = new LinkedList(loadingresult.deleted);
            linkedlist1.addAll(loadingresult1.deleted);
            deleteItems(linkedlist1, false);
            scanThumbs();
            scanFolders(loadingresult, loadingresult1);
            sentBroadcastEvents(linkedlist, linkedlist1, intent.getAction());
        }
        l = (new Date()).getTime() - date.getTime();
        if(!SharedPreferencesManager.galleryBuilderRun())
        {
            SharedPreferencesManager.setGalleryBuilderRun();
            EventBus.getDefault().post(new GalleryBuilderFinishedFirstTimeEvent());
        }
        Log.i(TAG, (new StringBuilder()).append("timing: done ").append(l).toString());
        hashmap = new HashMap();
        l1 = DaoHelper.getPhotos(1).count();
        hashmap.put("total photos", (new StringBuilder()).append(l1).append("").toString());
        ainteger = PHOTOS_BUCKETS;
        i = ainteger.length;
        j = 0;
        while(j < i)
        {
            integer = ainteger[j];
            if(l1 < (long)integer.intValue())
                break;
            hashmap.put((new StringBuilder()).append("has ").append(integer).append(" photos").toString(), "true");
            j++;
        }
        long l2 = DaoHelper.getVideos(1).count();
        hashmap.put("total videos", (new StringBuilder()).append(l2).append("").toString());
        hashmap.put("total folders", (new StringBuilder()).append(DaoHelper.getFolders(1).count()).append("").toString());
        KISSmetricsAPI.sharedAPI().set(hashmap);
        sendAssetsAnalyitcs(l2 + l1);
    }

    protected void sendMomentDeletedIntent(List<Long> list)
    {
        if(list.size() > 0)
        {
            long al[] = new long[list.size()];
            for(int i = 0; i < al.length; i++)
            {
                al[i] = ((Long)list.get(i)).longValue();
            }

            Intent intent = new Intent(IntentActions.ACTION_NEW_MOMENTS);
            intent.putExtra(IntentActions.EXTRA_MOMENTS_ADDED, new long[0]);
            intent.putExtra(IntentActions.EXTRA_MOMENTS_REMOVED, al);
            sendBroadcast(intent);
        }
    }

    protected void sentBroadcastEvents(Collection<MediaItem> collection, Collection<MediaItem> collection1, String s)
    {
        if(collection.size() != 0 || collection1.size() != 0)
        {
            HashSet hashset = new HashSet();
            HashSet hashset1 = new HashSet();
            MediaItem mediaitem;
            for(Iterator iterator = collection.iterator(); iterator.hasNext(); hashset1.add(mediaitem.getFolderId()))
            {
                mediaitem = (MediaItem)iterator.next();
                hashset.add(mediaitem.getId());
            }

            HashSet hashset2 = new HashSet();
            for(Iterator iterator1 = collection1.iterator(); iterator1.hasNext(); hashset2.add(((MediaItem)iterator1.next()).getId())) { }
            Intent intent = new Intent(IntentActions.ACTION_NEW_MEDIA);
            intent.putExtra(IntentActions.EXTRA_ITEMS_ADDED, ((java.io.Serializable) (hashset.toArray())));
            intent.putExtra(IntentActions.EXTRA_ITEMS_REMOVED, ((java.io.Serializable) (hashset2.toArray())));
            intent.putExtra(IntentActions.EXTRA_FOLDERS_WITH_ADDED, ((java.io.Serializable) (hashset1.toArray())));
            sendBroadcast(intent);
        } else
        if(s == IntentActions.ACTION_APP_CREATE)
        {
            sendBroadcast(new Intent(IntentActions.ACTION_GALLERY_BUILDER_NO_CHANGE));
            return;
        }
    }

    static 
    {
        Integer ainteger[] = new Integer[13];
        ainteger[0] = Integer.valueOf(50);
        ainteger[1] = Integer.valueOf(100);
        ainteger[2] = Integer.valueOf(200);
        ainteger[3] = Integer.valueOf(300);
        ainteger[4] = Integer.valueOf(400);
        ainteger[5] = Integer.valueOf(500);
        ainteger[6] = Integer.valueOf(750);
        ainteger[7] = Integer.valueOf(1000);
        ainteger[8] = Integer.valueOf(1500);
        ainteger[9] = Integer.valueOf(2000);
        ainteger[10] = Integer.valueOf(3000);
        ainteger[11] = Integer.valueOf(4000);
        ainteger[12] = Integer.valueOf(5000);
        PHOTOS_BUCKETS = ainteger;
    }


    private class LoadingResult
    {
        private final List<MediaItem> added;
        private final Map<Long, GalleryBuilderService.FolderInfo> addedFolders;
        private final List<MediaItem> deleted;
        private final Collection<Long> deletedFolders;

        public LoadingResult(List<MediaItem> list, List<MediaItem> list1, Map<Long, GalleryBuilderService.FolderInfo> map, Collection<Long> collection)
        {
            added = list;
            deleted = list1;
            addedFolders = map;
            deletedFolders = collection;
        }
    }


    private class FolderInfo
    {

        String name;
        String path;

        public FolderInfo(String s, String s1)
        {
            super();
            name = s;
            path = s1;
        }
    }

}

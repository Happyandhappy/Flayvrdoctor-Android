package com.flayvr.myrollshared.oldclasses;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.exceptions.MergingDifferentAlbumFlayvrsException;
import de.greenrobot.dao.DaoException;
import java.util.*;
import java.util.concurrent.Callable;

public class FlayvrsDBManager
{

    public static final String DB_FILE_NAME = "moments-db";
    private static final String TAG = "flayvr_db_manager";
    private static FlayvrsDBManager instance;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private SQLiteDatabase db;

    private FlayvrsDBManager()
    {
        db = (new MigrationHelper(FlayvrApplication.getAppContext(), "moments-db", null)).getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static FlayvrsDBManager getInstance()
    {
        synchronized (FlayvrsDBManager.class) {
            FlayvrsDBManager flayvrsdbmanager;
            if(instance == null)
                instance = new FlayvrsDBManager();
            flayvrsdbmanager = instance;
            return flayvrsdbmanager;
        }
    }

    public void batchUpdateItems(List list)
    {
        daoSession.getDBMediaItemDao().insertOrReplaceInTx(list);
    }

    public void batchUpdateMoments(final List moments, final List momentItems)
    {
        daoSession.runInTx(new Runnable() {
            @Override
            public void run() {
                daoSession.getDBMomentDao().insertOrReplaceInTx(moments);
                daoSession.getDBMomentsItemsDao().insertOrReplaceInTx(momentItems);
            }
        });
    }

    public List cacheItems()
    {
        return daoSession.getDBMediaItemDao().loadAll();
    }

    public void changeHideMoment(FlayvrGroup flayvrgroup)
    {
        boolean flag;
        DBMoment dbmoment;
        if(!flayvrgroup.isHidden())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        flayvrgroup.setIsHidden(flag);
        dbmoment = getOrCreateMoment(flayvrgroup);
        dbmoment.setIsHidden(Boolean.valueOf(flayvrgroup.isHidden()));
        daoSession.getDBMomentDao().update(dbmoment);
    }

    public List getFolderMoments(String s)
    {
        List list = getOrCreateFolder(s).getDBMomentList();
        Collections.sort(list, new Comparator<DBMoment>() {
            @Override
            public int compare(DBMoment dbmoment, DBMoment dbmoment1) {
                if(dbmoment.getDate() == null && dbmoment1.getDate() == null)
                {
                    return 0;
                }
                if(dbmoment.getDate() == null)
                {
                    return 1;
                }
                if(dbmoment1.getDate() == null)
                {
                    return -1;
                } else
                {
                    return dbmoment1.getDate().compareTo(dbmoment.getDate());
                }
            }
        });
        return list;
    }

    public DBMediaItem getItem(long l)
    {
        return (DBMediaItem)daoSession.getDBMediaItemDao().load(Long.valueOf(l));
    }

    protected DBFolder getOrCreateFolder(String s)
    {
        DBFolder dbfolder = (DBFolder)daoSession.getDBFolderDao().load(s);
        if(dbfolder == null)
        {
            dbfolder = new DBFolder(s);
            daoSession.getDBFolderDao().insert(dbfolder);
        }
        return dbfolder;
    }

    protected DBMoment getOrCreateMoment(FlayvrGroup flayvrgroup)
    {
        DBMoment dbmoment = (DBMoment)daoSession.getDBMomentDao().load(Long.valueOf(flayvrgroup.getFlayvrId().longValue()));
        if(dbmoment == null)
        {
            dbmoment = new DBMoment();
            dbmoment.setFolderId(getOrCreateFolder(flayvrgroup.getAlbumId()).getFolderId());
            flayvrgroup.setFlayvrId((int)daoSession.getDBMomentDao().insert(dbmoment));
        }
        return dbmoment;
    }

    public void mergeMoments(FlayvrGroup flayvrgroup, FlayvrGroup flayvrgroup1) throws MergingDifferentAlbumFlayvrsException {
        if(flayvrgroup.getAlbumId() != flayvrgroup1.getAlbumId())
        {
            throw new MergingDifferentAlbumFlayvrsException("different albums: "+flayvrgroup.getAlbumId()+" "+flayvrgroup1.getAlbumId());
        }
        Iterator iterator = flayvrgroup1.getPhotoItems().iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            PhotoMediaItem photomediaitem = (PhotoMediaItem)iterator.next();
            if(!flayvrgroup.getPhotoItems().contains(photomediaitem))
            {
                flayvrgroup.addPhotoItem(photomediaitem);
            }
        } while(true);
        Iterator iterator1 = flayvrgroup1.getVideoItems().iterator();
        do
        {
            if(!iterator1.hasNext())
            {
                break;
            }
            VideoMediaItem videomediaitem = (VideoMediaItem)iterator1.next();
            if(!flayvrgroup.getVideoItems().contains(videomediaitem))
            {
                flayvrgroup.addVideoItem(videomediaitem);
            }
        } while(true);
        if(!flayvrgroup.getDate().inTheSameDay(flayvrgroup1.getDate()))
        {
            flayvrgroup.setDate(new MediaGroupRangeDate(flayvrgroup1.getDate(), flayvrgroup.getDate()));
            flayvrgroup.setDateStr(flayvrgroup.getDate().toString());
        }
        DBMoment dbmoment = getOrCreateMoment(flayvrgroup);
        dbmoment.setDate(flayvrgroup.getDate());
        dbmoment.setTitle(flayvrgroup.getTitle());
        setItems(flayvrgroup, dbmoment);
        daoSession.getDBMomentDao().update(dbmoment);
        DBMoment dbmoment1 = getOrCreateMoment(flayvrgroup1);
        flayvrgroup1.setMerged(true);
        dbmoment1.setIsMerged(Boolean.valueOf(flayvrgroup1.isMerged()));
        daoSession.getDBMomentDao().update(dbmoment1);
    }

    public void removeMoment(final DBMoment moment)
    {
        FlayvrApplication.runAction(new Runnable() {
            @Override
            public void run() {
                daoSession.getDBMomentDao().delete(moment);
            }
        });
    }

    protected void setInteractions(FlayvrGroup flayvrgroup, DBMoment dbmoment)
    {
        Map map = flayvrgroup.getInteracitons();
        HashMap hashmap = new HashMap();
        AbstractMediaItem abstractmediaitem;
        for(Iterator iterator = map.keySet().iterator(); iterator.hasNext(); hashmap.put(abstractmediaitem.getItemId(), map.get(abstractmediaitem)))
        {
            abstractmediaitem = (AbstractMediaItem)iterator.next();
        }

        dbmoment.setInteractions(hashmap);
    }

    protected void setItems(FlayvrGroup flayvrgroup, DBMoment dbmoment)
    {
        Object obj;
        try {
            List list1 = dbmoment.getMomentsItems();
            obj = list1;
        }catch(DaoException daoexception){
            obj = new LinkedList();
        }
        HashMap hashmap;
        List list = flayvrgroup.getAllItems();
        hashmap = new HashMap();
        AbstractMediaItem abstractmediaitem1;
        for(Iterator iterator = list.iterator(); iterator.hasNext(); hashmap.put(abstractmediaitem1.getItemId(), abstractmediaitem1))
        {
            abstractmediaitem1 = (AbstractMediaItem)iterator.next();
        }
        for(Iterator iterator1 = ((List) (obj)).iterator(); iterator1.hasNext();)
        {
            DBMomentsItems dbmomentsitems1 = (DBMomentsItems)iterator1.next();
            if(hashmap.containsKey(dbmomentsitems1.getId()))
            {
                hashmap.remove(dbmomentsitems1.getId());
            } else
            {
                daoSession.getDBMomentsItemsDao().delete(dbmomentsitems1);
            }
        }

        Iterator iterator2 = hashmap.values().iterator();
        while(iterator2.hasNext()) 
        {
            AbstractMediaItem abstractmediaitem = (AbstractMediaItem)iterator2.next();
            DBMediaItem dbmediaitem = getItem(abstractmediaitem.getItemId().longValue());
            DBMediaItem dbmediaitem1;
            DBMomentsItems dbmomentsitems;
            if(dbmediaitem == null)
                dbmediaitem1 = updateItem(abstractmediaitem);
            else
                dbmediaitem1 = dbmediaitem;
            dbmomentsitems = new DBMomentsItems();
            dbmomentsitems.setItemId(dbmediaitem1.getId());
            dbmomentsitems.setMomentId(dbmoment.getId());
            daoSession.getDBMomentsItemsDao().insert(dbmomentsitems);
        }
    }

    protected void setNewerItems(FlayvrGroup flayvrgroup, DBMoment dbmoment)
    {
        boolean flag = true;
        if(flayvrgroup.hasVideo())
        {
            PhotoMediaItem photomediaitem = (PhotoMediaItem)flayvrgroup.getPhotoItems().get(0);
            VideoMediaItem videomediaitem = (VideoMediaItem)flayvrgroup.getVideoItems().get(0);
            if(photomediaitem.getDate().compareTo(videomediaitem.getDate()) > 0)
            {
                boolean flag1;
                if(!flayvrgroup.getOriginalItems().contains(photomediaitem.getItemId()))
                {
                    flag1 = flag;
                } else
                {
                    flag1 = false;
                }
                dbmoment.setNewerItemAdded(Boolean.valueOf(flag1));
                return;
            }
            if(flayvrgroup.getOriginalItems().contains(videomediaitem.getItemId()))
            {
                flag = false;
            }
            dbmoment.setNewerItemAdded(Boolean.valueOf(flag));
            return;
        }
        if(flayvrgroup.getOriginalItems().contains(((PhotoMediaItem)flayvrgroup.getPhotoItems().get(0)).getItemId()))
        {
            flag = false;
        }
        dbmoment.setNewerItemAdded(Boolean.valueOf(flag));
    }

    public void setTitleFromCalendar(FlayvrGroup flayvrgroup)
    {
        DBMoment dbmoment = getOrCreateMoment(flayvrgroup);
        dbmoment.setIsTitleFromCalendar(Boolean.valueOf(flayvrgroup.isTitleFromCalendar()));
        daoSession.getDBMomentDao().update(dbmoment);
    }

    public DBMediaItem updateItem(final AbstractMediaItem item)
    {
        return (DBMediaItem)daoSession.callInTxNoException(new Callable<DBMediaItem>() {
            @Override
            public DBMediaItem call() throws Exception {
                return updateSingleItem(item);
            }
        });
    }

    public void updateMoment(final FlayvrGroup moment)
    {
        FlayvrApplication.runAction(new Runnable() {
            @Override
            public void run() {
                daoSession.runInTx(new Runnable() {
                    @Override
                    public void run() {
                        DBMoment dbmoment = getOrCreateMoment(moment);
                        dbmoment.setTitle(moment.getTitle());
                        dbmoment.setLocation(moment.getLocation());
                        dbmoment.setDate(moment.getDate());
                        dbmoment.setOriginalItems(moment.getOriginalItems());
                        dbmoment.setIsMuted(moment.isMuted());
                        dbmoment.setIsTrip(Boolean.valueOf(moment.isTrip()));
                        dbmoment.setIsMerged(Boolean.valueOf(moment.isMerged()));
                        dbmoment.setIsHidden(Boolean.valueOf(moment.isHidden()));
                        dbmoment.setIsFavorite(Boolean.valueOf(moment.isFavorite()));
                        dbmoment.setIsTitleFromCalendar(Boolean.valueOf(moment.isTitleFromCalendar()));
                        dbmoment.setWatchCount(Integer.valueOf(moment.getWatchCount()));
                        if(moment.getCoverItem() != null)
                        {
                            dbmoment.setCoverId(moment.getCoverItem().getItemId());
                        }
                        setNewerItems(moment, dbmoment);
                        setInteractions(moment, dbmoment);
                        daoSession.getDBMomentDao().update(dbmoment);
                        setItems(moment, dbmoment);
                    }
                });
            }
        });
    }

    public void updateMomentFavorite(FlayvrGroup flayvrgroup)
    {
        DBMoment dbmoment = getOrCreateMoment(flayvrgroup);
        dbmoment.setIsFavorite(Boolean.valueOf(flayvrgroup.isFavorite()));
        daoSession.getDBMomentDao().update(dbmoment);
    }

    public void updateMomentInteractions(FlayvrGroup flayvrgroup)
    {
        DBMoment dbmoment = getOrCreateMoment(flayvrgroup);
        setInteractions(flayvrgroup, dbmoment);
        daoSession.getDBMomentDao().update(dbmoment);
    }

    public void updateMomentLocation(FlayvrGroup flayvrgroup)
    {
        DBMoment dbmoment = getOrCreateMoment(flayvrgroup);
        dbmoment.setLocation(flayvrgroup.getLocation());
        daoSession.getDBMomentDao().update(dbmoment);
    }

    public void updateMomentMute(FlayvrGroup flayvrgroup)
    {
        DBMoment dbmoment = getOrCreateMoment(flayvrgroup);
        dbmoment.setIsMuted(flayvrgroup.isMuted());
        daoSession.getDBMomentDao().update(dbmoment);
    }

    public void updateMomentTitle(FlayvrGroup flayvrgroup)
    {
        DBMoment dbmoment = getOrCreateMoment(flayvrgroup);
        dbmoment.setTitle(flayvrgroup.getTitle());
        daoSession.getDBMomentDao().update(dbmoment);
    }

    public void updateMomentWatchCount(FlayvrGroup flayvrgroup)
    {
        DBMoment dbmoment = getOrCreateMoment(flayvrgroup);
        dbmoment.setWatchCount(Integer.valueOf(flayvrgroup.getWatchCount()));
        daoSession.getDBMomentDao().update(dbmoment);
    }

    protected DBMediaItem updateSingleItem(AbstractMediaItem abstractmediaitem)
    {
        DBMediaItem dbmediaitem = getItem(abstractmediaitem.getItemId().longValue());
        DBMediaItem dbmediaitem2;
        PhotoMediaItem photomediaitem;
        PointF pointf;
        if(dbmediaitem == null)
        {
            DBMediaItem dbmediaitem1 = new DBMediaItem(abstractmediaitem.getItemId());
            if(abstractmediaitem instanceof PhotoMediaItem)
            {
                dbmediaitem1.setType("photo");
                dbmediaitem2 = dbmediaitem1;
            } else
            {
                if(abstractmediaitem instanceof VideoMediaItem)
                {
                    dbmediaitem1.setType("video");
                }
                dbmediaitem2 = dbmediaitem1;
            }
        } else
        {
            dbmediaitem2 = dbmediaitem;
        }
        if(abstractmediaitem instanceof PhotoMediaItem)
        {
            photomediaitem = (PhotoMediaItem)abstractmediaitem;
            dbmediaitem2.setBlurry(photomediaitem.getBlurry());
            dbmediaitem2.setColor(photomediaitem.getColorful());
            dbmediaitem2.setDark(photomediaitem.getDark());
            dbmediaitem2.setFacesJson(photomediaitem.getFacesJson());
            dbmediaitem2.setFacesCount(Integer.valueOf(photomediaitem.getFacesCount()));
            pointf = photomediaitem.getCenter();
            if(pointf != null)
            {
                dbmediaitem2.setCenterX(Float.valueOf(pointf.x));
                dbmediaitem2.setCenterY(Float.valueOf(pointf.y));
            } else
            {
                dbmediaitem2.setCenterX(Float.valueOf(-1F));
                dbmediaitem2.setCenterY(Float.valueOf(-1F));
            }
            dbmediaitem2.setShouldRunHeavyProcessing(Boolean.valueOf(photomediaitem.shouldRunHeavyFaceDetection()));
        }
        dbmediaitem2.setProp(abstractmediaitem.getProp());
        daoSession.getDBMediaItemDao().insertOrReplace(dbmediaitem2);
        return dbmediaitem2;
    }
}

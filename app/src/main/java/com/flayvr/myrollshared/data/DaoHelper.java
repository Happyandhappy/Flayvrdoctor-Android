package com.flayvr.myrollshared.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.flayvr.myrollshared.utils.GeneralUtils;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.util.*;
import org.apache.commons.lang3.StringUtils;

public class DaoHelper
{

    public DaoHelper()
    {
    }

    public static long getAllBackupUpPhotosCount(int i)
    {
        QueryBuilder querybuilder = getPhotos(i);
        querybuilder.where(MediaItemDao.Properties.ServerId.isNotNull(), new WhereCondition[0]);
        return querybuilder.count();
    }

    public static long getAllNotBackupUpPhotosCount(int i)
    {
        QueryBuilder querybuilder = getPhotos(i);
        querybuilder.where(MediaItemDao.Properties.ServerId.isNull(), new WhereCondition[0]);
        return querybuilder.count();
    }

    public static QueryBuilder getAllPhotos()
    {
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getMediaItemDao().queryBuilder();
        querybuilder.where(MediaItemDao.Properties.Type.eq(Integer.valueOf(1)), new WhereCondition[0]);
        querybuilder.where(MediaItemDao.Properties.WasDeleted.isNull(), new WhereCondition[0]);
        return querybuilder;
    }

    public static long getAllPhotosCount(int i)
    {
        return getPhotos(i).count();
    }

    public static List getBestMoments(int i, double d, Set set)
    {
        DaoSession daosession = DBManager.getInstance().getDaoSession();
        String as[] = daosession.getMomentDao().getAllColumns();
        StringBuilder stringbuilder = new StringBuilder();
        SqlUtils.appendColumns(stringbuilder, "mo", as);
        String s;
        StringBuilder stringbuilder1;
        String s1;
        String s2;
        Cursor cursor;
        ArrayList arraylist;
        if(set != null)
        {
            s = (new StringBuilder()).append("(").append(StringUtils.join(set, ",")).append(")").toString();
        } else
        {
            s = null;
        }
        stringbuilder1 = (new StringBuilder()).append("SELECT i.count, i.avg ,").append(stringbuilder.toString()).append(" ").append("FROM ").append("MOMENT").append(" as mo ").append("INNER JOIN ( SELECT ").append(MomentsItemsDao.Properties.MomentId.columnName).append(" AS momentId, AVG(mi.").append(MediaItemDao.Properties.Score.columnName).append(") as avg, COUNT(mi.").append(MediaItemDao.Properties.Id.columnName).append(") as count ").append("FROM ").append("MOMENTS_ITEMS").append(" ").append("INNER JOIN ").append("MEDIA_ITEM").append(" as mi ").append("ON mi.").append(MediaItemDao.Properties.Id.columnName).append(" = ").append(MomentsItemsDao.Properties.ItemId.columnName).append(" ").append("WHERE ").append(MediaItemDao.Properties.Score.columnName).append(" IS NOT NULL ").append("GROUP BY ").append(MomentsItemsDao.Properties.MomentId.columnName).append(") as i ").append("ON mo.").append(MomentDao.Properties.Id.columnName).append(" = momentId ").append("WHERE i.count > ").append(i).append(" AND i.avg > ").append(d);
        if(s != null)
        {
            s1 = (new StringBuilder()).append(" AND mo.").append(MomentDao.Properties.FolderId.columnName).append(" IN ").append(s).toString();
        } else
        {
            s1 = "";
        }
        s2 = stringbuilder1.append(s1).append(" ORDER BY ").append(MomentDao.Properties.StartDate.columnName).append(" DESC").toString();
        cursor = daosession.getMediaItemDao().getDatabase().rawQuery(s2, null);
        arraylist = new ArrayList();
        Moment moment;
        for(; cursor.moveToNext(); arraylist.add(moment))
        {
            moment = daosession.getMomentDao().readEntity(cursor, 2);
            moment.__setDaoSession(daosession);
        }

        return arraylist;
    }

    public static int getBestPhotoCount(ClassifierThreshold classifierthreshold, Set set)
    {
        String s;
        DaoSession daosession;
        StringBuilder stringbuilder;
        String s1;
        String s2;
        Cursor cursor;
        if(set != null)
        {
            s = (new StringBuilder()).append("(").append(StringUtils.join(set, ",")).append(")").toString();
        } else
        {
            s = null;
        }
        daosession = DBManager.getInstance().getDaoSession();
        stringbuilder = (new StringBuilder()).append("SELECT COUNT(").append(MediaItemDao.Properties.Id.columnName).append(") ").append("FROM ").append("MEDIA_ITEM").append(" AS mi").append(" ").append("WHERE mi.").append(MediaItemDao.Properties.Score.columnName).append(" >= ").append(classifierthreshold.getBestDirectoryScore()).append(" ").append("AND mi.").append(MediaItemDao.Properties.FolderId.columnName).append(" NOT IN ").append(getBestPhotosForbiddenIds()).append(" ");
        if(s != null)
        {
            s1 = (new StringBuilder()).append("AND mi.").append(MediaItemDao.Properties.FolderId.columnName).append(" IN ").append(s).append(" ").toString();
        } else
        {
            s1 = "";
        }
        s2 = stringbuilder.append(s1).append("AND mi.").append(MediaItemDao.Properties.WasDeleted.columnName).append(" is null").toString();
        cursor = daosession.getMediaItemDao().getDatabase().rawQuery(s2, null);
        if(cursor.moveToNext())
        {
            return cursor.getInt(0);
        } else
        {
            return 0;
        }
    }

    public static List getBestPhotos(ClassifierThreshold classifierthreshold, boolean flag, Set set)
    {
        Object obj;
        if(flag)
        {
            obj = getItemsWithDuplicateInFolders(set, null, false, true, classifierthreshold, false);
        } else
        {
            String s;
            DaoSession daosession;
            String as[];
            StringBuilder stringbuilder;
            String s1;
            Cursor cursor;
            if(set != null)
            {
                s = (new StringBuilder()).append("(").append(StringUtils.join(set, ",")).append(")").toString();
            } else
            {
                s = null;
            }
            daosession = DBManager.getInstance().getDaoSession();
            as = daosession.getMediaItemDao().getAllColumns();
            stringbuilder = new StringBuilder();
            SqlUtils.appendColumns(stringbuilder, "mi", as);
            s1 = (new StringBuilder()).append("SELECT ").append(stringbuilder.toString()).append(" ").append("FROM ").append("MEDIA_ITEM").append(" AS mi").append(" ").append("WHERE mi.").append(MediaItemDao.Properties.Score.columnName).append(" >= ").append(classifierthreshold.getBestDirectoryScore()).append(" ").append("AND mi.").append(MediaItemDao.Properties.Type.columnName).append(" = ").append(1).append(" ").append("AND mi.").append(MediaItemDao.Properties.FolderId.columnName).append(" NOT IN ").append(getBestPhotosForbiddenIds()).append(" ").append("AND mi.").append(MediaItemDao.Properties.FolderId.columnName).append(" IN ").append(s).append(" ").append("AND mi.").append(MediaItemDao.Properties.Source.columnName).append(" = ").append(1).append(" ").append("AND mi.").append(MediaItemDao.Properties.WasDeleted.columnName).append(" is null ").append("ORDER by mi.").append(MediaItemDao.Properties.Date.columnName).append(" desc").toString();
            cursor = daosession.getMediaItemDao().getDatabase().rawQuery(s1, null);
            obj = new ArrayList();
            while(cursor.moveToNext()) 
            {
                MediaItem mediaitem = daosession.getMediaItemDao().readEntity(cursor, 0);
                mediaitem.__setDaoSession(daosession);
                ((List) (obj)).add(mediaitem);
            }
        }
        return ((List) (obj));
    }

    public static String getBestPhotosForbiddenIds()
    {
        return (new StringBuilder()).append("(").append(StringUtils.join(GeneralUtils.getScreenshotFoldersIds(), ",")).append(")").toString();
    }

    public static QueryBuilder getFolders(int i)
    {
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getFolderDao().queryBuilder();
        querybuilder.where(FolderDao.Properties.Source.eq(Integer.valueOf(i)), new WhereCondition[0]);
        return querybuilder;
    }

    public static QueryBuilder getHiddenFoldersQueryBuilder(int i)
    {
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getFolderDao().queryBuilder();
        querybuilder.where(FolderDao.Properties.IsHidden.eq(Boolean.valueOf(true)), new WhereCondition[0]);
        querybuilder.where(FolderDao.Properties.Source.eq(Integer.valueOf(i)), new WhereCondition[0]);
        return querybuilder;
    }

    public static List getItemsWithDuplicateInFolders(Collection collection, Collection collection1, boolean flag, boolean flag1, ClassifierThreshold classifierthreshold, boolean flag2)
    {
        DaoSession daosession = DBManager.getInstance().getDaoSession();
        String as[] = daosession.getMediaItemDao().getAllColumns();
        StringBuilder stringbuilder = new StringBuilder();
        SqlUtils.appendColumns(stringbuilder, "mi", as);
        String s = (new StringBuilder()).append("(").append(StringUtils.join(collection, ",")).append(")").toString();
        String s1 = (new StringBuilder()).append("(").append(StringUtils.join(collection1, ",")).append(")").toString();
        StringBuilder stringbuilder1 = (new StringBuilder()).append("SELECT ds.").append(DuplicatesSetsToPhotosDao.Properties.DuplicatesSetId.columnName).append(", ").append(stringbuilder.toString()).append(" ").append("FROM ").append("MEDIA_ITEM").append(" AS mi").append(" ").append("LEFT JOIN ").append("DUPLICATES_SETS_TO_PHOTOS").append(" ds ").append("ON ds.").append(DuplicatesSetsToPhotosDao.Properties.PhotoId.columnName).append("=mi.").append(MediaItemDao.Properties.Id.columnName).append(" ").append("WHERE ");
        String s2;
        StringBuilder stringbuilder2;
        String s3;
        StringBuilder stringbuilder3;
        String s4;
        StringBuilder stringbuilder4;
        String s5;
        StringBuilder stringbuilder5;
        String s6;
        StringBuilder stringbuilder6;
        String s7;
        String s8;
        if(collection != null && collection.size() > 0)
        {
            s2 = (new StringBuilder()).append("mi.").append(MediaItemDao.Properties.FolderId.columnName).append(" IN ").append(s).append(" AND ").toString();
        } else
        {
            s2 = "";
        }
        stringbuilder2 = stringbuilder1.append(s2);
        if(flag1)
        {
            s3 = (new StringBuilder()).append("mi.").append(MediaItemDao.Properties.FolderId.columnName).append(" NOT IN ").append(getBestPhotosForbiddenIds()).append(" AND ").toString();
        } else
        {
            s3 = "";
        }
        stringbuilder3 = stringbuilder2.append(s3).append(MediaItemDao.Properties.WasDeleted.columnName).append(" is null ");
        if(collection1 != null && collection1.size() > 0)
        {
            s4 = (new StringBuilder()).append("AND mi.").append(MediaItemDao.Properties.Id.columnName).append(" IN ").append(s1).append(" ").toString();
        } else
        {
            s4 = "";
        }
        stringbuilder4 = stringbuilder3.append(s4).append("AND (( ").append(MediaItemDao.Properties.Type.columnName).append(" = ").append(1).append(" ");
        if(flag1)
        {
            s5 = (new StringBuilder()).append("AND ").append(MediaItemDao.Properties.Score.columnName).append(" >= ").append(classifierthreshold.getBestDirectoryScore()).append(" ").toString();
        } else
        {
            s5 = "";
        }
        stringbuilder5 = stringbuilder4.append(s5);
        if(flag)
        {
            s6 = (new StringBuilder()).append("AND NOT (").append(MediaItemDao.Properties.IsBad.columnName).append(" = 1) ").toString();
        } else
        {
            s6 = "";
        }
        stringbuilder6 = stringbuilder5.append(s6).append(") OR ( ").append(MediaItemDao.Properties.Type.columnName).append(" = ").append(2).append(" )) ");
        if(flag2)
        {
            s7 = "";
        } else
        {
            s7 = (new StringBuilder()).append(" AND mi.").append(MediaItemDao.Properties.Type.columnName).append(" = ").append(1).append(" ").toString();
        }
        s8 = stringbuilder6.append(s7).append("AND mi.").append(MediaItemDao.Properties.Source.columnName).append(" = ").append(1).append(" ").append("ORDER by mi.").append(MediaItemDao.Properties.Date.columnName).append(" desc").toString();
        return getItemsWithDuplicates(daosession.getMediaItemDao().getDatabase().rawQuery(s8, null));
    }

    public static List getItemsWithDuplicateInFolders(Set set, boolean flag)
    {
        return getItemsWithDuplicateInFolders(((Collection) (set)), ((Collection) (new HashSet())), flag, false, null, true);
    }

    public static List getItemsWithDuplicates(Cursor cursor)
    {
        DaoSession daosession = DBManager.getInstance().getDaoSession();
        ArrayList arraylist = new ArrayList();
        HashMap hashmap = new HashMap();
        do
        {
            if(!cursor.moveToNext())
            {
                break;
            }
            MediaItem mediaitem = daosession.getMediaItemDao().readEntity(cursor, 1);
            mediaitem.__setDaoSession(daosession);
            if(mediaitem != null)
            {
                Long long1 = Long.valueOf(cursor.getLong(0));
                if(long1.longValue() == 0L)
                {
                    LinkedList linkedlist = new LinkedList();
                    linkedlist.add(mediaitem);
                    arraylist.add(linkedlist);
                } else
                if(!hashmap.containsKey(long1))
                {
                    LinkedList linkedlist1 = new LinkedList();
                    linkedlist1.add(mediaitem);
                    arraylist.add(linkedlist1);
                    hashmap.put(long1, linkedlist1);
                } else
                {
                    ((List)hashmap.get(long1)).add(mediaitem);
                }
            }
        } while(true);
        ArrayList arraylist1 = new ArrayList();
        for(Iterator iterator = arraylist.iterator(); iterator.hasNext();)
        {
            List list = (List)iterator.next();
            if(list.size() >= 3)
            {
                arraylist1.add(new MultipleMediaItem(list));
            } else
            {
                Iterator iterator1 = list.iterator();
                while(iterator1.hasNext()) 
                {
                    arraylist1.add((MediaItem)iterator1.next());
                }
            }
        }

        return arraylist1;
    }

    public static MediaItem getLastBackedUptPhoto(int i)
    {
        DaoSession daosession = DBManager.getInstance().getDaoSession();
        String as[] = daosession.getMediaItemDao().getAllColumns();
        StringBuilder stringbuilder = new StringBuilder();
        SqlUtils.appendColumns(stringbuilder, "mi", as);
        String s = (new StringBuilder()).append("SELECT ").append(stringbuilder.toString()).append(" ").append("FROM ").append("MEDIA_ITEM").append(" AS mi").append(" ").append("WHERE mi.").append(MediaItemDao.Properties.Type.columnName).append(" = ").append(1).append(" ").append("AND mi.").append(MediaItemDao.Properties.Source.columnName).append(" = ").append(i).append(" ").append("AND mi.").append(MediaItemDao.Properties.WasDeleted.columnName).append(" is null ").append("AND mi.").append(MediaItemDao.Properties.ServerId.columnName).append(" is not null ").append("ORDER by mi.").append(MediaItemDao.Properties.Date.columnName).append(" desc").toString();
        Cursor cursor = daosession.getMediaItemDao().getDatabase().rawQuery(s, null);
        boolean flag = cursor.moveToNext();
        MediaItem mediaitem = null;
        if(flag)
        {
            mediaitem = daosession.getMediaItemDao().readEntity(cursor, 0);
        }
        return mediaitem;
    }

    public static MediaItem getLastBestPhoto(ClassifierThreshold classifierthreshold, Set set)
    {
        String s;
        DaoSession daosession;
        String as[];
        StringBuilder stringbuilder;
        StringBuilder stringbuilder1;
        String s1;
        String s2;
        Cursor cursor;
        boolean flag;
        MediaItem mediaitem;
        if(set != null)
        {
            s = (new StringBuilder()).append("(").append(StringUtils.join(set, ",")).append(")").toString();
        } else
        {
            s = null;
        }
        daosession = DBManager.getInstance().getDaoSession();
        as = daosession.getMediaItemDao().getAllColumns();
        stringbuilder = new StringBuilder();
        SqlUtils.appendColumns(stringbuilder, "mi", as);
        stringbuilder1 = (new StringBuilder()).append("SELECT ").append(stringbuilder.toString()).append(" ").append("FROM ").append("MEDIA_ITEM").append(" AS mi").append(" ").append("INNER JOIN ").append("(SELECT MAX(").append(MediaItemDao.Properties.Date.columnName).append(") as max, ").append(MediaItemDao.Properties.Score.columnName).append(" as score ").append("FROM ").append("MEDIA_ITEM").append(" ").append("WHERE score >= ").append(classifierthreshold.getBestDirectoryScore()).append(" ").append("AND ").append(MediaItemDao.Properties.FolderId.columnName).append(" NOT IN ").append(getBestPhotosForbiddenIds()).append(" ").append("AND ").append(MediaItemDao.Properties.WasDeleted.columnName).append(" is null ");
        if(s != null)
        {
            s1 = (new StringBuilder()).append("AND ").append(MediaItemDao.Properties.FolderId.columnName).append(" IN ").append(s).append(" ").toString();
        } else
        {
            s1 = "";
        }
        s2 = stringbuilder1.append(s1).append(" ) ").append("ON max = mi.").append(MediaItemDao.Properties.Date.columnName).toString();
        cursor = daosession.getMediaItemDao().getDatabase().rawQuery(s2, null);
        flag = cursor.moveToNext();
        mediaitem = null;
        if(flag)
        {
            mediaitem = daosession.getMediaItemDao().readEntity(cursor, 0);
        }
        return mediaitem;
    }

    public static HashSet getLocalNotHiddenFolderIds()
    {
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getFolderDao().queryBuilder();
        WhereCondition wherecondition = FolderDao.Properties.Source.eq(Integer.valueOf(1));
        WhereCondition awherecondition[] = new WhereCondition[1];
        awherecondition[0] = querybuilder.or(FolderDao.Properties.IsHidden.isNull(), FolderDao.Properties.IsHidden.eq(Boolean.valueOf(false)), new WhereCondition[0]);
        querybuilder.where(wherecondition, awherecondition);
        List list = querybuilder.list();
        HashSet hashset = new HashSet();
        for(Iterator iterator = list.iterator(); iterator.hasNext(); hashset.add(((Folder)iterator.next()).getId())) { }
        return hashset;
    }

    public static long getMomentsCount(int i)
    {
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getMomentDao().queryBuilder();
        List list = getFolders(i).list();
        LinkedList linkedlist = new LinkedList();
        for(Iterator iterator = list.iterator(); iterator.hasNext(); linkedlist.add(((Folder)iterator.next()).getId())) { }
        querybuilder.where(MomentDao.Properties.FolderId.in(linkedlist), new WhereCondition[0]);
        return querybuilder.count();
    }

    public static QueryBuilder getNotHiddenFoldersQueryBuilder(int i)
    {
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getFolderDao().queryBuilder();
        querybuilder.where(querybuilder.or(FolderDao.Properties.IsHidden.eq(Boolean.valueOf(false)), FolderDao.Properties.IsHidden.isNull(), new WhereCondition[0]), new WhereCondition[0]);
        querybuilder.where(FolderDao.Properties.Source.eq(Integer.valueOf(i)), new WhereCondition[0]);
        return querybuilder;
    }

    public static QueryBuilder getPhotos(int i)
    {
        QueryBuilder querybuilder = getAllPhotos();
        querybuilder.where(MediaItemDao.Properties.Source.eq(Integer.valueOf(i)), new WhereCondition[0]);
        return querybuilder;
    }

    public static ClassifierThreshold getThreshold(int i)
    {
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getClassifierThresholdDao().queryBuilder();
        querybuilder.where(ClassifierThresholdDao.Properties.Source.eq(Integer.valueOf(i)), new WhereCondition[0]);
        List list = querybuilder.list();
        if(list.size() > 0)
        {
            return (ClassifierThreshold)list.get(0);
        } else
        {
            return null;
        }
    }

    public static QueryBuilder getVideos(int i)
    {
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getMediaItemDao().queryBuilder();
        querybuilder.where(MediaItemDao.Properties.Type.eq(Integer.valueOf(2)), new WhereCondition[0]);
        querybuilder.where(MediaItemDao.Properties.Source.eq(Integer.valueOf(i)), new WhereCondition[0]);
        querybuilder.where(MediaItemDao.Properties.WasDeleted.isNull(), new WhereCondition[0]);
        return querybuilder;
    }
}

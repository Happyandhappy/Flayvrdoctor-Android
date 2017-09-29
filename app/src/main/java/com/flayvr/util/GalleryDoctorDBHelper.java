package com.flayvr.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.flayvr.application.PreferencesManager;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.utils.GeneralUtils;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.query.*;
import java.util.*;
import org.apache.commons.lang3.StringUtils;

public class GalleryDoctorDBHelper
{

    public GalleryDoctorDBHelper()
    {
    }

    public static List getBadPhotos(int i)
    {
        Set set = PreferencesManager.getExcludeAllFolder();
        QueryBuilder querybuilder = DaoHelper.getPhotos(i);
        WhereCondition wherecondition = querybuilder.and(com.flayvr.myrollshared.data.MediaItemDao.Properties.IsBad.eq(Boolean.valueOf(true)), querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]), new WhereCondition[0]);
        WhereCondition awherecondition[] = new WhereCondition[2];
        awherecondition[0] = querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]);
        awherecondition[1] = com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.notIn(set);
        querybuilder.where(wherecondition, awherecondition);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
        querybuilder.orderDesc(aproperty);
        return querybuilder.build().forCurrentThread().list();
    }

    public static List getDuplicates(int i)
    {
        Set set = PreferencesManager.getExcludeAllFolder();
        DaoSession daosession = DBManager.getInstance().getDaoSession();
        DuplicatesSetDao duplicatessetdao = daosession.getDuplicatesSetDao();
        String as[] = duplicatessetdao.getAllColumns();
        StringBuilder stringbuilder = new StringBuilder();
        SqlUtils.appendColumns(stringbuilder, "t", as);
        String s = (new StringBuilder()).append("SELECT ").append(stringbuilder.toString()).append(" ").append("FROM ").append("DUPLICATES_SET").append(" as t ").append("INNER JOIN (SELECT DISTINCT ").append(com.flayvr.myrollshared.data.DuplicatesSetsToPhotosDao.Properties.DuplicatesSetId.columnName).append(" ").append("FROM ").append("DUPLICATES_SETS_TO_PHOTOS").append(" AS di ").append("INNER JOIN ").append("MEDIA_ITEM").append(" AS i ").append("ON i.").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Id.columnName).append(" = di.").append(com.flayvr.myrollshared.data.DuplicatesSetsToPhotosDao.Properties.PhotoId.columnName).append(" ").append("WHERE i.").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Source.columnName).append(" = ").append(i).append(" AND ").append("i.").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.columnName).append(" NOT IN (").append(getSetParams(set)).append(")").append("ORDER BY ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Date.columnName).append(" DESC) as t2 ").append("ON t.").append(com.flayvr.myrollshared.data.DuplicatesSetDao.Properties.Id.columnName).append(" = t2.").append(com.flayvr.myrollshared.data.DuplicatesSetsToPhotosDao.Properties.DuplicatesSetId.columnName).toString();
        Cursor cursor = duplicatessetdao.getDatabase().rawQuery(s, null);
        ArrayList arraylist = new ArrayList();
        while(cursor.moveToNext())
        {
            DuplicatesSet duplicatesset = duplicatessetdao.readEntity(cursor, 0);
            if(duplicatesset != null)
            {
                duplicatesset.__setDaoSession(daosession);
                arraylist.add(duplicatesset);
            }
        }
        return arraylist;
    }

    public static List getFirstBadPhotos(int i, int j)
    {
        Set set = PreferencesManager.getExcludeAllFolder();
        QueryBuilder querybuilder = DaoHelper.getPhotos(j);
        WhereCondition wherecondition = querybuilder.and(com.flayvr.myrollshared.data.MediaItemDao.Properties.IsBad.eq(Boolean.valueOf(true)), querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]), new WhereCondition[0]);
        WhereCondition awherecondition[] = new WhereCondition[2];
        awherecondition[0] = querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]);
        awherecondition[1] = com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.notIn(set);
        querybuilder.where(wherecondition, awherecondition);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
        querybuilder.orderDesc(aproperty);
        return querybuilder.limit(i).build().forCurrentThread().list();
    }

    public static List getFirstDuplicates(int i, int j)
    {
        Set set = PreferencesManager.getExcludeAllFolder();
        LinkedList linkedlist = new LinkedList();
        MediaItemDao mediaitemdao = DBManager.getInstance().getDaoSession().getMediaItemDao();
        String as[] = mediaitemdao.getAllColumns();
        StringBuilder stringbuilder = new StringBuilder();
        SqlUtils.appendColumns(stringbuilder, mediaitemdao.getTablename(), as);
        String s = (new StringBuilder()).append("SELECT ").append(stringbuilder.toString()).append(" ").append("FROM ").append(mediaitemdao.getTablename()).append(" ").append("WHERE ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.columnName).append(" is null ").append("AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Source.columnName).append(" = ").append(j).append(" ").append("AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Id.columnName).append(" IN (SELECT ").append(com.flayvr.myrollshared.data.DuplicatesSetsToPhotosDao.Properties.PhotoId.columnName).append(" FROM ").append("DUPLICATES_SETS_TO_PHOTOS").append(") ").append("AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.columnName).append(" NOT IN (").append(getSetParams(set)).append(") ").append("LIMIT ").append(i).toString();
        Cursor cursor = mediaitemdao.getDatabase().rawQuery(s, null);
        while(cursor.moveToNext())
        {
            com.flayvr.myrollshared.data.MediaItem mediaitem = mediaitemdao.readEntity(cursor, 0);
            if(mediaitem != null)
            {
                linkedlist.add(mediaitem);
            }
        }
        return linkedlist;
    }

    public static List getFirstPhotosForReview(int i, int j)
    {
        Set set = PreferencesManager.getExcludeAllFolder();
        QueryBuilder querybuilder = DaoHelper.getPhotos(j);
        WhereCondition wherecondition = querybuilder.and(com.flayvr.myrollshared.data.MediaItemDao.Properties.IsForReview.eq(Boolean.valueOf(true)), querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]), new WhereCondition[0]);
        WhereCondition awherecondition[] = new WhereCondition[2];
        awherecondition[0] = querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]);
        awherecondition[1] = com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.notIn(set);
        querybuilder.where(wherecondition, awherecondition);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
        querybuilder.orderDesc(aproperty);
        return querybuilder.limit(i).build().list();
    }

    public static List getFirstScreenshots(int i, int j)
    {
        HashSet hashset = new HashSet(GeneralUtils.getScreenshotFoldersIds());
        hashset.removeAll(PreferencesManager.getExcludeAllFolder());
        QueryBuilder querybuilder = DaoHelper.getPhotos(j);
        WhereCondition wherecondition = querybuilder.and(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.in(hashset), querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]), new WhereCondition[0]);
        WhereCondition awherecondition[] = new WhereCondition[1];
        awherecondition[0] = querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]);
        querybuilder.where(wherecondition, awherecondition);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
        querybuilder.orderDesc(aproperty);
        return querybuilder.limit(i).build().forCurrentThread().list();
    }

    public static List getFirstVideos(int i, int j)
    {
        Set set = PreferencesManager.getExcludeAllFolder();
        QueryBuilder querybuilder = DaoHelper.getVideos(j);
        WhereCondition wherecondition = querybuilder.and(querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]), querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]), new WhereCondition[0]);
        WhereCondition awherecondition[] = new WhereCondition[1];
        awherecondition[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.notIn(set);
        querybuilder.where(wherecondition, awherecondition);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Duration;
        querybuilder.orderDesc(aproperty);
        return querybuilder.limit(i).build().list();
    }

    public static List getFirstWhatsappPhotos(int i, int j)
    {
        HashSet hashset = new HashSet(GeneralUtils.getWhatsappFolderIds());
        hashset.removeAll(PreferencesManager.getExcludeAllFolder());
        QueryBuilder querybuilder = DaoHelper.getPhotos(j);
        WhereCondition wherecondition = querybuilder.and(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.in(hashset), querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]), new WhereCondition[0]);
        WhereCondition awherecondition[] = new WhereCondition[1];
        awherecondition[0] = querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]);
        querybuilder.where(wherecondition, awherecondition);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
        querybuilder.orderDesc(aproperty);
        return querybuilder.limit(i).build().forCurrentThread().list();
    }

    public static List getNewBadPhotos()
    {
        Set set = PreferencesManager.getExcludeAllFolder();
        QueryBuilder querybuilder = DaoHelper.getAllPhotos();
        querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.IsBad.eq(Boolean.valueOf(true)), new WhereCondition[0]);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
        querybuilder.orderDesc(aproperty);
        WhereCondition wherecondition = querybuilder.and(querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]), querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasAnalyzedByGD.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasAnalyzedByGD.eq(Boolean.valueOf(false)), new WhereCondition[0]), new WhereCondition[0]);
        WhereCondition awherecondition[] = new WhereCondition[1];
        awherecondition[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.notIn(set);
        querybuilder.where(wherecondition, awherecondition);
        return querybuilder.build().forCurrentThread().list();
    }

    public static List getNewDuplicates()
    {
        Set set = PreferencesManager.getExcludeAllFolder();
        DaoSession daosession = DBManager.getInstance().getDaoSession();
        DuplicatesSetDao duplicatessetdao = daosession.getDuplicatesSetDao();
        String as[] = duplicatessetdao.getAllColumns();
        StringBuilder stringbuilder = new StringBuilder();
        SqlUtils.appendColumns(stringbuilder, "t", as);
        String s = (new StringBuilder()).append("SELECT ").append(stringbuilder.toString()).append(" ").append("FROM ").append("DUPLICATES_SET").append(" as t ").append("INNER JOIN (SELECT DISTINCT ").append(com.flayvr.myrollshared.data.DuplicatesSetsToPhotosDao.Properties.DuplicatesSetId.columnName).append(" ").append("FROM ").append("DUPLICATES_SETS_TO_PHOTOS").append(" AS di ").append("INNER JOIN ").append("MEDIA_ITEM").append(" AS i ").append("ON i.").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Id.columnName).append(" = di.").append(com.flayvr.myrollshared.data.DuplicatesSetsToPhotosDao.Properties.PhotoId.columnName).append(" ").append("WHERE i.").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.columnName).append(" NOT IN (").append(getSetParams(set)).append(") ").append("ORDER BY ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Date.columnName).append(" DESC) as t2 ").append("ON t.").append(com.flayvr.myrollshared.data.DuplicatesSetDao.Properties.Id.columnName).append(" = t2.").append(com.flayvr.myrollshared.data.DuplicatesSetsToPhotosDao.Properties.DuplicatesSetId.columnName).append(" ").append("WHERE (t.").append(com.flayvr.myrollshared.data.DuplicatesSetDao.Properties.WasAnalyzedByGD.columnName).append(" IS NULL OR ").append("t.").append(com.flayvr.myrollshared.data.DuplicatesSetDao.Properties.WasAnalyzedByGD.columnName).append(" = 0)").toString();
        Cursor cursor = duplicatessetdao.getDatabase().rawQuery(s, null);
        ArrayList arraylist = new ArrayList();
        while(cursor.moveToNext())
        {
            DuplicatesSet duplicatesset = duplicatessetdao.readEntity(cursor, 0);
            if(duplicatesset != null)
            {
                duplicatesset.__setDaoSession(daosession);
                arraylist.add(duplicatesset);
            }
        }
        return arraylist;
    }

    public static long getPhotosForReviewCount(int i)
    {
        Set set = PreferencesManager.getExcludeAllFolder();
        QueryBuilder querybuilder = DaoHelper.getPhotos(i);
        WhereCondition wherecondition = querybuilder.and(com.flayvr.myrollshared.data.MediaItemDao.Properties.IsForReview.eq(Boolean.valueOf(true)), querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]), new WhereCondition[0]);
        WhereCondition awherecondition[] = new WhereCondition[2];
        awherecondition[0] = querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]);
        awherecondition[1] = com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.notIn(set);
        querybuilder.where(wherecondition, awherecondition);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
        querybuilder.orderDesc(aproperty);
        return querybuilder.count();
    }

    public static List getPhotosForReviewLazy(int i)
    {
        Set set = PreferencesManager.getExcludeAllFolder();
        QueryBuilder querybuilder = DaoHelper.getPhotos(i);
        WhereCondition wherecondition = querybuilder.and(com.flayvr.myrollshared.data.MediaItemDao.Properties.IsForReview.eq(Boolean.valueOf(true)), querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]), new WhereCondition[0]);
        WhereCondition awherecondition[] = new WhereCondition[2];
        awherecondition[0] = querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]);
        awherecondition[1] = com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.notIn(set);
        querybuilder.where(wherecondition, awherecondition);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
        querybuilder.orderDesc(aproperty);
        return querybuilder.build().listLazy();
    }

    public static List getScreenshots(int i)
    {
        int j = 0;
        HashSet hashset = new HashSet(GeneralUtils.getScreenshotFoldersIds());
        hashset.removeAll(PreferencesManager.getExcludeAllFolder());
        QueryBuilder querybuilder = DaoHelper.getPhotos(i);
        WhereCondition wherecondition = querybuilder.and(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.in(hashset), querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]), new WhereCondition[0]);
        WhereCondition awherecondition[] = new WhereCondition[1];
        awherecondition[0] = querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]);
        querybuilder.where(wherecondition, awherecondition);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
        querybuilder.orderDesc(aproperty);
        querybuilder.offset(0).limit(1000);
        Query query = querybuilder.build();
        LinkedList linkedlist = new LinkedList();
        for(int k = 1000; k == '\u03E8';)
        {
            query.setOffset(j);
            List list = query.list();
            linkedlist.addAll(list);
            k = list.size();
            j += 1000;
        }

        return linkedlist;
    }

    private static String getSetParams(Set set)
    {
        return StringUtils.join(set, ",");
    }

    public static long getWhatsappPhotosCount(int i)
    {
        HashSet hashset = new HashSet(GeneralUtils.getWhatsappFolderIds());
        hashset.removeAll(PreferencesManager.getExcludeAllFolder());
        QueryBuilder querybuilder = DaoHelper.getPhotos(i);
        WhereCondition wherecondition = querybuilder.and(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.in(hashset), querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]), new WhereCondition[0]);
        WhereCondition awherecondition[] = new WhereCondition[1];
        awherecondition[0] = querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]);
        querybuilder.where(wherecondition, awherecondition);
        return querybuilder.count();
    }

    public static List getWhatsappPhotosLazy(int i)
    {
        HashSet hashset = new HashSet(GeneralUtils.getWhatsappFolderIds());
        hashset.removeAll(PreferencesManager.getExcludeAllFolder());
        QueryBuilder querybuilder = DaoHelper.getPhotos(i);
        WhereCondition wherecondition = querybuilder.and(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.in(hashset), querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]), new WhereCondition[0]);
        WhereCondition awherecondition[] = new WhereCondition[1];
        awherecondition[0] = querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]);
        querybuilder.where(wherecondition, awherecondition);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
        querybuilder.orderDesc(aproperty);
        return querybuilder.build().listLazy();
    }
}

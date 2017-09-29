package com.flayvr.myrollshared.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.identityscope.IdentityScope;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.query.*;
import java.util.ArrayList;
import java.util.List;

public class DuplicatesSetsToPhotosDao extends AbstractDao<DuplicatesSetsToPhotos, Long>
{
    public static final String TABLENAME = "DUPLICATES_SETS_TO_PHOTOS";
    private DaoSession daoSession;
    private Query duplicatesSet_DuplicatesSetPhotosQuery;
    private Query mediaItem_PhotoDuplicatesSetsQuery;
    private String selectDeep;

    public DuplicatesSetsToPhotosDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public DuplicatesSetsToPhotosDao(DaoConfig daoconfig, DaoSession daosession)
    {
        super(daoconfig, daosession);
        daoSession = daosession;
    }

    public static void createTable(SQLiteDatabase sqlitedatabase, boolean flag)
    {
        String s;
        if(flag)
        {
            s = "IF NOT EXISTS ";
        } else
        {
            s = "";
        }
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'DUPLICATES_SETS_TO_PHOTOS' (").append("'_id' INTEGER PRIMARY KEY ,").append("'DUPLICATES_SET_ID' INTEGER,").append("'PHOTO_ID' INTEGER);").toString());
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE INDEX ").append(s).append("IDX_DUPLICATES_SETS_TO_PHOTOS_DUPLICATES_SET_ID ON DUPLICATES_SETS_TO_PHOTOS").append(" (DUPLICATES_SET_ID);").toString());
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE INDEX ").append(s).append("IDX_DUPLICATES_SETS_TO_PHOTOS_PHOTO_ID ON DUPLICATES_SETS_TO_PHOTOS").append(" (PHOTO_ID);").toString());
    }

    public static void dropTable(SQLiteDatabase sqlitedatabase, boolean flag)
    {
        StringBuilder stringbuilder = (new StringBuilder()).append("DROP TABLE ");
        String s;
        if(flag)
        {
            s = "IF EXISTS ";
        } else
        {
            s = "";
        }
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'DUPLICATES_SETS_TO_PHOTOS'").toString());
    }

    public List _queryDuplicatesSet_DuplicatesSetPhotos(Long long1)
    {
        synchronized (this) {
            if(duplicatesSet_DuplicatesSetPhotosQuery == null)
            {
                QueryBuilder querybuilder = queryBuilder();
                querybuilder.where(Properties.DuplicatesSetId.eq(null), new WhereCondition[0]);
                duplicatesSet_DuplicatesSetPhotosQuery = querybuilder.build();
            }
        }
        Query query = duplicatesSet_DuplicatesSetPhotosQuery.forCurrentThread();
        query.setParameter(0, long1);
        return query.list();
    }

    public List _queryMediaItem_PhotoDuplicatesSets(Long long1)
    {
        synchronized (this){
            if(mediaItem_PhotoDuplicatesSetsQuery == null)
            {
                QueryBuilder querybuilder = queryBuilder();
                querybuilder.where(Properties.PhotoId.eq(null), new WhereCondition[0]);
                mediaItem_PhotoDuplicatesSetsQuery = querybuilder.build();
            }
        }
        Query query = mediaItem_PhotoDuplicatesSetsQuery.forCurrentThread();
        query.setParameter(0, long1);
        return query.list();
    }

    protected void attachEntity(DuplicatesSetsToPhotos duplicatessetstophotos)
    {
        super.attachEntity(duplicatessetstophotos);
        duplicatessetstophotos.__setDaoSession(daoSession);
    }

    protected void bindValues(SQLiteStatement sqlitestatement, DuplicatesSetsToPhotos duplicatessetstophotos)
    {
        sqlitestatement.clearBindings();
        Long long1 = duplicatessetstophotos.getId();
        if(long1 != null)
        {
            sqlitestatement.bindLong(1, long1.longValue());
        }
        Long long2 = duplicatessetstophotos.getDuplicatesSetId();
        if(long2 != null)
        {
            sqlitestatement.bindLong(2, long2.longValue());
        }
        Long long3 = duplicatessetstophotos.getPhotoId();
        if(long3 != null)
        {
            sqlitestatement.bindLong(3, long3.longValue());
        }
    }

    public Long getKey(DuplicatesSetsToPhotos duplicatessetstophotos)
    {
        if(duplicatessetstophotos != null)
        {
            return duplicatessetstophotos.getId();
        } else
        {
            return null;
        }
    }

    protected String getSelectDeep()
    {
        if(selectDeep == null)
        {
            StringBuilder stringbuilder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(stringbuilder, "T", getAllColumns());
            stringbuilder.append(',');
            SqlUtils.appendColumns(stringbuilder, "T0", daoSession.getMediaItemDao().getAllColumns());
            stringbuilder.append(',');
            SqlUtils.appendColumns(stringbuilder, "T1", daoSession.getDuplicatesSetDao().getAllColumns());
            stringbuilder.append(" FROM DUPLICATES_SETS_TO_PHOTOS T");
            stringbuilder.append(" LEFT JOIN MEDIA_ITEM T0 ON T.'PHOTO_ID'=T0.'_id'");
            stringbuilder.append(" LEFT JOIN DUPLICATES_SET T1 ON T.'DUPLICATES_SET_ID'=T1.'_id'");
            stringbuilder.append(' ');
            selectDeep = stringbuilder.toString();
        }
        return selectDeep;
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public List loadAllDeepFromCursor(Cursor cursor)
    {
        ArrayList arraylist;
        int i = cursor.getCount();
        try {
            arraylist = new ArrayList(i);
            if (!cursor.moveToFirst()) {
                return arraylist;
            }
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(i);
            }
            boolean flag;
            do {
                arraylist.add(loadCurrentDeep(cursor, false));
                flag = cursor.moveToNext();
            } while (flag);
            if (identityScope != null) {
                identityScope.unlock();
            }
            return arraylist;
        }catch(Exception exception) {
            if(identityScope != null)
            {
                identityScope.unlock();
            }
            try {
                throw exception;
            }catch(Exception e){
                return null;
            }
        }
    }

    protected DuplicatesSetsToPhotos loadCurrentDeep(Cursor cursor, boolean flag)
    {
        DuplicatesSetsToPhotos duplicatessetstophotos = (DuplicatesSetsToPhotos)loadCurrent(cursor, 0, flag);
        int i = getAllColumns().length;
        duplicatessetstophotos.setPhoto((MediaItem)loadCurrentOther(daoSession.getMediaItemDao(), cursor, i));
        int j = i + daoSession.getMediaItemDao().getAllColumns().length;
        duplicatessetstophotos.setDuplicatesSet((DuplicatesSet)loadCurrentOther(daoSession.getDuplicatesSetDao(), cursor, j));
        return duplicatessetstophotos;
    }

    public DuplicatesSetsToPhotos loadDeep(Long long1)
    {
        Cursor cursor;
        assertSinglePk();
        if(long1 == null)
        {
            return null;
        }
        StringBuilder stringbuilder = new StringBuilder(getSelectDeep());
        stringbuilder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(stringbuilder, "T", getPkColumns());
        String s = stringbuilder.toString();
        String as[] = new String[1];
        as[0] = long1.toString();
        cursor = db.rawQuery(s, as);
        boolean flag = cursor.moveToFirst();
        if(!flag)
        {
            cursor.close();
            return null;
        }
        if(!cursor.isLast())
        {
            throw new IllegalStateException((new StringBuilder()).append("Expected unique result, but count was ").append(cursor.getCount()).toString());
        }
        DuplicatesSetsToPhotos duplicatessetstophotos = loadCurrentDeep(cursor, true);
        cursor.close();
        return duplicatessetstophotos;
    }

    protected List loadDeepAllAndCloseCursor(Cursor cursor)
    {
        List list = loadAllDeepFromCursor(cursor);
        cursor.close();
        return list;
    }

    public List queryDeep(String s, String as[])
    {
        return loadDeepAllAndCloseCursor(db.rawQuery((new StringBuilder()).append(getSelectDeep()).append(s).toString(), as));
    }

    public DuplicatesSetsToPhotos readEntity(Cursor cursor, int i)
    {
        Long long1;
        Long long2;
        boolean flag;
        Long long3;
        if(cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        if(cursor.isNull(i + 1))
        {
            long2 = null;
        } else
        {
            long2 = Long.valueOf(cursor.getLong(i + 1));
        }
        flag = cursor.isNull(i + 2);
        long3 = null;
        if(!flag)
        {
            long3 = Long.valueOf(cursor.getLong(i + 2));
        }
        return new DuplicatesSetsToPhotos(long1, long2, long3);
    }

    public void readEntity(Cursor cursor, DuplicatesSetsToPhotos duplicatessetstophotos, int i)
    {
        Long long1;
        Long long2;
        boolean flag;
        Long long3;
        if(cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        duplicatessetstophotos.setId(long1);
        if(cursor.isNull(i + 1))
        {
            long2 = null;
        } else
        {
            long2 = Long.valueOf(cursor.getLong(i + 1));
        }
        duplicatessetstophotos.setDuplicatesSetId(long2);
        flag = cursor.isNull(i + 2);
        long3 = null;
        if(!flag)
        {
            long3 = Long.valueOf(cursor.getLong(i + 2));
        }
        duplicatessetstophotos.setPhotoId(long3);
    }

    public Long readKey(Cursor cursor, int i)
    {
        if(cursor.isNull(i + 0))
        {
            return null;
        } else
        {
            return Long.valueOf(cursor.getLong(i + 0));
        }
    }

    protected Long updateKeyAfterInsert(DuplicatesSetsToPhotos duplicatessetstophotos, long l)
    {
        duplicatessetstophotos.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    public static class Properties
    {
        public static final Property DuplicatesSetId = new Property(1, Long.class, "duplicatesSetId", false, "DUPLICATES_SET_ID");
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property PhotoId = new Property(2, Long.class, "photoId", false, "PHOTO_ID");
    }

}

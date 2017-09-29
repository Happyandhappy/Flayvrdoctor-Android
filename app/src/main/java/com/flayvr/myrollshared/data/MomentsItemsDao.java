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

public class MomentsItemsDao extends AbstractDao<MomentsItems, Long>
{

    public static final String TABLENAME = "MOMENTS_ITEMS";
    private DaoSession daoSession;
    private Query mediaItem_ItemMomentsQuery;
    private Query moment_MomentItemsQuery;
    private String selectDeep;

    public MomentsItemsDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public MomentsItemsDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'MOMENTS_ITEMS' (").append("'_id' INTEGER PRIMARY KEY ,").append("'MOMENT_ID' INTEGER,").append("'ITEM_ID' INTEGER);").toString());
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE INDEX ").append(s).append("IDX_MOMENTS_ITEMS_MOMENT_ID ON MOMENTS_ITEMS").append(" (MOMENT_ID);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'MOMENTS_ITEMS'").toString());
    }

    public List _queryMediaItem_ItemMoments(Long long1)
    {
        synchronized (this) {
            if(mediaItem_ItemMomentsQuery == null)
            {
                QueryBuilder querybuilder = queryBuilder();
                querybuilder.where(Properties.ItemId.eq(null), new WhereCondition[0]);
                mediaItem_ItemMomentsQuery = querybuilder.build();
            }
        }
        Query query = mediaItem_ItemMomentsQuery.forCurrentThread();
        query.setParameter(0, long1);
        return query.list();
    }

    public List _queryMoment_MomentItems(Long long1)
    {
        synchronized (this) {
            if(moment_MomentItemsQuery == null)
            {
                QueryBuilder querybuilder = queryBuilder();
                querybuilder.where(Properties.MomentId.eq(null), new WhereCondition[0]);
                moment_MomentItemsQuery = querybuilder.build();
            }
        }
        Query query = moment_MomentItemsQuery.forCurrentThread();
        query.setParameter(0, long1);
        return query.list();
    }

    protected void attachEntity(MomentsItems momentsitems)
    {
        super.attachEntity(momentsitems);
        momentsitems.__setDaoSession(daoSession);
    }

    protected void bindValues(SQLiteStatement sqlitestatement, MomentsItems momentsitems)
    {
        sqlitestatement.clearBindings();
        Long long1 = momentsitems.getId();
        if(long1 != null)
        {
            sqlitestatement.bindLong(1, long1.longValue());
        }
        Long long2 = momentsitems.getMomentId();
        if(long2 != null)
        {
            sqlitestatement.bindLong(2, long2.longValue());
        }
        Long long3 = momentsitems.getItemId();
        if(long3 != null)
        {
            sqlitestatement.bindLong(3, long3.longValue());
        }
    }

    public Long getKey(MomentsItems momentsitems)
    {
        if(momentsitems != null)
        {
            return momentsitems.getId();
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
            SqlUtils.appendColumns(stringbuilder, "T1", daoSession.getMomentDao().getAllColumns());
            stringbuilder.append(" FROM MOMENTS_ITEMS T");
            stringbuilder.append(" LEFT JOIN MEDIA_ITEM T0 ON T.'ITEM_ID'=T0.'_id'");
            stringbuilder.append(" LEFT JOIN MOMENT T1 ON T.'MOMENT_ID'=T1.'_id'");
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
        arraylist = new ArrayList(i);
        try {
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
        }catch(Exception exception){
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

    protected MomentsItems loadCurrentDeep(Cursor cursor, boolean flag)
    {
        MomentsItems momentsitems = (MomentsItems)loadCurrent(cursor, 0, flag);
        int i = getAllColumns().length;
        momentsitems.setItem((MediaItem)loadCurrentOther(daoSession.getMediaItemDao(), cursor, i));
        int j = i + daoSession.getMediaItemDao().getAllColumns().length;
        momentsitems.setMoment((Moment)loadCurrentOther(daoSession.getMomentDao(), cursor, j));
        return momentsitems;
    }

    public MomentsItems loadDeep(Long long1)
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
        MomentsItems momentsitems = loadCurrentDeep(cursor, true);
        cursor.close();
        return momentsitems;
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

    public MomentsItems readEntity(Cursor cursor, int i)
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
        return new MomentsItems(long1, long2, long3);
    }

    public void readEntity(Cursor cursor, MomentsItems momentsitems, int i)
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
        momentsitems.setId(long1);
        if(cursor.isNull(i + 1))
        {
            long2 = null;
        } else
        {
            long2 = Long.valueOf(cursor.getLong(i + 1));
        }
        momentsitems.setMomentId(long2);
        flag = cursor.isNull(i + 2);
        long3 = null;
        if(!flag)
        {
            long3 = Long.valueOf(cursor.getLong(i + 2));
        }
        momentsitems.setItemId(long3);
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

    protected Long updateKeyAfterInsert(MomentsItems momentsitems, long l)
    {
        momentsitems.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    public static class Properties
    {
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property ItemId = new Property(2, Long.class, "itemId", false, "ITEM_ID");
        public static final Property MomentId = new Property(1, Long.class, "momentId", false, "MOMENT_ID");
    }

}

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

public class DBMomentsItemsDao extends AbstractDao<DBMomentsItems, Long>
{

    public static final String TABLENAME = "DBMOMENTS_ITEMS";
    private Query dBMoment_MomentsItemsQuery;
    private DaoSession daoSession;
    private String selectDeep;

    public DBMomentsItemsDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public DBMomentsItemsDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'DBMOMENTS_ITEMS' (").append("'_id' INTEGER PRIMARY KEY ,").append("'MOMENT_ID' INTEGER,").append("'ITEM_ID' INTEGER);").toString());
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE INDEX ").append(s).append("IDX_DBMOMENTS_ITEMS_MOMENT_ID ON DBMOMENTS_ITEMS").append(" (MOMENT_ID);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'DBMOMENTS_ITEMS'").toString());
    }

    public List _queryDBMoment_MomentsItems(Long long1)
    {
        synchronized (this) {
            if(dBMoment_MomentsItemsQuery == null)
            {
                QueryBuilder querybuilder = queryBuilder();
                querybuilder.where(Properties.MomentId.eq(null), new WhereCondition[0]);
                dBMoment_MomentsItemsQuery = querybuilder.build();
            }
        }
        Query query = dBMoment_MomentsItemsQuery.forCurrentThread();
        query.setParameter(0, long1);
        return query.list();
    }

    protected void attachEntity(DBMomentsItems dbmomentsitems)
    {
        super.attachEntity(dbmomentsitems);
        dbmomentsitems.__setDaoSession(daoSession);
    }

    protected void bindValues(SQLiteStatement sqlitestatement, DBMomentsItems dbmomentsitems)
    {
        sqlitestatement.clearBindings();
        Long long1 = dbmomentsitems.getId();
        if(long1 != null)
        {
            sqlitestatement.bindLong(1, long1.longValue());
        }
        Long long2 = dbmomentsitems.getMomentId();
        if(long2 != null)
        {
            sqlitestatement.bindLong(2, long2.longValue());
        }
        Long long3 = dbmomentsitems.getItemId();
        if(long3 != null)
        {
            sqlitestatement.bindLong(3, long3.longValue());
        }
    }

    public Long getKey(DBMomentsItems dbmomentsitems)
    {
        if(dbmomentsitems != null)
        {
            return dbmomentsitems.getId();
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
            SqlUtils.appendColumns(stringbuilder, "T0", daoSession.getDBMediaItemDao().getAllColumns());
            stringbuilder.append(" FROM DBMOMENTS_ITEMS T");
            stringbuilder.append(" LEFT JOIN DBMEDIA_ITEM T0 ON T.'ITEM_ID'=T0.'_id'");
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
        try {
            int i = cursor.getCount();
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
            if (identityScope != null) {
                identityScope.unlock();
            }
            try {
                throw exception;
            }catch(Exception e){
                return null;
            }
        }
    }

    protected DBMomentsItems loadCurrentDeep(Cursor cursor, boolean flag)
    {
        DBMomentsItems dbmomentsitems = (DBMomentsItems)loadCurrent(cursor, 0, flag);
        int i = getAllColumns().length;
        dbmomentsitems.setItem((DBMediaItem)loadCurrentOther(daoSession.getDBMediaItemDao(), cursor, i));
        return dbmomentsitems;
    }

    public DBMomentsItems loadDeep(Long long1)
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
        DBMomentsItems dbmomentsitems = loadCurrentDeep(cursor, true);
        cursor.close();
        return dbmomentsitems;
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

    public DBMomentsItems readEntity(Cursor cursor, int i)
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
        return new DBMomentsItems(long1, long2, long3);
    }

    public void readEntity(Cursor cursor, DBMomentsItems dbmomentsitems, int i)
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
        dbmomentsitems.setId(long1);
        if(cursor.isNull(i + 1))
        {
            long2 = null;
        } else
        {
            long2 = Long.valueOf(cursor.getLong(i + 1));
        }
        dbmomentsitems.setMomentId(long2);
        flag = cursor.isNull(i + 2);
        long3 = null;
        if(!flag)
        {
            long3 = Long.valueOf(cursor.getLong(i + 2));
        }
        dbmomentsitems.setItemId(long3);
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

    protected Long updateKeyAfterInsert(DBMomentsItems dbmomentsitems, long l)
    {
        dbmomentsitems.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    private static class Properties
    {
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property ItemId = new Property(2, Long.class, "itemId", false, "ITEM_ID");
        public static final Property MomentId = new Property(1, Long.class, "momentId", false, "MOMENT_ID");
    }

}

package com.flayvr.myrollshared.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

public class DuplicatesSetDao extends AbstractDao<DuplicatesSet, Long>
{

    public static final String TABLENAME = "DUPLICATES_SET";
    private DaoSession daoSession;

    public DuplicatesSetDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public DuplicatesSetDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'DUPLICATES_SET' (").append("'_id' INTEGER PRIMARY KEY ,").append("'WAS_ANALYZED_BY_GD' INTEGER);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'DUPLICATES_SET'").toString());
    }

    protected void attachEntity(DuplicatesSet duplicatesset)
    {
        super.attachEntity(duplicatesset);
        duplicatesset.__setDaoSession(daoSession);
    }

    protected void bindValues(SQLiteStatement sqlitestatement, DuplicatesSet duplicatesset)
    {
        sqlitestatement.clearBindings();
        Long long1 = duplicatesset.getId();
        if(long1 != null)
        {
            sqlitestatement.bindLong(1, long1.longValue());
        }
        Boolean boolean1 = duplicatesset.getWasAnalyzedByGD();
        if(boolean1 != null)
        {
            long l;
            if(boolean1.booleanValue())
            {
                l = 1L;
            } else
            {
                l = 0L;
            }
            sqlitestatement.bindLong(2, l);
        }
    }

    public Long getKey(DuplicatesSet duplicatesset)
    {
        if(duplicatesset != null)
        {
            return duplicatesset.getId();
        } else
        {
            return null;
        }
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public DuplicatesSet readEntity(Cursor cursor, int i)
    {
        Long long1;
        boolean flag;
        Boolean boolean1;
        if(cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        flag = cursor.isNull(i + 1);
        boolean1 = null;
        if(!flag)
        {
            boolean flag1;
            if(cursor.getShort(i + 1) != 0)
            {
                flag1 = true;
            } else
            {
                flag1 = false;
            }
            boolean1 = Boolean.valueOf(flag1);
        }
        return new DuplicatesSet(long1, boolean1);
    }

    public void readEntity(Cursor cursor, DuplicatesSet duplicatesset, int i)
    {
        Long long1;
        boolean flag;
        Boolean boolean1;
        if(cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        duplicatesset.setId(long1);
        flag = cursor.isNull(i + 1);
        boolean1 = null;
        if(!flag)
        {
            boolean flag1;
            if(cursor.getShort(i + 1) != 0)
            {
                flag1 = true;
            } else
            {
                flag1 = false;
            }
            boolean1 = Boolean.valueOf(flag1);
        }
        duplicatesset.setWasAnalyzedByGD(boolean1);
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

    protected Long updateKeyAfterInsert(DuplicatesSet duplicatesset, long l)
    {
        duplicatesset.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    public static class Properties
    {
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property WasAnalyzedByGD = new Property(1, Boolean.class, "wasAnalyzedByGD", false, "WAS_ANALYZED_BY_GD");
    }
}

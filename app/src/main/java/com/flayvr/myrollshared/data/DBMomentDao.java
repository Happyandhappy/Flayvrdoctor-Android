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

public class DBMomentDao extends AbstractDao<DBMoment, Long>
{

    public static final String TABLENAME = "DBMOMENT";
    private Query dBFolder_DBMomentListQuery;
    private DaoSession daoSession;
    private String selectDeep;

    public DBMomentDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public DBMomentDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'DBMOMENT' (").append("'_id' INTEGER PRIMARY KEY ,").append("'TITLE' TEXT,").append("'LOCATION' TEXT,").append("'IS_MUTED' INTEGER,").append("'NEWER_ITEM_ADDED' INTEGER,").append("'IS_HIDDEN' INTEGER,").append("'IS_TRIP' INTEGER,").append("'IS_MERGED' INTEGER,").append("'IS_FAVORITE' INTEGER,").append("'ORIGINAL_ITEMS_JSON' TEXT,").append("'DATE_JSON' TEXT,").append("'INTERACTIONS_JSON' TEXT,").append("'IS_TITLE_FROM_CALENDAR' INTEGER,").append("'WATCH_COUNT' INTEGER,").append("'COVER_ID' INTEGER,").append("'FOLDER_ID' TEXT);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'DBMOMENT'").toString());
    }

    public List _queryDBFolder_DBMomentList(String s)
    {
        synchronized (this) {
            if(dBFolder_DBMomentListQuery == null)
            {
                QueryBuilder querybuilder = queryBuilder();
                querybuilder.where(Properties.FolderId.eq(null), new WhereCondition[0]);
                dBFolder_DBMomentListQuery = querybuilder.build();
            }
        }
        Query query = dBFolder_DBMomentListQuery.forCurrentThread();
        query.setParameter(0, s);
        return query.list();
    }

    protected void attachEntity(DBMoment dbmoment)
    {
        super.attachEntity(dbmoment);
        dbmoment.__setDaoSession(daoSession);
    }

    protected void bindValues(SQLiteStatement sqlitestatement, DBMoment dbmoment)
    {
        long l = 1L;
        Boolean boolean2;
        Boolean boolean3;
        Boolean boolean4;
        Boolean boolean5;
        Boolean boolean6;
        String s2;
        String s3;
        String s4;
        Boolean boolean7;
        Integer integer;
        Long long2;
        String s5;
        long l6;
        sqlitestatement.clearBindings();
        Long long1 = dbmoment.getId();
        if(long1 != null)
        {
            sqlitestatement.bindLong(1, long1.longValue());
        }
        String s = dbmoment.getTitle();
        if(s != null)
        {
            sqlitestatement.bindString(2, s);
        }
        String s1 = dbmoment.getLocation();
        if(s1 != null)
        {
            sqlitestatement.bindString(3, s1);
        }
        Boolean boolean1 = dbmoment.getIsMuted();
        if(boolean1 != null)
        {
            if(boolean1.booleanValue())
            {
                l6 = l;
            } else
            {
                l6 = 0L;
            }
            sqlitestatement.bindLong(4, l6);
        }
        boolean2 = dbmoment.getNewerItemAdded();
        if(boolean2 != null)
        {
            long l5;
            if(boolean2.booleanValue())
            {
                l5 = l;
            } else
            {
                l5 = 0L;
            }
            sqlitestatement.bindLong(5, l5);
        }
        boolean3 = dbmoment.getIsHidden();
        if(boolean3 != null)
        {
            long l4;
            if(boolean3.booleanValue())
            {
                l4 = l;
            } else
            {
                l4 = 0L;
            }
            sqlitestatement.bindLong(6, l4);
        }
        boolean4 = dbmoment.getIsTrip();
        if(boolean4 != null)
        {
            long l3;
            if(boolean4.booleanValue())
            {
                l3 = l;
            } else
            {
                l3 = 0L;
            }
            sqlitestatement.bindLong(7, l3);
        }
        boolean5 = dbmoment.getIsMerged();
        if(boolean5 != null)
        {
            long l2;
            if(boolean5.booleanValue())
            {
                l2 = l;
            } else
            {
                l2 = 0L;
            }
            sqlitestatement.bindLong(8, l2);
        }
        boolean6 = dbmoment.getIsFavorite();
        if(boolean6 != null)
        {
            long l1;
            if(boolean6.booleanValue())
            {
                l1 = l;
            } else
            {
                l1 = 0L;
            }
            sqlitestatement.bindLong(9, l1);
        }
        s2 = dbmoment.getOriginalItemsJson();
        if(s2 != null)
        {
            sqlitestatement.bindString(10, s2);
        }
        s3 = dbmoment.getDateJson();
        if(s3 != null)
        {
            sqlitestatement.bindString(11, s3);
        }
        s4 = dbmoment.getInteractionsJson();
        if(s4 != null)
        {
            sqlitestatement.bindString(12, s4);
        }
        boolean7 = dbmoment.getIsTitleFromCalendar();
        if(boolean7 != null)
        {
            if(!boolean7.booleanValue())
            {
                l = 0L;
            }
            sqlitestatement.bindLong(13, l);
        }
        integer = dbmoment.getWatchCount();
        if(integer != null)
        {
            sqlitestatement.bindLong(14, integer.intValue());
        }
        long2 = dbmoment.getCoverId();
        if(long2 != null)
        {
            sqlitestatement.bindLong(15, long2.longValue());
        }
        s5 = dbmoment.getFolderId();
        if(s5 != null)
        {
            sqlitestatement.bindString(16, s5);
        }
    }

    public Long getKey(DBMoment dbmoment)
    {
        if(dbmoment != null)
        {
            return dbmoment.getId();
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
            stringbuilder.append(" FROM DBMOMENT T");
            stringbuilder.append(" LEFT JOIN DBMEDIA_ITEM T0 ON T.'COVER_ID'=T0.'_id'");
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

    protected DBMoment loadCurrentDeep(Cursor cursor, boolean flag)
    {
        DBMoment dbmoment = (DBMoment)loadCurrent(cursor, 0, flag);
        int i = getAllColumns().length;
        dbmoment.setCover((DBMediaItem)loadCurrentOther(daoSession.getDBMediaItemDao(), cursor, i));
        return dbmoment;
    }

    public DBMoment loadDeep(Long long1)
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
        DBMoment dbmoment = loadCurrentDeep(cursor, true);
        cursor.close();
        return dbmoment;
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

    public DBMoment readEntity(Cursor cursor, int i)
    {
        Long long1;
        String s;
        String s1;
        Boolean boolean1;
        Boolean boolean2;
        Boolean boolean3;
        Boolean boolean4;
        Boolean boolean5;
        Boolean boolean6;
        String s2;
        String s3;
        String s4;
        Boolean boolean7;
        Integer integer;
        Long long2;
        String s5;
        if(cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        if(cursor.isNull(i + 1))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 1);
        }
        if(cursor.isNull(i + 2))
        {
            s1 = null;
        } else
        {
            s1 = cursor.getString(i + 2);
        }
        if(cursor.isNull(i + 3))
        {
            boolean1 = null;
        } else
        {
            boolean flag;
            if(cursor.getShort(i + 3) != 0)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            boolean1 = Boolean.valueOf(flag);
        }
        if(cursor.isNull(i + 4))
        {
            boolean2 = null;
        } else
        {
            boolean flag1;
            if(cursor.getShort(i + 4) != 0)
            {
                flag1 = true;
            } else
            {
                flag1 = false;
            }
            boolean2 = Boolean.valueOf(flag1);
        }
        if(cursor.isNull(i + 5))
        {
            boolean3 = null;
        } else
        {
            boolean flag2;
            if(cursor.getShort(i + 5) != 0)
            {
                flag2 = true;
            } else
            {
                flag2 = false;
            }
            boolean3 = Boolean.valueOf(flag2);
        }
        if(cursor.isNull(i + 6))
        {
            boolean4 = null;
        } else
        {
            boolean flag3;
            if(cursor.getShort(i + 6) != 0)
            {
                flag3 = true;
            } else
            {
                flag3 = false;
            }
            boolean4 = Boolean.valueOf(flag3);
        }
        if(cursor.isNull(i + 7))
        {
            boolean5 = null;
        } else
        {
            boolean flag4;
            if(cursor.getShort(i + 7) != 0)
            {
                flag4 = true;
            } else
            {
                flag4 = false;
            }
            boolean5 = Boolean.valueOf(flag4);
        }
        if(cursor.isNull(i + 8))
        {
            boolean6 = null;
        } else
        {
            boolean flag5;
            if(cursor.getShort(i + 8) != 0)
            {
                flag5 = true;
            } else
            {
                flag5 = false;
            }
            boolean6 = Boolean.valueOf(flag5);
        }
        if(cursor.isNull(i + 9))
        {
            s2 = null;
        } else
        {
            s2 = cursor.getString(i + 9);
        }
        if(cursor.isNull(i + 10))
        {
            s3 = null;
        } else
        {
            s3 = cursor.getString(i + 10);
        }
        if(cursor.isNull(i + 11))
        {
            s4 = null;
        } else
        {
            s4 = cursor.getString(i + 11);
        }
        if(cursor.isNull(i + 12))
        {
            boolean7 = null;
        } else
        {
            boolean flag6;
            if(cursor.getShort(i + 12) != 0)
            {
                flag6 = true;
            } else
            {
                flag6 = false;
            }
            boolean7 = Boolean.valueOf(flag6);
        }
        if(cursor.isNull(i + 13))
        {
            integer = null;
        } else
        {
            integer = Integer.valueOf(cursor.getInt(i + 13));
        }
        if(cursor.isNull(i + 14))
        {
            long2 = null;
        } else
        {
            long2 = Long.valueOf(cursor.getLong(i + 14));
        }
        if(cursor.isNull(i + 15))
        {
            s5 = null;
        } else
        {
            s5 = cursor.getString(i + 15);
        }
        return new DBMoment(long1, s, s1, boolean1, boolean2, boolean3, boolean4, boolean5, boolean6, s2, s3, s4, boolean7, integer, long2, s5);
    }

    public void readEntity(Cursor cursor, DBMoment dbmoment, int i)
    {
        boolean flag = true;
        Long long1;
        String s;
        String s1;
        Boolean boolean1;
        Boolean boolean2;
        Boolean boolean3;
        Boolean boolean4;
        Boolean boolean5;
        Boolean boolean6;
        String s2;
        String s3;
        String s4;
        Boolean boolean7;
        Integer integer;
        Long long2;
        boolean flag7;
        String s5;
        if(cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        dbmoment.setId(long1);
        if(cursor.isNull(i + 1))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 1);
        }
        dbmoment.setTitle(s);
        if(cursor.isNull(i + 2))
        {
            s1 = null;
        } else
        {
            s1 = cursor.getString(i + 2);
        }
        dbmoment.setLocation(s1);
        if(cursor.isNull(i + 3))
        {
            boolean1 = null;
        } else
        {
            boolean flag1;
            if(cursor.getShort(i + 3) != 0)
            {
                flag1 = flag;
            } else
            {
                flag1 = false;
            }
            boolean1 = Boolean.valueOf(flag1);
        }
        dbmoment.setIsMuted(boolean1);
        if(cursor.isNull(i + 4))
        {
            boolean2 = null;
        } else
        {
            boolean flag2;
            if(cursor.getShort(i + 4) != 0)
            {
                flag2 = flag;
            } else
            {
                flag2 = false;
            }
            boolean2 = Boolean.valueOf(flag2);
        }
        dbmoment.setNewerItemAdded(boolean2);
        if(cursor.isNull(i + 5))
        {
            boolean3 = null;
        } else
        {
            boolean flag3;
            if(cursor.getShort(i + 5) != 0)
            {
                flag3 = flag;
            } else
            {
                flag3 = false;
            }
            boolean3 = Boolean.valueOf(flag3);
        }
        dbmoment.setIsHidden(boolean3);
        if(cursor.isNull(i + 6))
        {
            boolean4 = null;
        } else
        {
            boolean flag4;
            if(cursor.getShort(i + 6) != 0)
            {
                flag4 = flag;
            } else
            {
                flag4 = false;
            }
            boolean4 = Boolean.valueOf(flag4);
        }
        dbmoment.setIsTrip(boolean4);
        if(cursor.isNull(i + 7))
        {
            boolean5 = null;
        } else
        {
            boolean flag5;
            if(cursor.getShort(i + 7) != 0)
            {
                flag5 = flag;
            } else
            {
                flag5 = false;
            }
            boolean5 = Boolean.valueOf(flag5);
        }
        dbmoment.setIsMerged(boolean5);
        if(cursor.isNull(i + 8))
        {
            boolean6 = null;
        } else
        {
            boolean flag6;
            if(cursor.getShort(i + 8) != 0)
            {
                flag6 = flag;
            } else
            {
                flag6 = false;
            }
            boolean6 = Boolean.valueOf(flag6);
        }
        dbmoment.setIsFavorite(boolean6);
        if(cursor.isNull(i + 9))
        {
            s2 = null;
        } else
        {
            s2 = cursor.getString(i + 9);
        }
        dbmoment.setOriginalItemsJson(s2);
        if(cursor.isNull(i + 10))
        {
            s3 = null;
        } else
        {
            s3 = cursor.getString(i + 10);
        }
        dbmoment.setDateJson(s3);
        if(cursor.isNull(i + 11))
        {
            s4 = null;
        } else
        {
            s4 = cursor.getString(i + 11);
        }
        dbmoment.setInteractionsJson(s4);
        if(cursor.isNull(i + 12))
        {
            boolean7 = null;
        } else
        {
            if(cursor.getShort(i + 12) == 0)
            {
                flag = false;
            }
            boolean7 = Boolean.valueOf(flag);
        }
        dbmoment.setIsTitleFromCalendar(boolean7);
        if(cursor.isNull(i + 13))
        {
            integer = null;
        } else
        {
            integer = Integer.valueOf(cursor.getInt(i + 13));
        }
        dbmoment.setWatchCount(integer);
        if(cursor.isNull(i + 14))
        {
            long2 = null;
        } else
        {
            long2 = Long.valueOf(cursor.getLong(i + 14));
        }
        dbmoment.setCoverId(long2);
        flag7 = cursor.isNull(i + 15);
        s5 = null;
        if(!flag7)
        {
            s5 = cursor.getString(i + 15);
        }
        dbmoment.setFolderId(s5);
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

    protected Long updateKeyAfterInsert(DBMoment dbmoment, long l)
    {
        dbmoment.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    private static class Properties
    {

        public static final Property CoverId = new Property(14, Long.class, "coverId", false, "COVER_ID");
        public static final Property DateJson = new Property(10, String.class, "dateJson", false, "DATE_JSON");
        public static final Property FolderId = new Property(15, String.class, "folderId", false, "FOLDER_ID");
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property InteractionsJson = new Property(11, String.class, "interactionsJson", false, "INTERACTIONS_JSON");
        public static final Property IsFavorite = new Property(8, Boolean.class, "isFavorite", false, "IS_FAVORITE");
        public static final Property IsHidden = new Property(5, Boolean.class, "isHidden", false, "IS_HIDDEN");
        public static final Property IsMerged = new Property(7, Boolean.class, "isMerged", false, "IS_MERGED");
        public static final Property IsMuted = new Property(3, Boolean.class, "isMuted", false, "IS_MUTED");
        public static final Property IsTitleFromCalendar = new Property(12, Boolean.class, "isTitleFromCalendar", false, "IS_TITLE_FROM_CALENDAR");
        public static final Property IsTrip = new Property(6, Boolean.class, "isTrip", false, "IS_TRIP");
        public static final Property Location = new Property(2, String.class, "location", false, "LOCATION");
        public static final Property NewerItemAdded = new Property(4, Boolean.class, "newerItemAdded", false, "NEWER_ITEM_ADDED");
        public static final Property OriginalItemsJson = new Property(9, String.class, "originalItemsJson", false, "ORIGINAL_ITEMS_JSON");
        public static final Property Title = new Property(1, String.class, "title", false, "TITLE");
        public static final Property WatchCount = new Property(13, Integer.class, "watchCount", false, "WATCH_COUNT");
    }

}

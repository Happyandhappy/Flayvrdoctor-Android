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
import java.util.*;

public class MomentDao extends AbstractDao<Moment, Long>
{

    public static final String TABLENAME = "MOMENT";
    private DaoSession daoSession;
    private Query folder_MomentListQuery;
    private String selectDeep;

    public MomentDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public MomentDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'MOMENT' (").append("'_id' INTEGER PRIMARY KEY ,").append("'TITLE' TEXT,").append("'LOCATION' TEXT,").append("'IS_MUTED' INTEGER,").append("'IS_HIDDEN' INTEGER,").append("'IS_FAVORITE' INTEGER,").append("'IS_TRIP' INTEGER,").append("'START_DATE' INTEGER,").append("'END_DATE' INTEGER,").append("'IS_TITLE_FROM_CALENDAR' INTEGER,").append("'WATCH_COUNT' INTEGER,").append("'FOLDER_ID' INTEGER,").append("'COVER_ID' INTEGER);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'MOMENT'").toString());
    }

    public List _queryFolder_MomentList(Long long1)
    {
        synchronized (this) {
            if(folder_MomentListQuery == null)
            {
                QueryBuilder querybuilder = queryBuilder();
                querybuilder.where(Properties.FolderId.eq(null), new WhereCondition[0]);
                folder_MomentListQuery = querybuilder.build();
            }
        }
        Query query = folder_MomentListQuery.forCurrentThread();
        query.setParameter(0, long1);
        return query.list();
    }

    protected void attachEntity(Moment moment)
    {
        super.attachEntity(moment);
        moment.__setDaoSession(daoSession);
    }

    protected void bindValues(SQLiteStatement sqlitestatement, Moment moment)
    {
        long l = 1L;
        Boolean boolean2;
        Boolean boolean3;
        Boolean boolean4;
        Date date;
        Date date1;
        Boolean boolean5;
        Integer integer;
        Long long2;
        Long long3;
        long l4;
        sqlitestatement.clearBindings();
        Long long1 = moment.getId();
        if(long1 != null)
        {
            sqlitestatement.bindLong(1, long1.longValue());
        }
        String s = moment.getTitle();
        if(s != null)
        {
            sqlitestatement.bindString(2, s);
        }
        String s1 = moment.getLocation();
        if(s1 != null)
        {
            sqlitestatement.bindString(3, s1);
        }
        Boolean boolean1 = moment.getIsMuted();
        if(boolean1 != null)
        {
            if(boolean1.booleanValue())
            {
                l4 = l;
            } else
            {
                l4 = 0L;
            }
            sqlitestatement.bindLong(4, l4);
        }
        boolean2 = moment.getIsHidden();
        if(boolean2 != null)
        {
            long l3;
            if(boolean2.booleanValue())
            {
                l3 = l;
            } else
            {
                l3 = 0L;
            }
            sqlitestatement.bindLong(5, l3);
        }
        boolean3 = moment.getIsFavorite();
        if(boolean3 != null)
        {
            long l2;
            if(boolean3.booleanValue())
            {
                l2 = l;
            } else
            {
                l2 = 0L;
            }
            sqlitestatement.bindLong(6, l2);
        }
        boolean4 = moment.getIsTrip();
        if(boolean4 != null)
        {
            long l1;
            if(boolean4.booleanValue())
            {
                l1 = l;
            } else
            {
                l1 = 0L;
            }
            sqlitestatement.bindLong(7, l1);
        }
        date = moment.getStartDate();
        if(date != null)
        {
            sqlitestatement.bindLong(8, date.getTime());
        }
        date1 = moment.getEndDate();
        if(date1 != null)
        {
            sqlitestatement.bindLong(9, date1.getTime());
        }
        boolean5 = moment.getIsTitleFromCalendar();
        if(boolean5 != null)
        {
            if(!boolean5.booleanValue())
            {
                l = 0L;
            }
            sqlitestatement.bindLong(10, l);
        }
        integer = moment.getWatchCount();
        if(integer != null)
        {
            sqlitestatement.bindLong(11, integer.intValue());
        }
        long2 = moment.getFolderId();
        if(long2 != null)
        {
            sqlitestatement.bindLong(12, long2.longValue());
        }
        long3 = moment.getCoverId();
        if(long3 != null)
        {
            sqlitestatement.bindLong(13, long3.longValue());
        }
    }

    public Long getKey(Moment moment)
    {
        if(moment != null)
        {
            return moment.getId();
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
            SqlUtils.appendColumns(stringbuilder, "T0", daoSession.getFolderDao().getAllColumns());
            stringbuilder.append(',');
            SqlUtils.appendColumns(stringbuilder, "T1", daoSession.getMediaItemDao().getAllColumns());
            stringbuilder.append(" FROM MOMENT T");
            stringbuilder.append(" LEFT JOIN FOLDER T0 ON T.'FOLDER_ID'=T0.'_id'");
            stringbuilder.append(" LEFT JOIN MEDIA_ITEM T1 ON T.'COVER_ID'=T1.'_id'");
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

    protected Moment loadCurrentDeep(Cursor cursor, boolean flag)
    {
        Moment moment = (Moment)loadCurrent(cursor, 0, flag);
        int i = getAllColumns().length;
        moment.setFolder((Folder)loadCurrentOther(daoSession.getFolderDao(), cursor, i));
        int j = i + daoSession.getFolderDao().getAllColumns().length;
        moment.setCover((MediaItem)loadCurrentOther(daoSession.getMediaItemDao(), cursor, j));
        return moment;
    }

    public Moment loadDeep(Long long1)
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
        Moment moment = loadCurrentDeep(cursor, true);
        cursor.close();
        return moment;
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

    public Moment readEntity(Cursor cursor, int i)
    {
        Long long1;
        String s;
        String s1;
        Boolean boolean1;
        Boolean boolean2;
        Boolean boolean3;
        Boolean boolean4;
        Date date;
        Date date1;
        Boolean boolean5;
        Integer integer;
        Long long2;
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
            date = null;
        } else
        {
            date = new Date(cursor.getLong(i + 7));
        }
        if(cursor.isNull(i + 8))
        {
            date1 = null;
        } else
        {
            date1 = new Date(cursor.getLong(i + 8));
        }
        if(cursor.isNull(i + 9))
        {
            boolean5 = null;
        } else
        {
            boolean flag4;
            if(cursor.getShort(i + 9) != 0)
            {
                flag4 = true;
            } else
            {
                flag4 = false;
            }
            boolean5 = Boolean.valueOf(flag4);
        }
        if(cursor.isNull(i + 10))
        {
            integer = null;
        } else
        {
            integer = Integer.valueOf(cursor.getInt(i + 10));
        }
        if(cursor.isNull(i + 11))
        {
            long2 = null;
        } else
        {
            long2 = Long.valueOf(cursor.getLong(i + 11));
        }
        if(cursor.isNull(i + 12))
        {
            long3 = null;
        } else
        {
            long3 = Long.valueOf(cursor.getLong(i + 12));
        }
        return new Moment(long1, s, s1, boolean1, boolean2, boolean3, boolean4, date, date1, boolean5, integer, long2, long3);
    }

    public void readEntity(Cursor cursor, Moment moment, int i)
    {
        boolean flag = true;
        Long long1;
        String s;
        String s1;
        Boolean boolean1;
        Boolean boolean2;
        Boolean boolean3;
        Boolean boolean4;
        Date date;
        Date date1;
        Boolean boolean5;
        Integer integer;
        Long long2;
        boolean flag5;
        Long long3;
        if(cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        moment.setId(long1);
        if(cursor.isNull(i + 1))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 1);
        }
        moment.setTitle(s);
        if(cursor.isNull(i + 2))
        {
            s1 = null;
        } else
        {
            s1 = cursor.getString(i + 2);
        }
        moment.setLocation(s1);
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
        moment.setIsMuted(boolean1);
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
        moment.setIsHidden(boolean2);
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
        moment.setIsFavorite(boolean3);
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
        moment.setIsTrip(boolean4);
        if(cursor.isNull(i + 7))
        {
            date = null;
        } else
        {
            date = new Date(cursor.getLong(i + 7));
        }
        moment.setStartDate(date);
        if(cursor.isNull(i + 8))
        {
            date1 = null;
        } else
        {
            date1 = new Date(cursor.getLong(i + 8));
        }
        moment.setEndDate(date1);
        if(cursor.isNull(i + 9))
        {
            boolean5 = null;
        } else
        {
            if(cursor.getShort(i + 9) == 0)
            {
                flag = false;
            }
            boolean5 = Boolean.valueOf(flag);
        }
        moment.setIsTitleFromCalendar(boolean5);
        if(cursor.isNull(i + 10))
        {
            integer = null;
        } else
        {
            integer = Integer.valueOf(cursor.getInt(i + 10));
        }
        moment.setWatchCount(integer);
        if(cursor.isNull(i + 11))
        {
            long2 = null;
        } else
        {
            long2 = Long.valueOf(cursor.getLong(i + 11));
        }
        moment.setFolderId(long2);
        flag5 = cursor.isNull(i + 12);
        long3 = null;
        if(!flag5)
        {
            long3 = Long.valueOf(cursor.getLong(i + 12));
        }
        moment.setCoverId(long3);
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

    protected Long updateKeyAfterInsert(Moment moment, long l)
    {
        moment.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    public static class Properties
    {
        public static final Property CoverId = new Property(12, Long.class, "coverId", false, "COVER_ID");
        public static final Property EndDate = new Property(8, Date.class, "endDate", false, "END_DATE");
        public static final Property FolderId = new Property(11, Long.class, "folderId", false, "FOLDER_ID");
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property IsFavorite = new Property(5, Boolean.class, "isFavorite", false, "IS_FAVORITE");
        public static final Property IsHidden = new Property(4, Boolean.class, "isHidden", false, "IS_HIDDEN");
        public static final Property IsMuted = new Property(3, Boolean.class, "isMuted", false, "IS_MUTED");
        public static final Property IsTitleFromCalendar = new Property(9, Boolean.class, "isTitleFromCalendar", false, "IS_TITLE_FROM_CALENDAR");
        public static final Property IsTrip = new Property(6, Boolean.class, "isTrip", false, "IS_TRIP");
        public static final Property Location = new Property(2, String.class, "location", false, "LOCATION");
        public static final Property StartDate = new Property(7, Date.class, "startDate", false, "START_DATE");
        public static final Property Title = new Property(1, String.class, "title", false, "TITLE");
        public static final Property WatchCount = new Property(10, Integer.class, "watchCount", false, "WATCH_COUNT");
    }

}

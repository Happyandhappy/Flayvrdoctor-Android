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

public class MediaItemDao extends AbstractDao<MediaItem, Long>
{
    public static final String TABLENAME = "MEDIA_ITEM";
    private DaoSession daoSession;
    private Query<MediaItem> folder_MediaItemListQuery;
    private String selectDeep;

    public MediaItemDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public MediaItemDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'MEDIA_ITEM' (").append("'_id' INTEGER PRIMARY KEY ,").append("'ANDROID_ID' INTEGER,").append("'TYPE' INTEGER,").append("'PATH' TEXT,").append("'THUMBNAIL' TEXT,").append("'ORIENTATION' INTEGER,").append("'DATE' INTEGER,").append("'LATITUDE' REAL,").append("'LONGITUDE' REAL,").append("'BLURRY' REAL,").append("'COLOR' REAL,").append("'DARK' REAL,").append("'PROP' REAL,").append("'CV_RAN' INTEGER,").append("'CENTER_X' REAL,").append("'CENTER_Y' REAL,").append("'FACES_COUNT' INTEGER,").append("'FACES_JSON' TEXT,").append("'CHECKED_THUMBNAIL' INTEGER,").append("'DURATION' INTEGER,").append("'SIMILARITY_SCORE_TO_PREV' REAL,").append("'SIMILARITY_SCORE_TO_NEXT' REAL,").append("'WAS_ANALYZED_FOR_DUPLICATES' INTEGER,").append("'SCORE' REAL,").append("'IS_BAD' INTEGER,").append("'IS_FOR_REVIEW' INTEGER,").append("'LAST_TIME_CLASSIFIED' INTEGER,").append("'WAS_DELETED' INTEGER,").append("'WAS_CLUSTERED' INTEGER,").append("'WAS_ANALYZED_BY_GD' INTEGER,").append("'WAS_KEPT_BY_USER' INTEGER,").append("'WAS_DELETED_BY_USER' INTEGER,").append("'FILE_SIZE_BYTES' INTEGER,").append("'SOURCE' INTEGER,").append("'WIDTH' INTEGER,").append("'HEIGHT' INTEGER,").append("'WAS_MINIMIZED_BY_USER' INTEGER,").append("'SERVER_ID' INTEGER,").append("'FOLDER_ID' INTEGER,").append("'INTERACTION_SCORE' REAL);").toString());
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE INDEX ").append(s).append("IDX_MEDIA_ITEM_DATE ON MEDIA_ITEM").append(" (DATE);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'MEDIA_ITEM'").toString());
    }

    public List<MediaItem> _queryFolder_MediaItemList(Long long1)
    {
        synchronized (this) {
            if(folder_MediaItemListQuery == null)
            {
                QueryBuilder querybuilder = queryBuilder();
                querybuilder.where(Properties.FolderId.eq(null), new WhereCondition[0]);
                folder_MediaItemListQuery = querybuilder.build();
            }
        }
        Query query = folder_MediaItemListQuery.forCurrentThread();
        query.setParameter(0, long1);
        return query.list();
    }

    protected void attachEntity(MediaItem mediaitem)
    {
        super.attachEntity(mediaitem);
        mediaitem.__setDaoSession(daoSession);
    }

    protected void bindValues(SQLiteStatement sqlitestatement, MediaItem mediaitem)
    {
        long l = 1L;
        sqlitestatement.clearBindings();
        Long long1 = mediaitem.getId();
        if(long1 != null)
        {
            sqlitestatement.bindLong(1, long1.longValue());
        }
        Long long2 = mediaitem.getAndroidId();
        if(long2 != null)
        {
            sqlitestatement.bindLong(2, long2.longValue());
        }
        Integer integer = mediaitem.getType();
        if(integer != null)
        {
            sqlitestatement.bindLong(3, integer.intValue());
        }
        String s = mediaitem.getPath();
        if(s != null)
        {
            sqlitestatement.bindString(4, s);
        }
        String s1 = mediaitem.getThumbnail();
        if(s1 != null)
        {
            sqlitestatement.bindString(5, s1);
        }
        Integer integer1 = mediaitem.getOrientation();
        if(integer1 != null)
        {
            sqlitestatement.bindLong(6, integer1.intValue());
        }
        Date date = mediaitem.getDate();
        if(date != null)
        {
            sqlitestatement.bindLong(7, date.getTime());
        }
        Double double1 = mediaitem.getLatitude();
        if(double1 != null)
        {
            sqlitestatement.bindDouble(8, double1.doubleValue());
        }
        Double double2 = mediaitem.getLongitude();
        if(double2 != null)
        {
            sqlitestatement.bindDouble(9, double2.doubleValue());
        }
        Double double3 = mediaitem.getBlurry();
        if(double3 != null)
        {
            sqlitestatement.bindDouble(10, double3.doubleValue());
        }
        Double double4 = mediaitem.getColor();
        if(double4 != null)
        {
            sqlitestatement.bindDouble(11, double4.doubleValue());
        }
        Double double5 = mediaitem.getDark();
        if(double5 != null)
        {
            sqlitestatement.bindDouble(12, double5.doubleValue());
        }
        Float float1 = mediaitem.getProp();
        if(float1 != null)
        {
            sqlitestatement.bindDouble(13, float1.floatValue());
        }
        Boolean boolean1 = mediaitem.getCvRan();
        Float float2;
        Float float3;
        Integer integer2;
        String s2;
        Boolean boolean2;
        Long long3;
        Double double6;
        Double double7;
        Boolean boolean3;
        Double double8;
        Boolean boolean4;
        Boolean boolean5;
        Date date1;
        Boolean boolean6;
        Boolean boolean7;
        Boolean boolean8;
        Boolean boolean9;
        Boolean boolean10;
        Long long4;
        Integer integer3;
        Integer integer4;
        Integer integer5;
        Boolean boolean11;
        Long long5;
        Long long6;
        Double double9;
        long l10;
        if(boolean1 != null)
        {
            if(boolean1.booleanValue())
            {
                l10 = l;
            } else
            {
                l10 = 0L;
            }
            sqlitestatement.bindLong(14, l10);
        }
        float2 = mediaitem.getCenterX();
        if(float2 != null)
        {
            sqlitestatement.bindDouble(15, float2.floatValue());
        }
        float3 = mediaitem.getCenterY();
        if(float3 != null)
        {
            sqlitestatement.bindDouble(16, float3.floatValue());
        }
        integer2 = mediaitem.getFacesCount();
        if(integer2 != null)
        {
            sqlitestatement.bindLong(17, integer2.intValue());
        }
        s2 = mediaitem.getFacesJson();
        if(s2 != null)
        {
            sqlitestatement.bindString(18, s2);
        }
        boolean2 = mediaitem.getCheckedThumbnail();
        if(boolean2 != null)
        {
            long l9;
            if(boolean2.booleanValue())
            {
                l9 = l;
            } else
            {
                l9 = 0L;
            }
            sqlitestatement.bindLong(19, l9);
        }
        long3 = mediaitem.getDuration();
        if(long3 != null)
        {
            sqlitestatement.bindLong(20, long3.longValue());
        }
        double6 = mediaitem.getSimilarityScoreToPrev();
        if(double6 != null)
        {
            sqlitestatement.bindDouble(21, double6.doubleValue());
        }
        double7 = mediaitem.getSimilarityScoreToNext();
        if(double7 != null)
        {
            sqlitestatement.bindDouble(22, double7.doubleValue());
        }
        boolean3 = mediaitem.getWasAnalyzedForDuplicates();
        if(boolean3 != null)
        {
            long l8;
            if(boolean3.booleanValue())
            {
                l8 = l;
            } else
            {
                l8 = 0L;
            }
            sqlitestatement.bindLong(23, l8);
        }
        double8 = mediaitem.getScore();
        if(double8 != null)
        {
            sqlitestatement.bindDouble(24, double8.doubleValue());
        }
        boolean4 = mediaitem.getIsBad();
        if(boolean4 != null)
        {
            long l7;
            if(boolean4.booleanValue())
            {
                l7 = l;
            } else
            {
                l7 = 0L;
            }
            sqlitestatement.bindLong(25, l7);
        }
        boolean5 = mediaitem.getIsForReview();
        if(boolean5 != null)
        {
            long l6;
            if(boolean5.booleanValue())
            {
                l6 = l;
            } else
            {
                l6 = 0L;
            }
            sqlitestatement.bindLong(26, l6);
        }
        date1 = mediaitem.getLastTimeClassified();
        if(date1 != null)
        {
            sqlitestatement.bindLong(27, date1.getTime());
        }
        boolean6 = mediaitem.getWasDeleted();
        if(boolean6 != null)
        {
            long l5;
            if(boolean6.booleanValue())
            {
                l5 = l;
            } else
            {
                l5 = 0L;
            }
            sqlitestatement.bindLong(28, l5);
        }
        boolean7 = mediaitem.getWasClustered();
        if(boolean7 != null)
        {
            long l4;
            if(boolean7.booleanValue())
            {
                l4 = l;
            } else
            {
                l4 = 0L;
            }
            sqlitestatement.bindLong(29, l4);
        }
        boolean8 = mediaitem.getWasAnalyzedByGD();
        if(boolean8 != null)
        {
            long l3;
            if(boolean8.booleanValue())
            {
                l3 = l;
            } else
            {
                l3 = 0L;
            }
            sqlitestatement.bindLong(30, l3);
        }
        boolean9 = mediaitem.getWasKeptByUser();
        if(boolean9 != null)
        {
            long l2;
            if(boolean9.booleanValue())
            {
                l2 = l;
            } else
            {
                l2 = 0L;
            }
            sqlitestatement.bindLong(31, l2);
        }
        boolean10 = mediaitem.getWasDeletedByUser();
        if(boolean10 != null)
        {
            long l1;
            if(boolean10.booleanValue())
            {
                l1 = l;
            } else
            {
                l1 = 0L;
            }
            sqlitestatement.bindLong(32, l1);
        }
        long4 = mediaitem.getFileSizeBytes();
        if(long4 != null)
        {
            sqlitestatement.bindLong(33, long4.longValue());
        }
        integer3 = mediaitem.getSource();
        if(integer3 != null)
        {
            sqlitestatement.bindLong(34, integer3.intValue());
        }
        integer4 = mediaitem.getWidth();
        if(integer4 != null)
        {
            sqlitestatement.bindLong(35, integer4.intValue());
        }
        integer5 = mediaitem.getHeight();
        if(integer5 != null)
        {
            sqlitestatement.bindLong(36, integer5.intValue());
        }
        boolean11 = mediaitem.getWasMinimizedByUser();
        if(boolean11 != null)
        {
            if(!boolean11.booleanValue())
            {
                l = 0L;
            }
            sqlitestatement.bindLong(37, l);
        }
        long5 = mediaitem.getServerId();
        if(long5 != null)
        {
            sqlitestatement.bindLong(38, long5.longValue());
        }
        long6 = mediaitem.getFolderId();
        if(long6 != null)
        {
            sqlitestatement.bindLong(39, long6.longValue());
        }
        double9 = mediaitem.getInteractionScore();
        if(double9 != null)
        {
            sqlitestatement.bindDouble(40, double9.doubleValue());
        }
    }

    public Long getKey(MediaItem mediaitem)
    {
        if(mediaitem != null)
        {
            return mediaitem.getId();
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
            stringbuilder.append(" FROM MEDIA_ITEM T");
            stringbuilder.append(" LEFT JOIN FOLDER T0 ON T.'FOLDER_ID'=T0.'_id'");
            stringbuilder.append(' ');
            selectDeep = stringbuilder.toString();
        }
        return selectDeep;
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public List<MediaItem> loadAllDeepFromCursor(Cursor cursor)
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

    protected MediaItem loadCurrentDeep(Cursor cursor, boolean flag)
    {
        MediaItem mediaitem = (MediaItem)loadCurrent(cursor, 0, flag);
        int i = getAllColumns().length;
        mediaitem.setFolder((Folder)loadCurrentOther(daoSession.getFolderDao(), cursor, i));
        return mediaitem;
    }

    public MediaItem loadDeep(Long long1)
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
        MediaItem mediaitem = loadCurrentDeep(cursor, true);
        cursor.close();
        return mediaitem;
    }

    protected List<MediaItem> loadDeepAllAndCloseCursor(Cursor cursor)
    {
        List list = loadAllDeepFromCursor(cursor);
        cursor.close();
        return list;
    }

    public List<MediaItem> queryDeep(String s, String as[])
    {
        return loadDeepAllAndCloseCursor(db.rawQuery((new StringBuilder()).append(getSelectDeep()).append(s).toString(), as));
    }

    public MediaItem readEntity(Cursor cursor, int i)
    {
        Long long1;
        Long long2;
        Integer integer;
        String s;
        String s1;
        Integer integer1;
        Date date;
        Double double1;
        Double double2;
        Double double3;
        Double double4;
        Double double5;
        Float float1;
        Boolean boolean1;
        Float float2;
        Float float3;
        Integer integer2;
        String s2;
        Boolean boolean2;
        Long long3;
        Double double6;
        Double double7;
        Boolean boolean3;
        Double double8;
        Boolean boolean4;
        Boolean boolean5;
        Date date1;
        Boolean boolean6;
        Boolean boolean7;
        Boolean boolean8;
        Boolean boolean9;
        Boolean boolean10;
        Long long4;
        Integer integer3;
        Integer integer4;
        Integer integer5;
        Boolean boolean11;
        Long long5;
        Long long6;
        Double double9;
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
        if(cursor.isNull(i + 2))
        {
            integer = null;
        } else
        {
            integer = Integer.valueOf(cursor.getInt(i + 2));
        }
        if(cursor.isNull(i + 3))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 3);
        }
        if(cursor.isNull(i + 4))
        {
            s1 = null;
        } else
        {
            s1 = cursor.getString(i + 4);
        }
        if(cursor.isNull(i + 5))
        {
            integer1 = null;
        } else
        {
            integer1 = Integer.valueOf(cursor.getInt(i + 5));
        }
        if(cursor.isNull(i + 6))
        {
            date = null;
        } else
        {
            date = new Date(cursor.getLong(i + 6));
        }
        if(cursor.isNull(i + 7))
        {
            double1 = null;
        } else
        {
            double1 = Double.valueOf(cursor.getDouble(i + 7));
        }
        if(cursor.isNull(i + 8))
        {
            double2 = null;
        } else
        {
            double2 = Double.valueOf(cursor.getDouble(i + 8));
        }
        if(cursor.isNull(i + 9))
        {
            double3 = null;
        } else
        {
            double3 = Double.valueOf(cursor.getDouble(i + 9));
        }
        if(cursor.isNull(i + 10))
        {
            double4 = null;
        } else
        {
            double4 = Double.valueOf(cursor.getDouble(i + 10));
        }
        if(cursor.isNull(i + 11))
        {
            double5 = null;
        } else
        {
            double5 = Double.valueOf(cursor.getDouble(i + 11));
        }
        if(cursor.isNull(i + 12))
        {
            float1 = null;
        } else
        {
            float1 = Float.valueOf(cursor.getFloat(i + 12));
        }
        if(cursor.isNull(i + 13))
        {
            boolean1 = null;
        } else
        {
            boolean flag;
            if(cursor.getShort(i + 13) != 0)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            boolean1 = Boolean.valueOf(flag);
        }
        if(cursor.isNull(i + 14))
        {
            float2 = null;
        } else
        {
            float2 = Float.valueOf(cursor.getFloat(i + 14));
        }
        if(cursor.isNull(i + 15))
        {
            float3 = null;
        } else
        {
            float3 = Float.valueOf(cursor.getFloat(i + 15));
        }
        if(cursor.isNull(i + 16))
        {
            integer2 = null;
        } else
        {
            integer2 = Integer.valueOf(cursor.getInt(i + 16));
        }
        if(cursor.isNull(i + 17))
        {
            s2 = null;
        } else
        {
            s2 = cursor.getString(i + 17);
        }
        if(cursor.isNull(i + 18))
        {
            boolean2 = null;
        } else
        {
            boolean flag1;
            if(cursor.getShort(i + 18) != 0)
            {
                flag1 = true;
            } else
            {
                flag1 = false;
            }
            boolean2 = Boolean.valueOf(flag1);
        }
        if(cursor.isNull(i + 19))
        {
            long3 = null;
        } else
        {
            long3 = Long.valueOf(cursor.getLong(i + 19));
        }
        if(cursor.isNull(i + 20))
        {
            double6 = null;
        } else
        {
            double6 = Double.valueOf(cursor.getDouble(i + 20));
        }
        if(cursor.isNull(i + 21))
        {
            double7 = null;
        } else
        {
            double7 = Double.valueOf(cursor.getDouble(i + 21));
        }
        if(cursor.isNull(i + 22))
        {
            boolean3 = null;
        } else
        {
            boolean flag2;
            if(cursor.getShort(i + 22) != 0)
            {
                flag2 = true;
            } else
            {
                flag2 = false;
            }
            boolean3 = Boolean.valueOf(flag2);
        }
        if(cursor.isNull(i + 23))
        {
            double8 = null;
        } else
        {
            double8 = Double.valueOf(cursor.getDouble(i + 23));
        }
        if(cursor.isNull(i + 24))
        {
            boolean4 = null;
        } else
        {
            boolean flag3;
            if(cursor.getShort(i + 24) != 0)
            {
                flag3 = true;
            } else
            {
                flag3 = false;
            }
            boolean4 = Boolean.valueOf(flag3);
        }
        if(cursor.isNull(i + 25))
        {
            boolean5 = null;
        } else
        {
            boolean flag4;
            if(cursor.getShort(i + 25) != 0)
            {
                flag4 = true;
            } else
            {
                flag4 = false;
            }
            boolean5 = Boolean.valueOf(flag4);
        }
        if(cursor.isNull(i + 26))
        {
            date1 = null;
        } else
        {
            date1 = new Date(cursor.getLong(i + 26));
        }
        if(cursor.isNull(i + 27))
        {
            boolean6 = null;
        } else
        {
            boolean flag5;
            if(cursor.getShort(i + 27) != 0)
            {
                flag5 = true;
            } else
            {
                flag5 = false;
            }
            boolean6 = Boolean.valueOf(flag5);
        }
        if(cursor.isNull(i + 28))
        {
            boolean7 = null;
        } else
        {
            boolean flag6;
            if(cursor.getShort(i + 28) != 0)
            {
                flag6 = true;
            } else
            {
                flag6 = false;
            }
            boolean7 = Boolean.valueOf(flag6);
        }
        if(cursor.isNull(i + 29))
        {
            boolean8 = null;
        } else
        {
            boolean flag7;
            if(cursor.getShort(i + 29) != 0)
            {
                flag7 = true;
            } else
            {
                flag7 = false;
            }
            boolean8 = Boolean.valueOf(flag7);
        }
        if(cursor.isNull(i + 30))
        {
            boolean9 = null;
        } else
        {
            boolean flag8;
            if(cursor.getShort(i + 30) != 0)
            {
                flag8 = true;
            } else
            {
                flag8 = false;
            }
            boolean9 = Boolean.valueOf(flag8);
        }
        if(cursor.isNull(i + 31))
        {
            boolean10 = null;
        } else
        {
            boolean flag9;
            if(cursor.getShort(i + 31) != 0)
            {
                flag9 = true;
            } else
            {
                flag9 = false;
            }
            boolean10 = Boolean.valueOf(flag9);
        }
        if(cursor.isNull(i + 32))
        {
            long4 = null;
        } else
        {
            long4 = Long.valueOf(cursor.getLong(i + 32));
        }
        if(cursor.isNull(i + 33))
        {
            integer3 = null;
        } else
        {
            integer3 = Integer.valueOf(cursor.getInt(i + 33));
        }
        if(cursor.isNull(i + 34))
        {
            integer4 = null;
        } else
        {
            integer4 = Integer.valueOf(cursor.getInt(i + 34));
        }
        if(cursor.isNull(i + 35))
        {
            integer5 = null;
        } else
        {
            integer5 = Integer.valueOf(cursor.getInt(i + 35));
        }
        if(cursor.isNull(i + 36))
        {
            boolean11 = null;
        } else
        {
            boolean flag10;
            if(cursor.getShort(i + 36) != 0)
            {
                flag10 = true;
            } else
            {
                flag10 = false;
            }
            boolean11 = Boolean.valueOf(flag10);
        }
        if(cursor.isNull(i + 37))
        {
            long5 = null;
        } else
        {
            long5 = Long.valueOf(cursor.getLong(i + 37));
        }
        if(cursor.isNull(i + 38))
        {
            long6 = null;
        } else
        {
            long6 = Long.valueOf(cursor.getLong(i + 38));
        }
        if(cursor.isNull(i + 39))
        {
            double9 = null;
        } else
        {
            double9 = Double.valueOf(cursor.getDouble(i + 39));
        }
        return new MediaItem(long1, long2, integer, s, s1, integer1, date, double1, double2, double3, double4, double5, float1, boolean1, float2, float3, integer2, s2, boolean2, long3, double6, double7, boolean3, double8, boolean4, boolean5, date1, boolean6, boolean7, boolean8, boolean9, boolean10, long4, integer3, integer4, integer5, boolean11, long5, long6, double9);
    }

    public void readEntity(Cursor cursor, MediaItem mediaitem, int i)
    {
        boolean flag = true;
        Long long1;
        Long long2;
        Integer integer;
        String s;
        String s1;
        Integer integer1;
        Date date;
        Double double1;
        Double double2;
        Double double3;
        Double double4;
        Double double5;
        Float float1;
        Boolean boolean1;
        Float float2;
        Float float3;
        Integer integer2;
        String s2;
        Boolean boolean2;
        Long long3;
        Double double6;
        Double double7;
        Boolean boolean3;
        Double double8;
        Boolean boolean4;
        Boolean boolean5;
        Date date1;
        Boolean boolean6;
        Boolean boolean7;
        Boolean boolean8;
        Boolean boolean9;
        Boolean boolean10;
        Long long4;
        Integer integer3;
        Integer integer4;
        Integer integer5;
        Boolean boolean11;
        Long long5;
        Long long6;
        boolean flag11;
        Double double9;
        if(cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        mediaitem.setId(long1);
        if(cursor.isNull(i + 1))
        {
            long2 = null;
        } else
        {
            long2 = Long.valueOf(cursor.getLong(i + 1));
        }
        mediaitem.setAndroidId(long2);
        if(cursor.isNull(i + 2))
        {
            integer = null;
        } else
        {
            integer = Integer.valueOf(cursor.getInt(i + 2));
        }
        mediaitem.setType(integer);
        if(cursor.isNull(i + 3))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 3);
        }
        mediaitem.setPath(s);
        if(cursor.isNull(i + 4))
        {
            s1 = null;
        } else
        {
            s1 = cursor.getString(i + 4);
        }
        mediaitem.setThumbnail(s1);
        if(cursor.isNull(i + 5))
        {
            integer1 = null;
        } else
        {
            integer1 = Integer.valueOf(cursor.getInt(i + 5));
        }
        mediaitem.setOrientation(integer1);
        if(cursor.isNull(i + 6))
        {
            date = null;
        } else
        {
            date = new Date(cursor.getLong(i + 6));
        }
        mediaitem.setDate(date);
        if(cursor.isNull(i + 7))
        {
            double1 = null;
        } else
        {
            double1 = Double.valueOf(cursor.getDouble(i + 7));
        }
        mediaitem.setLatitude(double1);
        if(cursor.isNull(i + 8))
        {
            double2 = null;
        } else
        {
            double2 = Double.valueOf(cursor.getDouble(i + 8));
        }
        mediaitem.setLongitude(double2);
        if(cursor.isNull(i + 9))
        {
            double3 = null;
        } else
        {
            double3 = Double.valueOf(cursor.getDouble(i + 9));
        }
        mediaitem.setBlurry(double3);
        if(cursor.isNull(i + 10))
        {
            double4 = null;
        } else
        {
            double4 = Double.valueOf(cursor.getDouble(i + 10));
        }
        mediaitem.setColor(double4);
        if(cursor.isNull(i + 11))
        {
            double5 = null;
        } else
        {
            double5 = Double.valueOf(cursor.getDouble(i + 11));
        }
        mediaitem.setDark(double5);
        if(cursor.isNull(i + 12))
        {
            float1 = null;
        } else
        {
            float1 = Float.valueOf(cursor.getFloat(i + 12));
        }
        mediaitem.setProp(float1);
        if(cursor.isNull(i + 13))
        {
            boolean1 = null;
        } else
        {
            boolean flag1;
            if(cursor.getShort(i + 13) != 0)
            {
                flag1 = flag;
            } else
            {
                flag1 = false;
            }
            boolean1 = Boolean.valueOf(flag1);
        }
        mediaitem.setCvRan(boolean1);
        if(cursor.isNull(i + 14))
        {
            float2 = null;
        } else
        {
            float2 = Float.valueOf(cursor.getFloat(i + 14));
        }
        mediaitem.setCenterX(float2);
        if(cursor.isNull(i + 15))
        {
            float3 = null;
        } else
        {
            float3 = Float.valueOf(cursor.getFloat(i + 15));
        }
        mediaitem.setCenterY(float3);
        if(cursor.isNull(i + 16))
        {
            integer2 = null;
        } else
        {
            integer2 = Integer.valueOf(cursor.getInt(i + 16));
        }
        mediaitem.setFacesCount(integer2);
        if(cursor.isNull(i + 17))
        {
            s2 = null;
        } else
        {
            s2 = cursor.getString(i + 17);
        }
        mediaitem.setFacesJson(s2);
        if(cursor.isNull(i + 18))
        {
            boolean2 = null;
        } else
        {
            boolean flag2;
            if(cursor.getShort(i + 18) != 0)
            {
                flag2 = flag;
            } else
            {
                flag2 = false;
            }
            boolean2 = Boolean.valueOf(flag2);
        }
        mediaitem.setCheckedThumbnail(boolean2);
        if(cursor.isNull(i + 19))
        {
            long3 = null;
        } else
        {
            long3 = Long.valueOf(cursor.getLong(i + 19));
        }
        mediaitem.setDuration(long3);
        if(cursor.isNull(i + 20))
        {
            double6 = null;
        } else
        {
            double6 = Double.valueOf(cursor.getDouble(i + 20));
        }
        mediaitem.setSimilarityScoreToPrev(double6);
        if(cursor.isNull(i + 21))
        {
            double7 = null;
        } else
        {
            double7 = Double.valueOf(cursor.getDouble(i + 21));
        }
        mediaitem.setSimilarityScoreToNext(double7);
        if(cursor.isNull(i + 22))
        {
            boolean3 = null;
        } else
        {
            boolean flag3;
            if(cursor.getShort(i + 22) != 0)
            {
                flag3 = flag;
            } else
            {
                flag3 = false;
            }
            boolean3 = Boolean.valueOf(flag3);
        }
        mediaitem.setWasAnalyzedForDuplicates(boolean3);
        if(cursor.isNull(i + 23))
        {
            double8 = null;
        } else
        {
            double8 = Double.valueOf(cursor.getDouble(i + 23));
        }
        mediaitem.setScore(double8);
        if(cursor.isNull(i + 24))
        {
            boolean4 = null;
        } else
        {
            boolean flag4;
            if(cursor.getShort(i + 24) != 0)
            {
                flag4 = flag;
            } else
            {
                flag4 = false;
            }
            boolean4 = Boolean.valueOf(flag4);
        }
        mediaitem.setIsBad(boolean4);
        if(cursor.isNull(i + 25))
        {
            boolean5 = null;
        } else
        {
            boolean flag5;
            if(cursor.getShort(i + 25) != 0)
            {
                flag5 = flag;
            } else
            {
                flag5 = false;
            }
            boolean5 = Boolean.valueOf(flag5);
        }
        mediaitem.setIsForReview(boolean5);
        if(cursor.isNull(i + 26))
        {
            date1 = null;
        } else
        {
            date1 = new Date(cursor.getLong(i + 26));
        }
        mediaitem.setLastTimeClassified(date1);
        if(cursor.isNull(i + 27))
        {
            boolean6 = null;
        } else
        {
            boolean flag6;
            if(cursor.getShort(i + 27) != 0)
            {
                flag6 = flag;
            } else
            {
                flag6 = false;
            }
            boolean6 = Boolean.valueOf(flag6);
        }
        mediaitem.setWasDeleted(boolean6);
        if(cursor.isNull(i + 28))
        {
            boolean7 = null;
        } else
        {
            boolean flag7;
            if(cursor.getShort(i + 28) != 0)
            {
                flag7 = flag;
            } else
            {
                flag7 = false;
            }
            boolean7 = Boolean.valueOf(flag7);
        }
        mediaitem.setWasClustered(boolean7);
        if(cursor.isNull(i + 29))
        {
            boolean8 = null;
        } else
        {
            boolean flag8;
            if(cursor.getShort(i + 29) != 0)
            {
                flag8 = flag;
            } else
            {
                flag8 = false;
            }
            boolean8 = Boolean.valueOf(flag8);
        }
        mediaitem.setWasAnalyzedByGD(boolean8);
        if(cursor.isNull(i + 30))
        {
            boolean9 = null;
        } else
        {
            boolean flag9;
            if(cursor.getShort(i + 30) != 0)
            {
                flag9 = flag;
            } else
            {
                flag9 = false;
            }
            boolean9 = Boolean.valueOf(flag9);
        }
        mediaitem.setWasKeptByUser(boolean9);
        if(cursor.isNull(i + 31))
        {
            boolean10 = null;
        } else
        {
            boolean flag10;
            if(cursor.getShort(i + 31) != 0)
            {
                flag10 = flag;
            } else
            {
                flag10 = false;
            }
            boolean10 = Boolean.valueOf(flag10);
        }
        mediaitem.setWasDeletedByUser(boolean10);
        if(cursor.isNull(i + 32))
        {
            long4 = null;
        } else
        {
            long4 = Long.valueOf(cursor.getLong(i + 32));
        }
        mediaitem.setFileSizeBytes(long4);
        if(cursor.isNull(i + 33))
        {
            integer3 = null;
        } else
        {
            integer3 = Integer.valueOf(cursor.getInt(i + 33));
        }
        mediaitem.setSource(integer3);
        if(cursor.isNull(i + 34))
        {
            integer4 = null;
        } else
        {
            integer4 = Integer.valueOf(cursor.getInt(i + 34));
        }
        mediaitem.setWidth(integer4);
        if(cursor.isNull(i + 35))
        {
            integer5 = null;
        } else
        {
            integer5 = Integer.valueOf(cursor.getInt(i + 35));
        }
        mediaitem.setHeight(integer5);
        if(cursor.isNull(i + 36))
        {
            boolean11 = null;
        } else
        {
            if(cursor.getShort(i + 36) == 0)
            {
                flag = false;
            }
            boolean11 = Boolean.valueOf(flag);
        }
        mediaitem.setWasMinimizedByUser(boolean11);
        if(cursor.isNull(i + 37))
        {
            long5 = null;
        } else
        {
            long5 = Long.valueOf(cursor.getLong(i + 37));
        }
        mediaitem.setServerId(long5);
        if(cursor.isNull(i + 38))
        {
            long6 = null;
        } else
        {
            long6 = Long.valueOf(cursor.getLong(i + 38));
        }
        mediaitem.setFolderId(long6);
        flag11 = cursor.isNull(i + 39);
        double9 = null;
        if(!flag11)
        {
            double9 = Double.valueOf(cursor.getDouble(i + 39));
        }
        mediaitem.setInteractionScore(double9);
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

    protected Long updateKeyAfterInsert(MediaItem mediaitem, long l)
    {
        mediaitem.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    public static class Properties
    {
        public static final Property AndroidId = new Property(1, Long.class, "androidId", false, "ANDROID_ID");
        public static final Property Blurry = new Property(9, Double.class, "blurry", false, "BLURRY");
        public static final Property CenterX = new Property(14, Float.class, "centerX", false, "CENTER_X");
        public static final Property CenterY = new Property(15, Float.class, "centerY", false, "CENTER_Y");
        public static final Property CheckedThumbnail = new Property(18, Boolean.class, "checkedThumbnail", false, "CHECKED_THUMBNAIL");
        public static final Property Color = new Property(10, Double.class, "color", false, "COLOR");
        public static final Property CvRan = new Property(13, Boolean.class, "cvRan", false, "CV_RAN");
        public static final Property Dark = new Property(11, Double.class, "dark", false, "DARK");
        public static final Property Date = new Property(6, Date.class, "date", false, "DATE");
        public static final Property Duration = new Property(19, Long.class, "duration", false, "DURATION");
        public static final Property FacesCount = new Property(16, Integer.class, "facesCount", false, "FACES_COUNT");
        public static final Property FacesJson = new Property(17, String.class, "facesJson", false, "FACES_JSON");
        public static final Property FileSizeBytes = new Property(32, Long.class, "fileSizeBytes", false, "FILE_SIZE_BYTES");
        public static final Property FolderId = new Property(38, Long.class, "folderId", false, "FOLDER_ID");
        public static final Property Height = new Property(35, Integer.class, "height", false, "HEIGHT");
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property InteractionScore = new Property(39, Double.class, "interactionScore", false, "INTERACTION_SCORE");
        public static final Property IsBad = new Property(24, Boolean.class, "isBad", false, "IS_BAD");
        public static final Property IsForReview = new Property(25, Boolean.class, "isForReview", false, "IS_FOR_REVIEW");
        public static final Property LastTimeClassified = new Property(26, Date.class, "lastTimeClassified", false, "LAST_TIME_CLASSIFIED");
        public static final Property Latitude = new Property(7, Double.class, "latitude", false, "LATITUDE");
        public static final Property Longitude = new Property(8, Double.class, "longitude", false, "LONGITUDE");
        public static final Property Orientation = new Property(5, Integer.class, "orientation", false, "ORIENTATION");
        public static final Property Path = new Property(3, String.class, "path", false, "PATH");
        public static final Property Prop = new Property(12, Float.class, "prop", false, "PROP");
        public static final Property Score = new Property(23, Double.class, "score", false, "SCORE");
        public static final Property ServerId = new Property(37, Long.class, "serverId", false, "SERVER_ID");
        public static final Property SimilarityScoreToNext = new Property(21, Double.class, "similarityScoreToNext", false, "SIMILARITY_SCORE_TO_NEXT");
        public static final Property SimilarityScoreToPrev = new Property(20, Double.class, "similarityScoreToPrev", false, "SIMILARITY_SCORE_TO_PREV");
        public static final Property Source = new Property(33, Integer.class, "source", false, "SOURCE");
        public static final Property Thumbnail = new Property(4, String.class, "thumbnail", false, "THUMBNAIL");
        public static final Property Type = new Property(2, Integer.class, "type", false, "TYPE");
        public static final Property WasAnalyzedByGD = new Property(29, Boolean.class, "wasAnalyzedByGD", false, "WAS_ANALYZED_BY_GD");
        public static final Property WasAnalyzedForDuplicates = new Property(22, Boolean.class, "wasAnalyzedForDuplicates", false, "WAS_ANALYZED_FOR_DUPLICATES");
        public static final Property WasClustered = new Property(28, Boolean.class, "wasClustered", false, "WAS_CLUSTERED");
        public static final Property WasDeleted = new Property(27, Boolean.class, "wasDeleted", false, "WAS_DELETED");
        public static final Property WasDeletedByUser = new Property(31, Boolean.class, "wasDeletedByUser", false, "WAS_DELETED_BY_USER");
        public static final Property WasKeptByUser = new Property(30, Boolean.class, "wasKeptByUser", false, "WAS_KEPT_BY_USER");
        public static final Property WasMinimizedByUser = new Property(36, Boolean.class, "wasMinimizedByUser", false, "WAS_MINIMIZED_BY_USER");
        public static final Property Width = new Property(34, Integer.class, "width", false, "WIDTH");


        public Properties()
        {
        }
    }

}

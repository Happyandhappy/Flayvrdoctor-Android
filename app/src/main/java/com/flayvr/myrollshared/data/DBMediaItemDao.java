package com.flayvr.myrollshared.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

public class DBMediaItemDao extends AbstractDao<DBMediaItem, Long>
{

    public static final String TABLENAME = "DBMEDIA_ITEM";

    public DBMediaItemDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public DBMediaItemDao(DaoConfig daoconfig, DaoSession daosession)
    {
        super(daoconfig, daosession);
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'DBMEDIA_ITEM' (").append("'_id' INTEGER PRIMARY KEY ,").append("'BLURRY' REAL,").append("'COLOR' REAL,").append("'DARK' REAL,").append("'PROP' REAL,").append("'SHOULD_RUN_HEAVY_PROCESSING' INTEGER,").append("'FACES_JSON' TEXT,").append("'TYPE' TEXT,").append("'CENTER_X' REAL,").append("'CENTER_Y' REAL,").append("'FACES_COUNT' INTEGER);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'DBMEDIA_ITEM'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, DBMediaItem dbmediaitem)
    {
        sqlitestatement.clearBindings();
        Long long1 = dbmediaitem.getId();
        if(long1 != null)
        {
            sqlitestatement.bindLong(1, long1.longValue());
        }
        Double double1 = dbmediaitem.getBlurry();
        if(double1 != null)
        {
            sqlitestatement.bindDouble(2, double1.doubleValue());
        }
        Double double2 = dbmediaitem.getColor();
        if(double2 != null)
        {
            sqlitestatement.bindDouble(3, double2.doubleValue());
        }
        Double double3 = dbmediaitem.getDark();
        if(double3 != null)
        {
            sqlitestatement.bindDouble(4, double3.doubleValue());
        }
        Float float1 = dbmediaitem.getProp();
        if(float1 != null)
        {
            sqlitestatement.bindDouble(5, float1.floatValue());
        }
        Boolean boolean1 = dbmediaitem.getShouldRunHeavyProcessing();
        String s;
        String s1;
        Float float2;
        Float float3;
        Integer integer;
        long l;
        if(boolean1 != null)
        {
            if(boolean1.booleanValue())
            {
                l = 1L;
            } else
            {
                l = 0L;
            }
            sqlitestatement.bindLong(6, l);
        }
        s = dbmediaitem.getFacesJson();
        if(s != null)
        {
            sqlitestatement.bindString(7, s);
        }
        s1 = dbmediaitem.getType();
        if(s1 != null)
        {
            sqlitestatement.bindString(8, s1);
        }
        float2 = dbmediaitem.getCenterX();
        if(float2 != null)
        {
            sqlitestatement.bindDouble(9, float2.floatValue());
        }
        float3 = dbmediaitem.getCenterY();
        if(float3 != null)
        {
            sqlitestatement.bindDouble(10, float3.floatValue());
        }
        integer = dbmediaitem.getFacesCount();
        if(integer != null)
        {
            sqlitestatement.bindLong(11, integer.intValue());
        }
    }

    public Long getKey(DBMediaItem dbmediaitem)
    {
        if(dbmediaitem != null)
        {
            return dbmediaitem.getId();
        } else
        {
            return null;
        }
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public DBMediaItem readEntity(Cursor cursor, int i)
    {
        Long long1;
        Double double1;
        Double double2;
        Double double3;
        Float float1;
        Boolean boolean1;
        String s;
        String s1;
        Float float2;
        Float float3;
        boolean flag1;
        Integer integer;
        if(cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        if(cursor.isNull(i + 1))
        {
            double1 = null;
        } else
        {
            double1 = Double.valueOf(cursor.getDouble(i + 1));
        }
        if(cursor.isNull(i + 2))
        {
            double2 = null;
        } else
        {
            double2 = Double.valueOf(cursor.getDouble(i + 2));
        }
        if(cursor.isNull(i + 3))
        {
            double3 = null;
        } else
        {
            double3 = Double.valueOf(cursor.getDouble(i + 3));
        }
        if(cursor.isNull(i + 4))
        {
            float1 = null;
        } else
        {
            float1 = Float.valueOf(cursor.getFloat(i + 4));
        }
        if(cursor.isNull(i + 5))
        {
            boolean1 = null;
        } else
        {
            boolean flag;
            if(cursor.getShort(i + 5) != 0)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            boolean1 = Boolean.valueOf(flag);
        }
        if(cursor.isNull(i + 6))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 6);
        }
        if(cursor.isNull(i + 7))
        {
            s1 = null;
        } else
        {
            s1 = cursor.getString(i + 7);
        }
        if(cursor.isNull(i + 8))
        {
            float2 = null;
        } else
        {
            float2 = Float.valueOf(cursor.getFloat(i + 8));
        }
        if(cursor.isNull(i + 9))
        {
            float3 = null;
        } else
        {
            float3 = Float.valueOf(cursor.getFloat(i + 9));
        }
        flag1 = cursor.isNull(i + 10);
        integer = null;
        if(!flag1)
        {
            integer = Integer.valueOf(cursor.getInt(i + 10));
        }
        return new DBMediaItem(long1, double1, double2, double3, float1, boolean1, s, s1, float2, float3, integer);
    }

    public void readEntity(Cursor cursor, DBMediaItem dbmediaitem, int i)
    {
        Long long1;
        Double double1;
        Double double2;
        Double double3;
        Float float1;
        Boolean boolean1;
        String s;
        String s1;
        Float float2;
        Float float3;
        boolean flag1;
        Integer integer;
        if(cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        dbmediaitem.setId(long1);
        if(cursor.isNull(i + 1))
        {
            double1 = null;
        } else
        {
            double1 = Double.valueOf(cursor.getDouble(i + 1));
        }
        dbmediaitem.setBlurry(double1);
        if(cursor.isNull(i + 2))
        {
            double2 = null;
        } else
        {
            double2 = Double.valueOf(cursor.getDouble(i + 2));
        }
        dbmediaitem.setColor(double2);
        if(cursor.isNull(i + 3))
        {
            double3 = null;
        } else
        {
            double3 = Double.valueOf(cursor.getDouble(i + 3));
        }
        dbmediaitem.setDark(double3);
        if(cursor.isNull(i + 4))
        {
            float1 = null;
        } else
        {
            float1 = Float.valueOf(cursor.getFloat(i + 4));
        }
        dbmediaitem.setProp(float1);
        if(cursor.isNull(i + 5))
        {
            boolean1 = null;
        } else
        {
            boolean flag;
            if(cursor.getShort(i + 5) != 0)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            boolean1 = Boolean.valueOf(flag);
        }
        dbmediaitem.setShouldRunHeavyProcessing(boolean1);
        if(cursor.isNull(i + 6))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 6);
        }
        dbmediaitem.setFacesJson(s);
        if(cursor.isNull(i + 7))
        {
            s1 = null;
        } else
        {
            s1 = cursor.getString(i + 7);
        }
        dbmediaitem.setType(s1);
        if(cursor.isNull(i + 8))
        {
            float2 = null;
        } else
        {
            float2 = Float.valueOf(cursor.getFloat(i + 8));
        }
        dbmediaitem.setCenterX(float2);
        if(cursor.isNull(i + 9))
        {
            float3 = null;
        } else
        {
            float3 = Float.valueOf(cursor.getFloat(i + 9));
        }
        dbmediaitem.setCenterY(float3);
        flag1 = cursor.isNull(i + 10);
        integer = null;
        if(!flag1)
        {
            integer = Integer.valueOf(cursor.getInt(i + 10));
        }
        dbmediaitem.setFacesCount(integer);
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

    protected Long updateKeyAfterInsert(DBMediaItem dbmediaitem, long l)
    {
        dbmediaitem.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    public static class Properties
    {
        public static final Property Blurry = new Property(1, Double.class, "blurry", false, "BLURRY");
        public static final Property CenterX = new Property(8, Float.class, "centerX", false, "CENTER_X");
        public static final Property CenterY = new Property(9, Float.class, "centerY", false, "CENTER_Y");
        public static final Property Color = new Property(2, Double.class, "color", false, "COLOR");
        public static final Property Dark = new Property(3, Double.class, "dark", false, "DARK");
        public static final Property FacesCount = new Property(10, Integer.class, "facesCount", false, "FACES_COUNT");
        public static final Property FacesJson = new Property(6, String.class, "facesJson", false, "FACES_JSON");
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property Prop = new Property(4, Float.class, "prop", false, "PROP");
        public static final Property ShouldRunHeavyProcessing = new Property(5, Boolean.class, "shouldRunHeavyProcessing", false, "SHOULD_RUN_HEAVY_PROCESSING");
        public static final Property Type = new Property(7, String.class, "type", false, "TYPE");
    }
}

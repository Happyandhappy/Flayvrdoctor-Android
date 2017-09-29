package com.flayvr.myrollshared.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

public class UserDao extends AbstractDao<User, Long>
{

    public static final String TABLENAME = "USER";

    public UserDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public UserDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'USER' (").append("'_id' INTEGER PRIMARY KEY ,").append("'HOME_LATITUDE' REAL,").append("'HOME_LONGITUDE' REAL,").append("'WORK_LATITUDE' REAL,").append("'WORK_LONGITUDE' REAL);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'USER'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, User user)
    {
        sqlitestatement.clearBindings();
        Long long1 = user.getId();
        if(long1 != null)
        {
            sqlitestatement.bindLong(1, long1.longValue());
        }
        Double double1 = user.getHomeLatitude();
        if(double1 != null)
        {
            sqlitestatement.bindDouble(2, double1.doubleValue());
        }
        Double double2 = user.getHomeLongitude();
        if(double2 != null)
        {
            sqlitestatement.bindDouble(3, double2.doubleValue());
        }
        Double double3 = user.getWorkLatitude();
        if(double3 != null)
        {
            sqlitestatement.bindDouble(4, double3.doubleValue());
        }
        Double double4 = user.getWorkLongitude();
        if(double4 != null)
        {
            sqlitestatement.bindDouble(5, double4.doubleValue());
        }
    }

    public Long getKey(User user)
    {
        if(user != null)
        {
            return user.getId();
        } else
        {
            return null;
        }
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public User readEntity(Cursor cursor, int i)
    {
        Long long1;
        Double double1;
        Double double2;
        Double double3;
        boolean flag;
        Double double4;
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
        flag = cursor.isNull(i + 4);
        double4 = null;
        if(!flag)
        {
            double4 = Double.valueOf(cursor.getDouble(i + 4));
        }
        return new User(long1, double1, double2, double3, double4);
    }

    public void readEntity(Cursor cursor, User user, int i)
    {
        Long long1;
        Double double1;
        Double double2;
        Double double3;
        boolean flag;
        Double double4;
        if(cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        user.setId(long1);
        if(cursor.isNull(i + 1))
        {
            double1 = null;
        } else
        {
            double1 = Double.valueOf(cursor.getDouble(i + 1));
        }
        user.setHomeLatitude(double1);
        if(cursor.isNull(i + 2))
        {
            double2 = null;
        } else
        {
            double2 = Double.valueOf(cursor.getDouble(i + 2));
        }
        user.setHomeLongitude(double2);
        if(cursor.isNull(i + 3))
        {
            double3 = null;
        } else
        {
            double3 = Double.valueOf(cursor.getDouble(i + 3));
        }
        user.setWorkLatitude(double3);
        flag = cursor.isNull(i + 4);
        double4 = null;
        if(!flag)
        {
            double4 = Double.valueOf(cursor.getDouble(i + 4));
        }
        user.setWorkLongitude(double4);
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

    protected Long updateKeyAfterInsert(User user, long l)
    {
        user.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    public static class Properties
    {
        public static final Property HomeLatitude = new Property(1, Double.class, "homeLatitude", false, "HOME_LATITUDE");
        public static final Property HomeLongitude = new Property(2, Double.class, "homeLongitude", false, "HOME_LONGITUDE");
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property WorkLatitude = new Property(3, Double.class, "workLatitude", false, "WORK_LATITUDE");
        public static final Property WorkLongitude = new Property(4, Double.class, "workLongitude", false, "WORK_LONGITUDE");
    }
}

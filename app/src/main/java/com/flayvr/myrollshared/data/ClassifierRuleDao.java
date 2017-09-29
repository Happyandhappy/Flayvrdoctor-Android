package com.flayvr.myrollshared.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

public class ClassifierRuleDao extends AbstractDao<ClassifierRule, Long>
{

    public static final String TABLENAME = "CLASSIFIER_RULE";
    private DaoSession daoSession;

    public ClassifierRuleDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public ClassifierRuleDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'CLASSIFIER_RULE' (").append("'_id' INTEGER PRIMARY KEY ,").append("'RULE_TYPE' TEXT,").append("'TOTAL_PHOTOS_KEPT' INTEGER,").append("'TOTAL_PHOTOS_DELETED' INTEGER);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'CLASSIFIER_RULE'").toString());
    }

    protected void attachEntity(ClassifierRule classifierrule)
    {
        super.attachEntity(classifierrule);
        classifierrule.__setDaoSession(daoSession);
    }

    protected void bindValues(SQLiteStatement sqlitestatement, ClassifierRule classifierrule)
    {
        sqlitestatement.clearBindings();
        Long long1 = classifierrule.getId();
        if(long1 != null)
        {
            sqlitestatement.bindLong(1, long1.longValue());
        }
        String s = classifierrule.getRuleType();
        if(s != null)
        {
            sqlitestatement.bindString(2, s);
        }
        Integer integer = classifierrule.getTotalPhotosKept();
        if(integer != null)
        {
            sqlitestatement.bindLong(3, integer.intValue());
        }
        Integer integer1 = classifierrule.getTotalPhotosDeleted();
        if(integer1 != null)
        {
            sqlitestatement.bindLong(4, integer1.intValue());
        }
    }

    public Long getKey(ClassifierRule classifierrule)
    {
        if(classifierrule != null)
        {
            return classifierrule.getId();
        } else
        {
            return null;
        }
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public ClassifierRule readEntity(Cursor cursor, int i)
    {
        Long long1;
        String s;
        Integer integer;
        boolean flag;
        Integer integer1;
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
            integer = null;
        } else
        {
            integer = Integer.valueOf(cursor.getInt(i + 2));
        }
        flag = cursor.isNull(i + 3);
        integer1 = null;
        if(!flag)
        {
            integer1 = Integer.valueOf(cursor.getInt(i + 3));
        }
        return new ClassifierRule(long1, s, integer, integer1);
    }

    public void readEntity(Cursor cursor, ClassifierRule classifierrule, int i)
    {
        Long long1;
        String s;
        Integer integer;
        boolean flag;
        Integer integer1;
        if(cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        classifierrule.setId(long1);
        if(cursor.isNull(i + 1))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 1);
        }
        classifierrule.setRuleType(s);
        if(cursor.isNull(i + 2))
        {
            integer = null;
        } else
        {
            integer = Integer.valueOf(cursor.getInt(i + 2));
        }
        classifierrule.setTotalPhotosKept(integer);
        flag = cursor.isNull(i + 3);
        integer1 = null;
        if(!flag)
        {
            integer1 = Integer.valueOf(cursor.getInt(i + 3));
        }
        classifierrule.setTotalPhotosDeleted(integer1);
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

    protected Long updateKeyAfterInsert(ClassifierRule classifierrule, long l)
    {
        classifierrule.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    public static class Properties
    {
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property RuleType = new Property(1, String.class, "ruleType", false, "RULE_TYPE");
        public static final Property TotalPhotosDeleted = new Property(3, Integer.class, "totalPhotosDeleted", false, "TOTAL_PHOTOS_DELETED");
        public static final Property TotalPhotosKept = new Property(2, Integer.class, "totalPhotosKept", false, "TOTAL_PHOTOS_KEPT");
    }
}

package com.flayvr.myrollshared.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

public class ClassifierThresholdDao extends AbstractDao<ClassifierThreshold, Long>
{

    public static final String TABLENAME = "CLASSIFIER_THRESHOLD";

    public ClassifierThresholdDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public ClassifierThresholdDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'CLASSIFIER_THRESHOLD' (").append("'_id' INTEGER PRIMARY KEY ,").append("'BAD_BLURRY' REAL,").append("'BAD_DARK' REAL,").append("'BAD_SCORE' REAL,").append("'FOR_REVIEW_SCORE' REAL,").append("'GOOD_ENOUGH_SCORE' REAL,").append("'BEST_SCORE' REAL,").append("'BEST_DIRECTORY_SCORE' REAL,").append("'SOURCE' INTEGER);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'CLASSIFIER_THRESHOLD'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, ClassifierThreshold classifierthreshold)
    {
        sqlitestatement.clearBindings();
        Long long1 = classifierthreshold.getId();
        if(long1 != null)
        {
            sqlitestatement.bindLong(1, long1.longValue());
        }
        Double double1 = classifierthreshold.getBadBlurry();
        if(double1 != null)
        {
            sqlitestatement.bindDouble(2, double1.doubleValue());
        }
        Double double2 = classifierthreshold.getBadDark();
        if(double2 != null)
        {
            sqlitestatement.bindDouble(3, double2.doubleValue());
        }
        Double double3 = classifierthreshold.getBadScore();
        if(double3 != null)
        {
            sqlitestatement.bindDouble(4, double3.doubleValue());
        }
        Double double4 = classifierthreshold.getForReviewScore();
        if(double4 != null)
        {
            sqlitestatement.bindDouble(5, double4.doubleValue());
        }
        Double double5 = classifierthreshold.getGoodEnoughScore();
        if(double5 != null)
        {
            sqlitestatement.bindDouble(6, double5.doubleValue());
        }
        Double double6 = classifierthreshold.getBestScore();
        if(double6 != null)
        {
            sqlitestatement.bindDouble(7, double6.doubleValue());
        }
        Double double7 = classifierthreshold.getBestDirectoryScore();
        if(double7 != null)
        {
            sqlitestatement.bindDouble(8, double7.doubleValue());
        }
        Integer integer = classifierthreshold.getSource();
        if(integer != null)
        {
            sqlitestatement.bindLong(9, integer.intValue());
        }
    }

    public Long getKey(ClassifierThreshold classifierthreshold)
    {
        if(classifierthreshold != null)
        {
            return classifierthreshold.getId();
        } else
        {
            return null;
        }
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public ClassifierThreshold readEntity(Cursor cursor, int i)
    {
        Long long1;
        Double double1;
        Double double2;
        Double double3;
        Double double4;
        Double double5;
        Double double6;
        Double double7;
        boolean flag;
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
            double4 = null;
        } else
        {
            double4 = Double.valueOf(cursor.getDouble(i + 4));
        }
        if(cursor.isNull(i + 5))
        {
            double5 = null;
        } else
        {
            double5 = Double.valueOf(cursor.getDouble(i + 5));
        }
        if(cursor.isNull(i + 6))
        {
            double6 = null;
        } else
        {
            double6 = Double.valueOf(cursor.getDouble(i + 6));
        }
        if(cursor.isNull(i + 7))
        {
            double7 = null;
        } else
        {
            double7 = Double.valueOf(cursor.getDouble(i + 7));
        }
        flag = cursor.isNull(i + 8);
        integer = null;
        if(!flag)
        {
            integer = Integer.valueOf(cursor.getInt(i + 8));
        }
        return new ClassifierThreshold(long1, double1, double2, double3, double4, double5, double6, double7, integer);
    }

    public void readEntity(Cursor cursor, ClassifierThreshold classifierthreshold, int i)
    {
        Long long1;
        Double double1;
        Double double2;
        Double double3;
        Double double4;
        Double double5;
        Double double6;
        Double double7;
        boolean flag;
        Integer integer;
        if(cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        classifierthreshold.setId(long1);
        if(cursor.isNull(i + 1))
        {
            double1 = null;
        } else
        {
            double1 = Double.valueOf(cursor.getDouble(i + 1));
        }
        classifierthreshold.setBadBlurry(double1);
        if(cursor.isNull(i + 2))
        {
            double2 = null;
        } else
        {
            double2 = Double.valueOf(cursor.getDouble(i + 2));
        }
        classifierthreshold.setBadDark(double2);
        if(cursor.isNull(i + 3))
        {
            double3 = null;
        } else
        {
            double3 = Double.valueOf(cursor.getDouble(i + 3));
        }
        classifierthreshold.setBadScore(double3);
        if(cursor.isNull(i + 4))
        {
            double4 = null;
        } else
        {
            double4 = Double.valueOf(cursor.getDouble(i + 4));
        }
        classifierthreshold.setForReviewScore(double4);
        if(cursor.isNull(i + 5))
        {
            double5 = null;
        } else
        {
            double5 = Double.valueOf(cursor.getDouble(i + 5));
        }
        classifierthreshold.setGoodEnoughScore(double5);
        if(cursor.isNull(i + 6))
        {
            double6 = null;
        } else
        {
            double6 = Double.valueOf(cursor.getDouble(i + 6));
        }
        classifierthreshold.setBestScore(double6);
        if(cursor.isNull(i + 7))
        {
            double7 = null;
        } else
        {
            double7 = Double.valueOf(cursor.getDouble(i + 7));
        }
        classifierthreshold.setBestDirectoryScore(double7);
        flag = cursor.isNull(i + 8);
        integer = null;
        if(!flag)
        {
            integer = Integer.valueOf(cursor.getInt(i + 8));
        }
        classifierthreshold.setSource(integer);
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

    protected Long updateKeyAfterInsert(ClassifierThreshold classifierthreshold, long l)
    {
        classifierthreshold.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    public static class Properties
    {
        public static final Property BadBlurry = new Property(1, Double.class, "badBlurry", false, "BAD_BLURRY");
        public static final Property BadDark = new Property(2, Double.class, "badDark", false, "BAD_DARK");
        public static final Property BadScore = new Property(3, Double.class, "badScore", false, "BAD_SCORE");
        public static final Property BestDirectoryScore = new Property(7, Double.class, "bestDirectoryScore", false, "BEST_DIRECTORY_SCORE");
        public static final Property BestScore = new Property(6, Double.class, "bestScore", false, "BEST_SCORE");
        public static final Property ForReviewScore = new Property(4, Double.class, "forReviewScore", false, "FOR_REVIEW_SCORE");
        public static final Property GoodEnoughScore = new Property(5, Double.class, "goodEnoughScore", false, "GOOD_ENOUGH_SCORE");
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property Source = new Property(8, Integer.class, "source", false, "SOURCE");
    }
}

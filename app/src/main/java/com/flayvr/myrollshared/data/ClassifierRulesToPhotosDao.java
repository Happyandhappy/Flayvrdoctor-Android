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

public class ClassifierRulesToPhotosDao extends AbstractDao<ClassifierRulesToPhotos, Long>
{

    public static final String TABLENAME = "CLASSIFIER_RULES_TO_PHOTOS";
    private Query classifierRule_ClassifierRulePhotosQuery;
    private DaoSession daoSession;
    private Query mediaItem_PhotoClassifierRulesQuery;
    private String selectDeep;

    public ClassifierRulesToPhotosDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public ClassifierRulesToPhotosDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'CLASSIFIER_RULES_TO_PHOTOS' (").append("'_id' INTEGER PRIMARY KEY ,").append("'CLASSIFIER_RULE_ID' INTEGER,").append("'PHOTO_ID' INTEGER);").toString());
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE INDEX ").append(s).append("IDX_CLASSIFIER_RULES_TO_PHOTOS_CLASSIFIER_RULE_ID ON CLASSIFIER_RULES_TO_PHOTOS").append(" (CLASSIFIER_RULE_ID);").toString());
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE INDEX ").append(s).append("IDX_CLASSIFIER_RULES_TO_PHOTOS_PHOTO_ID ON CLASSIFIER_RULES_TO_PHOTOS").append(" (PHOTO_ID);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'CLASSIFIER_RULES_TO_PHOTOS'").toString());
    }

    public List _queryClassifierRule_ClassifierRulePhotos(Long long1)
    {
        synchronized (this) {
            if(classifierRule_ClassifierRulePhotosQuery == null)
            {
                QueryBuilder querybuilder = queryBuilder();
                querybuilder.where(Properties.ClassifierRuleId.eq(null), new WhereCondition[0]);
                classifierRule_ClassifierRulePhotosQuery = querybuilder.build();
            }
        }
        Query query = classifierRule_ClassifierRulePhotosQuery.forCurrentThread();
        query.setParameter(0, long1);
        return query.list();
    }

    public List _queryMediaItem_PhotoClassifierRules(Long long1)
    {
        synchronized (this) {
            if (mediaItem_PhotoClassifierRulesQuery == null) {
                QueryBuilder querybuilder = queryBuilder();
                querybuilder.where(Properties.PhotoId.eq(null), new WhereCondition[0]);
                mediaItem_PhotoClassifierRulesQuery = querybuilder.build();
            }
        }
        Query query = mediaItem_PhotoClassifierRulesQuery.forCurrentThread();
        query.setParameter(0, long1);
        return query.list();
    }

    protected void attachEntity(ClassifierRulesToPhotos classifierrulestophotos)
    {
        super.attachEntity(classifierrulestophotos);
        classifierrulestophotos.__setDaoSession(daoSession);
    }

    protected void bindValues(SQLiteStatement sqlitestatement, ClassifierRulesToPhotos classifierrulestophotos)
    {
        sqlitestatement.clearBindings();
        Long long1 = classifierrulestophotos.getId();
        if(long1 != null)
        {
            sqlitestatement.bindLong(1, long1.longValue());
        }
        Long long2 = classifierrulestophotos.getClassifierRuleId();
        if(long2 != null)
        {
            sqlitestatement.bindLong(2, long2.longValue());
        }
        Long long3 = classifierrulestophotos.getPhotoId();
        if(long3 != null)
        {
            sqlitestatement.bindLong(3, long3.longValue());
        }
    }

    public Long getKey(ClassifierRulesToPhotos classifierrulestophotos)
    {
        if(classifierrulestophotos != null)
        {
            return classifierrulestophotos.getId();
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
            SqlUtils.appendColumns(stringbuilder, "T1", daoSession.getClassifierRuleDao().getAllColumns());
            stringbuilder.append(" FROM CLASSIFIER_RULES_TO_PHOTOS T");
            stringbuilder.append(" LEFT JOIN MEDIA_ITEM T0 ON T.'PHOTO_ID'=T0.'_id'");
            stringbuilder.append(" LEFT JOIN CLASSIFIER_RULE T1 ON T.'CLASSIFIER_RULE_ID'=T1.'_id'");
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
            }catch (Exception e){
                return null;
            }
        }
    }

    protected ClassifierRulesToPhotos loadCurrentDeep(Cursor cursor, boolean flag)
    {
        ClassifierRulesToPhotos classifierrulestophotos = (ClassifierRulesToPhotos)loadCurrent(cursor, 0, flag);
        int i = getAllColumns().length;
        classifierrulestophotos.setPhoto((MediaItem)loadCurrentOther(daoSession.getMediaItemDao(), cursor, i));
        int j = i + daoSession.getMediaItemDao().getAllColumns().length;
        classifierrulestophotos.setClassifierRule((ClassifierRule)loadCurrentOther(daoSession.getClassifierRuleDao(), cursor, j));
        return classifierrulestophotos;
    }

    public ClassifierRulesToPhotos loadDeep(Long long1)
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
        ClassifierRulesToPhotos classifierrulestophotos = loadCurrentDeep(cursor, true);
        cursor.close();
        return classifierrulestophotos;
    }

    protected List loadDeepAllAndCloseCursor(Cursor cursor)
    {
        try {
            List list = loadAllDeepFromCursor(cursor);
            cursor.close();
            return list;
        }catch(Exception e){
            return null;
        }
    }

    public List queryDeep(String s, String as[])
    {
        return loadDeepAllAndCloseCursor(db.rawQuery((new StringBuilder()).append(getSelectDeep()).append(s).toString(), as));
    }

    public ClassifierRulesToPhotos readEntity(Cursor cursor, int i)
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
        return new ClassifierRulesToPhotos(long1, long2, long3);
    }

    public void readEntity(Cursor cursor, ClassifierRulesToPhotos classifierrulestophotos, int i)
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
        classifierrulestophotos.setId(long1);
        if(cursor.isNull(i + 1))
        {
            long2 = null;
        } else
        {
            long2 = Long.valueOf(cursor.getLong(i + 1));
        }
        classifierrulestophotos.setClassifierRuleId(long2);
        flag = cursor.isNull(i + 2);
        long3 = null;
        if(!flag)
        {
            long3 = Long.valueOf(cursor.getLong(i + 2));
        }
        classifierrulestophotos.setPhotoId(long3);
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

    protected Long updateKeyAfterInsert(ClassifierRulesToPhotos classifierrulestophotos, long l)
    {
        classifierrulestophotos.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    public static class Properties {
        public static final Property ClassifierRuleId = new Property(1, Long.class, "classifierRuleId", false, "CLASSIFIER_RULE_ID");
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property PhotoId = new Property(2, Long.class, "photoId", false, "PHOTO_ID");
    }
}

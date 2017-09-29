package com.flayvr.myrollshared.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;

public class DaoMaster extends AbstractDaoMaster
{

    public static final int SCHEMA_VERSION = 50;

    public DaoMaster(SQLiteDatabase sqlitedatabase)
    {
        super(sqlitedatabase, 50);
        registerDaoClass(DBMediaItemDao.class);
        registerDaoClass(DBMomentDao.class);
        registerDaoClass(DBMomentsItemsDao.class);
        registerDaoClass(DBFolderDao.class);
        registerDaoClass(MediaItemDao.class);
        registerDaoClass(FolderDao.class);
        registerDaoClass(DuplicatesSetDao.class);
        registerDaoClass(DuplicatesSetsToPhotosDao.class);
        registerDaoClass(UserDao.class);
        registerDaoClass(MomentDao.class);
        registerDaoClass(MomentsItemsDao.class);
        registerDaoClass(ClassifierThresholdDao.class);
        registerDaoClass(InteractionDao.class);
        registerDaoClass(ClassifierRuleDao.class);
        registerDaoClass(ClassifierRulesToPhotosDao.class);
    }

    public static void createAllTables(SQLiteDatabase sqlitedatabase, boolean flag)
    {
        DBMediaItemDao.createTable(sqlitedatabase, flag);
        DBMomentDao.createTable(sqlitedatabase, flag);
        DBMomentsItemsDao.createTable(sqlitedatabase, flag);
        DBFolderDao.createTable(sqlitedatabase, flag);
        MediaItemDao.createTable(sqlitedatabase, flag);
        FolderDao.createTable(sqlitedatabase, flag);
        DuplicatesSetDao.createTable(sqlitedatabase, flag);
        DuplicatesSetsToPhotosDao.createTable(sqlitedatabase, flag);
        UserDao.createTable(sqlitedatabase, flag);
        MomentDao.createTable(sqlitedatabase, flag);
        MomentsItemsDao.createTable(sqlitedatabase, flag);
        ClassifierThresholdDao.createTable(sqlitedatabase, flag);
        InteractionDao.createTable(sqlitedatabase, flag);
        ClassifierRuleDao.createTable(sqlitedatabase, flag);
        ClassifierRulesToPhotosDao.createTable(sqlitedatabase, flag);
    }

    public static void dropAllTables(SQLiteDatabase sqlitedatabase, boolean flag)
    {
        DBMediaItemDao.dropTable(sqlitedatabase, flag);
        DBMomentDao.dropTable(sqlitedatabase, flag);
        DBMomentsItemsDao.dropTable(sqlitedatabase, flag);
        DBFolderDao.dropTable(sqlitedatabase, flag);
        MediaItemDao.dropTable(sqlitedatabase, flag);
        FolderDao.dropTable(sqlitedatabase, flag);
        DuplicatesSetDao.dropTable(sqlitedatabase, flag);
        DuplicatesSetsToPhotosDao.dropTable(sqlitedatabase, flag);
        UserDao.dropTable(sqlitedatabase, flag);
        MomentDao.dropTable(sqlitedatabase, flag);
        MomentsItemsDao.dropTable(sqlitedatabase, flag);
        ClassifierThresholdDao.dropTable(sqlitedatabase, flag);
        InteractionDao.dropTable(sqlitedatabase, flag);
        ClassifierRuleDao.dropTable(sqlitedatabase, flag);
        ClassifierRulesToPhotosDao.dropTable(sqlitedatabase, flag);
    }

    public DaoSession newSession()
    {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType identityscopetype)
    {
        return new DaoSession(db, identityscopetype, daoConfigMap);
    }

    public abstract static class OpenHelper extends SQLiteOpenHelper
    {
        public void onCreate(SQLiteDatabase sqlitedatabase)
        {
            DaoMaster.createAllTables(sqlitedatabase, false);
        }

        public OpenHelper(Context context, String s, SQLiteDatabase.CursorFactory factory)
        {
            super(context, s, factory, 50);
        }
    }

    public class DevOpenHelper extends DaoMaster.OpenHelper
    {
        public DevOpenHelper(Context paramContext, String paramString, SQLiteDatabase.CursorFactory paramCursorFactory)
        {
            super(paramContext, paramString, paramCursorFactory);
        }

        public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
        {
            Log.i("greenDAO", "Upgrading schema from version " + paramInt1 + " to " + paramInt2 + " by dropping all tables");
            DaoMaster.dropAllTables(paramSQLiteDatabase, true);
            onCreate(paramSQLiteDatabase);
        }
    }
}

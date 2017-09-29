package com.flayvr.myrollshared.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

public class DBFolderDao extends AbstractDao<DBFolder, String>
{

    public static final String TABLENAME = "DBFOLDER";
    private DaoSession daoSession;

    public DBFolderDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public DBFolderDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'DBFOLDER' (").append("'FOLDER_ID' TEXT PRIMARY KEY NOT NULL );").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'DBFOLDER'").toString());
    }

    protected void attachEntity(DBFolder dbfolder)
    {
        super.attachEntity(dbfolder);
        dbfolder.__setDaoSession(daoSession);
    }

    protected void bindValues(SQLiteStatement sqlitestatement, DBFolder dbfolder)
    {
        sqlitestatement.clearBindings();
        String s = dbfolder.getFolderId();
        if(s != null)
        {
            sqlitestatement.bindString(1, s);
        }
    }

    public String getKey(DBFolder dbfolder)
    {
        if(dbfolder != null)
        {
            return dbfolder.getFolderId();
        } else
        {
            return null;
        }
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public DBFolder readEntity(Cursor cursor, int i)
    {
        String s;
        if(cursor.isNull(i + 0))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 0);
        }
        return new DBFolder(s);
    }

    public void readEntity(Cursor cursor, DBFolder dbfolder, int i)
    {
        String s;
        if(cursor.isNull(i + 0))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 0);
        }
        dbfolder.setFolderId(s);
    }

    public String readKey(Cursor cursor, int i)
    {
        if(cursor.isNull(i + 0))
        {
            return null;
        } else
        {
            return cursor.getString(i + 0);
        }
    }

    protected String updateKeyAfterInsert(DBFolder dbfolder, long l)
    {
        return dbfolder.getFolderId();
    }

    public static class Properties
    {
        public static final Property FolderId = new Property(0, String.class, "folderId", true, "FOLDER_ID");
    }
}

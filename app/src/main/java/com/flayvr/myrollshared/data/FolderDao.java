package com.flayvr.myrollshared.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

public class FolderDao extends AbstractDao<Folder, Long>
{
    public static final String TABLENAME = "FOLDER";
    private DaoSession daoSession;

    public FolderDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public FolderDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'FOLDER' (").append("'_id' INTEGER PRIMARY KEY ,").append("'NAME' TEXT,").append("'USER_TYPE' TEXT,").append("'IS_CAMERA' INTEGER,").append("'SOURCE' INTEGER,").append("'FOLDER_SOURCE_ID' INTEGER,").append("'IS_USER_CREATED' INTEGER,").append("'IS_HIDDEN' INTEGER,").append("'FOLDER_PATH' TEXT);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'FOLDER'").toString());
    }

    protected void attachEntity(Folder folder)
    {
        super.attachEntity(folder);
        folder.__setDaoSession(daoSession);
    }

    protected void bindValues(SQLiteStatement sqlitestatement, Folder folder)
    {
        long l = 1L;
        sqlitestatement.clearBindings();
        Long long1 = folder.getId();
        if(long1 != null)
        {
            sqlitestatement.bindLong(1, long1.longValue());
        }
        String s = folder.getName();
        if(s != null)
        {
            sqlitestatement.bindString(2, s);
        }
        String s1 = folder.getUserType();
        if(s1 != null)
        {
            sqlitestatement.bindString(3, s1);
        }
        Boolean boolean1 = folder.getIsCamera();
        Integer integer;
        Long long2;
        Boolean boolean2;
        Boolean boolean3;
        String s2;
        long l2;
        if(boolean1 != null)
        {
            if(boolean1.booleanValue())
            {
                l2 = l;
            } else
            {
                l2 = 0L;
            }
            sqlitestatement.bindLong(4, l2);
        }
        integer = folder.getSource();
        if(integer != null)
        {
            sqlitestatement.bindLong(5, integer.intValue());
        }
        long2 = folder.getFolderSourceId();
        if(long2 != null)
        {
            sqlitestatement.bindLong(6, long2.longValue());
        }
        boolean2 = folder.getIsUserCreated();
        if(boolean2 != null)
        {
            long l1;
            if(boolean2.booleanValue())
            {
                l1 = l;
            } else
            {
                l1 = 0L;
            }
            sqlitestatement.bindLong(7, l1);
        }
        boolean3 = folder.getIsHidden();
        if(boolean3 != null)
        {
            if(!boolean3.booleanValue())
            {
                l = 0L;
            }
            sqlitestatement.bindLong(8, l);
        }
        s2 = folder.getFolderPath();
        if(s2 != null)
        {
            sqlitestatement.bindString(9, s2);
        }
    }

    public Long getKey(Folder folder)
    {
        if(folder != null)
        {
            return folder.getId();
        } else
        {
            return null;
        }
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public Folder readEntity(Cursor cursor, int i)
    {
        boolean flag = true;
        Long long1;
        String s;
        String s1;
        Boolean boolean1;
        Integer integer;
        Long long2;
        Boolean boolean2;
        Boolean boolean3;
        boolean flag3;
        String s2;
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
        if(cursor.isNull(i + 4))
        {
            integer = null;
        } else
        {
            integer = Integer.valueOf(cursor.getInt(i + 4));
        }
        if(cursor.isNull(i + 5))
        {
            long2 = null;
        } else
        {
            long2 = Long.valueOf(cursor.getLong(i + 5));
        }
        if(cursor.isNull(i + 6))
        {
            boolean2 = null;
        } else
        {
            boolean flag2;
            if(cursor.getShort(i + 6) != 0)
            {
                flag2 = flag;
            } else
            {
                flag2 = false;
            }
            boolean2 = Boolean.valueOf(flag2);
        }
        if(cursor.isNull(i + 7))
        {
            boolean3 = null;
        } else
        {
            if(cursor.getShort(i + 7) == 0)
            {
                flag = false;
            }
            boolean3 = Boolean.valueOf(flag);
        }
        flag3 = cursor.isNull(i + 8);
        s2 = null;
        if(!flag3)
        {
            s2 = cursor.getString(i + 8);
        }
        return new Folder(long1, s, s1, boolean1, integer, long2, boolean2, boolean3, s2);
    }

    public void readEntity(Cursor cursor, Folder folder, int i)
    {
        boolean flag = true;
        Long long1;
        String s;
        String s1;
        Boolean boolean1;
        Integer integer;
        Long long2;
        Boolean boolean2;
        Boolean boolean3;
        boolean flag3;
        String s2;
        if(cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        folder.setId(long1);
        if(cursor.isNull(i + 1))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 1);
        }
        folder.setName(s);
        if(cursor.isNull(i + 2))
        {
            s1 = null;
        } else
        {
            s1 = cursor.getString(i + 2);
        }
        folder.setUserType(s1);
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
        folder.setIsCamera(boolean1);
        if(cursor.isNull(i + 4))
        {
            integer = null;
        } else
        {
            integer = Integer.valueOf(cursor.getInt(i + 4));
        }
        folder.setSource(integer);
        if(cursor.isNull(i + 5))
        {
            long2 = null;
        } else
        {
            long2 = Long.valueOf(cursor.getLong(i + 5));
        }
        folder.setFolderSourceId(long2);
        if(cursor.isNull(i + 6))
        {
            boolean2 = null;
        } else
        {
            boolean flag2;
            if(cursor.getShort(i + 6) != 0)
            {
                flag2 = flag;
            } else
            {
                flag2 = false;
            }
            boolean2 = Boolean.valueOf(flag2);
        }
        folder.setIsUserCreated(boolean2);
        if(cursor.isNull(i + 7))
        {
            boolean3 = null;
        } else
        {
            if(cursor.getShort(i + 7) == 0)
            {
                flag = false;
            }
            boolean3 = Boolean.valueOf(flag);
        }
        folder.setIsHidden(boolean3);
        flag3 = cursor.isNull(i + 8);
        s2 = null;
        if(!flag3)
        {
            s2 = cursor.getString(i + 8);
        }
        folder.setFolderPath(s2);
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

    protected Long updateKeyAfterInsert(Folder folder, long l)
    {
        folder.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    public static class Properties
    {
        public static final Property FolderPath = new Property(8, String.class, "folderPath", false, "FOLDER_PATH");
        public static final Property FolderSourceId = new Property(5, Long.class, "folderSourceId", false, "FOLDER_SOURCE_ID");
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property IsCamera = new Property(3, Boolean.class, "isCamera", false, "IS_CAMERA");
        public static final Property IsHidden = new Property(7, Boolean.class, "isHidden", false, "IS_HIDDEN");
        public static final Property IsUserCreated = new Property(6, Boolean.class, "isUserCreated", false, "IS_USER_CREATED");
        public static final Property Name = new Property(1, String.class, "name", false, "NAME");
        public static final Property Source = new Property(4, Integer.class, "source", false, "SOURCE");
        public static final Property UserType = new Property(2, String.class, "userType", false, "USER_TYPE");
    }
}

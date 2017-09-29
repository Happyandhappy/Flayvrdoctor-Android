package com.flayvr.myrollshared.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.identityscope.IdentityScope;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.internal.SqlUtils;
import java.util.*;

public class InteractionDao extends AbstractDao<Interaction, Long>
{

    public static final String TABLENAME = "INTERACTION";
    private DaoSession daoSession;
    private String selectDeep;

    public InteractionDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public InteractionDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'INTERACTION' (").append("'_id' INTEGER PRIMARY KEY ,").append("'INTERACTION_TYPE' INTEGER,").append("'INTERACTION_DATE' INTEGER,").append("'ITEM_ID' INTEGER);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'INTERACTION'").toString());
    }

    protected void attachEntity(Interaction interaction)
    {
        super.attachEntity(interaction);
        interaction.__setDaoSession(daoSession);
    }

    protected void bindValues(SQLiteStatement sqlitestatement, Interaction interaction)
    {
        sqlitestatement.clearBindings();
        Long long1 = interaction.getId();
        if(long1 != null)
        {
            sqlitestatement.bindLong(1, long1.longValue());
        }
        Integer integer = interaction.getInteractionType();
        if(integer != null)
        {
            sqlitestatement.bindLong(2, integer.intValue());
        }
        Date date = interaction.getInteractionDate();
        if(date != null)
        {
            sqlitestatement.bindLong(3, date.getTime());
        }
        Long long2 = interaction.getItemId();
        if(long2 != null)
        {
            sqlitestatement.bindLong(4, long2.longValue());
        }
    }

    public Long getKey(Interaction interaction)
    {
        if(interaction != null)
        {
            return interaction.getId();
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
            stringbuilder.append(" FROM INTERACTION T");
            stringbuilder.append(" LEFT JOIN MEDIA_ITEM T0 ON T.'ITEM_ID'=T0.'_id'");
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

    protected Interaction loadCurrentDeep(Cursor cursor, boolean flag)
    {
        Interaction interaction = (Interaction)loadCurrent(cursor, 0, flag);
        int i = getAllColumns().length;
        interaction.setMediaItem((MediaItem)loadCurrentOther(daoSession.getMediaItemDao(), cursor, i));
        return interaction;
    }

    public Interaction loadDeep(Long long1)
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
        Interaction interaction = loadCurrentDeep(cursor, true);
        cursor.close();
        return interaction;
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

    public Interaction readEntity(Cursor cursor, int i)
    {
        Long long1;
        Integer integer;
        Date date;
        boolean flag;
        Long long2;
        if(cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        if(cursor.isNull(i + 1))
        {
            integer = null;
        } else
        {
            integer = Integer.valueOf(cursor.getInt(i + 1));
        }
        if(cursor.isNull(i + 2))
        {
            date = null;
        } else
        {
            date = new Date(cursor.getLong(i + 2));
        }
        flag = cursor.isNull(i + 3);
        long2 = null;
        if(!flag)
        {
            long2 = Long.valueOf(cursor.getLong(i + 3));
        }
        return new Interaction(long1, integer, date, long2);
    }

    public void readEntity(Cursor cursor, Interaction interaction, int i)
    {
        Long long1;
        Integer integer;
        Date date;
        boolean flag;
        Long long2;
        if(cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        interaction.setId(long1);
        if(cursor.isNull(i + 1))
        {
            integer = null;
        } else
        {
            integer = Integer.valueOf(cursor.getInt(i + 1));
        }
        interaction.setInteractionType(integer);
        if(cursor.isNull(i + 2))
        {
            date = null;
        } else
        {
            date = new Date(cursor.getLong(i + 2));
        }
        interaction.setInteractionDate(date);
        flag = cursor.isNull(i + 3);
        long2 = null;
        if(!flag)
        {
            long2 = Long.valueOf(cursor.getLong(i + 3));
        }
        interaction.setItemId(long2);
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

    protected Long updateKeyAfterInsert(Interaction interaction, long l)
    {
        interaction.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    public static class Properties
    {
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property InteractionDate = new Property(2, Date.class, "interactionDate", false, "INTERACTION_DATE");
        public static final Property InteractionType = new Property(1, Integer.class, "interactionType", false, "INTERACTION_TYPE");
        public static final Property ItemId = new Property(3, Long.class, "itemId", false, "ITEM_ID");
    }
}

package com.flayvr.myrollshared.data;

import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.events.MomentChangedEvent;
import de.greenrobot.event.EventBus;

public class DBManager
{

    private static final String DB_FILE_NAME = "myroll";
    public static final int FETCH_LIMIT = 1000;
    public static final int MAX_ITEMS_IN_IN_CLAUSE = 900;
    private static DBManager instance;
    private final DaoSession daoSession = (new DaoMaster((new MigrationHelper(FlayvrApplication.getAppContext(), "myroll", null)).getWritableDatabase())).newSession();

    private DBManager()
    {
        EventBus.getDefault().register(this);
    }

    public static DBManager getInstance()
    {
        synchronized (DBManager.class) {
            DBManager dbmanager;
            if(instance == null)
            {
                instance = new DBManager();
            }
            dbmanager = instance;
            return dbmanager;
        }
    }

    public DaoSession getDaoSession()
    {
        return daoSession;
    }

    public void onEvent(MomentChangedEvent momentchangedevent)
    {
        momentchangedevent.getMoment().update();
    }
}

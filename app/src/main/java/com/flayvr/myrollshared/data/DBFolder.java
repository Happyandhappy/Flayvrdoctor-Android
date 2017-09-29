package com.flayvr.myrollshared.data;

import de.greenrobot.dao.DaoException;
import java.util.List;

public class DBFolder
{

    private List dBMomentList;
    private DaoSession daoSession;
    private String folderId;
    private DBFolderDao myDao;

    public DBFolder()
    {
    }

    public DBFolder(String s)
    {
        folderId = s;
    }

    public void __setDaoSession(DaoSession daosession)
    {
        daoSession = daosession;
        DBFolderDao dbfolderdao;
        if(daosession != null)
        {
            dbfolderdao = daosession.getDBFolderDao();
        } else
        {
            dbfolderdao = null;
        }
        myDao = dbfolderdao;
    }

    public void delete()
    {
        if(myDao == null)
        {
            throw new DaoException("Entity is detached from DAO context");
        } else
        {
            myDao.delete(this);
            return;
        }
    }

    public List getDBMomentList()
    {
        if(dBMomentList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            List list = daoSession.getDBMomentDao()._queryDBFolder_DBMomentList(folderId);
            synchronized (this) {
                if (dBMomentList == null)
                    dBMomentList = list;
            }
        }
        return dBMomentList;
    }

    public String getFolderId()
    {
        return folderId;
    }

    public void refresh()
    {
        if(myDao == null)
        {
            throw new DaoException("Entity is detached from DAO context");
        } else
            myDao.refresh(this);
    }

    public void resetDBMomentList()
    {
        synchronized (this) {
            dBMomentList = null;
        }
    }

    public void setFolderId(String s)
    {
        folderId = s;
    }

    public void update()
    {
        if(myDao == null)
        {
            throw new DaoException("Entity is detached from DAO context");
        } else
            myDao.update(this);
    }
}

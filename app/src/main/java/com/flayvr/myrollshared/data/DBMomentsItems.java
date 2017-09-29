package com.flayvr.myrollshared.data;

import de.greenrobot.dao.DaoException;

public class DBMomentsItems
{

    private DaoSession daoSession;
    private Long id;
    private DBMediaItem item;
    private Long itemId;
    private Long item__resolvedKey;
    private Long momentId;
    private DBMomentsItemsDao myDao;

    public DBMomentsItems()
    {
    }

    public DBMomentsItems(Long long1)
    {
        id = long1;
    }

    public DBMomentsItems(Long long1, Long long2, Long long3)
    {
        id = long1;
        momentId = long2;
        itemId = long3;
    }

    public void __setDaoSession(DaoSession daosession)
    {
        daoSession = daosession;
        DBMomentsItemsDao dbmomentsitemsdao;
        if(daosession != null)
        {
            dbmomentsitemsdao = daosession.getDBMomentsItemsDao();
        } else
        {
            dbmomentsitemsdao = null;
        }
        myDao = dbmomentsitemsdao;
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

    public Long getId()
    {
        return id;
    }

    public DBMediaItem getItem()
    {
        Long long1 = itemId;
        if(!(item__resolvedKey != null && item__resolvedKey.equals(long1))) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DBMediaItem dbmediaitem = (DBMediaItem) daoSession.getDBMediaItemDao().load(long1);
            synchronized (this) {
                item = dbmediaitem;
                item__resolvedKey = long1;
            }
        }
        return item;
    }

    public Long getItemId()
    {
        return itemId;
    }

    public Long getMomentId()
    {
        return momentId;
    }

    public void refresh()
    {
        if(myDao == null)
        {
            throw new DaoException("Entity is detached from DAO context");
        } else
        {
            myDao.refresh(this);
            return;
        }
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setItem(DBMediaItem dbmediaitem)
    {
        synchronized (this) {
            item = dbmediaitem;
            Long long1;
            if(dbmediaitem != null)
                long1 = dbmediaitem.getId();
            else
                long1 = null;
            synchronized (this) {
                itemId = long1;
                item__resolvedKey = itemId;
            }
        }
    }

    public void setItemId(Long long1)
    {
        itemId = long1;
    }

    public void setMomentId(Long long1)
    {
        momentId = long1;
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

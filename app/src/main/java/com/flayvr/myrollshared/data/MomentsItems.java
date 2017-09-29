package com.flayvr.myrollshared.data;

import de.greenrobot.dao.DaoException;

public class MomentsItems
{

    private DaoSession daoSession;
    private Long id;
    private MediaItem item;
    private Long itemId;
    private Long item__resolvedKey;
    private Moment moment;
    private Long momentId;
    private Long moment__resolvedKey;
    private MomentsItemsDao myDao;
    private Moment tempMoment;

    public MomentsItems()
    {
    }

    public MomentsItems(Long long1)
    {
        id = long1;
    }

    public MomentsItems(Long long1, Long long2, Long long3)
    {
        id = long1;
        momentId = long2;
        itemId = long3;
    }

    public void __setDaoSession(DaoSession daosession)
    {
        daoSession = daosession;
        MomentsItemsDao momentsitemsdao;
        if(daosession != null)
        {
            momentsitemsdao = daosession.getMomentsItemsDao();
        } else
        {
            momentsitemsdao = null;
        }
        myDao = momentsitemsdao;
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

    public MediaItem getItem()
    {
        Long long1 = itemId;
        if(!(item__resolvedKey != null && item__resolvedKey.equals(long1))) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MediaItem mediaitem = (MediaItem) daoSession.getMediaItemDao().load(long1);
            synchronized (this) {
                item = mediaitem;
                item__resolvedKey = long1;
            }
        }
        return item;
    }

    public Long getItemId()
    {
        return itemId;
    }

    public Moment getMoment()
    {
        Long long1 = momentId;
        if(!(moment__resolvedKey != null && moment__resolvedKey.equals(long1))) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            Moment moment1 = (Moment) daoSession.getMomentDao().load(long1);
            synchronized (this) {
                moment = moment1;
                moment__resolvedKey = long1;
            }
        }
        return moment;
    }

    public Long getMomentId()
    {
        return momentId;
    }

    public Moment getTempMoment()
    {
        return tempMoment;
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

    public void setItem(MediaItem mediaitem)
    {
        synchronized (this) {
            item = mediaitem;
            Long long1;
            if(mediaitem != null) {
                long1 = mediaitem.getId();
            } else
                long1 = null;
            itemId = long1;
            item__resolvedKey = itemId;
        }
    }

    public void setItemId(Long long1)
    {
        itemId = long1;
    }

    public void setMoment(Moment moment1)
    {
        synchronized (this) {
            moment = moment1;
            Long long1;
            if(moment1 != null)
                long1 = moment1.getId();
            else
                long1 = null;
            momentId = long1;
            moment__resolvedKey = momentId;
        }
    }

    public void setMomentId(Long long1)
    {
        momentId = long1;
    }

    public void setTempMoment(Moment moment1)
    {
        tempMoment = moment1;
    }

    public void update()
    {
        if(myDao == null)
        {
            throw new DaoException("Entity is detached from DAO context");
        } else
        {
            myDao.update(this);
        }
    }
}

package com.flayvr.myrollshared.data;

import de.greenrobot.dao.DaoException;
import java.util.Date;

public class Interaction
{

    private DaoSession daoSession;
    private Long id;
    private Date interactionDate;
    private Integer interactionType;
    private Long itemId;
    private MediaItem mediaItem;
    private Long mediaItem__resolvedKey;
    private InteractionDao myDao;

    public Interaction()
    {
    }

    public Interaction(Long long1)
    {
        id = long1;
    }

    public Interaction(Long long1, Integer integer, Date date, Long long2)
    {
        id = long1;
        interactionType = integer;
        interactionDate = date;
        itemId = long2;
    }

    public void __setDaoSession(DaoSession daosession)
    {
        daoSession = daosession;
        InteractionDao interactiondao;
        if(daosession != null)
        {
            interactiondao = daosession.getInteractionDao();
        } else
        {
            interactiondao = null;
        }
        myDao = interactiondao;
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

    public Date getInteractionDate()
    {
        return interactionDate;
    }

    public Integer getInteractionType()
    {
        return interactionType;
    }

    public Long getItemId()
    {
        return itemId;
    }

    public MediaItem getMediaItem()
    {
        Long long1 = itemId;
        if(!(mediaItem__resolvedKey != null && mediaItem__resolvedKey.equals(long1))) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MediaItem mediaitem = (MediaItem) daoSession.getMediaItemDao().load(long1);
            synchronized (this) {
                mediaItem = mediaitem;
                mediaItem__resolvedKey = long1;
            }
        }
        return mediaItem;
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

    public void setInteractionDate(Date date)
    {
        interactionDate = date;
    }

    public void setInteractionType(Integer integer)
    {
        interactionType = integer;
    }

    public void setItemId(Long long1)
    {
        itemId = long1;
    }

    public void setMediaItem(MediaItem mediaitem)
    {
        synchronized (this) {
            mediaItem = mediaitem;
            Long long1;
            if(mediaitem != null)
                long1 = mediaitem.getId();
            else
                long1 = null;
            itemId = long1;
            mediaItem__resolvedKey = itemId;
        }
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

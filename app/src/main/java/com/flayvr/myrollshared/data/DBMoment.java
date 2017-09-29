package com.flayvr.myrollshared.data;

import com.flayvr.myrollshared.oldclasses.ImageInteraction;
import com.flayvr.myrollshared.oldclasses.MediaGroupDate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import de.greenrobot.dao.DaoException;
import java.lang.reflect.Type;
import java.util.*;

public class DBMoment
{

    public static Gson gson = (new GsonBuilder()).registerTypeHierarchyAdapter(MediaGroupDate.class, new com.flayvr.myrollshared.oldclasses.MediaGroupDate.MediaGroupDateJsonAdapater()).create();
    static Type interType = (new TypeToken<Map<Long, List<ImageInteraction>>>(){}).getType();
    static Type itemType = (new TypeToken<Set<Long>>(){}).getType();
    private DBMediaItem cover;
    private Long coverId;
    private Long cover__resolvedKey;
    private DaoSession daoSession;
    MediaGroupDate date;
    private String dateJson;
    private String folderId;
    private Long id;
    private Map interactions;
    private String interactionsJson;
    private Boolean isFavorite;
    private Boolean isHidden;
    private Boolean isMerged;
    private Boolean isMuted;
    private Boolean isTitleFromCalendar;
    private Boolean isTrip;
    private String location;
    private List momentsItems;
    private DBMomentDao myDao;
    private Boolean newerItemAdded;
    private Set originalItems;
    private String originalItemsJson;
    private String title;
    private Integer watchCount;

    public DBMoment()
    {
    }

    public DBMoment(Long long1)
    {
        id = long1;
    }

    public DBMoment(Long long1, String s, String s1, Boolean boolean1, Boolean boolean2, Boolean boolean3, Boolean boolean4, 
            Boolean boolean5, Boolean boolean6, String s2, String s3, String s4, Boolean boolean7, Integer integer, 
            Long long2, String s5)
    {
        id = long1;
        title = s;
        location = s1;
        isMuted = boolean1;
        newerItemAdded = boolean2;
        isHidden = boolean3;
        isTrip = boolean4;
        isMerged = boolean5;
        isFavorite = boolean6;
        originalItemsJson = s2;
        dateJson = s3;
        interactionsJson = s4;
        isTitleFromCalendar = boolean7;
        watchCount = integer;
        coverId = long2;
        folderId = s5;
    }

    public void __setDaoSession(DaoSession daosession)
    {
        daoSession = daosession;
        DBMomentDao dbmomentdao;
        if(daosession != null)
        {
            dbmomentdao = daosession.getDBMomentDao();
        } else
        {
            dbmomentdao = null;
        }
        myDao = dbmomentdao;
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

    public DBMediaItem getCover()
    {
        Long long1 = coverId;
        if(!(cover__resolvedKey != null && cover__resolvedKey.equals(long1))) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DBMediaItem dbmediaitem = (DBMediaItem) daoSession.getDBMediaItemDao().load(long1);
            synchronized (this) {
                cover = dbmediaitem;
                cover__resolvedKey = long1;
            }
        }
        return cover;
    }

    public Long getCoverId()
    {
        return coverId;
    }

    public MediaGroupDate getDate()
    {
        if(date == null)
        {
            String s = getDateJson();
            date = (MediaGroupDate)gson.fromJson(s, MediaGroupDate.class);
        }
        return date;
    }

    public String getDateJson()
    {
        return dateJson;
    }

    public String getFolderId()
    {
        return folderId;
    }

    public Long getId()
    {
        return id;
    }

    public Map getInteractions()
    {
        if(interactions == null)
        {
            interactions = (Map)gson.fromJson(getInteractionsJson(), interType);
        }
        return interactions;
    }

    public String getInteractionsJson()
    {
        return interactionsJson;
    }

    public Boolean getIsFavorite()
    {
        return isFavorite;
    }

    public Boolean getIsHidden()
    {
        return isHidden;
    }

    public Boolean getIsMerged()
    {
        return isMerged;
    }

    public Boolean getIsMuted()
    {
        return isMuted;
    }

    public Boolean getIsTitleFromCalendar()
    {
        return isTitleFromCalendar;
    }

    public Boolean getIsTrip()
    {
        return isTrip;
    }

    public List getItemsIds()
    {
        LinkedList linkedlist = new LinkedList();
        for(Iterator iterator = getMomentsItems().iterator(); iterator.hasNext(); linkedlist.add(((DBMomentsItems)iterator.next()).getItemId())) { }
        return linkedlist;
    }

    public String getLocation()
    {
        return location;
    }

    public List getMomentsItems()
    {
        if(momentsItems == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            List list = daoSession.getDBMomentsItemsDao()._queryDBMoment_MomentsItems(id);
            synchronized (this) {
                if (momentsItems == null) {
                    momentsItems = list;
                }
            }
        }
        return momentsItems;
    }

    public Boolean getNewerItemAdded()
    {
        return newerItemAdded;
    }

    public Set getOriginalItems()
    {
        if(originalItems == null)
        {
            originalItems = (Set)gson.fromJson(getOriginalItemsJson(), itemType);
        }
        return originalItems;
    }

    public String getOriginalItemsJson()
    {
        return originalItemsJson;
    }

    public String getTitle()
    {
        return title;
    }

    public Integer getWatchCount()
    {
        return watchCount;
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

    public void resetMomentsItems()
    {
        synchronized (this) {
            momentsItems = null;
        }
    }

    public void setCover(DBMediaItem dbmediaitem)
    {
        synchronized (this) {
            cover = dbmediaitem;
            Long long1;
            if(dbmediaitem != null)
                long1 = dbmediaitem.getId();
            else
                long1 = null;
            coverId = long1;
            cover__resolvedKey = coverId;
        }
    }

    public void setCoverId(Long long1)
    {
        coverId = long1;
    }

    public void setDate(MediaGroupDate mediagroupdate)
    {
        date = mediagroupdate;
        setDateJson(gson.toJson(mediagroupdate));
    }

    public void setDateJson(String s)
    {
        dateJson = s;
    }

    public void setFolderId(String s)
    {
        folderId = s;
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setInteractions(Map map)
    {
        interactions = map;
        setInteractionsJson(gson.toJson(map));
    }

    public void setInteractionsJson(String s)
    {
        interactionsJson = s;
    }

    public void setIsFavorite(Boolean boolean1)
    {
        isFavorite = boolean1;
    }

    public void setIsHidden(Boolean boolean1)
    {
        isHidden = boolean1;
    }

    public void setIsMerged(Boolean boolean1)
    {
        isMerged = boolean1;
    }

    public void setIsMuted(Boolean boolean1)
    {
        isMuted = boolean1;
    }

    public void setIsTitleFromCalendar(Boolean boolean1)
    {
        isTitleFromCalendar = boolean1;
    }

    public void setIsTrip(Boolean boolean1)
    {
        isTrip = boolean1;
    }

    public void setLocation(String s)
    {
        location = s;
    }

    public void setNewerItemAdded(Boolean boolean1)
    {
        newerItemAdded = boolean1;
    }

    public void setOriginalItems(Set set)
    {
        originalItems = set;
        setOriginalItemsJson(gson.toJson(set));
    }

    public void setOriginalItemsJson(String s)
    {
        originalItemsJson = s;
    }

    public void setTitle(String s)
    {
        title = s;
    }

    public void setWatchCount(Integer integer)
    {
        watchCount = integer;
    }

    public void update()
    {
        if(myDao == null)
        {
            throw new DaoException("Entity is detached from DAO context");
        } else
        {
            myDao.update(this);
            return;
        }
    }


    private class _cls1 extends TypeToken
    {

        _cls1()
        {
        }
    }


    private class _cls2 extends TypeToken
    {

        _cls2()
        {
        }
    }

}

package com.flayvr.myrollshared.data;

import de.greenrobot.dao.DaoException;
import java.util.*;

public class DuplicatesSet
{

    private DaoSession daoSession;
    private List duplicatesSetPhotos;
    private Long id;
    private DuplicatesSetDao myDao;
    private Boolean wasAnalyzedByGD;

    public DuplicatesSet()
    {
    }

    public DuplicatesSet(Long long1)
    {
        id = long1;
    }

    public DuplicatesSet(Long long1, Boolean boolean1)
    {
        id = long1;
        wasAnalyzedByGD = boolean1;
    }

    public void __setDaoSession(DaoSession daosession)
    {
        daoSession = daosession;
        DuplicatesSetDao duplicatessetdao;
        if(daosession != null)
        {
            duplicatessetdao = daosession.getDuplicatesSetDao();
        } else
        {
            duplicatessetdao = null;
        }
        myDao = duplicatessetdao;
    }

    public void delete()
    {
        if(myDao == null)
        {
            throw new DaoException("Entity is detached from DAO context");
        } else
        {
            myDao.delete(this);
        }
    }

    public MediaItem getBestPhotoOfSet()
    {
        List list = getDuplicatesSetPhotos();
        MediaItem mediaitem = null;
        double d = -1D;
        Iterator iterator = list.iterator();
        while(iterator.hasNext()) 
        {
            MediaItem mediaitem1 = ((DuplicatesSetsToPhotos)iterator.next()).getPhoto();
            MediaItem mediaitem2;
            double d2;
            double d3;
            if(mediaitem1 != null && (mediaitem == null || mediaitem1.getScore() != null && mediaitem1.getScore().doubleValue() > d))
            {
                if(mediaitem1.getScore() != null)
                {
                    d2 = mediaitem1.getScore().doubleValue();
                    mediaitem2 = mediaitem1;
                } else
                {
                    d2 = d;
                    mediaitem2 = mediaitem1;
                }
            } else
            {
                double d1 = d;
                mediaitem2 = mediaitem;
                d2 = d1;
            }
            d3 = d2;
            mediaitem = mediaitem2;
            d = d3;
        }
        return mediaitem;
    }

    public List getDuplicatesSetPhotos()
    {
        if(duplicatesSetPhotos == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            List list = daoSession.getDuplicatesSetsToPhotosDao()._queryDuplicatesSet_DuplicatesSetPhotos(id);
            synchronized (this) {
                if (duplicatesSetPhotos == null) {
                    duplicatesSetPhotos = list;
                }
            }
        }
        return duplicatesSetPhotos;
    }

    public Long getId()
    {
        return id;
    }

    public List getSortedDuplicatesSetPhotos()
    {
        List list = getDuplicatesSetPhotos();
        ArrayList arraylist = new ArrayList();
        for(Iterator iterator = list.iterator(); iterator.hasNext(); arraylist.add(((DuplicatesSetsToPhotos)iterator.next()).getPhoto())) { }
        Collections.sort(arraylist, new Comparator<MediaItem>(){
            @Override
            public int compare(MediaItem mediaItem, MediaItem t1) {
                if(!mediaItem.getDate().equals(t1.getDate()))
                    return mediaItem.getDate().compareTo(t1.getDate());
                else
                    return mediaItem.getId().compareTo(t1.getId());
            }
        });
        return arraylist;
    }

    public Boolean getWasAnalyzedByGD()
    {
        return wasAnalyzedByGD;
    }

    public void refresh()
    {
        if(myDao == null)
        {
            throw new DaoException("Entity is detached from DAO context");
        } else
            myDao.refresh(this);
    }

    public void resetDuplicatesSetPhotos()
    {
        synchronized (this) {
            duplicatesSetPhotos = null;
        }
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setWasAnalyzedByGD(Boolean boolean1)
    {
        wasAnalyzedByGD = boolean1;
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

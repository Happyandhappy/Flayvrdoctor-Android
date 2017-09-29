package com.flayvr.myrollshared.data;

import de.greenrobot.dao.DaoException;

public class DuplicatesSetsToPhotos
{
    private DaoSession daoSession;
    private DuplicatesSet duplicatesSet;
    private Long duplicatesSetId;
    private Long duplicatesSet__resolvedKey;
    private Long id;
    private DuplicatesSetsToPhotosDao myDao;
    private MediaItem photo;
    private Long photoId;
    private Long photo__resolvedKey;

    public DuplicatesSetsToPhotos()
    {
    }

    public DuplicatesSetsToPhotos(Long long1)
    {
        id = long1;
    }

    public DuplicatesSetsToPhotos(Long long1, Long long2, Long long3)
    {
        id = long1;
        duplicatesSetId = long2;
        photoId = long3;
    }

    public void __setDaoSession(DaoSession daosession)
    {
        daoSession = daosession;
        DuplicatesSetsToPhotosDao duplicatessetstophotosdao;
        if(daosession != null)
        {
            duplicatessetstophotosdao = daosession.getDuplicatesSetsToPhotosDao();
        } else
        {
            duplicatessetstophotosdao = null;
        }
        myDao = duplicatessetstophotosdao;
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

    public DuplicatesSet getDuplicatesSet()
    {
        Long long1 = duplicatesSetId;
        if(!(duplicatesSet__resolvedKey != null && duplicatesSet__resolvedKey.equals(long1))) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DuplicatesSet duplicatesset = (DuplicatesSet) daoSession.getDuplicatesSetDao().load(long1);
            synchronized (this) {
                duplicatesSet = duplicatesset;
                duplicatesSet__resolvedKey = long1;
            }
        }
        return duplicatesSet;
    }

    public Long getDuplicatesSetId()
    {
        return duplicatesSetId;
    }

    public Long getId()
    {
        return id;
    }

    public MediaItem getPhoto()
    {
        Long long1 = photoId;
        if(!(photo__resolvedKey != null && photo__resolvedKey.equals(long1))) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MediaItem mediaitem = (MediaItem) daoSession.getMediaItemDao().load(long1);
            synchronized (this) {
                photo = mediaitem;
                photo__resolvedKey = long1;
            }
        }
        return photo;
    }

    public Long getPhotoId()
    {
        return photoId;
    }

    public void refresh()
    {
        if(myDao == null)
        {
            throw new DaoException("Entity is detached from DAO context");
        } else
            myDao.refresh(this);
    }

    public void setDuplicatesSet(DuplicatesSet duplicatesset)
    {
        synchronized (this){
            duplicatesSet = duplicatesset;
            Long long1;
            if(duplicatesset != null)
                long1 = duplicatesset.getId();
            else
                long1 = null;
            duplicatesSetId = long1;
            duplicatesSet__resolvedKey = duplicatesSetId;
        }
    }

    public void setDuplicatesSetId(Long long1)
    {
        duplicatesSetId = long1;
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setPhoto(MediaItem mediaitem)
    {
        synchronized (this){
            photo = mediaitem;
            Long long1;
            if(mediaitem != null)
                long1 = mediaitem.getId();
            else
                long1 = null;
            photoId = long1;
            photo__resolvedKey = photoId;
        }
    }

    public void setPhotoId(Long long1)
    {
        photoId = long1;
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
}

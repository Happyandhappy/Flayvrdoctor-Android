package com.flayvr.myrollshared.data;

import de.greenrobot.dao.DaoException;

public class ClassifierRulesToPhotos
{

    private ClassifierRule classifierRule;
    private Long classifierRuleId;
    private Long classifierRule__resolvedKey;
    private DaoSession daoSession;
    private Long id;
    private ClassifierRulesToPhotosDao myDao;
    private MediaItem photo;
    private Long photoId;
    private Long photo__resolvedKey;

    public ClassifierRulesToPhotos()
    {
    }

    public ClassifierRulesToPhotos(Long long1)
    {
        id = long1;
    }

    public ClassifierRulesToPhotos(Long long1, Long long2, Long long3)
    {
        id = long1;
        classifierRuleId = long2;
        photoId = long3;
    }

    public void __setDaoSession(DaoSession daosession)
    {
        daoSession = daosession;
        ClassifierRulesToPhotosDao classifierrulestophotosdao;
        if(daosession != null)
        {
            classifierrulestophotosdao = daosession.getClassifierRulesToPhotosDao();
        } else
        {
            classifierrulestophotosdao = null;
        }
        myDao = classifierrulestophotosdao;
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

    public ClassifierRule getClassifierRule()
    {
        Long long1 = classifierRuleId;
        if(!(classifierRule__resolvedKey != null && classifierRule__resolvedKey.equals(long1))) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ClassifierRule classifierrule = (ClassifierRule) daoSession.getClassifierRuleDao().load(long1);
            synchronized (this) {
                classifierRule = classifierrule;
                classifierRule__resolvedKey = long1;
            }
        }
        return classifierRule;
    }

    public Long getClassifierRuleId()
    {
        return classifierRuleId;
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
        {
            myDao.refresh(this);
            return;
        }
    }

    public void setClassifierRule(ClassifierRule classifierrule)
    {
        synchronized (this) {
            classifierRule = classifierrule;
            Long long1;
            if(classifierrule != null)
                long1 = classifierrule.getId();
            else
                long1 = null;
            classifierRuleId = long1;
            classifierRule__resolvedKey = classifierRuleId;
        }
    }

    public void setClassifierRuleId(Long long1)
    {
        classifierRuleId = long1;
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setPhoto(MediaItem mediaitem)
    {
        synchronized (this) {
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
            throw new DaoException("Entity is detached from DAO context");
        else
            myDao.update(this);
    }


}

package com.flayvr.myrollshared.data;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.*;
import java.util.List;

public class Folder
{

    public static final int TYPE_LOCAL = 1;
    public static final int TYPE_MYROLL_CLOUD = 3;
    public static final int TYPE_PICASA = 2;
    private DaoSession daoSession;
    private String folderPath;
    private Long folderSourceId;
    private Long id;
    private Boolean isCamera;
    private Boolean isHidden;
    private Boolean isUserCreated;
    private List mediaItemList;
    private List momentList;
    private FolderDao myDao;
    private String name;
    private Integer source;
    private String userType;

    public Folder()
    {
    }

    public Folder(Long long1)
    {
        id = long1;
    }

    public Folder(Long long1, String s, String s1, Boolean boolean1, Integer integer, Long long2, Boolean boolean2, 
            Boolean boolean3, String s2)
    {
        id = long1;
        name = s;
        userType = s1;
        isCamera = boolean1;
        source = integer;
        folderSourceId = long2;
        isUserCreated = boolean2;
        isHidden = boolean3;
        folderPath = s2;
    }

    public void __setDaoSession(DaoSession daosession)
    {
        daoSession = daosession;
        FolderDao folderdao;
        if(daosession != null)
        {
            folderdao = daosession.getFolderDao();
        } else
        {
            folderdao = null;
        }
        myDao = folderdao;
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

    public String getFolderPath()
    {
        return folderPath;
    }

    public Long getFolderSourceId()
    {
        return folderSourceId;
    }

    public Long getId()
    {
        return id;
    }

    public Boolean getIsCamera()
    {
        return isCamera;
    }

    public Boolean getIsHidden()
    {
        return isHidden;
    }

    public Boolean getIsUserCreated()
    {
        return isUserCreated;
    }

    public List getMediaItemList()
    {
        if(mediaItemList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            List list = daoSession.getMediaItemDao()._queryFolder_MediaItemList(id);
            synchronized (this) {
                if (mediaItemList == null) {
                    mediaItemList = list;
                }
            }
        }
        return mediaItemList;
    }

    public List getMomentList()
    {
        if(momentList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            List list = daoSession.getMomentDao()._queryFolder_MomentList(id);
            synchronized (this) {
                if (momentList == null) {
                    momentList = list;
                }
            }
        }
        return momentList;
    }

    public String getName()
    {
        return name;
    }

    public Long getNotDeletedMediaItemCount()
    {
        QueryBuilder querybuilder = daoSession.getMediaItemDao().queryBuilder();
        querybuilder.where(MediaItemDao.Properties.FolderId.eq(id), new WhereCondition[0]);
        querybuilder.where(MediaItemDao.Properties.WasDeleted.isNull(), new WhereCondition[0]);
        return Long.valueOf(querybuilder.count());
    }

    public List getNotDeletedMediaItemList()
    {
        QueryBuilder querybuilder = daoSession.getMediaItemDao().queryBuilder();
        querybuilder.where(MediaItemDao.Properties.FolderId.eq(id), new WhereCondition[0]);
        querybuilder.where(MediaItemDao.Properties.WasDeleted.isNull(), new WhereCondition[0]);
        Property aproperty[] = new Property[1];
        aproperty[0] = MediaItemDao.Properties.Date;
        querybuilder.orderDesc(aproperty);
        return querybuilder.build().forCurrentThread().list();
    }

    public Long getNotDeletedPhotoCount()
    {
        QueryBuilder querybuilder = daoSession.getMediaItemDao().queryBuilder();
        querybuilder.where(MediaItemDao.Properties.FolderId.eq(id), new WhereCondition[0]);
        querybuilder.where(MediaItemDao.Properties.WasDeleted.isNull(), new WhereCondition[0]);
        querybuilder.where(MediaItemDao.Properties.Type.eq(Integer.valueOf(1)), new WhereCondition[0]);
        return Long.valueOf(querybuilder.count());
    }

    public Long getNotDeletedVideoCount()
    {
        QueryBuilder querybuilder = daoSession.getMediaItemDao().queryBuilder();
        querybuilder.where(MediaItemDao.Properties.FolderId.eq(id), new WhereCondition[0]);
        querybuilder.where(MediaItemDao.Properties.WasDeleted.isNull(), new WhereCondition[0]);
        querybuilder.where(MediaItemDao.Properties.Type.eq(Integer.valueOf(2)), new WhereCondition[0]);
        return Long.valueOf(querybuilder.count());
    }

    public Integer getSource()
    {
        return source;
    }

    public String getUserType()
    {
        return userType;
    }

    public UserType getUserTypeEnum()
    {
        if(userType == null)
        {
            return null;
        } else
        {
            return UserType.valueOf(userType);
        }
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

    public void resetMediaItemList()
    {
        synchronized (this) {
            mediaItemList = null;
        }
    }

    public void resetMomentList()
    {
        synchronized (this) {
            momentList = null;
        }
    }

    public void setFolderPath(String s)
    {
        folderPath = s;
    }

    public void setFolderSourceId(Long long1)
    {
        folderSourceId = long1;
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setIsCamera(Boolean boolean1)
    {
        isCamera = boolean1;
    }

    public void setIsHidden(Boolean boolean1)
    {
        isHidden = boolean1;
    }

    public void setIsUserCreated(Boolean boolean1)
    {
        isUserCreated = boolean1;
    }

    public void setName(String s)
    {
        name = s;
    }

    public void setSource(Integer integer)
    {
        source = integer;
    }

    public void setUserType(String s)
    {
        userType = s;
    }

    public void setUserTypeEnum(UserType usertype)
    {
        if(usertype == null)
        {
            userType = null;
        }
        userType = usertype.name();
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

    public enum UserType
    {
        FEW("FEW", 0),
        NORMAL("NORMAL", 1),
        LOT("LOT", 2);

        private UserType(String s, int i)
        {
        }
    }
}

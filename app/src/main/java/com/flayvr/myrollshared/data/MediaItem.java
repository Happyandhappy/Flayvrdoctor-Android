package com.flayvr.myrollshared.data;

import android.graphics.BitmapFactory;
import android.location.Location;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.imageloading.ImagesDiskCache;
import de.greenrobot.dao.DaoException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class MediaItem
{

    public static final int TYPE_PHOTO = 1;
    public static final int TYPE_VIDEO = 2;
    private Long androidId;
    private Double blurry;
    private Float centerX;
    private Float centerY;
    private Boolean checkedThumbnail;
    private Double color;
    private Boolean cvRan;
    private DaoSession daoSession;
    private Double dark;
    private Date date;
    private Long duration;
    private Integer facesCount;
    private String facesJson;
    private Long fileSizeBytes;
    private Folder folder;
    private Long folderId;
    private Long folder__resolvedKey;
    private Integer height;
    private Long id;
    private Double interactionScore;
    private Boolean isBad;
    private Boolean isForReview;
    private List itemMoments;
    private Date lastTimeClassified;
    private Double latitude;
    private Double longitude;
    private MediaItemDao myDao;
    private Integer orientation;
    private String path;
    private List photoClassifierRules;
    private List photoDuplicatesSets;
    private Float prop;
    private Double score;
    private Long serverId;
    private Double similarityScoreToNext;
    private Double similarityScoreToPrev;
    private Integer source;
    private String thumbnail;
    private Integer type;
    private Boolean wasAnalyzedByGD;
    private Boolean wasAnalyzedForDuplicates;
    private Boolean wasClustered;
    private Boolean wasDeleted;
    private Boolean wasDeletedByUser;
    private Boolean wasKeptByUser;
    private Boolean wasMinimizedByUser;
    private Integer width;

    public MediaItem()
    {
    }

    public MediaItem(Long long1)
    {
        id = long1;
    }

    public MediaItem(Long long1, Long long2, Integer integer, String s, String s1, Integer integer1, Date date1, 
            Double double1, Double double2, Double double3, Double double4, Double double5, Float float1, Boolean boolean1, 
            Float float2, Float float3, Integer integer2, String s2, Boolean boolean2, Long long3, Double double6, 
            Double double7, Boolean boolean3, Double double8, Boolean boolean4, Boolean boolean5, Date date2, Boolean boolean6, 
            Boolean boolean7, Boolean boolean8, Boolean boolean9, Boolean boolean10, Long long4, Integer integer3, Integer integer4, 
            Integer integer5, Boolean boolean11, Long long5, Long long6, Double double9)
    {
        id = long1;
        androidId = long2;
        type = integer;
        path = s;
        thumbnail = s1;
        orientation = integer1;
        date = date1;
        latitude = double1;
        longitude = double2;
        blurry = double3;
        color = double4;
        dark = double5;
        prop = float1;
        cvRan = boolean1;
        centerX = float2;
        centerY = float3;
        facesCount = integer2;
        facesJson = s2;
        checkedThumbnail = boolean2;
        duration = long3;
        similarityScoreToPrev = double6;
        similarityScoreToNext = double7;
        wasAnalyzedForDuplicates = boolean3;
        score = double8;
        isBad = boolean4;
        isForReview = boolean5;
        lastTimeClassified = date2;
        wasDeleted = boolean6;
        wasClustered = boolean7;
        wasAnalyzedByGD = boolean8;
        wasKeptByUser = boolean9;
        wasDeletedByUser = boolean10;
        fileSizeBytes = long4;
        source = integer3;
        width = integer4;
        height = integer5;
        wasMinimizedByUser = boolean11;
        serverId = long5;
        folderId = long6;
        interactionScore = double9;
    }

    private void calcFullImageSize()
    {
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(getPath(), options);
        if(options.outWidth == 0)
        {
            width = Integer.valueOf(options.outWidth);
        }
        if(options.outHeight == 0)
        {
            height = Integer.valueOf(options.outHeight);
        }
    }

    private void calcImageSize()
    {
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(getThumbnail(), options);
        if(options.outWidth == 0 || options.outHeight == 0)
        {
            prop = Float.valueOf(1.0F);
            return;
        }
        if(orientation == null || orientation.intValue() % 180 == 0)
        {
            prop = Float.valueOf((1.0F * (float)options.outWidth) / (float)options.outHeight);
            return;
        } else
        {
            prop = Float.valueOf((1.0F * (float)options.outHeight) / (float)options.outWidth);
            return;
        }
    }

    public void __setDaoSession(DaoSession daosession)
    {
        daoSession = daosession;
        MediaItemDao mediaitemdao;
        if(daosession != null)
        {
            mediaitemdao = daosession.getMediaItemDao();
        } else
        {
            mediaitemdao = null;
        }
        myDao = mediaitemdao;
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

    public boolean equals(Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(obj == null || getClass() != obj.getClass())
        {
            return false;
        } else
        {
            MediaItem mediaitem = (MediaItem)obj;
            return id.equals(mediaitem.id);
        }
    }

    public Long getAndroidId()
    {
        return androidId;
    }

    public Double getBlurry()
    {
        return blurry;
    }

    public Float getCenterX()
    {
        return centerX;
    }

    public Float getCenterY()
    {
        return centerY;
    }

    public Boolean getCheckedThumbnail()
    {
        return checkedThumbnail;
    }

    public Double getColor()
    {
        return color;
    }

    public Boolean getCvRan()
    {
        return cvRan;
    }

    public Double getDark()
    {
        return dark;
    }

    public Date getDate()
    {
        return date;
    }

    public Long getDuration()
    {
        return duration;
    }

    public Integer getFacesCount()
    {
        return facesCount;
    }

    public String getFacesJson()
    {
        return facesJson;
    }

    public Long getFileSizeBytes()
    {
        return fileSizeBytes;
    }

    public Long getFileSizeBytesSafe()
    {
        long l;
        if(fileSizeBytes == null)
        {
            l = 0L;
        } else
        {
            l = fileSizeBytes.longValue();
        }
        return Long.valueOf(l);
    }

    public Folder getFolder()
    {
        Long long1 = folderId;
        if(!(folder__resolvedKey != null && folder__resolvedKey.equals(long1))) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            Folder folder1 = (Folder) daoSession.getFolderDao().load(long1);
            synchronized (this) {
                folder = folder1;
                folder__resolvedKey = long1;
            }
        }
        return folder;
    }

    public Long getFolderId()
    {
        return folderId;
    }

    public String getFormatedDuration()
    {
        int i = (int)(duration.longValue() / 1000L);
        Object aobj[] = new Object[2];
        aobj[0] = Integer.valueOf(i / 60);
        aobj[1] = Integer.valueOf(i % 60);
        return String.format("%d:%02d", aobj);
    }

    public Integer getHeight()
    {
        return height;
    }

    public Long getId()
    {
        return id;
    }

    public Double getIntegratedScore()
    {
        Double double3;
label0:
        {
            Double double1 = score;
            Double double2 = null;
            if(double1 != null)
            {
                double3 = score;
                if(interactionScore != null)
                {
                    double3 = Double.valueOf(double3.doubleValue() + interactionScore.doubleValue());
                }
                Folder folder1 = getFolder();
                if(folder1.getIsCamera() == null || !folder1.getIsCamera().booleanValue())
                {
                    break label0;
                }
                double2 = Double.valueOf(0.20000000000000001D + double3.doubleValue());
            }
            return double2;
        }
        return Double.valueOf(double3.doubleValue() - 0.10000000000000001D);
    }

    public Double getInteractionScore()
    {
        return interactionScore;
    }

    public Boolean getIsBad()
    {
        return isBad;
    }

    public Boolean getIsForReview()
    {
        return isForReview;
    }

    public List getItemMoments()
    {
        if(itemMoments == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            List list = daoSession.getMomentsItemsDao()._queryMediaItem_ItemMoments(id);
            synchronized (this) {
                if (itemMoments == null) {
                    itemMoments = list;
                }
            }
        }
        return itemMoments;
    }

    public Date getLastTimeClassified()
    {
        return lastTimeClassified;
    }

    public Double getLatitude()
    {
        return latitude;
    }

    public Location getLocation()
    {
        if(getLongitude().doubleValue() == 0.0D || getLatitude().doubleValue() == 0.0D)
        {
            return null;
        } else
        {
            Location location = new Location((new StringBuilder()).append(getId()).append("").toString());
            location.setLongitude(getLongitude().doubleValue());
            location.setLatitude(getLatitude().doubleValue());
            return location;
        }
    }

    public Double getLongitude()
    {
        return longitude;
    }

    public List getMoments()
    {
        List list = getItemMoments();
        ArrayList arraylist = new ArrayList();
        if(list != null && list.size() >= 0)
        {
            for(Iterator iterator = list.iterator(); iterator.hasNext(); arraylist.add(((MomentsItems)iterator.next()).getMoment())) { }
        }
        return arraylist;
    }

    public Integer getNullableHeight()
    {
        if(height == null)
        {
            calcFullImageSize();
            FlayvrApplication.runAction(new Runnable() {
                @Override
                public void run() {
                    update();
                }
            });
        }
        return height;
    }

    public Float getNullableProp()
    {
        if(prop == null)
        {
            if(thumbnail == null)
            {
                return Float.valueOf(1.0F);
            }
            calcImageSize();
            FlayvrApplication.runAction(new Runnable() {
                @Override
                public void run() {
                    update();
                }
            });
        }
        return prop;
    }

    public Integer getNullableWidth()
    {
        if(width == null)
        {
            calcFullImageSize();
            FlayvrApplication.runAction(new Runnable() {
                @Override
                public void run() {
                    update();
                }
            });
        }
        return width;
    }

    public Integer getOrientation()
    {
        return orientation;
    }

    public String getPath()
    {
        return path;
    }

    public List getPhotoClassifierRules()
    {
        if(photoClassifierRules == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            List list = daoSession.getClassifierRulesToPhotosDao()._queryMediaItem_PhotoClassifierRules(id);
            synchronized (this) {
                if (photoClassifierRules == null) {
                    photoClassifierRules = list;
                }
            }
        }
        return photoClassifierRules;
    }

    public List getPhotoDuplicatesSets()
    {
        if(photoDuplicatesSets == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            List list = daoSession.getDuplicatesSetsToPhotosDao()._queryMediaItem_PhotoDuplicatesSets(id);
            synchronized (this) {
                if (photoDuplicatesSets == null) {
                    photoDuplicatesSets = list;
                }
            }
        }
        return photoDuplicatesSets;
    }

    public Float getProp()
    {
        return prop;
    }

    public Double getScore()
    {
        return score;
    }

    public Long getServerId()
    {
        return serverId;
    }

    public Double getSimilarityScoreToNext()
    {
        return similarityScoreToNext;
    }

    public Double getSimilarityScoreToPrev()
    {
        return similarityScoreToPrev;
    }

    public Integer getSource()
    {
        return source;
    }

    public InputStream getStream() throws FileNotFoundException {
        if(isCloudItem())
        {
            ImagesDiskCache imagesdiskcache = FlayvrApplication.getDiskCache();
            com.flayvr.myrollshared.imageloading.DiskLruCache.Snapshot snapshot = imagesdiskcache.get(this);
            if(snapshot == null)
            {
                imagesdiskcache.put(this);
                snapshot = imagesdiskcache.get(this);
                if(snapshot == null)
                {
                    return null;
                }
            }
            return snapshot.getInputStream(0);
        } else
            return new FileInputStream(getPath());
    }

    public String getThumbnail()
    {
        return thumbnail;
    }

    public Integer getType()
    {
        return type;
    }

    public Boolean getWasAnalyzedByGD()
    {
        return wasAnalyzedByGD;
    }

    public Boolean getWasAnalyzedForDuplicates()
    {
        return wasAnalyzedForDuplicates;
    }

    public Boolean getWasClustered()
    {
        return wasClustered;
    }

    public Boolean getWasDeleted()
    {
        return wasDeleted;
    }

    public Boolean getWasDeletedByUser()
    {
        return wasDeletedByUser;
    }

    public Boolean getWasKeptByUser()
    {
        return wasKeptByUser;
    }

    public Boolean getWasMinimizedByUser()
    {
        return wasMinimizedByUser;
    }

    public Integer getWidth()
    {
        return width;
    }

    public int hashCode()
    {
        return id.hashCode();
    }

    public boolean isCloudItem()
    {
        return getSource() != null && (getSource().intValue() == 2 || getSource().intValue() == 3);
    }

    public boolean isGif()
    {
        return getPath() != null && getPath().toLowerCase().endsWith("gif");
    }

    public boolean isHorizontal()
    {
        return getNullableProp().floatValue() >= 1.0F;
    }

    public void refresh()
    {
        if(myDao == null)
        {
            throw new DaoException("Entity is detached from DAO context");
        } else
        {
            myDao.refresh(this);
        }
    }

    public void resetItemMoments()
    {
        synchronized (this) {
            itemMoments = null;
        }
    }

    public void resetPhotoClassifierRules()
    {
        synchronized (this) {
            photoClassifierRules = null;
        }
    }

    public void resetPhotoDuplicatesSets()
    {
        synchronized (this) {
            photoDuplicatesSets = null;
        }
    }

    public void setAndroidId(Long long1)
    {
        androidId = long1;
    }

    public void setBlurry(Double double1)
    {
        blurry = double1;
    }

    public void setCenterX(Float float1)
    {
        centerX = float1;
    }

    public void setCenterY(Float float1)
    {
        centerY = float1;
    }

    public void setCheckedThumbnail(Boolean boolean1)
    {
        checkedThumbnail = boolean1;
    }

    public void setColor(Double double1)
    {
        color = double1;
    }

    public void setCvRan(Boolean boolean1)
    {
        cvRan = boolean1;
    }

    public void setDark(Double double1)
    {
        dark = double1;
    }

    public void setDate(Date date1)
    {
        date = date1;
    }

    public void setDuration(Long long1)
    {
        duration = long1;
    }

    public void setFacesCount(Integer integer)
    {
        facesCount = integer;
    }

    public void setFacesJson(String s)
    {
        facesJson = s;
    }

    public void setFileSizeBytes(Long long1)
    {
        fileSizeBytes = long1;
    }

    public void setFolder(Folder folder1)
    {
        synchronized (this) {
            folder = folder1;
            Long long1;
            if(folder1 != null)
                long1 = folder1.getId();
            else
                long1 = null;
            folderId = long1;
            folder__resolvedKey = folderId;
        }
    }

    public void setFolderId(Long long1)
    {
        folderId = long1;
    }

    public void setHeight(Integer integer)
    {
        height = integer;
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setInteractionScore(Double double1)
    {
        interactionScore = double1;
    }

    public void setIsBad(Boolean boolean1)
    {
        isBad = boolean1;
    }

    public void setIsForReview(Boolean boolean1)
    {
        isForReview = boolean1;
    }

    public void setLastTimeClassified(Date date1)
    {
        lastTimeClassified = date1;
    }

    public void setLatitude(Double double1)
    {
        latitude = double1;
    }

    public void setLongitude(Double double1)
    {
        longitude = double1;
    }

    public void setOrientation(Integer integer)
    {
        orientation = integer;
    }

    public void setPath(String s)
    {
        path = s;
    }

    public void setProp(Float float1)
    {
        prop = float1;
    }

    public void setScore(Double double1)
    {
        score = double1;
    }

    public void setServerId(Long long1)
    {
        serverId = long1;
    }

    public void setSimilarityScoreToNext(Double double1)
    {
        similarityScoreToNext = double1;
    }

    public void setSimilarityScoreToPrev(Double double1)
    {
        similarityScoreToPrev = double1;
    }

    public void setSource(Integer integer)
    {
        source = integer;
    }

    public void setThumbnail(String s)
    {
        thumbnail = s;
    }

    public void setType(Integer integer)
    {
        type = integer;
    }

    public void setWasAnalyzedByGD(Boolean boolean1)
    {
        wasAnalyzedByGD = boolean1;
    }

    public void setWasAnalyzedForDuplicates(Boolean boolean1)
    {
        wasAnalyzedForDuplicates = boolean1;
    }

    public void setWasClustered(Boolean boolean1)
    {
        wasClustered = boolean1;
    }

    public void setWasDeleted(Boolean boolean1)
    {
        wasDeleted = boolean1;
    }

    public void setWasDeletedByUser(Boolean boolean1)
    {
        wasDeletedByUser = boolean1;
    }

    public void setWasKeptByUser(Boolean boolean1)
    {
        wasKeptByUser = boolean1;
    }

    public void setWasMinimizedByUser(Boolean boolean1)
    {
        wasMinimizedByUser = boolean1;
    }

    public void setWidth(Integer integer)
    {
        width = integer;
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

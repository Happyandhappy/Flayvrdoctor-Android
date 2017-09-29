package com.flayvr.myrollshared.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.flayvr.myrollshared.utils.AndroidUtils;
import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.Property;
import java.util.*;

public class Moment
{
    private MediaItem cover;
    private Long coverId;
    private Long cover__resolvedKey;
    private DaoSession daoSession;
    private String dateStr;
    private Date endDate;
    private Folder folder;
    private Long folderId;
    private Long folder__resolvedKey;
    private Long id;
    private Boolean isFavorite;
    private Boolean isHidden;
    private Boolean isMuted;
    private Boolean isTitleFromCalendar;
    private Boolean isTrip;
    int itemsCount;
    private String location;
    private List momentItems;
    private MomentDao myDao;
    int photosCount;
    private Date startDate;
    private List tempItems;
    private String title;
    private String token;
    private MomentImagesType type;
    private String url;
    int videosCount;
    private Integer watchCount;

    public Moment()
    {
        type = null;
        itemsCount = -1;
        photosCount = -1;
        videosCount = -1;
    }

    public Moment(Long long1)
    {
        type = null;
        itemsCount = -1;
        photosCount = -1;
        videosCount = -1;
        id = long1;
    }

    public Moment(Long long1, String s, String s1, Boolean boolean1, Boolean boolean2, Boolean boolean3, Boolean boolean4, 
            Date date, Date date1, Boolean boolean5, Integer integer, Long long2, Long long3)
    {
        type = null;
        itemsCount = -1;
        photosCount = -1;
        videosCount = -1;
        id = long1;
        title = s;
        location = s1;
        isMuted = boolean1;
        isHidden = boolean2;
        isFavorite = boolean3;
        isTrip = boolean4;
        startDate = date;
        endDate = date1;
        isTitleFromCalendar = boolean5;
        watchCount = integer;
        folderId = long2;
        coverId = long3;
    }

    private void calcType()
    {
        String s;
        Cursor cursor;
        int i;
        double d;
        int k;
        int j;
        if(momentItems != null)
        {
            Iterator iterator = getMomentItems().iterator();
            j = 0;
            while(iterator.hasNext()) 
            {
                if(((MomentsItems)iterator.next()).getItem().isHorizontal())
                {
                    k = j + 1;
                } else
                {
                    k = j;
                }
                j = k;
            }
            i = getMomentItems().size();
        } else {
            s = (new StringBuilder()).append("SELECT count(t.").append(MediaItemDao.Properties.Id.columnName).append("), count(case when t.").append(MediaItemDao.Properties.Prop.columnName).append(" >= 1 then 1 else null end)").append("FROM ").append("MOMENTS_ITEMS").append(" ").append("JOIN ").append("MEDIA_ITEM").append(" AS t ").append("ON ").append(MomentsItemsDao.Properties.ItemId.columnName).append(" = t.").append(MediaItemDao.Properties.Id.columnName).append(" ").append("WHERE ").append(MomentsItemsDao.Properties.MomentId.columnName).append(" = ").append(id).toString();
            cursor = daoSession.getMediaItemDao().getDatabase().rawQuery(s, null);
            if(cursor.moveToNext())
            {
                i = cursor.getInt(0);
                j = cursor.getInt(1);
            } else
            {
                i = 0;
                j = 0;
            }
        }
        d = (1.0D * (double)j) / (double)i;
        if(d > 0.90000000000000002D)
        {
            type = MomentImagesType.AllLandscape;
            return;
        }
        if(d < 0.10000000000000001D)
        {
            type = MomentImagesType.AllPortrait;
            return;
        }
        if(d > 0.65000000000000002D)
        {
            type = MomentImagesType.MostlyLandscape;
            return;
        }
        if(d < 0.34999999999999998D)
        {
            type = MomentImagesType.MostlyPortrait;
            return;
        } else
        {
            type = MomentImagesType.Mixed;
            return;
        }
    }

    public static MediaItem getBestPhotoInList(List<MediaItem> list)
    {
label0:
        {
            MediaItem mediaitem;
            try
            {
                if(((MediaItem)list.get(0)).getIntegratedScore() != null)
                {
                    break label0;
                }
                mediaitem = (MediaItem)list.get(list.size() / 2);
            }
            catch(IndexOutOfBoundsException indexoutofboundsexception)
            {
                return null;
            }
            return mediaitem;
        }
        return (MediaItem)Collections.min(list, new ScoreComparator());
    }

    private int getItemCount(int i)
    {
        String s = (new StringBuilder()).append("SELECT count(t.").append(MediaItemDao.Properties.Id.columnName).append(") ").append("FROM ").append("MOMENTS_ITEMS").append(" ").append("JOIN ").append("MEDIA_ITEM").append(" AS t ").append("ON ").append(MomentsItemsDao.Properties.ItemId.columnName).append(" = t.").append(MediaItemDao.Properties.Id.columnName).append(" ").append("WHERE ").append(MomentsItemsDao.Properties.MomentId.columnName).append(" = ").append(id).append(" ").append("AND t.").append(MediaItemDao.Properties.Type.columnName).append(" = ").append(i).toString();
        Cursor cursor = daoSession.getMediaItemDao().getDatabase().rawQuery(s, null);
        boolean flag = cursor.moveToNext();
        int j = 0;
        if(flag)
        {
            j = cursor.getInt(0);
        }
        return j;
    }

    public void __setDaoSession(DaoSession daosession)
    {
        daoSession = daosession;
        MomentDao momentdao;
        if(daosession != null)
        {
            momentdao = daosession.getMomentDao();
        } else
        {
            momentdao = null;
        }
        myDao = momentdao;
    }

    public void clearDateStr()
    {
        dateStr = null;
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

    public List getAllItems()
    {
        LinkedList linkedlist = new LinkedList();
        for(Iterator iterator = getMomentItems().iterator(); iterator.hasNext(); linkedlist.add(((MomentsItems)iterator.next()).getItem())) { }
        Collections.sort(linkedlist, new Comparator<MediaItem>() {
            @Override
            public int compare(MediaItem mediaItem, MediaItem t1) {
                if(!mediaItem.getDate().equals(t1.getDate()))
                {
                    return mediaItem.getDate().compareTo(t1.getDate());
                } else
                {
                    return mediaItem.getId().compareTo(t1.getId());
                }
            }
        });
        return linkedlist;
    }

    public List getAllWithDuplicates()
    {
        LinkedList linkedlist = new LinkedList();
        for(Iterator iterator = getPhotosWithDuplicates().iterator(); iterator.hasNext(); linkedlist.add((MediaItem)iterator.next())) { }
        for(Iterator iterator1 = getVideos().iterator(); iterator1.hasNext(); linkedlist.add((MediaItem)iterator1.next())) { }
        Collections.sort(linkedlist, new Comparator<MediaItem>() {
            @Override
            public int compare(MediaItem mediaitem, MediaItem mediaitem1) {
                if(!mediaitem.getDate().equals(mediaitem1.getDate()))
                {
                    return mediaitem.getDate().compareTo(mediaitem1.getDate());
                } else
                {
                    return mediaitem.getId().compareTo(mediaitem1.getId());
                }
            }
        });
        return linkedlist;
    }

    public List getBestPhotos(int i)
    {
        int j;
        LinkedList linkedlist;
        j = (int)(1.2D * (double)i);
        linkedlist = new LinkedList();
        List list = getPhotos();
        if(list.size() / i >= 2)
        {
            if(list.size() / j < 2)
            {
                j = i;
            }
            int k = 0;
            while(k < j) 
            {
                int l = k * (list.size() / j);
                int i1 = (k + 1) * (list.size() / j);
                if(i1 > list.size() || k == j - 1)
                {
                    i1 = list.size();
                }
                if(l != i1)
                {
                    linkedlist.add(getBestPhotoInList(list.subList(l, i1)));
                }
                k++;
            }
        } else
        {
            linkedlist.addAll(list);
        }
        Collections.sort(linkedlist, new ScoreComparator());
        if(!linkedlist.contains(getNonEmptyCoverItem())) {
            if(j - 1 >= 0 && j - 1 < linkedlist.size())
            {
                linkedlist.remove(j - 1);
                linkedlist.add(0, getNonEmptyCoverItem());
            }
        }else {
            linkedlist.remove(getNonEmptyCoverItem());
            linkedlist.add(0, getNonEmptyCoverItem());
        }
        return linkedlist.subList(0, Math.min(linkedlist.size(), i));
    }

    public MediaItem getCover()
    {
        Long long1 = coverId;
        if(!(cover__resolvedKey != null && cover__resolvedKey.equals(long1))) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MediaItem mediaitem = (MediaItem) daoSession.getMediaItemDao().load(long1);
            synchronized (this) {
                cover = mediaitem;
                cover__resolvedKey = long1;
            }
        }
        return cover;
    }

    public Long getCoverId()
    {
        return coverId;
    }

    public String getDateStr()
    {
        if(dateStr == null)
        {
            dateStr = AndroidUtils.getDateStr(startDate, endDate, true);
        }
        return dateStr;
    }

    public String getDefaultDateFormat()
    {
        return AndroidUtils.getDateStr(startDate, endDate, false);
    }

    public Date getEndDate()
    {
        return endDate;
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

    public Long getId()
    {
        return id;
    }

    public Boolean getIsFavorite()
    {
        return isFavorite;
    }

    public Boolean getIsHidden()
    {
        return isHidden;
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

    public int getItemsCount()
    {
        if(momentItems == null) {
            if(itemsCount == -1)
            {
                String s = (new StringBuilder()).append("SELECT count(").append(MomentsItemsDao.Properties.Id.columnName).append(") ").append("FROM ").append("MOMENTS_ITEMS").append(" ").append("WHERE ").append(MomentsItemsDao.Properties.MomentId.columnName).append(" = ").append(id).toString();
                Cursor cursor = daoSession.getMediaItemDao().getDatabase().rawQuery(s, null);
                if(cursor.moveToNext())
                {
                    itemsCount = cursor.getInt(0);
                }
                itemsCount = 0;
            }
        }else
            itemsCount = getMomentItems().size();
        return itemsCount;
    }

    public String getLocation()
    {
        return location;
    }

    public List getMomentItems()
    {
        if(momentItems == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            List list = daoSession.getMomentsItemsDao()._queryMoment_MomentItems(id);
            synchronized (this) {
                if (momentItems == null) {
                    momentItems = list;
                }
            }
        }
        return momentItems;
    }

    public MediaItem getNonEmptyCoverItem()
    {
        if(getCover() == null)
        {
            return getBestPhotoInList(getPhotos());
        } else
        {
            return getCover();
        }
    }

    public String getNonEmptyTitle(boolean flag)
    {
        if(hasTitle())
        {
            return title;
        }
        if(flag)
        {
            return getDateStr();
        } else
        {
            return getDefaultDateFormat();
        }
    }

    public List getPhotos()
    {
        LinkedList linkedlist = new LinkedList();
        Iterator iterator = getMomentItems().iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            MomentsItems momentsitems = (MomentsItems)iterator.next();
            if(momentsitems.getItem().getType().intValue() == 1)
            {
                linkedlist.add(momentsitems.getItem());
            }
        } while(true);
        return linkedlist;
    }

    public int getPhotosCount()
    {
        if(momentItems == null) {
            if(photosCount == -1)
            {
                photosCount = getItemCount(1);
            }
        }else
            photosCount = getPhotos().size();
        return photosCount;
    }

    public List getPhotosWithDuplicates()
    {
        HashSet hashset = new HashSet();
        for(Iterator iterator = getPhotos().iterator(); iterator.hasNext(); hashset.add(((MediaItem)iterator.next()).getId())) { }
        return DaoHelper.getItemsWithDuplicateInFolders(Collections.singleton(getFolderId()), hashset, false, false, null, true);
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public List getTempItems()
    {
        if(tempItems != null)
        {
            return tempItems;
        } else
        {
            return getMomentItems();
        }
    }

    public String getTitle()
    {
        return title;
    }

    public String getToken()
    {
        return token;
    }

    public MomentImagesType getType()
    {
        if(type == null)
        {
            calcType();
        }
        return type;
    }

    public String getUrl()
    {
        return url;
    }

    public List getVideos()
    {
        LinkedList linkedlist = new LinkedList();
        Iterator iterator = getMomentItems().iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            MomentsItems momentsitems = (MomentsItems)iterator.next();
            if(momentsitems.getItem().getType().intValue() == 2)
            {
                linkedlist.add(momentsitems.getItem());
            }
        } while(true);
        return linkedlist;
    }

    public int getVideosCount()
    {
        if(momentItems == null) {
            if(videosCount == -1)
            {
                videosCount = getItemCount(2);
            }
        } else
            videosCount = getVideos().size();
        return videosCount;
    }

    public Integer getWatchCount()
    {
        return watchCount;
    }

    public boolean hasLocation()
    {
        return location != null && !location.equals("");
    }

    public boolean hasTitle()
    {
        return title != null && !title.equals("");
    }

    public boolean hasVideo()
    {
        for(Iterator iterator = getMomentItems().iterator(); iterator.hasNext();)
        {
            if(((MomentsItems)iterator.next()).getItem().getType().intValue() == 2)
            {
                return true;
            }
        }

        return false;
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

    public void resetMomentItems()
    {
        synchronized (this) {
            momentItems = null;
        }
    }

    public void setCover(MediaItem mediaitem)
    {
        synchronized (this){
            cover = mediaitem;
            Long long1;
            if(mediaitem != null)
                long1 = mediaitem.getId();
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

    public void setEndDate(Date date)
    {
        endDate = date;
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

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setIsFavorite(Boolean boolean1)
    {
        isFavorite = boolean1;
    }

    public void setIsHidden(Boolean boolean1)
    {
        isHidden = boolean1;
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

    public void setStartDate(Date date)
    {
        startDate = date;
    }

    public void setTempItems(List list)
    {
        tempItems = list;
    }

    public void setTitle(String s)
    {
        title = s;
    }

    public void setToken(String s)
    {
        token = s;
    }

    public void setUrl(String s)
    {
        url = s;
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

    public enum MomentImagesType
    {
        AllLandscape("AllLandscape", 0),
        AllPortrait("AllPortrait", 1),
        MostlyLandscape("MostlyLandscape", 2),
        MostlyPortrait("MostlyPortrait", 3),
        Mixed("Mixed", 4);

        private MomentImagesType(String s, int i)
        {
        }
    }

    private static class ScoreComparator implements Comparator<MediaItem>
    {
        public int compare(MediaItem mediaitem, MediaItem mediaitem1)
        {
            Double double1 = mediaitem.getIntegratedScore();
            if(double1 == null)
            {
                return 1;
            }
            Double double2 = mediaitem1.getIntegratedScore();
            if(double2 == null)
            {
                return -1;
            } else
            {
                return -double1.compareTo(double2);
            }
        }
    }
}

package com.flayvr.myrollshared.oldclasses;

import com.flayvr.myrollshared.utils.AndroidUtils;
import java.text.DateFormat;
import java.util.*;
import org.apache.commons.lang3.SerializationUtils;

public class FlayvrGroup extends AbstractLocalGroup
{

    private static final long serialVersionUID = 0xec2fca3bde2ddcdaL;
    private PhotoMediaItem cover;
    private String dateStr;
    public int flayvrId;
    private boolean isFavorite;
    private boolean isHidden;
    private boolean isMerged;
    private boolean isTrip;
    private String location;
    private Boolean manuallyIsMuted;
    private String title;
    private boolean titleFromCalendar;
    private String token;
    private FlayvrImagesType type;
    private String url;
    private int watchCount;

    public FlayvrGroup(String s, List list, List list1)
    {
        super(s, list, list1);
        type = null;
        setOrinalDate(getLastDate());
        flayvrId = -1;
        watchCount = 0;
    }

    public static PhotoMediaItem getBestPhotoInList(List list, FlayvrGroup flayvrgroup)
    {
        if(((PhotoMediaItem)list.get(0)).getScore().doubleValue() == -1D)
        {
            return (PhotoMediaItem)list.get(list.size() / 2);
        } else
        {
            return (PhotoMediaItem)Collections.min(list, flayvrgroup. new ScoreComparator());
        }
    }

    public void calcType()
    {
        Iterator iterator = getPhotoItems().iterator();
        int i = 0;
        double d;
        int j;
        while(iterator.hasNext()) 
        {
            if(((PhotoMediaItem)iterator.next()).isHorizontal())
            {
                j = i + 1;
            } else
            {
                j = i;
            }
            i = j;
        }
        d = (1.0D * (double)i) / (double)getPhotoItems().size();
        if(d > 0.90000000000000002D)
        {
            type = FlayvrImagesType.AllLandscape;
            return;
        }
        if(d < 0.10000000000000001D)
        {
            type = FlayvrImagesType.AllPortrait;
            return;
        }
        if(d > 0.65000000000000002D)
        {
            type = FlayvrImagesType.MostlyLandscape;
            return;
        }
        if(d < 0.34999999999999998D)
        {
            type = FlayvrImagesType.MostlyPortrait;
            return;
        } else
        {
            type = FlayvrImagesType.Mixed;
            return;
        }
    }

    public FlayvrGroup clone()
    {
        return (FlayvrGroup)SerializationUtils.clone(this);
    }

    public List getAllItems()
    {
        return getAllItems(true);
    }

    public List getAllItems(boolean flag)
    {
        ArrayList arraylist = new ArrayList(getPhotoItems());
        arraylist.addAll(getVideoItems());
        if(flag)
        {
            Collections.sort(arraylist);
            return arraylist;
        } else
        {
            Collections.sort(arraylist, new Comparator<AbstractMediaItem>(){
                @Override
                public int compare(AbstractMediaItem abstractmediaitem, AbstractMediaItem abstractmediaitem1) {
                    return -abstractmediaitem.compareTo(abstractmediaitem1);
                }
            });
            return arraylist;
        }
    }

    public List getBestPhotos(int i)
    {
        int k;
        Object obj;
        int j = (int)(1.2D * (double)i);
        LinkedList linkedlist = new LinkedList();
        List list = getPhotoItems();
        if(list.size() / i >= 2)
        {
            if(list.size() / j < 2)
            {
                j = i;
            }
            for(int l = 0; l < j; l++)
            {
                int i1 = l * (list.size() / j);
                int j1 = (l + 1) * (list.size() / j);
                if(j1 > list.size() || l == j - 1)
                {
                    j1 = list.size();
                }
                linkedlist.add(getBestPhotoInList(list.subList(i1, j1), this));
            }

            k = j;
            obj = linkedlist;
        } else
        {
            k = j;
            obj = list;
        }
        Collections.sort(((List) (obj)), new ScoreComparator());
        if(!((List) (obj)).contains(getNonEmptyCoverItem())) {
            if(k - 1 >= 0 && k - 1 < ((List) (obj)).size())
            {
                ((List) (obj)).remove(k - 1);
                ((List) (obj)).add(0, getNonEmptyCoverItem());
            }
        }else {
            ((List) (obj)).remove(getNonEmptyCoverItem());
            ((List) (obj)).add(0, getNonEmptyCoverItem());
        }
        return ((List) (obj)).subList(0, Math.min(((List) (obj)).size(), i));
    }

    public PhotoMediaItem getCoverItem()
    {
        return cover;
    }

    public String getDateStr()
    {
        return dateStr;
    }

    public String getDefaultDateFormat()
    {
        if(getDate() instanceof MediaGroupSingleDate)
        {
            return AndroidUtils.getDefaultDateFormat().format(getDate().getLastDate());
        } else
        {
            return getDate().toString();
        }
    }

    public Date getFirstDate()
    {
        if(hasVideo())
        {
            Date date = ((PhotoMediaItem)photoItems.get(-1 + photoItems.size())).getDate();
            Date date1 = ((VideoMediaItem)videoItems.get(-1 + videoItems.size())).getDate();
            if(date.compareTo(date1) < 0)
            {
                date1 = date;
            }
            return date1;
        } else
        {
            return ((PhotoMediaItem)photoItems.get(-1 + photoItems.size())).getDate();
        }
    }

    public AbstractMediaItem getFirstMediaItemWithLocation()
    {
        for(Iterator iterator = getPhotoItems().iterator(); iterator.hasNext();)
        {
            PhotoMediaItem photomediaitem = (PhotoMediaItem)iterator.next();
            if(photomediaitem.getLatitude() != 0.0D)
            {
                return photomediaitem;
            }
        }

        return null;
    }

    public Integer getFlayvrId()
    {
        return Integer.valueOf(flayvrId);
    }

    public String getLocation()
    {
        if(location != null)
        {
            return location;
        } else
        {
            return "";
        }
    }

    public PhotoMediaItem getNonEmptyCoverItem()
    {
        if(cover == null)
        {
            return getBestPhotoInList(photoItems, this);
        } else
        {
            return cover;
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
            return getDefaultDateFormat();
        } else
        {
            return dateStr;
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

    public FlayvrImagesType getType()
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

    public int getWatchCount()
    {
        return watchCount;
    }

    public boolean hasLocation()
    {
        return location != null && location.length() > 0;
    }

    public boolean hasTitle()
    {
        return title != null && title.length() > 0;
    }

    public boolean isFavorite()
    {
        return isFavorite;
    }

    public boolean isHidden()
    {
        return isHidden;
    }

    public boolean isMerged()
    {
        return isMerged;
    }

    public Boolean isMuted()
    {
        return manuallyIsMuted;
    }

    public boolean isTitleFromCalendar()
    {
        return titleFromCalendar;
    }

    public boolean isTrip()
    {
        return isTrip;
    }

    public void momentWatched()
    {
        watchCount = 1 + watchCount;
    }

    public void removeAllItems(List list)
    {
        for(Iterator iterator = list.iterator(); iterator.hasNext(); removePhotoItem((PhotoMediaItem)iterator.next())) { }
    }

    public void replaceItems(List list)
    {
        photoItems = new ArrayList();
        videoItems = new ArrayList();
        for(Iterator iterator = list.iterator(); iterator.hasNext();)
        {
            AbstractMediaItem abstractmediaitem = (AbstractMediaItem)iterator.next();
            if(abstractmediaitem instanceof PhotoMediaItem)
            {
                photoItems.add((PhotoMediaItem)abstractmediaitem);
            } else
            {
                videoItems.add((VideoMediaItem)abstractmediaitem);
            }
        }

    }

    public void setCover(PhotoMediaItem photomediaitem)
    {
        cover = photomediaitem;
    }

    public void setDateStr(String s)
    {
        dateStr = s;
    }

    public void setFavorite(boolean flag)
    {
        isFavorite = flag;
    }

    public void setFlayvrId(int i)
    {
        flayvrId = i;
    }

    public void setIsHidden(boolean flag)
    {
        isHidden = flag;
    }

    public void setIsMuted(Boolean boolean1)
    {
        manuallyIsMuted = boolean1;
    }

    public void setIsTitleFromCalendar(boolean flag)
    {
        titleFromCalendar = flag;
    }

    public void setLocation(String s)
    {
        if(s != null && s.length() > 0)
        {
            location = s;
            return;
        } else
        {
            location = null;
            return;
        }
    }

    public void setMerged(boolean flag)
    {
        isMerged = flag;
    }

    public void setTitle(String s)
    {
        if(s != null && s.length() > 0)
        {
            title = s;
        } else
        {
            title = null;
        }
    }

    public void setToken(String s)
    {
        token = s;
    }

    public void setTrip(boolean flag)
    {
        isTrip = flag;
    }

    public void setUrl(String s)
    {
        url = s;
    }

    public void setWatchCount(Integer integer)
    {
        watchCount = integer.intValue();
    }

    private class ScoreComparator implements Comparator<PhotoMediaItem>
    {

        private FlayvrGroup group;

        public int compare(PhotoMediaItem photomediaitem, PhotoMediaItem photomediaitem1)
        {
            Double double1 = photomediaitem.getScore();
            Double double2 = photomediaitem1.getScore();
            return -Double.valueOf(double1.doubleValue() + 0.10000000000000001D * (double)group.getInteracitons(photomediaitem).size()).compareTo(Double.valueOf(double2.doubleValue() + 0.10000000000000001D * (double)group.getInteracitons(photomediaitem1).size()));
        }

        public ScoreComparator()
        {
            group = FlayvrGroup.this;
        }
    }


    private enum FlayvrImagesType
    {
        AllLandscape("AllLandscape", 0),
        AllPortrait("AllPortrait", 1),
        MostlyLandscape("MostlyLandscape", 2),
        MostlyPortrait("MostlyPortrait", 3),
        Mixed("Mixed", 4);

        private FlayvrImagesType(String s, int i)
        {
        }
    }
}

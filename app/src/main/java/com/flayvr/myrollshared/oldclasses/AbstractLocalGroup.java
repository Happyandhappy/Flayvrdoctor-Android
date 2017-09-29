package com.flayvr.myrollshared.oldclasses;

import java.util.*;

public class AbstractLocalGroup extends AbstractGroup
{

    private static final long serialVersionUID = 0xae68782ef74e35a7L;
    protected String albumId;
    private MediaGroupDate date;
    protected Set originalItems;
    protected Date orinalDate;

    public AbstractLocalGroup(String s, List list, List list1)
    {
        super(list, list1);
        albumId = s;
        originalItems = new HashSet();
        PhotoMediaItem photomediaitem;
        for(Iterator iterator = photoItems.iterator(); iterator.hasNext(); originalItems.add(photomediaitem.getItemId()))
        {
            photomediaitem = (PhotoMediaItem)iterator.next();
        }

        VideoMediaItem videomediaitem;
        for(Iterator iterator1 = videoItems.iterator(); iterator1.hasNext(); originalItems.add(videomediaitem.getItemId()))
        {
            videomediaitem = (VideoMediaItem)iterator1.next();
        }

        Date date1 = getLastDate();
        if(date1 != null)
        {
            date = new MediaGroupSingleDate(date1);
        }
    }

    public String getAlbumId()
    {
        return albumId;
    }

    public MediaGroupDate getDate()
    {
        return date;
    }

    public Date getLastDate()
    {
        if(hasVideo())
        {
            Date date1 = ((VideoMediaItem)videoItems.get(0)).getDate();
            if(photoItems.size() > 0)
            {
                Date date2 = ((PhotoMediaItem)photoItems.get(0)).getDate();
                if(date2.compareTo(date1) > 0)
                {
                    return date2;
                } else
                {
                    return date1;
                }
            } else
            {
                return date1;
            }
        }
        if(photoItems.size() > 0)
        {
            return ((PhotoMediaItem)photoItems.get(0)).getDate();
        } else
        {
            return null;
        }
    }

    public Date getOriginalDate()
    {
        return orinalDate;
    }

    public Set getOriginalItems()
    {
        return originalItems;
    }

    public void setDate(MediaGroupDate mediagroupdate)
    {
        date = mediagroupdate;
    }

    public void setOriginalItems(Set set)
    {
        originalItems = set;
    }

    public void setOrinalDate(Date date1)
    {
        orinalDate = date1;
    }
}

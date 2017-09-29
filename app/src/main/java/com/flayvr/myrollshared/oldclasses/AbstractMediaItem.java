package com.flayvr.myrollshared.oldclasses;

import android.location.Location;
import android.util.Pair;
import java.io.File;
import java.io.Serializable;
import java.util.Date;

public abstract class AbstractMediaItem
    implements Serializable, Comparable<AbstractMediaItem>
{

    private static final long serialVersionUID = 0x5de6432e5f2a7debL;
    protected String bucketId;
    protected Date date;
    protected String imagePath;
    protected Long itemId;
    protected double latitude;
    protected double longitude;
    protected Float prop;
    private String thumbnailPath;

    public AbstractMediaItem()
    {
    }

    public int compareTo(AbstractMediaItem abstractmediaitem)
    {
        return abstractmediaitem.getDate().compareTo(getDate());
    }

    public boolean equals(Object obj)
    {
        if(this != obj)
        {
            if(obj == null)
            {
                return false;
            }
            if(!getClass().equals(obj.getClass()))
            {
                return false;
            }
            AbstractMediaItem abstractmediaitem = (AbstractMediaItem)obj;
            if(bucketId == null)
            {
                if(abstractmediaitem.bucketId != null)
                {
                    return false;
                }
            } else
            if(!bucketId.equals(abstractmediaitem.bucketId))
            {
                return false;
            }
            if(!itemId.equals(abstractmediaitem.itemId))
            {
                return false;
            }
        }
        return true;
    }

    public String getBucketId()
    {
        return bucketId;
    }

    public Date getDate()
    {
        return date;
    }

    public long getFileSize()
    {
        return (new File(getImagePath())).length();
    }

    public String getImagePath()
    {
        return imagePath;
    }

    public Long getItemId()
    {
        return itemId;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public Location getLocation()
    {
        if(longitude == 0.0D || latitude == 0.0D)
        {
            return null;
        } else
        {
            Location location = new Location(itemId.toString());
            location.setLongitude(longitude);
            location.setLatitude(latitude);
            return location;
        }
    }

    public double getLongitude()
    {
        return longitude;
    }

    public abstract Float getProp();

    public abstract Pair getSize();

    public String getThumbnailPath()
    {
        return thumbnailPath;
    }

    public int hashCode()
    {
        int i;
        if(bucketId == null)
        {
            i = 0;
        } else
        {
            i = bucketId.hashCode();
        }
        return 31 * (i + 31) + (int)(itemId.longValue() ^ itemId.longValue() >>> 32);
    }

    public boolean isHorizontal()
    {
        return getProp().floatValue() >= 1.0F;
    }

    public void setBucketId(String s)
    {
        bucketId = s;
    }

    public void setDate(Date date1)
    {
        date = date1;
    }

    public void setImagePath(String s)
    {
        imagePath = s;
    }

    public void setItemId(Long long1)
    {
        itemId = long1;
    }

    public void setLatitude(double d)
    {
        latitude = d;
    }

    public void setLongitude(double d)
    {
        longitude = d;
    }

    public void setProp(Float float1)
    {
        prop = float1;
    }

    public void setThumbnailPath(String s)
    {
        thumbnailPath = s;
    }
}

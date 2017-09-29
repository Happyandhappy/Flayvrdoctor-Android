package com.flayvr.myrollshared.data;

import java.util.*;

public class MultipleMediaItem extends MediaItem
{

    private final List items;

    public MultipleMediaItem()
    {
        items = new ArrayList();
    }

    public MultipleMediaItem(Collection collection)
    {
        items = new ArrayList(collection);
    }

    public void addItem(MediaItem mediaitem)
    {
        items.add(mediaitem);
    }

    public Date getDate()
    {
        if(items.size() > 0)
        {
            return ((MediaItem)items.get(0)).getDate();
        } else
        {
            return null;
        }
    }

    public Long getId()
    {
        if(items.size() > 0)
        {
            return ((MediaItem)items.get(0)).getId();
        } else
        {
            return null;
        }
    }

    public Double getIntegratedScore()
    {
        if(items.size() > 0)
        {
            return ((MediaItem)items.get(0)).getIntegratedScore();
        } else
        {
            return null;
        }
    }

    public List getItems()
    {
        return items;
    }

    public Integer getSource()
    {
        if(items.size() > 0)
        {
            return ((MediaItem)items.get(0)).getSource();
        } else
        {
            return null;
        }
    }

    public Integer getType()
    {
        if(items.size() > 0)
        {
            return ((MediaItem)items.get(0)).getType();
        } else
        {
            return null;
        }
    }
}

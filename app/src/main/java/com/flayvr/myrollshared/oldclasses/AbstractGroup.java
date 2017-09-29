package com.flayvr.myrollshared.oldclasses;

import java.io.Serializable;
import java.util.*;

public abstract class AbstractGroup
    implements Serializable
{

    private static final long serialVersionUID = 0xca3b3b1d5b13e68bL;
    protected Map interacitons;
    protected List photoItems;
    protected List videoItems;

    public AbstractGroup()
    {
        this(((List) (new ArrayList())), ((List) (new ArrayList())));
    }

    public AbstractGroup(List list, List list1)
    {
        photoItems = list;
        videoItems = list1;
        interacitons = new HashMap();
    }

    public void addAllPhotoItems(Collection collection)
    {
        photoItems.addAll(collection);
    }

    public void addAllVideoItems(Collection collection)
    {
        videoItems.addAll(collection);
    }

    public void addInteraction(AbstractMediaItem abstractmediaitem, ImageInteraction imageinteraction)
    {
        if(interacitons.get(abstractmediaitem) == null)
        {
            interacitons.put(abstractmediaitem, new LinkedList());
        }
        ((List)interacitons.get(abstractmediaitem)).add(imageinteraction);
    }

    public void addMediaItem(AbstractMediaItem abstractmediaitem)
    {
        if(abstractmediaitem instanceof PhotoMediaItem)
        {
            addPhotoItem((PhotoMediaItem)abstractmediaitem);
        } else
        if(abstractmediaitem instanceof VideoMediaItem)
        {
            addVideoItem((VideoMediaItem)abstractmediaitem);
            return;
        }
    }

    public void addPhotoItem(PhotoMediaItem photomediaitem)
    {
        photoItems.add(photomediaitem);
    }

    public void addVideoItem(VideoMediaItem videomediaitem)
    {
        videoItems.add(videomediaitem);
    }

    public AbstractMediaItem findItemById(Long long1)
    {
        for(Iterator iterator = photoItems.iterator(); iterator.hasNext();)
        {
            PhotoMediaItem photomediaitem = (PhotoMediaItem)iterator.next();
            if(photomediaitem.getItemId().equals(long1))
            {
                return photomediaitem;
            }
        }

        for(Iterator iterator1 = videoItems.iterator(); iterator1.hasNext();)
        {
            VideoMediaItem videomediaitem = (VideoMediaItem)iterator1.next();
            if(videomediaitem.getItemId().equals(long1))
            {
                return videomediaitem;
            }
        }

        return null;
    }

    public abstract MediaGroupDate getDate();

    public List getInteracitons(AbstractMediaItem abstractmediaitem)
    {
        if(interacitons.get(abstractmediaitem) != null)
        {
            return (List)interacitons.get(abstractmediaitem);
        } else
        {
            return new LinkedList();
        }
    }

    public Map getInteracitons()
    {
        return interacitons;
    }

    public int getMediaItemsCount()
    {
        return getPhotoItems().size() + getVideoItems().size();
    }

    public List getPhotoItems()
    {
        return new LinkedList(photoItems);
    }

    public List getVideoItems()
    {
        return new LinkedList(videoItems);
    }

    public boolean hasVideo()
    {
        return videoItems.size() > 0;
    }

    public void removeMediaItem(AbstractMediaItem abstractmediaitem)
    {
        if(abstractmediaitem instanceof PhotoMediaItem)
        {
            removePhotoItem((PhotoMediaItem)abstractmediaitem);
        } else
        if(abstractmediaitem instanceof VideoMediaItem)
        {
            removeVideoItem((VideoMediaItem)abstractmediaitem);
            return;
        }
    }

    public void removePhotoItem(PhotoMediaItem photomediaitem)
    {
        photoItems.remove(photomediaitem);
    }

    public void removeVideoItem(VideoMediaItem videomediaitem)
    {
        videoItems.remove(videomediaitem);
    }

    public void setInteractions(Map map)
    {
        interacitons = map;
    }

    public void setPhotoItems(List list)
    {
        photoItems = list;
    }

    public void setVideoItems(List list)
    {
        videoItems = list;
    }
}

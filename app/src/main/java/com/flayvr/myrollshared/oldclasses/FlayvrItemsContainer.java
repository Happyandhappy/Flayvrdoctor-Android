package com.flayvr.myrollshared.oldclasses;

import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.data.Moment;
import java.util.*;

public class FlayvrItemsContainer
{

    private Set currentItems;
    private List photos;
    private int photosIndex;
    private List videos;
    private int videosIndex;

    public FlayvrItemsContainer(Moment moment)
    {
        photosIndex = 0;
        videosIndex = 0;
        photos = moment.getPhotos();
        Collections.reverse(photos);
        videos = moment.getVideos();
        Collections.reverse(videos);
        currentItems = new HashSet();
    }

    public int getChangesCount()
    {
        return photosIndex + videosIndex;
    }

    public MediaItem getNextItem()
    {
        boolean flag;
        if(videos.size() > 0) {
            Iterator iterator = currentItems.iterator();
            flag = false;
            while (iterator.hasNext()) {
                if (((MediaItem) iterator.next()).getType().intValue() == 2) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                MediaItem mediaitem1 = (MediaItem) videos.get(videosIndex % videos.size());
                videosIndex = 1 + videosIndex;
                currentItems.add(mediaitem1);
                return mediaitem1;
            }
        }
        MediaItem mediaitem = (MediaItem)photos.get(photosIndex % photos.size());
        photosIndex = 1 + photosIndex;
        currentItems.add(mediaitem);
        return mediaitem;
    }

    public MediaItem getNextItem(MediaItem mediaitem)
    {
        MediaItem mediaitem1 = getNextItem();
        currentItems.remove(mediaitem);
        return mediaitem1;
    }
}

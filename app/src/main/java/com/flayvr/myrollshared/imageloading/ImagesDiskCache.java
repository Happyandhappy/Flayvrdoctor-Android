package com.flayvr.myrollshared.imageloading;

import android.util.Log;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.MediaItem;
import java.io.IOException;
import java.util.Date;

public class ImagesDiskCache
{

    private static final String TAG = ImagesDiskCache.class.getSimpleName();
    protected DiskLruImageCache fullCache;
    protected DiskLruImageCache thumbsCache;

    public ImagesDiskCache(int i)
    {
        thumbsCache = new DiskLruImageCache(FlayvrApplication.getAppContext(), "Cloud-thumbs", (i * 4) / 5);
        fullCache = new DiskLruImageCache(FlayvrApplication.getAppContext(), "Cloud-full", i / 5);
    }

    public void evictAll()
    {
        thumbsCache.clearCache();
        fullCache.clearCache();
    }

    public DiskLruCache.Snapshot get(MediaItem mediaitem)
    {
        if(mediaitem.getId() == null)
            return null;
        DiskLruCache.Snapshot snapshot = fullCache.getBitmap(mediaitem.getId().toString());
        return snapshot;
    }

    public DiskLruCache.Snapshot get(MediaItem mediaitem, ImageCacheBitmap.ThumbnailSize thumbnailsize)
    {
        if(thumbnailsize.width == 0)
        {
            thumbnailsize.width = 256;
        }
        if(thumbnailsize.height == 0)
        {
            thumbnailsize.height = 192;
        }
        if(mediaitem.getType().intValue() == 1 && mediaitem.getOrientation().intValue() > 0)
        {
            thumbnailsize = new ImageCacheBitmap.CustomizedThumbnailSize(thumbnailsize.height, thumbnailsize.width);
        }
        if(mediaitem.getId() == null)
        {
            return null;
        }
        DiskLruCache.Snapshot snapshot;
        if(thumbnailsize.compareTo(ImageCacheBitmap.ThumbnailSize.Normal) <= 0)
            return thumbsCache.getBitmap(mediaitem.getId().toString());
        snapshot = fullCache.getBitmap(mediaitem.getId().toString());
        return snapshot;
    }

    public void put(MediaItem mediaitem)
    {
        Date date = new Date();
        fullCache.put(mediaitem.getId().toString(), mediaitem.getPath());
        Log.d(TAG, (new StringBuilder()).append("put full: ").append((new Date()).getTime() - date.getTime()).append(" (").append(fullCache.getmDiskCache().getItemsCount()).append(")").toString());
    }

    public void put(MediaItem mediaitem, ImageCacheBitmap.ThumbnailSize thumbnailsize)
    {
        if(thumbnailsize.compareTo(ImageCacheBitmap.ThumbnailSize.Normal) <= 0)
        {
            Date date = new Date();
            thumbsCache.put(mediaitem.getId().toString(), mediaitem.getThumbnail());
            Log.d(TAG, (new StringBuilder()).append("put thumb: ").append((new Date()).getTime() - date.getTime()).append(" (").append(thumbsCache.getmDiskCache().getItemsCount()).append(")").toString());
        } else {
            Date date1 = new Date();
            fullCache.put(mediaitem.getId().toString(), mediaitem.getPath());
            Log.d(TAG, (new StringBuilder()).append("put full: ").append((new Date()).getTime() - date1.getTime()).append(" (").append(fullCache.getmDiskCache().getItemsCount()).append(")").toString());
        }
    }

}

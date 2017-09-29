package com.flayvr.myrollshared.imageloading;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.flayvr.myrollshared.data.MediaItem;

public class ImagesCache
{

    protected LruCache<Object, ImageCacheBitmap> cache;

    public ImagesCache(int i)
    {
        cache = new LruCache<Object, ImageCacheBitmap>(i){
            @Override
            protected int sizeOf(Object obj, ImageCacheBitmap imagecachebitmap)
            {
                return imagecachebitmap.getBitmap().getRowBytes() * imagecachebitmap.getBitmap().getHeight();
            }
        };
    }

    public void evictAll()
    {
        cache.evictAll();
    }

    public Bitmap get(MediaItem mediaitem, ImageCacheBitmap.ThumbnailSize thumbnailsize)
    {
        ImageCacheBitmap.CustomizedThumbnailSize customizedthumbnailsize = new ImageCacheBitmap.CustomizedThumbnailSize(thumbnailsize.width, thumbnailsize.height);
        if(((ImageCacheBitmap.ThumbnailSize) (customizedthumbnailsize)).width == 0)
        {
            customizedthumbnailsize.width = 256;
        }
        if(((ImageCacheBitmap.ThumbnailSize) (customizedthumbnailsize)).height == 0)
        {
            customizedthumbnailsize.height = 192;
        }
        if(mediaitem.getType().intValue() == 1 && mediaitem.getOrientation().intValue() > 0)
        {
            customizedthumbnailsize = new ImageCacheBitmap.CustomizedThumbnailSize(((ImageCacheBitmap.ThumbnailSize) (customizedthumbnailsize)).height, ((ImageCacheBitmap.ThumbnailSize) (customizedthumbnailsize)).width);
        }
        if(mediaitem.getId() == null)
        {
            return null;
        }
        ImageCacheBitmap imagecachebitmap = (ImageCacheBitmap)cache.get(mediaitem.getId());
        if(imagecachebitmap != null && imagecachebitmap.compareSize(customizedthumbnailsize) >= 0)
        {
            return imagecachebitmap.getBitmap();
        } else
        {
            return null;
        }
    }

    public Bitmap get(Long long1)
    {
        if(long1 == null)
        {
            return null;
        }
        ImageCacheBitmap imagecachebitmap = (ImageCacheBitmap)cache.get(long1);
        if(imagecachebitmap != null)
        {
            return imagecachebitmap.getBitmap();
        } else
        {
            return null;
        }
    }

    public int maxSize()
    {
        synchronized (this) {
            int i = cache.maxSize();
            return i;
        }
    }

    public Bitmap put(Long long1, Bitmap bitmap, ImageCacheBitmap.ThumbnailSize thumbnailsize)
    {
        ImageCacheBitmap imagecachebitmap = new ImageCacheBitmap(bitmap, thumbnailsize);
        cache.put(long1, imagecachebitmap);
        return bitmap;
    }

    public int size()
    {
        synchronized (this) {
            int i = cache.size();
            return i;
        }
    }

    public void trimToSize(int i)
    {
        cache.trimToSize(i);
    }
}

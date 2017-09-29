package com.flayvr.myrollshared.imageloading;

import com.flayvr.myrollshared.data.MediaItem;

public interface ICancelableTask
{

    public abstract void cancelTask();

    public abstract MediaItem getItem();

    public abstract ImageCacheBitmap.ThumbnailSize getSize();
}

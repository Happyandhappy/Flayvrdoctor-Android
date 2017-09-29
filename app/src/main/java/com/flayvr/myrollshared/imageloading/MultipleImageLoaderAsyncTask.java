package com.flayvr.myrollshared.imageloading;

import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.data.MultipleMediaItem;
import com.flayvr.myrollshared.views.itemview.IMediaItemView;
import java.util.List;

public class MultipleImageLoaderAsyncTask extends ImageLoaderAsyncTask
{

    private int currentIndex;

    public MultipleImageLoaderAsyncTask(int i, IMediaItemView imediaitemview, ImageCacheBitmap.ThumbnailSize thumbnailsize, MediaItem mediaitem, boolean flag, boolean flag1)
    {
        super(imediaitemview, thumbnailsize, mediaitem, flag, flag1);
        currentIndex = i;
    }

    public int getCurrentIndex()
    {
        return currentIndex;
    }

    public MediaItem getShownItem()
    {
        return (MediaItem)((MultipleMediaItem)item).getItems().get(currentIndex);
    }

    public void loadNext()
    {
        AndroidImagesUtils.getBitmapForMultipleItem(currentIndex, view, (MultipleMediaItem)item, nextSize, false, false);
    }

    public void onFinish()
    {
        super.onFinish();
    }

    protected boolean shouldClear()
    {
        return false;
    }
}

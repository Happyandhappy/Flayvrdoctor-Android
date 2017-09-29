package com.flayvr.myrollshared.views.itemview;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewTreeObserver;
import com.flayvr.myrollshared.data.MediaItem;

public interface IMediaItemView
{
    public abstract void clearImage();

    public abstract int getHeight();

    public abstract Bitmap getImage();

    public abstract View getImageView();

    public abstract MediaItem getItem();

    public abstract Object getTag(int i);

    public abstract ViewTreeObserver getViewTreeObserver();

    public abstract int getWidth();

    public abstract void setImage(Bitmap bitmap);

    public abstract void setItem(MediaItem mediaitem);

    public abstract void setTag(int i, Object obj);
}

package com.flayvr.myrollshared.imageloading;

import android.graphics.Bitmap;

public class ImageCacheBitmap
{

    public static final int NORMAL_HEIGHT = 384;
    public static final int NORMAL_WIDTH = 512;
    public static final int SMALL_HEIGHT = 192;
    public static final int SMALL_WIDTH = 256;
    protected Bitmap bitmap;
    private ThumbnailSize size;

    public ImageCacheBitmap(Bitmap bitmap1, ThumbnailSize thumbnailsize)
    {
        bitmap = bitmap1;
        size = thumbnailsize;
    }

    public int compareSize(ThumbnailSize thumbnailsize)
    {
        return size.compareTo(thumbnailsize);
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap1)
    {
        bitmap = bitmap1;
    }

    public String toString()
    {
        return size.toString();
    }

    public static class ThumbnailSize implements Comparable<ThumbnailSize>
    {
        public static ThumbnailSize Normal = new CustomizedThumbnailSize(512, 384);
        public static ThumbnailSize Small = new CustomizedThumbnailSize(256, 192);
        public int height;
        public int width;

        public int compareTo(ThumbnailSize thumbnailsize)
        {
            if(thumbnailsize != null)
            {
                int i = Integer.valueOf(width).compareTo(Integer.valueOf(thumbnailsize.width));
                int j = Integer.valueOf(height).compareTo(Integer.valueOf(thumbnailsize.height));
                if(i <= 0 && j <= 0)
                {
                    return j != 0 && i != 0 ? -1 : 0;
                }
            }
            return 1;
        }

        public String toString()
        {
            return (new StringBuilder()).append(width).append(" ").append(height).toString();
        }

        public ThumbnailSize()
        {
        }
    }

    public static class CustomizedThumbnailSize extends ThumbnailSize
    {
        public CustomizedThumbnailSize(int i, int j)
        {
            width = i;
            height = j;
        }
    }

}

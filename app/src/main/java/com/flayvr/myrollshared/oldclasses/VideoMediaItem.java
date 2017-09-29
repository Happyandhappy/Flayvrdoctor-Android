package com.flayvr.myrollshared.oldclasses;

import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Pair;
import com.flayvr.myrollshared.application.FlayvrApplication;

public class VideoMediaItem extends AbstractMediaItem
{

    private static final long serialVersionUID = 0xc22c0119e4ae2e60L;
    protected double duration;

    public VideoMediaItem()
    {
    }

    private void calcImageProp()
    {
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(getThumbnailPath(), options);
        if(options.outWidth == 0 || options.outHeight == 0)
            prop = Float.valueOf(1.0F);
        else
            prop = Float.valueOf((1.0F * (float)options.outWidth) / (float)options.outHeight);
    }

    public double getDuration()
    {
        return duration;
    }

    public String getFormatedDuration()
    {
        int i = (int)(duration / 1000D);
        Object aobj[] = new Object[2];
        aobj[0] = Integer.valueOf(i / 60);
        aobj[1] = Integer.valueOf(i % 60);
        return String.format("%d:%02d", aobj);
    }

    public Float getProp()
    {
        if(prop == null)
        {
            calcImageProp();
            FlayvrApplication.runAction(new Runnable() {
                @Override
                public void run() {
                    FlayvrsDBManager.getInstance().updateItem(VideoMediaItem.this);
                }
            });
        }
        return prop;
    }

    public Pair getSize()
    {
        if(android.os.Build.VERSION.SDK_INT >= 14)
        {
            MediaMetadataRetriever mediametadataretriever = new MediaMetadataRetriever();
            mediametadataretriever.setDataSource(getImagePath());
            String s = mediametadataretriever.extractMetadata(19);
            Integer integer = Integer.valueOf(0);
            if(s != null)
            {
                integer = Integer.valueOf(s);
            }
            String s1 = mediametadataretriever.extractMetadata(18);
            Integer integer1 = Integer.valueOf(0);
            if(s1 != null)
            {
                integer1 = Integer.valueOf(s1);
            }
            return new Pair(integer1, integer);
        } else
        {
            return null;
        }
    }

    public void setDuration(double d)
    {
        duration = d;
    }

}

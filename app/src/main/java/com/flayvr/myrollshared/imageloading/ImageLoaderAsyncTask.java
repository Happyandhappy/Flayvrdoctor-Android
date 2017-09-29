package com.flayvr.myrollshared.imageloading;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.views.itemview.IMediaItemView;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ImageLoaderAsyncTask extends AsyncTask
    implements ICancelableTask
{

    private static final String TAG = ImageLoaderAsyncTask.class.getSimpleName();
    public static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
    protected boolean animate;
    private ImagesCache cache;
    private ContentResolver cr;
    protected MediaItem item;
    protected ImageCacheBitmap.ThumbnailSize nextSize;
    protected final boolean runNext;
    protected ImageCacheBitmap.ThumbnailSize size;
    protected IMediaItemView view;

    public ImageLoaderAsyncTask(IMediaItemView imediaitemview, ImageCacheBitmap.ThumbnailSize thumbnailsize, MediaItem mediaitem)
    {
        this(imediaitemview, thumbnailsize, mediaitem, true);
    }

    public ImageLoaderAsyncTask(IMediaItemView imediaitemview, ImageCacheBitmap.ThumbnailSize thumbnailsize, MediaItem mediaitem, boolean flag)
    {
        this(imediaitemview, thumbnailsize, mediaitem, flag, true);
    }

    public ImageLoaderAsyncTask(IMediaItemView imediaitemview, ImageCacheBitmap.ThumbnailSize thumbnailsize, MediaItem mediaitem, boolean flag, boolean flag1)
    {
        cr = FlayvrApplication.getAppContext().getContentResolver();
        view = imediaitemview;
        if(!flag1 || thumbnailsize.compareTo(ImageCacheBitmap.ThumbnailSize.Normal) <= 0)
        {
            size = thumbnailsize;
        } else
        {
            size = ImageCacheBitmap.ThumbnailSize.Normal;
            nextSize = thumbnailsize;
        }
        item = mediaitem;
        cache = FlayvrApplication.getImagesCache();
        animate = flag;
        runNext = flag1;
    }

    public void cancelTask()
    {
        cancel(false);
    }

    protected Bitmap doInBackground(Void avoid[])
    {
        ImageLoaderAsyncTask imageloaderasynctask = (ImageLoaderAsyncTask)view.getTag(R.id.async_task);
        Bitmap bitmap1;
        if(imageloaderasynctask != null && (!item.equals(imageloaderasynctask.getItem()) || !equals(imageloaderasynctask)))
        {
            cancel(true);
            bitmap1 = null;
        } else
        {
            Bitmap bitmap = cache.get(getShownItem().getId());
            if(bitmap != null)
            {
                animate = false;
                ImageCacheBitmap.CustomizedThumbnailSize customizedthumbnailsize1;
                if(getShownItem().getType().intValue() == 1 && getShownItem().getOrientation().intValue() > 0)
                {
                    customizedthumbnailsize1 = new ImageCacheBitmap.CustomizedThumbnailSize(bitmap.getHeight(), bitmap.getWidth());
                } else
                {
                    customizedthumbnailsize1 = new ImageCacheBitmap.CustomizedThumbnailSize(bitmap.getWidth(), bitmap.getHeight());
                }
                if(customizedthumbnailsize1.compareTo(size) >= 0)
                {
                    if(nextSize != null && customizedthumbnailsize1.compareTo(nextSize) >= 0)
                    {
                        size = nextSize;
                        nextSize = null;
                    }
                    return bitmap;
                }
            } else
            if(view.getItem() != getShownItem() && shouldClear())
            {
                view.clearImage();
            }
            bitmap1 = AndroidImagesUtils.createBitmapForItem(cr, getShownItem(), size);
            if(bitmap1 == null)
            {
                Log.w(TAG, (new StringBuilder()).append("memory error while loading image, size ").append(size).toString());
                if(view.getImage() == null)
                {
                    size = ImageCacheBitmap.ThumbnailSize.Small;
                    nextSize = null;
                    bitmap1 = AndroidImagesUtils.createBitmapForItem(cr, getShownItem(), size);
                }
            }
            if(getShownItem() != null && bitmap1 != null && getShownItem().getId() != null && cache.get(getShownItem(), size) == null)
            {
                cache.put(getShownItem().getId(), bitmap1, size);
            }
            if(bitmap1 != null)
            {
                ImageCacheBitmap.CustomizedThumbnailSize customizedthumbnailsize;
                if(getShownItem().getType().intValue() == 1 && getShownItem().getOrientation().intValue() > 0)
                {
                    customizedthumbnailsize = new ImageCacheBitmap.CustomizedThumbnailSize(bitmap1.getHeight(), bitmap1.getWidth());
                } else
                {
                    customizedthumbnailsize = new ImageCacheBitmap.CustomizedThumbnailSize(bitmap1.getWidth(), bitmap1.getHeight());
                }
                if(nextSize != null && customizedthumbnailsize.compareTo(nextSize) >= 0)
                {
                    size = nextSize;
                    nextSize = null;
                    return bitmap1;
                }
            }
        }
        return bitmap1;
    }

    protected Object doInBackground(Object aobj[])
    {
        return doInBackground((Void[])aobj);
    }

    public MediaItem getItem()
    {
        return item;
    }

    public MediaItem getShownItem()
    {
        return item;
    }

    public ImageCacheBitmap.ThumbnailSize getSize()
    {
        return size;
    }

    public void loadNext()
    {
        AndroidImagesUtils.getBitmapForItem(view, getShownItem(), nextSize, false, false);
    }

    public void onFinish()
    {
    }

    protected void onPostExecute(Bitmap bitmap)
    {
        try {
            if(bitmap == null || view.getTag(R.id.async_task) != this)
                return;
            if(!animate || view.getImage() != null)
            {
                view.setImage(bitmap);
            } else {
                view.setImage(bitmap);
                AlphaAnimation alphaanimation = new AlphaAnimation(0.0F, 1.0F);
                alphaanimation.setDuration(200L);
                view.getImageView().startAnimation(alphaanimation);
            }
            view.setTag(R.id.async_task, null);
            if(nextSize != null)
                loadNext();
            else
                onFinish();
        } catch(IllegalStateException illegalstateexception) {
            Log.e(TAG, illegalstateexception.getMessage(), illegalstateexception);
        } catch(NullPointerException nullpointerexception) {
            Log.e(TAG, nullpointerexception.getMessage(), nullpointerexception);
        }
    }

    protected void onPostExecute(Object obj)
    {
        onPostExecute((Bitmap)obj);
    }

    protected boolean shouldClear()
    {
        return true;
    }

}

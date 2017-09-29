package com.flayvr.myrollshared.views.itemview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.data.MultipleMediaItem;
import com.flayvr.myrollshared.imageloading.AndroidImagesUtils;

public abstract class AbstractMediaItemView extends RelativeLayout
    implements IMediaItemView
{

    static LayoutInflater inflater = (LayoutInflater)FlayvrApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    protected ImageView imageView;
    protected MediaItem item;
    private Orientation orientation;
    private Float prop;
    protected View videoFrame;
    protected View view;

    public AbstractMediaItemView(Context context)
    {
        super(context);
        init(context);
    }

    public AbstractMediaItemView(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public AbstractMediaItemView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        TypedArray typedarray;
        typedarray = context.getTheme().obtainStyledAttributes(attributeset, R.styleable.ThumbnailItemView, 0, 0);
        int j;
        prop = Float.valueOf(1.0F / typedarray.getFloat(R.styleable.ThumbnailItemView_propoprtion, -1F));
        j = typedarray.getInt(R.styleable.ThumbnailItemView_viewOrientation, 2);
        switch(j){
            default:
                break;
            case 0:
                setOrientation(Orientation.HORIZONTAL);
                break;
            case 1:
                setOrientation(Orientation.VERTICAL);
                break;
            case 2:
                setOrientation(Orientation.MIXED);
                break;
        }
        typedarray.recycle();
        init(context);
    }

    public AbstractMediaItemView(Context context, MediaItem mediaitem)
    {
        super(context);
        init(context);
        setItem(mediaitem);
    }

    private void clearImageFromUIThread()
    {
        videoFrame.setVisibility(View.GONE);
        imageView.setImageBitmap(null);
    }

    public void clearImage()
    {
        if(Looper.getMainLooper().getThread() == Thread.currentThread())
            clearImageFromUIThread();
        else
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    clearImageFromUIThread();
                }
            });
    }

    public Bitmap getImage()
    {
        if(imageView.getDrawable() == null)
        {
            return null;
        } else
        {
            return ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        }
    }

    public ImageView getImageView()
    {
        return imageView;
    }

    public MediaItem getItem()
    {
        return item;
    }

    protected abstract int getLayout();

    public Orientation getOrientation()
    {
        return orientation;
    }

    protected void init(Context context)
    {
        if(inflater != null)
        {
            view = inflater.inflate(getLayout(), this);
            imageView = (ImageView)view.findViewById(R.id.thumbnail_view);
            videoFrame = view.findViewById(R.id.video_frame);
        }
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(!AndroidImagesUtils.maximumBitmapSizeWasSet())
        {
            AndroidImagesUtils.setMaxBitmapSize(canvas);
        }
    }

    protected void onMeasure(int i, int j)
    {
        if(prop == null || prop.floatValue() < 0.0F)
        {
            super.onMeasure(i, j);
        } else {
            int k = android.view.View.MeasureSpec.getSize(i) - getPaddingRight() - getPaddingLeft();
            int l = (int)((float)k / prop.floatValue());
            setMeasuredDimension(k, resolveSize(l, j));
            super.onMeasure(i, android.view.View.MeasureSpec.makeMeasureSpec(l, MeasureSpec.EXACTLY));
        }
    }

    public void setBackgroundColor(final int color)
    {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.setBackgroundColor(color);
            }
        });
    }

    public void setImage(final Bitmap bmp)
    {
        if(Looper.getMainLooper().getThread() == Thread.currentThread())
        {
            setImageFromUIThread(bmp);
        } else {
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    setImageFromUIThread(bmp);
                }
            });
        }
    }

    protected void setImageFromUIThread(Bitmap bitmap)
    {
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
        updateVideoFrame();
    }

    public void setItem(MediaItem mediaitem)
    {
        item = mediaitem;
        if(mediaitem == null)
        {
            clearImage();
        }
    }

    public void setOrientation(Orientation orientation1)
    {
        orientation = orientation1;
    }

    public void setProp(Float float1)
    {
        prop = float1;
    }

    protected void updateVideoFrame()
    {
        if(!(item instanceof MultipleMediaItem))
        {
            if(item.getType().intValue() == 2 && item.getSource().intValue() != 2)
            {
                if(videoFrame.getVisibility() == View.GONE)
                {
                    videoFrame.setVisibility(View.VISIBLE);
                }
            } else
            if(videoFrame.getVisibility() == View.VISIBLE)
            {
                videoFrame.setVisibility(View.GONE);
                return;
            }
        }
    }

    private enum Orientation
    {
        HORIZONTAL("HORIZONTAL", 0),
        VERTICAL("VERTICAL", 1),
        MIXED("MIXED", 2);

        private Orientation(String s, int i)
        {
        }
    }
}

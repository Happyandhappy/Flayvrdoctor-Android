package com.flayvr.myrollshared.views.itemview;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.data.MultipleMediaItem;
import com.flayvr.myrollshared.imageloading.AndroidImagesUtils;
import java.util.Timer;

public class MediaItemView extends AbstractMediaItemView
{

    private static final String TAG = "AbstractMediaItemView";
    private View animationIndicator;
    private ImageView placeholder;
    private boolean shouldCenterOnFace;

    public MediaItemView(Context context)
    {
        super(context);
        shouldCenterOnFace = true;
    }

    public MediaItemView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        shouldCenterOnFace = true;
    }

    public MediaItemView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        shouldCenterOnFace = true;
    }

    public MediaItemView(Context context, MediaItem mediaitem)
    {
        super(context, mediaitem);
        shouldCenterOnFace = true;
    }

    public Matrix getImageMatrix()
    {
        return imageView.getImageMatrix();
    }

    public PointF getImageOffset()
    {
        float af[] = new float[9];
        imageView.getImageMatrix().getValues(af);
        return new PointF(af[2], af[5]);
    }

    protected int getLayout()
    {
        return R.layout.item_view;
    }

    protected void init(Context context)
    {
        super.init(context);
        placeholder = (ImageView)view.findViewById(R.id.thumbnail_placeholder);
        animationIndicator = findViewById(R.id.gallery_animation_icon);
    }

    public boolean isShouldCenterOnFace()
    {
        return shouldCenterOnFace;
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        if(item != null && item.getType().intValue() == 1)
        {
            setBitmapMatrix(item);
        }
        super.onLayout(flag, i, j, k, l);
    }

    public void removePlaceholder()
    {
        placeholder.setVisibility(View.GONE);
    }

    public void resumeAnimation()
    {
        if(item instanceof MultipleMediaItem)
        {
            AndroidImagesUtils.getBitmapForItem(this, item, com.flayvr.myrollshared.imageloading.ImageCacheBitmap.ThumbnailSize.Normal);
        }
    }

    protected void setBitmapMatrix(MediaItem mediaitem)
    {
        float f = 0.0F;
        if(shouldCenterOnFace && mediaitem.getCenterX() != null && mediaitem.getCenterY() != null)
        {
            PointF pointf = new PointF(mediaitem.getCenterX().floatValue(), mediaitem.getCenterY().floatValue());
            Drawable drawable = imageView.getDrawable();
            if(drawable != null && getWidth() > 0 && getHeight() > 0 && drawable.getIntrinsicWidth() > 0 && drawable.getIntrinsicHeight() > 0)
            {
                int i = drawable.getIntrinsicWidth();
                int j = drawable.getIntrinsicHeight();
                int k = getWidth();
                int l = getHeight();
                imageView.setScaleType(android.widget.ImageView.ScaleType.MATRIX);
                final Matrix mDrawMatrix = imageView.getImageMatrix();
                float f1;
                float f2;
                if(i * l > k * j)
                {
                    f1 = (float)l / (float)j;
                    f2 = Math.max((float)k - f1 * (float)i, Math.min(0.0F, 0.5F * (float)k - f1 * (pointf.x * (float)i)));
                } else
                {
                    f1 = (float)k / (float)i;
                    f = Math.max((float)l - f1 * (float)j, Math.min(0.0F, 0.5F * (float)l - f1 * (pointf.y * (float)j)));
                    f2 = 0.0F;
                }
                mDrawMatrix.setScale(f1, f1);
                mDrawMatrix.postTranslate((int)(f2 + 0.5F), (int)(f + 0.5F));
                if(Looper.getMainLooper().getThread() == Thread.currentThread())
                {
                    imageView.setImageMatrix(mDrawMatrix);
                } else {
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageMatrix(mDrawMatrix);
                        }
                    });
                }
            }
        }
    }

    public void setDrawable(Drawable drawable)
    {
        imageView.setImageDrawable(drawable);
    }

    public void setImage(Bitmap bitmap)
    {
label0:
        {
            super.setImage(bitmap);
            if(item.getType().intValue() == 1)
            {
                if(Looper.getMainLooper().getThread() != Thread.currentThread())
                {
                    break label0;
                }
                setBitmapMatrix(item);
            }
            return;
        }
        imageView.post(new Runnable() {
            @Override
            public void run() {
                if(item != null && item.getType().intValue() == 1)
                    setBitmapMatrix(item);
            }
        });
    }

    public void setItem(MediaItem mediaitem)
    {
label0:
        {
            super.setItem(mediaitem);
            if(mediaitem != null)
            {
                if(mediaitem.isCloudItem())
                {
                    if(Looper.getMainLooper().getThread() == Thread.currentThread())
                    {
                        placeholder.setVisibility(View.VISIBLE);
                    } else
                    {
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                if(item.isCloudItem())
                                    placeholder.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
                if(!(mediaitem instanceof MultipleMediaItem))
                {
                    break label0;
                }
                animationIndicator.setVisibility(View.VISIBLE);
            }
            return;
        }
        animationIndicator.setVisibility(View.GONE);
    }

    public void setShouldCenterOnFace(boolean flag)
    {
        shouldCenterOnFace = flag;
    }

    public void stopAnimation()
    {
        if(item instanceof MultipleMediaItem)
        {
            Timer timer = (Timer)getTag(R.id.timer);
            if(timer != null)
            {
                timer.cancel();
            }
        }
    }
}

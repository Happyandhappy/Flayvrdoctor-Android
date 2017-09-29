package com.flayvr.views;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.*;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.imageloading.AndroidImagesUtils;
import com.flayvr.myrollshared.utils.GeneralUtils;
import com.nineoldandroids.view.ViewHelper;

public class PhotoForReviewCard extends RotatableCardView
{

    public Bitmap bitmap;
    private boolean isAnimating;
    private MediaItem item;

    public PhotoForReviewCard(Context context)
    {
        super(context);
    }

    public PhotoForReviewCard(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public PhotoForReviewCard(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public boolean GetIsAnimating()
    {
        return isAnimating;
    }

    public ImageView getDeleteActionImageView()
    {
        return (ImageView)findViewById(R.id.action_image);
    }

    public MediaItem getItem()
    {
        return item;
    }

    public ImageView getKeepActionImageView()
    {
        return (ImageView)findViewById(R.id.action_image2);
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(bitmap != null)
        {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            RectF rectf = new RectF(0.0F, 0.0F, getWidth(), getHeight());
            float f = GeneralUtils.pxFromDp(getContext(), 7F);
            rectf.inset(f, f);
            canvas.drawBitmap(bitmap, null, rectf, paint);
        }
    }

    protected void onMeasure(int i, int j)
    {
        View view = (View)getParent();
        float f = (float)view.getWidth() - GeneralUtils.pxFromDp(getContext(), 50F);
        float f1;
        float f2;
        if(!isInEditMode())
        {
            f1 = (int)(f / item.getNullableProp().floatValue());
        } else
        {
            f1 = (int)f;
        }
        if(f1 >= (float)view.getHeight() - GeneralUtils.pxFromDp(getContext(), 50F))
        {
            f1 = (float)view.getHeight() - GeneralUtils.pxFromDp(getContext(), 100F);
            if(!isInEditMode())
            {
                f2 = (int)(f1 * item.getNullableProp().floatValue());
            } else
            {
                f2 = f1;
            }
        } else
        {
            f2 = f;
        }
        super.onMeasure(android.view.View.MeasureSpec.makeMeasureSpec((int)f2, MeasureSpec.EXACTLY), android.view.View.MeasureSpec.makeMeasureSpec((int)f1, MeasureSpec.EXACTLY));
    }

    public Bitmap scaleCenterCrop(Bitmap bitmap1, int i, int j)
    {
        int k = bitmap1.getWidth();
        int l = bitmap1.getHeight();
        float f = Math.max((float)i / (float)k, (float)j / (float)l);
        float f1 = f * (float)k;
        float f2 = f * (float)l;
        float f3 = ((float)i - f1) / 2.0F;
        float f4 = ((float)j - f2) / 2.0F;
        RectF rectf = new RectF(f3, f4, f1 + f3, f2 + f4);
        android.graphics.Bitmap.Config config = bitmap1.getConfig();
        if(config == null)
        {
            config = android.graphics.Bitmap.Config.ARGB_8888;
        }
        Bitmap bitmap2 = Bitmap.createBitmap(i, j, config);
        (new Canvas(bitmap2)).drawBitmap(bitmap1, null, rectf, null);
        return bitmap2;
    }

    public void setIsAnimating(boolean flag)
    {
        isAnimating = flag;
    }

    public void setItem(MediaItem mediaitem, ContentResolver contentresolver)
    {
        setItem(mediaitem, contentresolver, null);
    }

    public void setItem(MediaItem mediaitem, final ContentResolver res, final OnItemLoadListener listener)
    {
        item = mediaitem;
        bitmap = null;
        final View parent = (View)getParent();
        if(parent == null)
        {
            return;
        }
        if(parent.getWidth() == 0 || parent.getHeight() == 0)
        {
            try
            {
                parent.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener(){
                    @Override
                    public void onGlobalLayout() {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        (new LoadingAsyncTask(listener, parent, res)).execute(new Object[0]);
                    }
                });
                return;
            }
            catch(Throwable throwable)
            {
            }
            return;
        } else
        {
            (new LoadingAsyncTask(listener, parent, res)).execute(new Object[0]);
            return;
        }
    }

    public void setOffsets(float f, float f1, float f2)
    {
        super.setOffsets(f, f1, f2);
        if(f1 != 0.0F)
        {
            ViewHelper.setAlpha(getKeepActionImageView(), Math.max((-3F * f1) / (float)getWidth(), 0.0F));
            ViewHelper.setAlpha(getDeleteActionImageView(), Math.max((3F * f1) / (float)getWidth(), 0.0F));
            return;
        } else
        {
            ViewHelper.setAlpha(getKeepActionImageView(), 0.0F);
            ViewHelper.setAlpha(getDeleteActionImageView(), 0.0F);
            return;
        }
    }

    private class LoadingAsyncTask extends AsyncTask
    {

        private final OnItemLoadListener listener;
        private final View parent;
        private final ContentResolver res;

        protected Object doInBackground(Object aobj[])
        {
            int i = -150 + parent.getWidth();
            int j = (int)((float)i / item.getNullableProp().floatValue());
            Bitmap bitmap1 = AndroidImagesUtils.createBitmapForItem(res, item, new com.flayvr.myrollshared.imageloading.ImageCacheBitmap.CustomizedThumbnailSize(i, j));
            if(bitmap1 == null)
            {
                bitmap1 = AndroidImagesUtils.createBitmapForItem(res, item, new com.flayvr.myrollshared.imageloading.ImageCacheBitmap.CustomizedThumbnailSize(i, j));
            }
            if(bitmap1 != null)
            {
                bitmap = scaleCenterCrop(bitmap1, i, j);
            }
            return null;
        }

        protected void onPostExecute(Object obj)
        {
            requestLayout();
            invalidate();
            if(listener != null)
            {
                listener.doAfterItemLoad();
            }
        }

        public LoadingAsyncTask(OnItemLoadListener onitemloadlistener, View view, ContentResolver contentresolver)
        {
            super();
            listener = onitemloadlistener;
            parent = view;
            res = contentresolver;
        }
    }

    public interface OnItemLoadListener
    {
        public abstract void doAfterItemLoad();
    }

    public enum Direction {
        LEFT("LEFT", 0),
        RIGHT("RIGHT", 1);

        Direction(String s, int i) {

        }
    }

}

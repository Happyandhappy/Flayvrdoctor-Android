package com.flayvr.myrollshared.views.itemview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.os.AsyncTask;
import android.util.*;
import android.view.*;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.imageloading.AndroidImagesUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.*;

public class SubsamplingScaleImageView extends View
    implements IMediaItemView
{

    public static final int SCALE_TYPE_CENTER_CROP = 2;
    public static final int SCALE_TYPE_CENTER_INSIDE = 1;
    private static final String TAG = SubsamplingScaleImageView.class.getSimpleName();
    private static final List VALID_SCALE_TYPES;
    private Anim anim;
    private Paint bitmapPaint;
    int currentSampleSize;
    private boolean debug;
    private Paint debugPaint;
    private BitmapRegionDecoder decoder;
    private final Object decoderLock;
    private GestureDetector detector;
    private float doubleTapZoomScale;
    private int fullImageSampleSize;
    private BitmapInitTask initTask;
    private boolean isPanning;
    public boolean isZoomable;
    private boolean isZooming;
    private MediaItem item;
    private BitmapTileTask loadingTask;
    private float maxScale;
    private int maxTouchCount;
    private int minimumScaleType;
    private int minimumTileDpi;
    private Float pendingScale;
    int placeHolderSampleSize;
    private boolean readySent;
    private int sHeight;
    private PointF sPendingCenter;
    private PointF sRequestedCenter;
    private int sWidth;
    private float scale;
    private float scaleStart;
    private Bitmap thumbnail;
    private Paint tileBgPaint;
    private Map tileMap;
    private PointF vCenterStart;
    private float vDistStart;
    private PointF vTranslate;
    private PointF vTranslateStart;
    private boolean zoomEnabled;

    private Context mContext;

    public SubsamplingScaleImageView(Context context)
    {
        this(context, null);
        mContext = context;
    }

    public SubsamplingScaleImageView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mContext = context;
        debug = false;
        maxScale = 2.0F;
        minimumTileDpi = -1;
        minimumScaleType = 1;
        zoomEnabled = true;
        doubleTapZoomScale = 1.0F;
        placeHolderSampleSize = -1;
        decoderLock = new Object();
        readySent = false;
        isZoomable = true;
        setMinimumDpi(160);
        setDoubleTapZoomDpi(160);
        setGestureDetector(context);
        if(attributeset != null)
        {
            TypedArray typedarray = getContext().obtainStyledAttributes(attributeset, R.styleable.SubsamplingScaleImageView);
            if(typedarray.hasValue(R.styleable.SubsamplingScaleImageView_zoomEnabled))
                setZoomEnabled(typedarray.getBoolean(R.styleable.SubsamplingScaleImageView_zoomEnabled, true));
            if(typedarray.hasValue(R.styleable.SubsamplingScaleImageView_tileBackgroundColor))
                setTileBackgroundColor(typedarray.getColor(R.styleable.SubsamplingScaleImageView_tileBackgroundColor, Color.argb(0, 0, 0, 0)));
            typedarray.recycle();
        }
    }

    private int calculateInSampleSize()
    {
        int i = 1;
        float f = scale;
        if(minimumTileDpi > 0)
        {
            DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
            float f1 = (displaymetrics.xdpi + displaymetrics.ydpi) / 2.0F;
            f = ((float)minimumTileDpi / f1) * scale;
        }
        int j = (int)(f * (float)sWidth());
        int k = (int)(f * (float)sHeight());
        int l;
        if(j == 0 || k == 0)
        {
            l = 32;
        } else
        {
            if(sHeight() > k || sWidth() > j)
            {
                l = Math.round((float)sHeight() / (float)k);
                int i1 = Math.round((float)sWidth() / (float)j);
                if(l >= i1)
                {
                    l = i1;
                }
            } else
            {
                l = i;
            }
            for(; i * 2 < l; i *= 2) { }
            if(i != l)
            {
                return i * 2;
            }
        }
        return l;
    }

    private void cancelTasks()
    {
        if(initTask != null)
            initTask.cancel(true);
        initTask = null;
        if(loadingTask != null)
            loadingTask.cancel(true);
    }

    private Rect convertRect(RectF rectf)
    {
        return new Rect((int)rectf.left, (int)rectf.top, (int)rectf.right, (int)rectf.bottom);
    }

    private RectF convertRect(Rect rect)
    {
        return new RectF(rect.left, rect.top, rect.right, rect.bottom);
    }

    private void createPaints()
    {
        if(bitmapPaint == null)
        {
            bitmapPaint = new Paint();
            bitmapPaint.setAntiAlias(true);
            bitmapPaint.setFilterBitmap(true);
            bitmapPaint.setDither(true);
        }
        if(debugPaint == null && debug)
        {
            debugPaint = new Paint();
            debugPaint.setTextSize(18F);
            debugPaint.setColor(0xffff00ff);
            debugPaint.setStyle(android.graphics.Paint.Style.STROKE);
        }
    }

    private float distance(float f, float f1, float f2, float f3)
    {
        float f4 = f - f1;
        float f5 = f2 - f3;
        return (float)Math.sqrt((double)(f4 * f4 + f5 * f5));
    }

    private float ease(long l, float f, float f1, long l1)
    {
        float f2 = (float)l / (float)l1;
        return f + f2 * -f1 * (f2 - 2.0F);
    }

    private Rect fileSRect(Rect rect)
    {
        if(item.getOrientation().intValue() == 0)
        {
            return rect;
        }
        if(item.getOrientation().intValue() == 90)
        {
            return new Rect(rect.top, sHeight - rect.right, rect.bottom, sHeight - rect.left);
        }
        if(item.getOrientation().intValue() == 180)
        {
            return new Rect(sWidth - rect.right, sHeight - rect.bottom, sWidth - rect.left, sHeight - rect.top);
        } else
        {
            return new Rect(sWidth - rect.bottom, rect.left, sWidth - rect.top, rect.right);
        }
    }

    private void fitToBounds(boolean flag)
    {
        PointF pointf = vTranslate;
        boolean flag1 = false;
        if(pointf == null)
        {
            flag1 = true;
            vTranslate = new PointF(0.0F, 0.0F);
        }
        ScaleAndTranslate scaleandtranslate = new ScaleAndTranslate(scale, vTranslate, null);
        fitToBounds(flag, scaleandtranslate);
        scale = scaleandtranslate.scale;
        if(flag1)
        {
            vTranslate = vTranslateForSCenter(new PointF(sWidth() / 2, sHeight() / 2), scale);
        }
    }

    private void fitToBounds(boolean flag, ScaleAndTranslate scaleandtranslate)
    {
        PointF pointf = scaleandtranslate.translate;
        float f = limitedScale(scaleandtranslate.scale);
        float f1 = f * (float)sWidth();
        float f2 = f * (float)sHeight();
        float f3;
        float f4;
        if(flag)
        {
            pointf.x = Math.max(pointf.x, (float)getWidth() - f1);
            pointf.y = Math.max(pointf.y, (float)getHeight() - f2);
        } else
        {
            pointf.x = Math.max(pointf.x, -f1);
            pointf.y = Math.max(pointf.y, -f2);
        }
        if(flag)
        {
            f3 = Math.max(0.0F, ((float)getWidth() - f1) / 2.0F);
            f4 = Math.max(0.0F, ((float)getHeight() - f2) / 2.0F);
        } else
        {
            f3 = Math.max(0, getWidth());
            f4 = Math.max(0, getHeight());
        }
        pointf.x = Math.min(pointf.x, f3);
        pointf.y = Math.min(pointf.y, f4);
        scaleandtranslate.scale = f;
    }

    private void initialiseBaseLayer()
    {
        synchronized (this) {
            BitmapTileTask bitmaptiletask;
            if(tileMap == null)
            {
                fitToBounds(false);
                fullImageSampleSize = calculateInSampleSize();
                initialiseTileMap();
                currentSampleSize = placeHolderSampleSize;
            }
            List list = (List)tileMap.get(Integer.valueOf(fullImageSampleSize));
            bitmaptiletask = new BitmapTileTask(decoder, decoderLock, list);
            if(android.os.Build.VERSION.SDK_INT < 11)
                bitmaptiletask.execute(new Void[0]);
            else
                bitmaptiletask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            if(loadingTask != null)
                loadingTask.cancel(true);
            loadingTask = bitmaptiletask;
        }
    }

    private void initialiseTileMap()
    {
        int i;
        int j;
        int k;
        int l;
        int i1;
        int k1;
        int j3 = 0;
        ArrayList arraylist1;
        int j2;
        int k2;
        tileMap = new LinkedHashMap();
        i = fullImageSampleSize;
        j = 1;
        k = 1;
        Tile tile = new Tile();
        tile.sampleSize = placeHolderSampleSize;
        tile.visible = true;
        tile.sRect = new Rect(0, 0, sWidth(), sHeight());
        tile.bitmap = thumbnail;
        ArrayList arraylist = new ArrayList(1);
        arraylist.add(tile);
        tileMap.put(Integer.valueOf(placeHolderSampleSize), arraylist);
        while (true) {
            l = sWidth() / j;
            i1 = sHeight() / k;
            int j1 = l / i;
            k1 = i1 / i;
            while (j1 > AndroidImagesUtils.getMaximumBitmapWidth() || (double) j1 > 1.25D * (double) getWidth() && i < fullImageSampleSize) {
                int l1 = j + 1;
                int i2 = l / i;
                l = sWidth() / l1;
                j1 = i2;
                j = l1;
            }

            j2 = k1;
            k2 = i1;
            for (; j2 > AndroidImagesUtils.getMaximumBitmapHieght() || (double) j2 > 1.25D * (double) getHeight() && i < fullImageSampleSize; j2 = j3) {
                int l2 = k + 1;
                int i3 = sHeight() / l2;
                j3 = i3 / i;
                k = l2;
                k2 = i3;
            }

            arraylist1 = new ArrayList(j * k);
            for (int k3 = 0; k3 < j; k3++) {
                int l3 = 0;
                while (l3 < k) {
                    Tile tile1 = new Tile();
                    tile1.sampleSize = i;
                    boolean flag;
                    if (i == fullImageSampleSize) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                    tile1.visible = flag;
                    tile1.sRect = new Rect(k3 * l, l3 * k2, l * (k3 + 1), k2 * (l3 + 1));
                    arraylist1.add(tile1);
                    l3++;
                }
            }

            tileMap.put(Integer.valueOf(i), arraylist1);
            if (i == 1)
                return;
            i /= 2;
        }
    }

    private PointF limitedSCenter(PointF pointf, float f)
    {
        PointF pointf1 = vTranslateForSCenter(pointf, f);
        int _tmp = getHeight() / 2;
        return new PointF(((float)(getWidth() / 2) - pointf1.x) / f, ((float)(getHeight() / 2) - pointf1.y) / f);
    }

    private float limitedScale(float f)
    {
        float f1 = Math.max(minScale(), f);
        return Math.min(maxScale, f1);
    }

    private void loadPlaceholder(Canvas canvas)
    {
        if(thumbnail != null)
        {
            int i = getWidth();
            int j = getHeight();
            Rect rect;
            if((float)i / (float)j < item.getNullableProp().floatValue())
            {
                int i1 = (int)((float)i / item.getNullableProp().floatValue());
                int j1 = (int)((double)(j - i1) / 2D);
                rect = new Rect(0, j1, i, i1 + j1);
            } else
            {
                int k = (int)((float)j * item.getNullableProp().floatValue());
                int l = (int)((double)(i - k) / 2D);
                rect = new Rect(l, 0, k + l, j);
            }
            canvas.drawBitmap(thumbnail, null, rect, bitmapPaint);
        }
    }

    private float minScale()
    {
        if(minimumScaleType == 1)
        {
            return Math.min((float)getWidth() / (float)sWidth(), (float)getHeight() / (float)sHeight());
        } else
        {
            return Math.max((float)getWidth() / (float)sWidth(), (float)getHeight() / (float)sHeight());
        }
    }

    private void onImageInited(BitmapRegionDecoder bitmapregiondecoder, int i, int j)
    {
        decoder = bitmapregiondecoder;
        sWidth = i;
        sHeight = j;
        invalidate();
    }

    private void onTileLoaded()
    {
        currentSampleSize = calculateInSampleSize();
        invalidate();
    }

    private void refreshRequiredTiles(boolean flag)
    {
        int i = Math.min(fullImageSampleSize, calculateInSampleSize());
        RectF rectf = viewToSourceRect(new RectF(0.0F, 0.0F, getWidth(), getHeight()));
        LinkedList linkedlist = new LinkedList();
        Iterator iterator = tileMap.entrySet().iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            Iterator iterator1 = ((List)((java.util.Map.Entry)iterator.next()).getValue()).iterator();
            do
            {
                if(!iterator1.hasNext())
                {
                    break;
                }
                Tile tile = (Tile)iterator1.next();
                if(tile.sampleSize >= i && tile.sampleSize > i && tile.sampleSize != fullImageSampleSize)
                {
                    if(tile.sampleSize == placeHolderSampleSize);
                }
                if(tile.sampleSize == i)
                {
                    if(RectF.intersects(rectf, convertRect(tile.sRect)))
                    {
                        tile.visible = true;
                        if(!tile.loading && tile.bitmap == null && flag)
                        {
                            linkedlist.add(tile);
                        }
                    } else
                    if(tile.sampleSize == fullImageSampleSize);
                } else
                if(tile.sampleSize == fullImageSampleSize)
                {
                    tile.visible = true;
                }
            } while(true);
            if(linkedlist.size() > 0)
            {
                if(loadingTask != null)
                {
                    loadingTask.cancel(true);
                }
                BitmapTileTask bitmaptiletask = new BitmapTileTask(decoder, decoderLock, linkedlist);
                if(android.os.Build.VERSION.SDK_INT >= 11)
                {
                    bitmaptiletask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                } else
                {
                    bitmaptiletask.execute(new Void[0]);
                }
                loadingTask = bitmaptiletask;
            }
        } while(true);
    }

    private void restoreState(ImageViewState imageviewstate)
    {
        if(imageviewstate != null && imageviewstate.getCenter() != null)
        {
            pendingScale = Float.valueOf(imageviewstate.getScale());
            sPendingCenter = imageviewstate.getCenter();
            invalidate();
        }
    }

    private int sHeight()
    {
        int i = item.getOrientation().intValue();
        if(i == 90 || i == 270)
        {
            return sWidth;
        } else
        {
            return sHeight;
        }
    }

    private int sWidth()
    {
        if(item.getOrientation() == null)
        {
            return sWidth;
        }
        int i = item.getOrientation().intValue();
        if(i == 90 || i == 270)
        {
            return sHeight;
        } else
        {
            return sWidth;
        }
    }

    private void setGestureDetector(final Context context)
    {
        detector = new GestureDetector(context, new _cls1());
    }

    private final void setImageFile(MediaItem mediaitem, ImageViewState imageviewstate)
    {
        restoreState(imageviewstate);
        initTask = new BitmapInitTask(mediaitem);
        if(android.os.Build.VERSION.SDK_INT >= 11)
        {
            initTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        } else
        {
            initTask.execute(new Void[0]);
        }
        invalidate();
    }

    private RectF sourceToViewRect(Rect rect)
    {
        return sourceToViewRect(convertRect(rect));
    }

    private RectF sourceToViewRect(RectF rectf)
    {
        PointF pointf = sourceToViewCoord(new PointF(rectf.left, rectf.top));
        PointF pointf1 = sourceToViewCoord(new PointF(rectf.right, rectf.bottom));
        return new RectF(pointf.x, pointf.y, pointf1.x, pointf1.y);
    }

    private PointF vTranslateForSCenter(PointF pointf, float f)
    {
        PointF pointf1 = new PointF((float)(getWidth() / 2) - f * pointf.x, (float)(getHeight() / 2) - f * pointf.y);
        fitToBounds(true, new ScaleAndTranslate(f, pointf1, null));
        return pointf1;
    }

    private RectF viewToSourceRect(RectF rectf)
    {
        PointF pointf = viewToSourceCoord(new PointF(rectf.left, rectf.top));
        PointF pointf1 = viewToSourceCoord(new PointF(rectf.right, rectf.bottom));
        return new RectF(pointf.x, pointf.y, pointf1.x, pointf1.y);
    }

    public AnimationBuilder animateCenter(PointF pointf)
    {
        if(!isImageReady())
        {
            return null;
        } else
        {
            return new AnimationBuilder(pointf, null);
        }
    }

    public AnimationBuilder animateScale(float f)
    {
        if(!isImageReady())
        {
            return null;
        } else
        {
            return new AnimationBuilder(f);
        }
    }

    public AnimationBuilder animateScaleAndCenter(float f, PointF pointf)
    {
        if(!isImageReady())
        {
            return null;
        } else
        {
            return new AnimationBuilder(f, pointf);
        }
    }

    public void clearImage()
    {
    }

    public final PointF getCenter()
    {
        int i = getWidth() / 2;
        int j = getHeight() / 2;
        return viewToSourceCoord(i, j);
    }

    public Bitmap getImage()
    {
        if(tileMap != null)
        {
            List list = (List)tileMap.get(Integer.valueOf(fullImageSampleSize));
            if(list != null && list.size() > 0)
            {
                return ((Tile)list.get(0)).bitmap;
            }
        }
        return null;
    }

    public View getImageView()
    {
        return null;
    }

    public MediaItem getItem()
    {
        return item;
    }

    public float getMaxScale()
    {
        return maxScale;
    }

    public final float getMinScale()
    {
        return minScale();
    }

    public final int getSHeight()
    {
        return sHeight;
    }

    public final int getSWidth()
    {
        return sWidth;
    }

    public final float getScale()
    {
        return scale;
    }

    public final ImageViewState getState()
    {
        if(vTranslate != null && sWidth > 0 && sHeight > 0)
        {
            return new ImageViewState(getScale(), getCenter(), item.getOrientation().intValue());
        } else
        {
            return null;
        }
    }

    public final boolean isImageReady()
    {
        return readySent && vTranslate != null && tileMap != null && sWidth > 0 && sHeight > 0;
    }

    public final boolean isZoomEnabled()
    {
        return zoomEnabled;
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        InputStream inputstream;
        Iterator iterator;
        Iterator iterator1;
        java.util.Map.Entry entry;
        Iterator iterator2;
        Tile tile1;
        boolean flag1;
        long l1;
        float f;
        float f1;
        PointF pointf4;
        PointF pointf5;
        PointF pointf6;
        boolean flag2;
        InputStream inputstream1;
        android.graphics.BitmapFactory.Options options;
        boolean flag;
        StringBuilder stringbuilder;
        Object aobj[];
        StringBuilder stringbuilder1;
        Object aobj1[];
        StringBuilder stringbuilder2;
        Object aobj2[];
        PointF pointf;
        StringBuilder stringbuilder3;
        Object aobj3[];
        StringBuilder stringbuilder4;
        Object aobj4[];
        PointF pointf1;
        PointF pointf2;
        PointF pointf3;
        createPaints();
        if(getWidth() == 0 || getHeight() == 0)
            return;
        if(sWidth == 0 || sHeight == 0) {
            loadPlaceholder(canvas);
            if (item.getWidth() == null || item.getHeight() == null) {
                try {
                    inputstream1 = item.getStream();
                }catch(Exception e){
                    inputstream1 = null;
                }
                inputstream = inputstream1;
                if (inputstream == null)
                    return;
                options = new android.graphics.BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputstream, null, options);
                sWidth = options.outWidth;
                sHeight = options.outHeight;
                if (inputstream != null)
                    try {
                        inputstream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            } else {
                sWidth = item.getWidth().intValue();
                sHeight = item.getHeight().intValue();
            }
        }
        if(tileMap == null)
        {
            initialiseBaseLayer();
        }
        if(sPendingCenter != null && pendingScale != null)
        {
            scale = pendingScale.floatValue();
            vTranslate.x = (float)(getWidth() / 2) - scale * sPendingCenter.x;
            vTranslate.y = (float)(getHeight() / 2) - scale * sPendingCenter.y;
            sPendingCenter = null;
            pendingScale = null;
            fitToBounds(true);
            refreshRequiredTiles(true);
        }
        fitToBounds(false);
        if(!readySent)
        {
            readySent = true;
        }
        int i;
        int j;
        if(anim != null)
        {
            long l = System.currentTimeMillis() - anim.time;

            if(l > anim.duration)
                flag1 = true;
            else
                flag1 = false;
            l1 = Math.min(l, anim.duration);
            scale = ease(l1, anim.scaleStart, anim.scaleEnd - anim.scaleStart, anim.duration);
            f = ease(l1, anim.vFocusStart.x, anim.vFocusEnd.x - anim.vFocusStart.x, anim.duration);
            f1 = ease(l1, anim.vFocusStart.y, anim.vFocusEnd.y - anim.vFocusStart.y, anim.duration);
            pointf4 = sourceToViewCoord(anim.sCenterEnd);
            pointf5 = vTranslate;
            pointf5.x = pointf5.x - (pointf4.x - f);
            pointf6 = vTranslate;
            pointf6.y = pointf6.y - (pointf4.y - f1);
            if(flag1 || anim.scaleStart == anim.scaleEnd)
                flag2 = true;
            else
                flag2 = false;
            fitToBounds(flag2);
            refreshRequiredTiles(flag1);
            if(flag1)
                anim = null;
            invalidate();
        }
        i = Math.min(fullImageSampleSize, calculateInSampleSize());
        if(currentSampleSize == placeHolderSampleSize)
            j = placeHolderSampleSize;
        else {
            currentSampleSize = i;
            j = i;
        }
        iterator = ((List)tileMap.get(Integer.valueOf(j))).iterator();
        flag = false;
        while(iterator.hasNext())
        {
            tile1 = (Tile)iterator.next();
            if(tile1.loading && tile1.bitmap != null) {
                flag = true;
                break;
            }
        }
        iterator1 = tileMap.entrySet().iterator();
        while(iterator1.hasNext())
        {
            entry = (java.util.Map.Entry)iterator1.next();
            if(((Integer)entry.getKey()).intValue() == j || flag) {
                iterator2 = ((List)entry.getValue()).iterator();
                while(iterator2.hasNext())
                {
                    Tile tile = (Tile)iterator2.next();
                    Rect rect = convertRect(sourceToViewRect(tile.sRect));
                    if(!tile.loading && tile.bitmap != null)
                    {
                        if(tileBgPaint != null)
                        {
                            canvas.drawRect(rect, tileBgPaint);
                        }
                        canvas.drawBitmap(tile.bitmap, null, rect, bitmapPaint);
                        if(debug)
                        {
                            canvas.drawRect(rect, debugPaint);
                        }
                    } else
                    if(tile.loading && debug)
                    {
                        canvas.drawText("LOADING", 5 + rect.left, 35 + rect.top, debugPaint);
                    }
                    if(tile.visible && debug)
                    {
                        canvas.drawText((new StringBuilder()).append("ISS ").append(tile.sampleSize).append(" RECT ").append(tile.sRect.top).append(",").append(tile.sRect.left).append(",").append(tile.sRect.bottom).append(",").append(tile.sRect.right).toString(), 5 + rect.left, 15 + rect.top, debugPaint);
                    }
                }
            }
        }
        if(!debug)
            return;
        stringbuilder = (new StringBuilder()).append("Scale: ");
        aobj = new Object[1];
        aobj[0] = Float.valueOf(scale);
        canvas.drawText(stringbuilder.append(String.format("%.2f", aobj)).toString(), 5F, 15F, debugPaint);
        stringbuilder1 = (new StringBuilder()).append("Translate: ");
        aobj1 = new Object[1];
        aobj1[0] = Float.valueOf(vTranslate.x);
        stringbuilder2 = stringbuilder1.append(String.format("%.2f", aobj1)).append(":");
        aobj2 = new Object[1];
        aobj2[0] = Float.valueOf(vTranslate.y);
        canvas.drawText(stringbuilder2.append(String.format("%.2f", aobj2)).toString(), 5F, 35F, debugPaint);
        pointf = getCenter();
        stringbuilder3 = (new StringBuilder()).append("Source center: ");
        aobj3 = new Object[1];
        aobj3[0] = Float.valueOf(pointf.x);
        stringbuilder4 = stringbuilder3.append(String.format("%.2f", aobj3)).append(":");
        aobj4 = new Object[1];
        aobj4[0] = Float.valueOf(pointf.y);
        canvas.drawText(stringbuilder4.append(String.format("%.2f", aobj4)).toString(), 5F, 55F, debugPaint);
        if(anim != null) {
            pointf1 = sourceToViewCoord(anim.sCenterStart);
            pointf2 = sourceToViewCoord(anim.sCenterEndRequested);
            pointf3 = sourceToViewCoord(anim.sCenterEnd);
            canvas.drawCircle(pointf1.x, pointf1.y, 10F, debugPaint);
            canvas.drawCircle(pointf2.x, pointf2.y, 20F, debugPaint);
            canvas.drawCircle(pointf3.x, pointf3.y, 25F, debugPaint);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, 30F, debugPaint);
        }
    }

    protected void onMeasure(int i, int j)
    {
        boolean flag;
        int i1;
        int j1;
        boolean flag1;
        int k1 = 0;
        int l1 = 0;
        flag = true;
        int k = android.view.View.MeasureSpec.getMode(i);
        int l = android.view.View.MeasureSpec.getMode(j);
        i1 = android.view.View.MeasureSpec.getSize(i);
        j1 = android.view.View.MeasureSpec.getSize(j);
        if(k != 0x40000000)
            flag1 = flag;
        else
            flag1 = false;
        if(l == 0x40000000)
            flag = false;
        if(sWidth <= 0 || sHeight <= 0) {
            k1 = j1;
            l1 = i1;
        }else if(!flag1 || !flag) {
            if(flag)
            {
                k1 = (int)(((double)sHeight() / (double)sWidth()) * (double)i1);
                l1 = i1;
            }else if(flag1){
                l1 = (int)(((double)sWidth() / (double)sHeight()) * (double)j1);
                k1 = j1;
            }
        }else {
            l1 = sWidth();
            k1 = sHeight();
        }
        setMeasuredDimension(Math.max(l1, getSuggestedMinimumWidth()), Math.max(k1, getSuggestedMinimumHeight()));
    }

    protected void onSizeChanged(int i, int j, int k, int l)
    {
        if(readySent)
        {
            setScaleAndCenter(getScale(), getCenter());
        }
    }

    public boolean onTouchEvent(MotionEvent motionevent)
    {
        boolean flag;
        boolean flag1;
        float f;
        float f1;
        boolean flag2;
        float f2;
        float f3;
        int i;
        if(!isZoomable)
            return true;
        if(anim != null && !anim.interruptible)
        {
            getParent().requestDisallowInterceptTouchEvent(true);
            return true;
        }
        anim = null;
        if(vTranslate == null || detector == null || detector.onTouchEvent(motionevent))
            return true;
        i = motionevent.getPointerCount();
        switch (motionevent.getAction()) {
            default:
                break;
            case 0:case 5:case 261:
                anim = null;
                getParent().requestDisallowInterceptTouchEvent(true);
                maxTouchCount = Math.max(maxTouchCount, i);
                if(i >= 2)
                {
                    if(zoomEnabled)
                    {
                        float f9 = distance(motionevent.getX(0), motionevent.getX(1), motionevent.getY(0), motionevent.getY(1));
                        scaleStart = scale;
                        vDistStart = f9;
                        vTranslateStart = new PointF(vTranslate.x, vTranslate.y);
                        vCenterStart = new PointF((motionevent.getX(0) + motionevent.getX(1)) / 2.0F, (motionevent.getY(0) + motionevent.getY(1)) / 2.0F);
                        return true;
                    } else {
                        maxTouchCount = 0;
                        return true;
                    }
                } else {
                    vTranslateStart = new PointF(vTranslate.x, vTranslate.y);
                    vCenterStart = new PointF(motionevent.getX(), motionevent.getY());
                    return true;
                }
            case 1:case 6:case 262:
                if(maxTouchCount > 0 && (isZooming || isPanning))
                {
                    if(isZooming && i == 2)
                    {
                        isPanning = true;
                        vTranslateStart = new PointF(vTranslate.x, vTranslate.y);
                        if(motionevent.getActionIndex() == 1)
                        {
                            vCenterStart = new PointF(motionevent.getX(0), motionevent.getY(0));
                        } else
                        {
                            vCenterStart = new PointF(motionevent.getX(1), motionevent.getY(1));
                        }
                    }
                    if(i < 3)
                    {
                        isZooming = false;
                    }
                    if(i < 2)
                    {
                        isPanning = false;
                        maxTouchCount = 0;
                    }
                    refreshRequiredTiles(true);
                    return true;
                }
                if(i == 1)
                {
                    isZooming = false;
                    isPanning = false;
                    maxTouchCount = 0;
                    return true;
                }
                break;
            case 2:
                int j = maxTouchCount;
                flag = false;
                if(j > 0)
                {
                    if(i < 2) {
                        flag1 = isZooming;
                        flag = false;
                        if(!flag1) {
                            f = Math.abs(motionevent.getX() - vCenterStart.x);
                            f1 = Math.abs(motionevent.getY() - vCenterStart.y);
                            if(!(f > 5F || f1 > 5F)) {
                                flag2 = isPanning;
                                flag = false;
                                if(!flag2) {
                                    if(flag)
                                    {
                                        invalidate();
                                        return true;
                                    }
                                    break;
                                }
                            }
                            vTranslate.x = vTranslateStart.x + (motionevent.getX() - vCenterStart.x);
                            vTranslate.y = vTranslateStart.y + (motionevent.getY() - vCenterStart.y);
                            f2 = vTranslate.x;
                            f3 = vTranslate.y;
                            fitToBounds(true);
                            if (f2 != vTranslate.x && (f3 != vTranslate.y || f1 <= 10F)) {
                                if (f > 5F) {
                                    maxTouchCount = 0;
                                    getParent().requestDisallowInterceptTouchEvent(false);
                                    return true;
                                }
                            } else
                                isPanning = true;
                            refreshRequiredTiles(false);
                            flag = true;
                        }
                    }else {
                        float f4 = distance(motionevent.getX(0), motionevent.getX(1), motionevent.getY(0), motionevent.getY(1));
                        PointF pointf = new PointF((motionevent.getX(0) + motionevent.getX(1)) / 2.0F, (motionevent.getY(0) + motionevent.getY(1)) / 2.0F);
                        boolean flag3 = zoomEnabled;
                        flag = false;
                        if (flag3) {
                            if (distance(vCenterStart.x, pointf.x, vCenterStart.y, pointf.y) <= 5F && Math.abs(f4 - vDistStart) <= 5F) {
                                boolean flag4 = isPanning;
                                flag = false;
                                if (flag4) {
                                    isZooming = true;
                                    isPanning = true;
                                    scale = Math.min(maxScale, (f4 / vDistStart) * scaleStart);
                                    if (scale <= minScale()) {
                                        vDistStart = f4;
                                        scaleStart = minScale();
                                        vCenterStart = pointf;
                                        vTranslateStart = vTranslate;
                                    } else {
                                        float f5 = vCenterStart.x - vTranslateStart.x;
                                        float f6 = vCenterStart.y - vTranslateStart.y;
                                        float f7 = f5 * (scale / scaleStart);
                                        float f8 = f6 * (scale / scaleStart);
                                        vTranslate.x = pointf.x - f7;
                                        vTranslate.y = pointf.y - f8;
                                    }
                                    fitToBounds(true);
                                    refreshRequiredTiles(false);
                                    flag = true;
                                }
                            } else {
                                isZooming = true;
                                isPanning = true;
                                scale = Math.min(maxScale, (f4 / vDistStart) * scaleStart);
                                if (scale <= minScale()) {
                                    vDistStart = f4;
                                    scaleStart = minScale();
                                    vCenterStart = pointf;
                                    vTranslateStart = vTranslate;
                                } else {
                                    float f5 = vCenterStart.x - vTranslateStart.x;
                                    float f6 = vCenterStart.y - vTranslateStart.y;
                                    float f7 = f5 * (scale / scaleStart);
                                    float f8 = f6 * (scale / scaleStart);
                                    vTranslate.x = pointf.x - f7;
                                    vTranslate.y = pointf.y - f8;
                                }
                                fitToBounds(true);
                                refreshRequiredTiles(false);
                                flag = true;
                            }
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(motionevent);
    }

    public void reset(boolean flag)
    {
        scale = 0.0F;
        scaleStart = 0.0F;
        vTranslate = null;
        vTranslateStart = null;
        pendingScale = null;
        sPendingCenter = null;
        sRequestedCenter = null;
        isZooming = false;
        isPanning = false;
        maxTouchCount = 0;
        fullImageSampleSize = 0;
        vCenterStart = null;
        vDistStart = 0.0F;
        anim = null;
        item = null;
        cancelTasks();
        if(flag)
        {
            if(decoder != null)
            {
                synchronized(decoderLock)
                {
                    decoder.recycle();
                    decoder = null;
                }
            }
            sWidth = 0;
            sHeight = 0;
            readySent = false;
        }
        if(tileMap == null)
        {
            tileMap = null;
            clearImage();
            return;
        }
        for(Iterator iterator = tileMap.entrySet().iterator(); iterator.hasNext();)
        {
            Iterator iterator1 = ((List)((java.util.Map.Entry)iterator.next()).getValue()).iterator();
            while(iterator1.hasNext()) 
            {
                ((Tile)iterator1.next()).reset();
            }
        }
    }

    public final void resetScaleAndCenter()
    {
        anim = null;
        pendingScale = Float.valueOf(limitedScale(0.0F));
        if(isImageReady())
        {
            sPendingCenter = new PointF(sWidth() / 2, sHeight() / 2);
        } else
        {
            sPendingCenter = new PointF(0.0F, 0.0F);
        }
        invalidate();
    }

    public final void setDebug(boolean flag)
    {
        debug = flag;
    }

    public final void setDoubleTapZoomDpi(int i)
    {
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        setDoubleTapZoomScale((displaymetrics.xdpi + displaymetrics.ydpi) / 2.0F / (float)i);
    }

    public final void setDoubleTapZoomScale(float f)
    {
        doubleTapZoomScale = f;
    }

    public void setImage(Bitmap bitmap)
    {
        thumbnail = bitmap;
        if(tileMap != null)
        {
            ((Tile)((List)tileMap.get(Integer.valueOf(placeHolderSampleSize))).get(0)).bitmap = bitmap;
        }
    }

    public final void setImageFile(MediaItem mediaitem)
    {
        if(decoder == null && initTask == null)
        {
            setImageFile(mediaitem, null);
        }
    }

    public void setItem(MediaItem mediaitem)
    {
        item = mediaitem;
    }

    public final void setMaxScale(float f)
    {
        maxScale = f;
    }

    public final void setMinimumDpi(int i)
    {
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        setMaxScale((displaymetrics.xdpi + displaymetrics.ydpi) / 2.0F / (float)i);
    }

    public final void setMinimumScaleType(int i)
    {
        if(!VALID_SCALE_TYPES.contains(Integer.valueOf(i)))
        {
            throw new IllegalArgumentException((new StringBuilder()).append("Invalid scale type: ").append(i).toString());
        }
        minimumScaleType = i;
        if(isImageReady())
        {
            fitToBounds(true);
            invalidate();
        }
    }

    public void setMinimumTileDpi(int i)
    {
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        minimumTileDpi = (int)Math.min((displaymetrics.xdpi + displaymetrics.ydpi) / 2.0F, i);
        if(isImageReady())
        {
            reset(false);
            invalidate();
        }
    }

    public final void setScaleAndCenter(float f, PointF pointf)
    {
        anim = null;
        pendingScale = Float.valueOf(f);
        sPendingCenter = pointf;
        sRequestedCenter = pointf;
        invalidate();
    }

    public final void setTileBackgroundColor(int i)
    {
        if(Color.alpha(i) == 0)
        {
            tileBgPaint = null;
        } else
        {
            tileBgPaint = new Paint();
            tileBgPaint.setStyle(android.graphics.Paint.Style.FILL);
            tileBgPaint.setColor(i);
        }
        invalidate();
    }

    public final void setZoomEnabled(boolean flag)
    {
        zoomEnabled = flag;
    }

    public final PointF sourceToViewCoord(float f, float f1)
    {
        if(vTranslate == null)
        {
            return null;
        } else
        {
            return new PointF(f * scale + vTranslate.x, f1 * scale + vTranslate.y);
        }
    }

    public final PointF sourceToViewCoord(PointF pointf)
    {
        return sourceToViewCoord(pointf.x, pointf.y);
    }

    public final PointF viewToSourceCoord(float f, float f1)
    {
        if(vTranslate == null)
        {
            return null;
        } else
        {
            return new PointF((f - vTranslate.x) / scale, (f1 - vTranslate.y) / scale);
        }
    }

    public final PointF viewToSourceCoord(PointF pointf)
    {
        return viewToSourceCoord(pointf.x, pointf.y);
    }

    static 
    {
        Integer ainteger[] = new Integer[2];
        ainteger[0] = Integer.valueOf(2);
        ainteger[1] = Integer.valueOf(1);
        VALID_SCALE_TYPES = Arrays.asList(ainteger);
    }

    private class BitmapInitTask extends AsyncTask<Void, Void, int[]>
    {

        private BitmapRegionDecoder decoder;
        private final MediaItem item;
        private final WeakReference viewRef;

        protected int[] doInBackground(Void avoid[])
        {
            InputStream inputstream1;
            InputStream inputstream;
            if(viewRef == null)
            {
                inputstream = null;
                return null;
            }
            try {
                inputstream1 = item.getStream();
                inputstream = inputstream1;
                if(inputstream == null)
                    return null;
                int ai[];
                decoder = BitmapRegionDecoder.newInstance(inputstream, true);
                ai = new int[2];
                ai[0] = decoder.getWidth();
                ai[1] = decoder.getHeight();
                if (inputstream != null)
                    inputstream.close();
                return ai;
            }catch(Exception e){
                return null;
            }
        }

        protected void onPostExecute(int ai[])
        {
            if(viewRef != null && decoder != null)
            {
                SubsamplingScaleImageView subsamplingscaleimageview = (SubsamplingScaleImageView)viewRef.get();
                if(subsamplingscaleimageview != null)
                {
                    subsamplingscaleimageview.initTask = null;
                    if(decoder != null && ai != null)
                    {
                        subsamplingscaleimageview.onImageInited(decoder, ai[0], ai[1]);
                    }
                    subsamplingscaleimageview.initialiseBaseLayer();
                }
            }
        }

        public BitmapInitTask(MediaItem mediaitem)
        {
            viewRef = new WeakReference(SubsamplingScaleImageView.this);
            item = mediaitem;
        }
    }

    private class Tile
    {

        protected Bitmap bitmap;
        protected boolean loading;
        protected Rect sRect;
        protected int sampleSize;
        protected boolean visible;

        protected void reset()
        {
            visible = false;
            if(bitmap != null)
            {
                if(bitmap != thumbnail)
                {
                    bitmap.recycle();
                }
                bitmap = null;
            }
        }

        private Tile()
        {
            super();
        }
    }

    private class BitmapTileTask extends AsyncTask<Void, Void, Void>
    {
        private final WeakReference decoderLockRef;
        private final WeakReference decoderRef;
        private List tileRefs;
        private final WeakReference viewRef;

        protected Void doInBackground(Void avoid[])
        {
            BitmapRegionDecoder bitmapregiondecoder;
            Object obj;
            android.graphics.BitmapFactory.Options options;
            Bitmap bitmap1;
            SubsamplingScaleImageView subsamplingscaleimageview;
            bitmapregiondecoder = (BitmapRegionDecoder)decoderRef.get();
            obj = decoderLockRef.get();
            subsamplingscaleimageview = (SubsamplingScaleImageView)viewRef.get();
            if(decoderRef == null || viewRef == null || bitmapregiondecoder == null || obj == null)
                return null;
            Bitmap bitmap;
            Iterator iterator = tileRefs.iterator();
            while(iterator.hasNext()){
                Tile tile = (Tile) ((WeakReference) iterator.next()).get();
                try {
                    if (tile == null || subsamplingscaleimageview == null) {
                        if (tile != null)
                            tile.loading = false;
                    } else if (bitmapregiondecoder.isRecycled()) {
                        if (tile != null)
                            tile.loading = false;
                    } else {
                        options = new android.graphics.BitmapFactory.Options();
                        options.inSampleSize = tile.sampleSize;
                        options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
                        bitmap = bitmapregiondecoder.decodeRegion(subsamplingscaleimageview.fileSRect(tile.sRect), options);
                        int i = subsamplingscaleimageview.getItem().getOrientation().intValue();
                        if (i != 0) {
                            Matrix matrix = new Matrix();
                            matrix.postRotate(i);
                            bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            bitmap.recycle();
                            bitmap = bitmap1;
                            tile.bitmap = bitmap;
                        }
                        tile.loading = false;
                    }
                }catch (OutOfMemoryError outofmemoryerror) {
                    tile.loading = false;
                }catch(Exception e){}
            }
            return null;
        }

        protected void onPostExecute(Void void1)
        {
            if(!isCancelled())
            {
                SubsamplingScaleImageView subsamplingscaleimageview = (SubsamplingScaleImageView)viewRef.get();
                if(subsamplingscaleimageview != null)
                {
                    subsamplingscaleimageview.loadingTask = null;
                    subsamplingscaleimageview.onTileLoaded();
                }
            }
        }

        public BitmapTileTask(BitmapRegionDecoder bitmapregiondecoder, Object obj, List list)
        {
            viewRef = new WeakReference(SubsamplingScaleImageView.this);
            decoderRef = new WeakReference(bitmapregiondecoder);
            decoderLockRef = new WeakReference(obj);
            tileRefs = new LinkedList();
            for(Iterator iterator = list.iterator(); iterator.hasNext();)
            {
                Tile tile = (Tile)iterator.next();
                tileRefs.add(new WeakReference(tile));
                tile.loading = true;
            }

        }
    }


    private class ScaleAndTranslate
    {

        private float scale;
        private PointF translate;

        private ScaleAndTranslate(float f, PointF pointf)
        {
            scale = f;
            translate = pointf;
        }

        ScaleAndTranslate(float f, PointF pointf, _cls1 _pcls1)
        {
            this(f, pointf);
        }
    }


    public class _cls1 extends android.view.GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onDoubleTap(MotionEvent motionevent)
        {
            if(!isZoomable)
            {
                return true;
            }
            if(zoomEnabled && readySent && vTranslate != null)
            {
                float f = Math.min(maxScale, scale * doubleTapZoomScale);
                float f1 = Math.min((float)getWidth() / (float)sWidth(), (float)getHeight() / (float)sHeight());
                boolean flag;
                PointF pointf;
                if(f1 >= scale)
                {
                    flag = true;
                } else
                {
                    flag = false;
                }
                if(!flag)
                {
                    f = f1;
                }
                pointf = viewToSourceCoord(new PointF(motionevent.getX(), motionevent.getY()));
                if(!flag)
                {
                    (new SubsamplingScaleImageView.AnimationBuilder(f, pointf)).withInterruptible(false).start();
                } else
                {
                    (new AnimationBuilder(f, pointf, new PointF(motionevent.getX(), motionevent.getY()), null)).withInterruptible(false).start();
                }
                setGestureDetector(mContext);
                invalidate();
                return true;
            } else
            {
                return super.onDoubleTapEvent(motionevent);
            }
        }

        @Override
        public boolean onFling(MotionEvent motionevent, MotionEvent motionevent1, float f, float f1)
        {
            if(readySent && vTranslate != null && motionevent != null && motionevent1 != null && (Math.abs(motionevent.getX() - motionevent1.getX()) > 50F || Math.abs(motionevent.getY() - motionevent1.getY()) > 50F) && (Math.abs(f) > 500F || Math.abs(f1) > 500F) && !isZooming)
            {
                PointF pointf = new PointF(vTranslate.x + f * 0.25F, vTranslate.y + 0.25F * f1);
                float f2 = ((float)(getWidth() / 2) - pointf.x) / scale;
                float f3 = ((float)(getHeight() / 2) - pointf.y) / scale;
                (new AnimationBuilder(new PointF(f2, f3), null)).withPanLimited(false).start();
                return true;
            } else
            {
                return super.onFling(motionevent, motionevent1, f, f1);
            }
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionevent)
        {
            performClick();
            return true;
        }

        _cls1()
        {
            super();
        }
    }


    private class AnimationBuilder
    {
        private long duration;
        private boolean interruptible;
        private boolean panLimited;
        private final PointF targetSCenter;
        private final float targetScale;
        private final PointF vFocus;

        private AnimationBuilder withPanLimited(boolean flag)
        {
            panLimited = flag;
            return this;
        }

        public void start()
        {
            float f = limitedScale(targetScale);
            PointF pointf;
            if(panLimited)
            {
                pointf = limitedSCenter(targetSCenter, f);
            } else
            {
                pointf = targetSCenter;
            }
            anim = new Anim(null);
            anim.scaleStart = scale;
            anim.scaleEnd = f;
            anim.time = System.currentTimeMillis();
            anim.sCenterEndRequested = pointf;
            anim.sCenterStart = getCenter();
            anim.sCenterEnd = pointf;
            anim.vFocusStart = sourceToViewCoord(pointf);
            anim.vFocusEnd = new PointF(getWidth() / 2, getHeight() / 2);
            anim.duration = duration;
            anim.interruptible = interruptible;
            anim.time = System.currentTimeMillis();
            if(vFocus != null)
            {
                float f1 = vFocus.x - f * anim.sCenterStart.x;
                float f2 = vFocus.y - f * anim.sCenterStart.y;
                ScaleAndTranslate scaleandtranslate = new ScaleAndTranslate(f, new PointF(f1, f2), null);
                fitToBounds(true, scaleandtranslate);
                anim.vFocusEnd = new PointF(vFocus.x + (scaleandtranslate.translate.x - f1), vFocus.y + (scaleandtranslate.translate.y - f2));
            }
            invalidate();
        }

        public AnimationBuilder withDuration(long l)
        {
            duration = l;
            return this;
        }

        public AnimationBuilder withInterruptible(boolean flag)
        {
            interruptible = flag;
            return this;
        }

        private AnimationBuilder(float f)
        {
            super();
            duration = 500L;
            interruptible = true;
            panLimited = true;
            targetScale = f;
            targetSCenter = getCenter();
            vFocus = null;
        }

        private AnimationBuilder(float f, PointF pointf)
        {
            super();
            duration = 500L;
            interruptible = true;
            panLimited = true;
            targetScale = f;
            targetSCenter = pointf;
            vFocus = null;
        }

        private AnimationBuilder(float f, PointF pointf, PointF pointf1)
        {
            super();
            duration = 500L;
            interruptible = true;
            panLimited = true;
            targetScale = f;
            targetSCenter = pointf;
            vFocus = pointf1;
        }

        AnimationBuilder(float f, PointF pointf, PointF pointf1, _cls1 _pcls1)
        {
            this(f, pointf, pointf1);
        }

        AnimationBuilder(float f, PointF pointf, _cls1 _pcls1)
        {
            this(f, pointf);
        }

        AnimationBuilder(float f, _cls1 _pcls1)
        {
            this(f);
        }

        private AnimationBuilder(PointF pointf)
        {
            super();
            duration = 500L;
            interruptible = true;
            panLimited = true;
            targetScale = scale;
            targetSCenter = pointf;
            vFocus = null;
        }

        AnimationBuilder(PointF pointf, _cls1 _pcls1)
        {
            this(pointf);
        }
    }


    private class Anim
    {
        private long duration;
        private boolean interruptible;
        private PointF sCenterEnd;
        private PointF sCenterEndRequested;
        private PointF sCenterStart;
        private float scaleEnd;
        private float scaleStart;
        private long time;
        private PointF vFocusEnd;
        private PointF vFocusStart;

        private Anim()
        {
            duration = 500L;
            interruptible = true;
            time = System.currentTimeMillis();
        }

        Anim(_cls1 _pcls1)
        {
            this();
        }
    }

}

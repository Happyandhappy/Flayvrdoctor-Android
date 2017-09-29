package com.flayvr.myrollshared.imageloading;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.graphics.*;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.utils.AndroidUtils;
import com.flayvr.myrollshared.views.itemview.IMediaItemView;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.RejectedExecutionException;
import org.apache.commons.io.FilenameUtils;

public class AndroidImagesUtils
{
    public static int IMAGE_MAX_SIZE = 0;
    public static final int IO_BUFFER_SIZE = 8192;
    public static int MAX_BITMAP_HIEGHT = 0;
    public static int MAX_BITMAP_WIDTH = 0;
    private static final String TAG = "flayvr_images_utils";

    public AndroidImagesUtils()
    {
    }

    private static int calculateInSampleSize(android.graphics.BitmapFactory.Options options, int i)
    {
        int j = options.outWidth;
        int k = options.outHeight;
        int l = j;
        int i1 = k;
        int j1;
        for(j1 = 1; 2 * (l * i1) > i; j1 *= 2)
        {
            l /= 2;
            i1 /= 2;
        }

        if(j1 < 1)
            return 1;
        else
            return j1;
    }

    private static int calculateInSampleSize(android.graphics.BitmapFactory.Options options, int i, int j)
    {
        int k = options.outWidth;
        int l = options.outHeight;
        int i1 = k;
        int j1 = l;
        int k1;
        for(k1 = 1; i1 / 2 >= i && j1 / 2 >= j || i1 >= getMaximumBitmapWidth() || j1 >= getMaximumBitmapHieght(); k1 *= 2)
        {
            i1 /= 2;
            j1 /= 2;
        }

        if(k1 < 1)
        {
            return 1;
        } else
        {
            return k1;
        }
    }

    private static void copyStream(InputStream inputstream, OutputStream outputstream)
    {
        byte abyte0[] = new byte[8192];
        try {
            do {
                int i = inputstream.read(abyte0, 0, 8192);
                if (i == -1)
                    return;
                outputstream.write(abyte0, 0, i);
            } while (true);
        }catch(Exception e){}
    }

    public static Bitmap createBitmapForItem(ContentResolver contentresolver, MediaItem mediaitem, ImageCacheBitmap.ThumbnailSize thumbnailsize)
    {
        Bitmap bitmap;
        try {
            if (mediaitem.isCloudItem()) {
                return handlePicasa(mediaitem, thumbnailsize);
            }
            if (mediaitem instanceof URIMediaItem) {
                return handleUri(contentresolver, (URIMediaItem) mediaitem, thumbnailsize);
            }
            if (mediaitem.getType().intValue() == 1) {
                return handleLocal(contentresolver, mediaitem, thumbnailsize);
            }
            if (mediaitem.getType().intValue() != 2) {
                return null;
            }
            bitmap = handleVideo(contentresolver, mediaitem);
            return bitmap;
        }catch(Throwable throwable){
            return null;
        }
    }

    private static Bitmap createBitmapFromStream(ImageCacheBitmap.ThumbnailSize thumbnailsize, InputStream inputstream, Integer integer)
    {
        if(integer.intValue() % 180 == 0)
            return createBitmapFromStream(inputstream, thumbnailsize.width, thumbnailsize.height);
        else
            return createBitmapFromStream(inputstream, thumbnailsize.height, thumbnailsize.width);
    }

    private static Bitmap createBitmapFromStream(InputStream inputstream, int i)
    {
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inSampleSize = i;
        options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
        return BitmapFactory.decodeStream(inputstream, null, options);
    }

    private static Bitmap createBitmapFromStream(InputStream inputstream, int i, int j)
    {
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputstream, null, options);
        try {
            inputstream.reset();
        }catch(Exception e){}
        return createBitmapFromStream(inputstream, calculateInSampleSize(options, i, j));
    }

    public static Bitmap createBitmapFromThumbnail(ContentResolver contentresolver, MediaItem mediaitem, int i)
    {
        android.graphics.BitmapFactory.Options options;
        Bitmap bitmap;
        options = new android.graphics.BitmapFactory.Options();
        options.inSampleSize = i;
        options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
        try {
            Bitmap bitmap1 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(contentresolver, mediaitem.getAndroidId().longValue(), 1, options);
            bitmap = bitmap1;
        }catch(OutOfMemoryError outofmemoryerror){
            trimCache();
            bitmap = android.provider.MediaStore.Images.Thumbnails.getThumbnail(contentresolver, mediaitem.getAndroidId().longValue(), 1, options);
        }
        if(mediaitem.getThumbnail() != null)
        {
            mediaitem.setThumbnail(null);
            mediaitem.setCheckedThumbnail(null);
            mediaitem.update();
            Uri uri = android.provider.MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
            String as[] = new String[1];
            as[0] = (new StringBuilder()).append(mediaitem.getAndroidId()).append("").toString();
            contentresolver.delete(uri, "image_id = ? ", as);
        }
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(MediaItem mediaitem, int i)
    {
        android.graphics.BitmapFactory.Options options;
        MarkableInputStream markableinputstream;
        Bitmap bitmap;
        MarkableInputStream markableinputstream1;
        Bitmap bitmap1;
        Bitmap bitmap2;
        Bitmap bitmap3;
        options = new android.graphics.BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try
        {
            markableinputstream = new MarkableInputStream(mediaitem.getStream());
            markableinputstream.mark(markableinputstream.available());
            BitmapFactory.decodeStream(markableinputstream, null, options);
            markableinputstream.reset();
            options.inSampleSize = calculateInSampleSize(options, i);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
            try {
                bitmap3 = BitmapFactory.decodeStream(markableinputstream, null, options);
                bitmap = bitmap3;
            }catch(OutOfMemoryError error){
                trimCache();
                markableinputstream.reset();
                bitmap1 = BitmapFactory.decodeStream(markableinputstream, null, options);
                bitmap = bitmap1;
            }
            if(mediaitem.getOrientation().intValue() > 0)
            {
                Matrix matrix = new Matrix();
                matrix.postRotate(mediaitem.getOrientation().intValue());
                bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                bitmap = bitmap2;
            }
            if(markableinputstream != null)
                markableinputstream.close();
        }
        catch(OutOfMemoryError outofmemoryerror2)
        {
            bitmap = null;
        }catch(Exception e){
            bitmap = null;
        }
        return bitmap;
    }

    public static Uri dowanlodRemoteItemToFile(MediaItem mediaitem)
    {
        Uri uri;
        try
        {
            File file = File.createTempFile(FilenameUtils.getBaseName(mediaitem.getPath()), (new StringBuilder()).append(".").append(FilenameUtils.getExtension(mediaitem.getPath())).toString(), AndroidUtils.getExternalCacheDir(FlayvrApplication.getAppContext()));
            file.deleteOnExit();
            FileOutputStream fileoutputstream = new FileOutputStream(file);
            writeImageToStream(mediaitem.getPath(), fileoutputstream);
            uri = Uri.fromFile(file);
        }
        catch(IOException ioexception)
        {
            Log.w("flayvr_images_utils", (new StringBuilder()).append("problem while downloading url: ").append(mediaitem.getPath()).toString(), ioexception);
            return null;
        }
        return uri;
    }

    public static void getBitmapForItem(IMediaItemView imediaitemview, MediaItem mediaitem)
    {
        if(mediaitem instanceof MultipleMediaItem)
            getBitmapForMultipleItem(imediaitemview, (MultipleMediaItem)mediaitem, true);
        else
            getBitmapForItem(imediaitemview, mediaitem, true);
    }

    public static void getBitmapForItem(IMediaItemView imediaitemview, MediaItem mediaitem, ImageCacheBitmap.ThumbnailSize thumbnailsize)
    {
        if(mediaitem instanceof MultipleMediaItem)
            getBitmapForMultipleItem(imediaitemview, (MultipleMediaItem)mediaitem, thumbnailsize);
        else
            getBitmapForItem(imediaitemview, mediaitem, thumbnailsize, true);
    }

    public static void getBitmapForItem(IMediaItemView imediaitemview, MediaItem mediaitem, ImageCacheBitmap.ThumbnailSize thumbnailsize, boolean flag)
    {
        if(mediaitem instanceof MultipleMediaItem)
            getBitmapForMultipleItem(imediaitemview, (MultipleMediaItem)mediaitem, thumbnailsize, flag);
        else
            getBitmapForItem(imediaitemview, mediaitem, thumbnailsize, flag, true);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void getBitmapForItem(final IMediaItemView view, final MediaItem item, ImageCacheBitmap.ThumbnailSize thumbnailsize, final boolean animate, boolean flag)
    {
        try
        {
            ImageLoaderAsyncTask imageloaderasynctask = (ImageLoaderAsyncTask)view.getTag(R.id.async_task);
            if(imageloaderasynctask != null) {
                if (!(item == null || imageloaderasynctask.getItem() == null || !item.equals(imageloaderasynctask.getItem()) || thumbnailsize.compareTo(imageloaderasynctask.getSize()) > 0))
                    return;
                imageloaderasynctask.cancel(false);
                view.setTag(R.id.async_task, null);
            }
            Timer timer = (Timer)view.getTag(R.id.timer);
            if(timer != null)
            {
                timer.cancel();
            }
            if(item != null) {
                ImageLoaderAsyncTask imageloaderasynctask1;
                ImagesCache imagescache = FlayvrApplication.getImagesCache();
                MediaItem mediaitem = view.getItem();
                view.setItem(item);
                Bitmap bitmap = imagescache.get(item, thumbnailsize);
                boolean flag1 = true;
                if (bitmap != null) {
                    view.setImage(bitmap);
                    flag1 = false;
                } else if (flag) {
                    Bitmap bitmap1 = imagescache.get(item.getId());
                    if (bitmap1 != null) {
                        view.setImage(bitmap1);
                    } else if (item != null && !item.equals(mediaitem)) {
                        view.clearImage();
                    }
                } else if (item != null && !item.equals(mediaitem)) {
                    view.clearImage();
                }
                if (thumbnailsize.height == 0 || thumbnailsize.width == 0) {
                    RejectedExecutionException rejectedexecutionexception;
                    if (android.os.Build.VERSION.SDK_INT < 19 || view.getImageView().isAttachedToWindow()) {
                        view.getImageView().post(new Runnable() {
                            @Override
                            public void run()
                            {
                                if(view.getHeight() != 0)
                                {
                                    AndroidImagesUtils.getBitmapForItem(view, item, animate);
                                    return;
                                } else
                                {
                                    Log.w("flayvr_images_utils", "This should not happen!!!");
                                    return;
                                }
                            }
                        });
                    } else {
                        view.getImageView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                            @Override
                            public void onViewAttachedToWindow(View view1)
                            {
                                view.getImageView().removeOnAttachStateChangeListener(this);
                                view.getImageView().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(view.getHeight() != 0)
                                        {
                                            AndroidImagesUtils.getBitmapForItem(view, item, animate);
                                        } else
                                        {
                                            Log.w("flayvr_images_utils", "This should not happen!!!");
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onViewDetachedFromWindow(View view1)
                            {
                                view.getImageView().removeOnAttachStateChangeListener(this);
                            }
                        });
                    }
                    flag1 = false;
                }
                if (flag1) {
                    imageloaderasynctask1 = new ImageLoaderAsyncTask(view, thumbnailsize, item, animate, flag);
                    if (android.os.Build.VERSION.SDK_INT < 11)
                        imageloaderasynctask1.execute(new Void[0]);
                    else
                        imageloaderasynctask1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    view.setTag(R.id.async_task, imageloaderasynctask1);
                }
            }
        }
        catch(RejectedExecutionException rejectedexecutionexception)
        {
            Log.w("flayvr_images_utils", rejectedexecutionexception.getMessage());
        }
    }

    public static void getBitmapForItem(IMediaItemView imediaitemview, MediaItem mediaitem, boolean flag)
    {
        ImageCacheBitmap.CustomizedThumbnailSize customizedthumbnailsize = new ImageCacheBitmap.CustomizedThumbnailSize(imediaitemview.getWidth(), imediaitemview.getHeight());
        if(mediaitem instanceof MultipleMediaItem)
        {
            getBitmapForMultipleItem(imediaitemview, (MultipleMediaItem)mediaitem, flag);
        } else
        {
            getBitmapForItem(imediaitemview, mediaitem, ((ImageCacheBitmap.ThumbnailSize) (customizedthumbnailsize)), flag);
        }
    }

    public static void getBitmapForMultipleItem(final int index, final IMediaItemView view, final MultipleMediaItem item, final ImageCacheBitmap.ThumbnailSize size, final boolean animate, final boolean loadThumbnailFirst)
    {
        boolean flag;
        ImageLoaderAsyncTask imageloaderasynctask;
        flag = true;
        try {
            imageloaderasynctask = (ImageLoaderAsyncTask) view.getTag(R.id.async_task);
            if (imageloaderasynctask != null) {
                if (!(item == null || imageloaderasynctask.getItem() == null || !item.equals(imageloaderasynctask.getItem()) || size.compareTo(imageloaderasynctask.getSize()) > 0))
                    return;
                imageloaderasynctask.cancel(false);
                view.setTag(R.id.async_task, null);
            }
            Timer timer = (Timer) view.getTag(R.id.timer);
            if (timer != null) {
                timer.cancel();
            }
            if (item == null)
                return;
            ImagesCache imagescache = FlayvrApplication.getImagesCache();
            MediaItem mediaitem = view.getItem();
            MediaItem mediaitem1 = (MediaItem) item.getItems().get(index);
            boolean flag1;
            Bitmap bitmap;
            MultipleImageLoaderAsyncTask multipleimageloaderasynctask;
            if (view.getItem() != item) {
                view.setItem(item);
                flag1 = flag;
            } else {
                flag1 = false;
            }
            bitmap = imagescache.get(mediaitem1, size);
            if (bitmap != null) {
                view.setImage(bitmap);
                flag = false;
            } else {
                Bitmap bitmap1 = imagescache.get(mediaitem1.getId());
                if (bitmap1 != null) {
                    view.setImage(bitmap1);
                } else if (!(item instanceof MultipleMediaItem)) {
                    if (mediaitem1 != null && !mediaitem1.equals(mediaitem) && flag1) {
                        view.clearImage();
                    }
                } else if (flag1) {
                    view.clearImage();
                }
            }
            if (size.height == 0 || size.width == 0) {
                view.getImageView().post(new Runnable() {
                    @Override
                    public void run()
                    {
                        if(view.getHeight() != 0)
                        {
                            AndroidImagesUtils.getBitmapForMultipleItem(view, item, animate);
                        }
                    }
                });
                flag = false;
            }
            if (flag) {
                multipleimageloaderasynctask = new MultipleImageLoaderAsyncTask(index, view, size, item, animate, loadThumbnailFirst);
                if (android.os.Build.VERSION.SDK_INT < 11)
                    multipleimageloaderasynctask.execute(new Void[0]);
                else
                    multipleimageloaderasynctask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                view.setTag(R.id.async_task, multipleimageloaderasynctask);
            }
        }catch(Exception e){

        }
        Timer timer1 = new Timer();
        view.setTag(R.id.timer, timer1);
        timer1.schedule(new TimerTask() {
            @Override
            public void run()
            {
                if(view.getItem() == item)
                {
                    AndroidImagesUtils.getBitmapForMultipleItem((1 + index) % item.getItems().size(), view, item, size, animate, loadThumbnailFirst);
                }
            }
        }, 1000L);
    }

    public static void getBitmapForMultipleItem(IMediaItemView imediaitemview, MultipleMediaItem multiplemediaitem, ImageCacheBitmap.ThumbnailSize thumbnailsize)
    {
        getBitmapForMultipleItem(imediaitemview, multiplemediaitem, thumbnailsize, true);
    }

    public static void getBitmapForMultipleItem(IMediaItemView imediaitemview, MultipleMediaItem multiplemediaitem, ImageCacheBitmap.ThumbnailSize thumbnailsize, boolean flag)
    {
        getBitmapForMultipleItem(0, imediaitemview, multiplemediaitem, thumbnailsize, flag, true);
    }

    public static void getBitmapForMultipleItem(IMediaItemView imediaitemview, MultipleMediaItem multiplemediaitem, boolean flag)
    {
        getBitmapForMultipleItem(imediaitemview, multiplemediaitem, ((ImageCacheBitmap.ThumbnailSize) (new ImageCacheBitmap.CustomizedThumbnailSize(imediaitemview.getWidth(), imediaitemview.getHeight()))), flag);
    }

    private static Bitmap getBitmapFromStream(InputStream inputstream, ImageCacheBitmap.ThumbnailSize thumbnailsize, int i)
    {
        Bitmap bitmap = null;
        try
        {
            if(inputstream != null) {
                MarkableInputStream markableinputstream;
                markableinputstream = new MarkableInputStream(inputstream);
                markableinputstream.mark(markableinputstream.available());
                if(thumbnailsize.compareTo(ImageCacheBitmap.ThumbnailSize.Small) > 0) {
                    int j = thumbnailsize.compareTo(ImageCacheBitmap.ThumbnailSize.Normal);
                    bitmap = null;
                    if(j <= 0) {
                        Bitmap bitmap1 = createBitmapFromStream(markableinputstream, 1);
                        bitmap = bitmap1;
                    }
                }else
                    bitmap = createBitmapFromStream(markableinputstream, 2);
                if(bitmap == null)
                {
                    markableinputstream.reset();
                    bitmap = createBitmapFromStream(thumbnailsize, markableinputstream, Integer.valueOf(i));
                }
                if(!(bitmap == null || i <= 0))
                {
                    Bitmap bitmap2;
                    Matrix matrix = new Matrix();
                    matrix.postRotate(i);
                    bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    bitmap.recycle();
                    bitmap = bitmap2;
                }
                if(markableinputstream != null)
                    markableinputstream.close();
            }
        }
        catch(Exception exception)
        {
        }
        return bitmap;
    }

    public static Bitmap getBitmapFromUri(ContentResolver contentresolver, Uri uri, ImageCacheBitmap.ThumbnailSize thumbnailsize, int i)
    {
        try {
            return getBitmapFromStream(contentresolver.openInputStream(uri), thumbnailsize, i);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static int getMaximumBitmapHieght()
    {
        if(MAX_BITMAP_WIDTH == -1)
        {
            return 2048;
        } else
        {
            return MAX_BITMAP_HIEGHT;
        }
    }

    public static int getMaximumBitmapWidth()
    {
        if(MAX_BITMAP_WIDTH == -1)
        {
            return 2048;
        } else
        {
            return MAX_BITMAP_WIDTH;
        }
    }

    private static Bitmap handleLocal(ContentResolver contentresolver, MediaItem mediaitem, ImageCacheBitmap.ThumbnailSize thumbnailsize)
    {
        Object obj;
        Object obj1;
        Bitmap bitmap2 = null;
        Bitmap bitmap3;
        FileInputStream fileinputstream1;
        Bitmap bitmap4;
        Bitmap bitmap;
        Bitmap bitmap1;
        FileInputStream fileinputstream;

        try
        {
            if(thumbnailsize.compareTo(ImageCacheBitmap.ThumbnailSize.Normal) > 0)
                fileinputstream = new FileInputStream(mediaitem.getPath());
            else {
                if (mediaitem.getThumbnail() == null) {
                    fileinputstream = null;
                } else {
                    fileinputstream = new FileInputStream(mediaitem.getThumbnail());
                }
            }
            bitmap = null;
            if(fileinputstream != null)
            {
                bitmap = getBitmapFromStream(fileinputstream, thumbnailsize, mediaitem.getOrientation().intValue());
            }
            if(bitmap != null) {
                obj1 = fileinputstream;
                bitmap2 = bitmap;
            }else {
                if (thumbnailsize.compareTo(ImageCacheBitmap.ThumbnailSize.Small) > 0) {
                    if (thumbnailsize.compareTo(ImageCacheBitmap.ThumbnailSize.Normal) <= 0) {
                        bitmap1 = createBitmapFromThumbnail(contentresolver, mediaitem, 1);
                        bitmap = bitmap1;
                    }
                } else {
                    Bitmap bitmap5 = createBitmapFromThumbnail(contentresolver, mediaitem, 2);
                    bitmap = bitmap5;
                }
                if (bitmap != null) {
                    obj1 = fileinputstream;
                    bitmap2 = bitmap;
                } else {
                    if (fileinputstream != null) {
                        try {
                            fileinputstream.close();
                        } catch (IOException ioexception2) {
                        }
                    }
                    fileinputstream1 = new FileInputStream(mediaitem.getPath());
                    obj = new MarkableInputStream(fileinputstream1);
                    ((InputStream) (obj)).mark(((InputStream) (obj)).available());
                    ((InputStream) (obj)).reset();
                    bitmap4 = createBitmapFromStream(thumbnailsize, ((InputStream) (obj)), mediaitem.getOrientation());
                    bitmap2 = bitmap4;
                    obj1 = obj;
                }
                if (bitmap2 != null) {
                    if (mediaitem.getOrientation().intValue() > 0) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(mediaitem.getOrientation().intValue());
                        bitmap3 = Bitmap.createBitmap(bitmap2, 0, 0, bitmap2.getWidth(), bitmap2.getHeight(), matrix, true);
                        bitmap2.recycle();
                        bitmap2 = bitmap3;
                    }
                }
            }
            if(obj1 != null)
                ((InputStream) (obj1)).close();
        }
        catch(IOException ioexception1)
        {
        }
        return bitmap2;
    }

    private static Bitmap handlePicasa(MediaItem mediaitem, ImageCacheBitmap.ThumbnailSize thumbnailsize)
    {
        ImagesDiskCache imagesdiskcache = FlayvrApplication.getDiskCache();
        DiskLruCache.Snapshot snapshot = imagesdiskcache.get(mediaitem, thumbnailsize);
        if(snapshot == null)
        {
            imagesdiskcache.put(mediaitem, thumbnailsize);
            snapshot = imagesdiskcache.get(mediaitem, thumbnailsize);
            if(snapshot == null)
            {
                return null;
            }
        }
        return getBitmapFromStream(snapshot.getInputStream(0), thumbnailsize, mediaitem.getOrientation().intValue());
    }

    private static Bitmap handleUri(ContentResolver contentresolver, URIMediaItem urimediaitem, ImageCacheBitmap.ThumbnailSize thumbnailsize)
    {
        try {
            Bitmap bitmap = getBitmapFromStream(contentresolver.openInputStream(urimediaitem.getUri()), thumbnailsize, urimediaitem.getOrientation().intValue());
            urimediaitem.setProp(Float.valueOf((1.0F * (float) bitmap.getWidth()) / (float) bitmap.getHeight()));
            return bitmap;
        } catch (Exception e){
            return null;
        }
    }

    private static Bitmap handleVideo(ContentResolver contentresolver, MediaItem mediaitem)
    {
        Bitmap bitmap = null;
        FileInputStream fileinputstream = null;
        String s = mediaitem.getThumbnail();
        try {
            if (s == null) {
                bitmap = null;
                fileinputstream = null;
            } else {
                FileInputStream fileinputstream1 = new FileInputStream(mediaitem.getThumbnail());
                Bitmap bitmap1 = createBitmapFromStream(fileinputstream1, 2);
                FileInputStream fileinputstream2 = fileinputstream1;
                bitmap = bitmap1;
                fileinputstream = fileinputstream2;
            }
            if (bitmap != null)
                return bitmap;
            if (mediaitem.getAndroidId() != null) {
                android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
                bitmap = android.provider.MediaStore.Video.Thumbnails.getThumbnail(contentresolver, mediaitem.getAndroidId().longValue(), 1, options);
                if (mediaitem.getThumbnail() != null) {
                    mediaitem.setThumbnail(null);
                    mediaitem.setCheckedThumbnail(null);
                    mediaitem.update();
                    Uri uri = android.provider.MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI;
                    String as[] = new String[1];
                    as[0] = (new StringBuilder()).append(mediaitem.getAndroidId()).append("").toString();
                    contentresolver.delete(uri, "video_id = ? ", as);
                }
            }
            if (fileinputstream != null)
                fileinputstream.close();
        }catch(Exception e){}
        return bitmap;
    }

    public static boolean maximumBitmapSizeWasSet()
    {
        return MAX_BITMAP_WIDTH != -1;
    }

    public static void setMaxBitmapSize(Canvas canvas)
    {
        if(android.os.Build.VERSION.SDK_INT < 14)
            return;
        MAX_BITMAP_WIDTH = canvas.getMaximumBitmapWidth();
        MAX_BITMAP_HIEGHT = canvas.getMaximumBitmapHeight();
    }

    public static void trimCache()
    {
        FlayvrApplication.getImagesCache().trimToSize(FlayvrApplication.getImagesCache().size() / 2);
    }

    public static void writeImageToStream(String s, OutputStream outputstream)
    {
        InputStream inputstream = null;
        try {
            InputStream inputstream1 = ((HttpURLConnection) (new URL(s)).openConnection()).getInputStream();
            BufferedOutputStream bufferedoutputstream = new BufferedOutputStream(outputstream, 8192);
            copyStream(inputstream1, bufferedoutputstream);
            if (inputstream1 != null) {
                inputstream1.close();
            }
            if (bufferedoutputstream != null)
                bufferedoutputstream.close();
        }catch (Exception e){}
    }

    static 
    {
        IMAGE_MAX_SIZE = 0x30d40;
        MAX_BITMAP_WIDTH = -1;
        MAX_BITMAP_HIEGHT = -1;
    }
}

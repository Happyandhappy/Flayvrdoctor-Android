package com.flayvr.myrollshared.imageloading;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.flayvr.myrollshared.utils.AndroidUtils;
import java.io.File;
import java.io.IOException;

public class DiskLruImageCache
{

    private static final int APP_VERSION = 1;
    private static final String TAG = DiskLruImageCache.class.getSimpleName();
    private static final int VALUE_COUNT = 1;
    private DiskLruCache mDiskCache;

    public DiskLruImageCache(Context context, String s, int i)
    {
        mDiskCache = DiskLruCache.open(getDiskCacheDir(context, s), 1, 1, i);
    }

    private File getDiskCacheDir(Context context, String s)
    {
        boolean flag;
        String s1;
        if("mounted".equals(Environment.getExternalStorageState()) || !AndroidUtils.isExternalStorageRemovable())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if(flag)
        {
            File file = AndroidUtils.getExternalCacheDir(context);
            if(file != null)
            {
                s1 = file.getPath();
            } else
            {
                s1 = context.getCacheDir().getPath();
            }
        } else
        {
            s1 = context.getCacheDir().getPath();
        }
        return new File((new StringBuilder()).append(s1).append(File.separator).append(s).toString());
    }

    public void clearCache()
    {
        Log.d(TAG, "disk cache CLEARED");
        File file = mDiskCache.getDirectory();
        long l = mDiskCache.getMaxSize();
        mDiskCache.delete();
        mDiskCache = DiskLruCache.open(file, 1, 1, l);
    }

    public boolean containsKey(String s)
    {
        DiskLruCache.Snapshot snapshot = mDiskCache.get(s);
        boolean flag;
        flag = false;
        try {
            if (snapshot != null)
                flag = true;
            if (snapshot != null)
                snapshot.close();
        }catch(Exception e){
            flag = false;
        }
        return flag;
    }

    public DiskLruCache.Snapshot getBitmap(String s)
    {
        return mDiskCache.get(s);
    }

    public File getCacheFolder()
    {
        return mDiskCache.getDirectory();
    }

    public DiskLruCache getmDiskCache()
    {
        return mDiskCache;
    }

    public void put(String s, String s1)
    {
        DiskLruCache.Editor editor = null;
        editor = mDiskCache.edit(s);
        if(editor == null)
            return;
        AndroidImagesUtils.writeImageToStream(s1, editor.newOutputStream(0));
        mDiskCache.flush();
        editor.commit();
        if(editor != null)
            editor.abort();
    }

    public long size()
    {
        return mDiskCache.size();
    }

}

package com.flayvr.myrollshared.data;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import com.flayvr.myrollshared.application.FlayvrApplication;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class URIMediaItem extends MediaItem
{

    private Uri uri;

    public URIMediaItem(Uri uri1, ContentResolver contentresolver)
    {
        uri = uri1;
        setPath(uri1.getPath());
        setOrientation(Integer.valueOf(0));
        String s = contentresolver.getType(uri1);
        if(s == null)
        {
            String s1 = MimeTypeMap.getFileExtensionFromUrl(uri1.getPath());
            if(s1 != null)
            {
                s = MimeTypeMap.getSingleton().getMimeTypeFromExtension(s1);
            }
        }
        if(s != null && s.contains("video"))
        {
            setType(Integer.valueOf(2));
            return;
        } else
        {
            setType(Integer.valueOf(1));
            return;
        }
    }

    public Integer getSource()
    {
        return Integer.valueOf(1);
    }

    public InputStream getStream()
    {
        try {
            return FlayvrApplication.getAppContext().getContentResolver().openInputStream(getUri());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Uri getUri()
    {
        return uri;
    }
}

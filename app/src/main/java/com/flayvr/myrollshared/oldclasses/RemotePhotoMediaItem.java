package com.flayvr.myrollshared.oldclasses;

import java.net.URL;

public class RemotePhotoMediaItem extends PhotoMediaItem
{

    private static final long serialVersionUID = 0xa602963bcf4c7041L;
    private boolean isSaved;
    private String thumbPath;
    private URL thumbUrl;
    private URL url;

    public RemotePhotoMediaItem(URL url1)
    {
        url = url1;
        isSaved = false;
    }

    public RemotePhotoMediaItem(URL url1, URL url2)
    {
        this(url1);
        thumbUrl = url2;
    }

    public String getThumbPath()
    {
        return thumbPath;
    }

    public URL getThumbUrl()
    {
        return thumbUrl;
    }

    public URL getUrl()
    {
        return url;
    }

    public boolean isSaved()
    {
        return isSaved;
    }

    public void setSaved(boolean flag)
    {
        isSaved = flag;
    }

    public void setThumbPath(String s)
    {
        thumbPath = s;
    }
}

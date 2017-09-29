package com.flayvr.myrollshared.oldclasses;

import java.net.URL;

public class RemoteVideoMediaItem extends VideoMediaItem
{

    private static final long serialVersionUID = 0x2d619ecedaa72ca3L;
    private String thumbnailPath;
    private URL thumbnailUrl;

    public RemoteVideoMediaItem(String s, URL url)
    {
        setImagePath(s);
        thumbnailUrl = url;
    }

    public String getThumbnailPath()
    {
        return thumbnailPath;
    }

    public URL getThumbnailUrl()
    {
        return thumbnailUrl;
    }

    public void setThumbnailPath(String s)
    {
        thumbnailPath = s;
    }
}

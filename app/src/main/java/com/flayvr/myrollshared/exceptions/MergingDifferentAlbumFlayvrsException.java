package com.flayvr.myrollshared.exceptions;


public class MergingDifferentAlbumFlayvrsException extends Exception
{

    private static final long serialVersionUID = 0xfdd53afdf882423fL;

    public MergingDifferentAlbumFlayvrsException(String s)
    {
        super(s);
    }

    public MergingDifferentAlbumFlayvrsException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public MergingDifferentAlbumFlayvrsException(Throwable throwable)
    {
        super(throwable);
    }
}

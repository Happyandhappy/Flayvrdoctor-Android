package com.flayvr.myrollshared.exceptions;


public class UploadingException extends Exception
{

    public UploadingException()
    {
    }

    public UploadingException(String s)
    {
        super(s);
    }

    public UploadingException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public UploadingException(Throwable throwable)
    {
        super(throwable);
    }
}

package com.flayvr.myrollshared.events;


public class IABOnBillingError
{

    private int errorCode;
    private Throwable throwable;

    public IABOnBillingError(int i, Throwable throwable1)
    {
        errorCode = i;
        throwable = throwable1;
    }

    public int getErrorCode()
    {
        return errorCode;
    }

    public Throwable getThrowable()
    {
        return throwable;
    }
}

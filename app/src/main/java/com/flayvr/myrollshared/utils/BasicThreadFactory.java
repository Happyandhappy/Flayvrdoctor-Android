package com.flayvr.myrollshared.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class BasicThreadFactory
    implements ThreadFactory
{

    public String prefix;
    public AtomicInteger threadNumber;

    public BasicThreadFactory(String s)
    {
        prefix = s;
        threadNumber = new AtomicInteger();
    }

    public Thread newThread(Runnable runnable)
    {
        return new Thread(runnable, (new StringBuilder()).append(prefix).append("-").append(threadNumber.getAndIncrement()).toString());
    }
}

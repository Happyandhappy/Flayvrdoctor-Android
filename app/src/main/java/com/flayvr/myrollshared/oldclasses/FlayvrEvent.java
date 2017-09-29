package com.flayvr.myrollshared.oldclasses;

import java.io.Serializable;
import java.util.Date;

public class FlayvrEvent
    implements Serializable
{

    private static final long serialVersionUID = 0xd99831ebbd3cc827L;
    Date end;
    String flayvrTitle;
    Date start;

    public FlayvrEvent(Date date, Date date1, String s)
    {
        start = date;
        end = date1;
        flayvrTitle = s;
    }

    public Date getEnd()
    {
        return end;
    }

    public String getFlayvrTitle()
    {
        return flayvrTitle;
    }

    public Date getStart()
    {
        return start;
    }

    public String toString()
    {
        return (new StringBuilder()).append(getFlayvrTitle()).append(": ").append(getStart()).append(" - ").append(getEnd()).toString();
    }
}

package com.flayvr.myrollshared.oldclasses;

import java.text.SimpleDateFormat;
import java.util.*;

public class MediaGroupRangeDate implements MediaGroupDate
{
    private static final long serialVersionUID = 0xc2b13afd10929d07L;
    private MediaGroupSingleDate endDate;
    private MediaGroupSingleDate startDate;

    public MediaGroupRangeDate(MediaGroupDate mediagroupdate, MediaGroupDate mediagroupdate1)
    {
        if(mediagroupdate instanceof MediaGroupRangeDate)
            startDate = ((MediaGroupRangeDate)mediagroupdate).getStartDate();
        else if(mediagroupdate instanceof MediaGroupSingleDate)
            startDate = (MediaGroupSingleDate)mediagroupdate;
        if(mediagroupdate1 instanceof MediaGroupRangeDate)
            endDate = ((MediaGroupRangeDate)mediagroupdate1).getEndDate();
        else if(mediagroupdate1 instanceof MediaGroupSingleDate)
            endDate = (MediaGroupSingleDate)mediagroupdate1;
    }

    public boolean after(MediaGroupDate mediagroupdate)
    {
        return endDate.after(mediagroupdate);
    }

    public int compareTo(MediaGroupDate mediagroupdate)
    {
        return endDate.compareTo(mediagroupdate);
    }

    public MediaGroupSingleDate getEndDate()
    {
        return endDate;
    }

    public Date getLastDate()
    {
        return endDate.getDate();
    }

    public MediaGroupSingleDate getStartDate()
    {
        return startDate;
    }

    public boolean inTheSameDay(MediaGroupDate mediagroupdate)
    {
        return false;
    }

    public String toString()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate.getDate());
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(endDate.getDate());
        if(calendar.get(Calendar.MONTH) != calendar1.get(Calendar.MONTH))
        {
            SimpleDateFormat simpledateformat = new SimpleDateFormat("MMM dd", Locale.getDefault());
            return (new StringBuilder()).append(simpledateformat.format(startDate.getDate())).append("-").append(simpledateformat.format(endDate.getDate())).toString();
        } else {
            SimpleDateFormat simpledateformat1 = new SimpleDateFormat("MMM dd", Locale.getDefault());
            SimpleDateFormat simpledateformat2 = new SimpleDateFormat("dd", Locale.getDefault());
            return (new StringBuilder()).append(simpledateformat1.format(startDate.getDate())).append("-").append(simpledateformat2.format(endDate.getDate())).toString();
        }
    }
}

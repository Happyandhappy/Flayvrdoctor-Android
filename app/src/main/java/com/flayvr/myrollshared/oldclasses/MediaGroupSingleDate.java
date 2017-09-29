package com.flayvr.myrollshared.oldclasses;

import com.flayvr.myrollshared.utils.AndroidUtils;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;

public class MediaGroupSingleDate implements MediaGroupDate
{
    private static final long serialVersionUID = 0xf68f254e3b46140cL;
    private Date date;

    public MediaGroupSingleDate(Date date1)
    {
        date = date1;
    }

    public boolean after(MediaGroupDate mediagroupdate)
    {
        Date date1 = mediagroupdate.getLastDate();
        return date.after(date1);
    }

    public int compareTo(MediaGroupDate mediagroupdate)
    {
        return date.compareTo(mediagroupdate.getLastDate());
    }

    public Date getDate()
    {
        return date;
    }

    public Date getLastDate()
    {
        return date;
    }

    public boolean inTheSameDay(MediaGroupDate mediagroupdate)
    {
        if(mediagroupdate instanceof MediaGroupSingleDate)
        {
            MediaGroupSingleDate mediagroupsingledate = (MediaGroupSingleDate)mediagroupdate;
            return DateUtils.isSameDay(getDate(), mediagroupsingledate.getDate());
        } else
        {
            return false;
        }
    }

    public String toString()
    {
        return AndroidUtils.getDateStr(date);
    }
}

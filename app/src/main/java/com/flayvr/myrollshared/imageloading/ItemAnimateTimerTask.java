package com.flayvr.myrollshared.imageloading;

import com.flayvr.myrollshared.data.MediaItem;
import java.util.TimerTask;

// Referenced classes of package com.flayvr.myrollshared.imageloading:
//            ICancelableTask

public abstract class ItemAnimateTimerTask extends TimerTask
    implements ICancelableTask
{

    private final MediaItem item;

    public ItemAnimateTimerTask(MediaItem mediaitem)
    {
        item = mediaitem;
    }

    public void cancelTask()
    {
        cancel();
    }

    public MediaItem getItem()
    {
        return item;
    }
}

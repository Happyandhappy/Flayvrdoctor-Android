package com.flayvr.screens.videos;

import android.content.Context;
import android.widget.TextView;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.utils.GeneralUtils;
import com.flayvr.views.GDEditMediaItemView;
import org.apache.commons.lang3.time.DurationFormatUtils;

public class LongVideoMediaItemView extends GDEditMediaItemView
{

    private TextView duration;

    public LongVideoMediaItemView(Context context)
    {
        super(context);
    }

    protected int getLayout()
    {
        return R.layout.edit_item_view_video;
    }

    protected void init(Context context)
    {
        super.init(context);
        duration = (TextView)findViewById(R.id.video_duration);
    }

    public void setItem(MediaItem mediaitem)
    {
        super.setItem(mediaitem);
        String s = DurationFormatUtils.formatDuration(mediaitem.getDuration().longValue(), "H:mm:ss", true);
        String s1 = GeneralUtils.readableFileSize(mediaitem.getFileSizeBytesSafe().longValue());
        duration.setText((new StringBuilder()).append(s).append(" / ").append(s1).toString());
    }
}

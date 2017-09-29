package com.flayvr.myrollshared.views.itemview.fullscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.views.itemview.AbstractMediaItemView;

public class FullscreenVideoView extends AbstractMediaItemView
{

    public FullscreenVideoView(Context context)
    {
        super(context);
    }

    protected int getLayout()
    {
        return R.layout.fullscreen_video_view;
    }

    protected void setImageFromUIThread(Bitmap bitmap)
    {
        imageView.setImageBitmap(bitmap);
    }

    public void setItem(MediaItem mediaitem)
    {
        super.setItem(mediaitem);
        if(Looper.getMainLooper().getThread() == Thread.currentThread())
        {
            updateVideoFrame();
            return;
        } else
        {
            videoFrame.post(new Runnable() {
                @Override
                public void run() {
                    updateVideoFrame();
                }
            });
            return;
        }
    }
}

package com.flayvr.myrollshared.views.itemview.fullscreen;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.views.itemview.AbstractMediaItemView;

public class FullscreenThumbnailView extends AbstractMediaItemView
{

    private boolean isSelected;
    private Paint paint;

    public FullscreenThumbnailView(Context context)
    {
        super(context);
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.flayvr_aqua_color));
        paint.setStyle(android.graphics.Paint.Style.STROKE);
        paint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.gallery_selected_image_frame_size));
        setLayoutParams(new it.sephiroth.android.library.widget.AbsHListView.LayoutParams(getResources().getDimensionPixelSize(R.dimen.fullscreen_thumb_width), getResources().getDimensionPixelSize(R.dimen.fullscreen_thumb_hieght)));
        isSelected = false;
    }

    public void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
        if(isSelected)
        {
            canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), paint);
        }
    }

    protected int getLayout()
    {
        return R.layout.fullscreen_thumbnail_view;
    }

    public void setIsSelected(boolean flag)
    {
        isSelected = flag;
    }
}

package com.flayvr.myrollshared.views.itemview;

import android.graphics.PointF;
import java.io.Serializable;

public class ImageViewState
    implements Serializable
{

    private float centerX;
    private float centerY;
    private int orientation;
    private float scale;

    public ImageViewState(float f, PointF pointf, int i)
    {
        scale = f;
        centerX = pointf.x;
        centerY = pointf.y;
        orientation = i;
    }

    public PointF getCenter()
    {
        return new PointF(centerX, centerY);
    }

    public int getOrientation()
    {
        return orientation;
    }

    public float getScale()
    {
        return scale;
    }
}

package com.flayvr.myrollshared.oldclasses;

import android.graphics.PointF;
import java.io.Serializable;

public class FaceData
    implements Serializable
{

    private static final long serialVersionUID = 0x3f1566c8ea59d1deL;
    private float confidence;
    private float eyeDistance;
    private float midX;
    private float midY;

    public FaceData()
    {
    }

    public FaceData(PointF pointf, float f, float f1)
    {
        setMidPoint(pointf);
        confidence = f1;
        eyeDistance = f;
    }

    public float getConfidence()
    {
        return confidence;
    }

    public float getEyeDistance()
    {
        return eyeDistance;
    }

    public PointF getMidPoint()
    {
        return new PointF(midX, midY);
    }

    public float getMidX()
    {
        return midX;
    }

    public float getMidY()
    {
        return midY;
    }

    public void setConfidence(float f)
    {
        confidence = f;
    }

    public void setEyeDistance(float f)
    {
        eyeDistance = f;
    }

    public void setMidPoint(PointF pointf)
    {
        midX = pointf.x;
        midY = pointf.y;
    }

    public void setMidX(float f)
    {
        midX = f;
    }

    public void setMidY(float f)
    {
        midY = f;
    }
}

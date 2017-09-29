package com.flayvr.myrollshared.oldclasses;

import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.util.Pair;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

public class PhotoMediaItem extends AbstractMediaItem
{

    public static Gson gson = new Gson();
    public static Type jsonType = (new TypeToken<ArrayList<FaceData>>(){}).getType();
    private static final long serialVersionUID = 0x4e577a7cf7b31fbeL;
    private Double blurry;
    private transient PointF center;
    private Double colorful;
    private Double dark;
    private ArrayList faces;
    private int facesCount;
    private String facesJson;
    protected int orientation;
    private boolean shouldRunHeavyFaceDetection;

    public PhotoMediaItem()
    {
        shouldRunHeavyFaceDetection = false;
    }

    private void calcImageSize()
    {
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(getImagePath(), options);
        if(options.outWidth == 0 || options.outHeight == 0)
        {
            prop = Float.valueOf(1.0F);
            return;
        }
        if(orientation % 180 == 0)
        {
            prop = Float.valueOf((1.0F * (float)options.outWidth) / (float)options.outHeight);
            return;
        } else
        {
            prop = Float.valueOf((1.0F * (float)options.outHeight) / (float)options.outWidth);
            return;
        }
    }

    private void readObject(ObjectInputStream objectinputstream)
    {
        try
        {
            objectinputstream.defaultReadObject();
            setCenter(new PointF(objectinputstream.readFloat(), objectinputstream.readFloat()));
        } catch(Exception eofexception) {
        }
    }

    private void writeObject(ObjectOutputStream objectoutputstream)
    {
        try {
            objectoutputstream.defaultWriteObject();
            if (center != null) {
                objectoutputstream.writeFloat(center.x);
                objectoutputstream.writeFloat(center.y);
            }
        } catch (Exception e){}
    }

    public Double getBlurry()
    {
        return blurry;
    }

    public PointF getCenter()
    {
        return center;
    }

    public Double getColorful()
    {
        return colorful;
    }

    public Double getDark()
    {
        return dark;
    }

    public ArrayList getFaces()
    {
        if(faces == null)
        {
            faces = (ArrayList)gson.fromJson(getFacesJson(), jsonType);
        }
        return faces;
    }

    public int getFacesCount()
    {
        return facesCount;
    }

    public String getFacesJson()
    {
        return facesJson;
    }

    public int getOrientation()
    {
        return orientation;
    }

    public Float getProp()
    {
        if(prop == null)
        {
            calcImageSize();
            FlayvrApplication.runAction(new Runnable() {
                @Override
                public void run() {
                    FlayvrsDBManager.getInstance().updateItem(PhotoMediaItem.this);
                }
            });
        }
        return prop;
    }

    public Double getScore()
    {
        double d = 0.0D;
        if(getBlurry() == null || getColorful() == null || getDark() == null)
        {
            return Double.valueOf(-1D);
        }
        double d1;
        double d2;
        double d3;
        if(getBlurry().doubleValue() > 0.10000000000000001D)
        {
            d1 = 0.10000000000000001D;
        } else
        {
            d1 = d;
        }
        if(getBlurry().doubleValue() > 0.90000000000000002D)
        {
            d2 = 0.10000000000000001D;
        } else
        {
            d2 = d;
        }
        d3 = d1 + d2 + 0.20000000000000001D * getBlurry().doubleValue() + 0.20000000000000001D * getDark().doubleValue() + 0.20000000000000001D * getColorful().doubleValue();
        if(facesCount > 0)
        {
            d = 0.10000000000000001D + 0.10000000000000001D * (double)facesCount;
        }
        return Double.valueOf(d3 + d);
    }

    public Pair getSize()
    {
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(getImagePath(), options);
        if(orientation % 180 == 0)
        {
            return new Pair(Integer.valueOf(options.outWidth), Integer.valueOf(options.outHeight));
        } else
        {
            return new Pair(Integer.valueOf(options.outHeight), Integer.valueOf(options.outWidth));
        }
    }

    public void setBlurry(Double double1)
    {
        blurry = double1;
    }

    public void setCenter(PointF pointf)
    {
        center = pointf;
    }

    public void setColorful(Double double1)
    {
        colorful = double1;
    }

    public void setDark(Double double1)
    {
        dark = double1;
    }

    public void setFaces(ArrayList arraylist)
    {
        faces = arraylist;
        setFacesJson(gson.toJson(arraylist));
        Iterator iterator = arraylist.iterator();
        FaceData facedata = null;
        while(iterator.hasNext()) 
        {
            FaceData facedata1 = (FaceData)iterator.next();
            if(facedata != null && facedata.getEyeDistance() >= facedata1.getEyeDistance())
            {
                facedata1 = facedata;
            }
            facedata = facedata1;
        }
        if(facedata != null)
        {
            setCenter(facedata.getMidPoint());
        } else
        {
            setCenter(null);
        }
        setFacesCount(arraylist.size());
    }

    public void setFacesCount(int i)
    {
        facesCount = i;
    }

    public void setFacesJson(String s)
    {
        facesJson = s;
    }

    public void setOrientation(int i)
    {
        orientation = i;
    }

    public void setShouldRunHeavyFaceDetection(boolean flag)
    {
        shouldRunHeavyFaceDetection = flag;
    }

    public boolean shouldRunHeavyFaceDetection()
    {
        return shouldRunHeavyFaceDetection;
    }

}

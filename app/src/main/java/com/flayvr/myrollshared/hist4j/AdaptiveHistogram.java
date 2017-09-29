package com.flayvr.myrollshared.hist4j;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;

public class AdaptiveHistogram
    implements Serializable
{

    private static final long serialVersionUID = -1L;
    private HistogramNode root;
    private long totalCount;

    public AdaptiveHistogram()
    {
        root = null;
        reset();
    }

    public void addValue(float f)
    {
        synchronized (this) {
            totalCount = 1L + totalCount;
            if(root == null)
            {
                root = new HistogramDataNode();
            }
            root = root.addValue(this, f);
        }
    }

    public long getAccumCount(float f)
    {
        long l = 0L;
        if(root != null)
        {
            l = root.getAccumCount(f);
        }
        return l;
    }

    public long getCount(float f)
    {
        long l = 0L;
        if(root != null)
        {
            l = root.getCount(f);
        }
        return l;
    }

    protected int getCountPerNodeLimit()
    {
        int i = (int)(totalCount / 10L);
        if(i == 0)
        {
            i = 1;
        }
        return i;
    }

    public float getValueForPercentile(int i)
    {
        long l = (totalCount * (long)i) / 100L;
        Float float1 = new Float(0.0F);
        if(root != null)
        {
            float1 = root.getValueForAccumCount(new long[] {
                0L, l
            });
        }
        float f = 0.0F;
        if(float1 != null)
        {
            f = float1.floatValue();
        }
        return f;
    }

    public void normalize(final float b, float f)
    {
        float f1 = 1.0F;
        if(root != null)
        {
            final float min = getValueForPercentile(0);
            float f2 = getValueForPercentile(100);
            float f3 = f - b;
            if(f2 > min)
            {
                f1 /= f2 - min;
            }
            final float m = f1 * f3;
            root.apply(new ValueConversion() {
                @Override
                public float convertValue(float f)
                {
                    return m * (f - min) + b;
                }
            });
        }
    }

    public void reset()
    {
        if(root != null)
        {
            root.reset();
            root = null;
        }
        totalCount = 0L;
    }

    public void show()
    {
        System.out.println((new StringBuilder()).append("Histogram has ").append(totalCount).append(" values:").toString());
        if(root != null)
        {
            root.show(0);
        }
    }

    public ArrayList toTable()
    {
        ArrayList arraylist = new ArrayList();
        if(root != null)
        {
            root.toTable(arraylist);
        }
        return arraylist;
    }

    public interface ValueConversion
    {
        public abstract float convertValue(float paramFloat);
    }
}

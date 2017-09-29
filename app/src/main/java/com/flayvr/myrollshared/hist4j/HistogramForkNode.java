package com.flayvr.myrollshared.hist4j;

import java.io.PrintStream;
import java.util.ArrayList;

public class HistogramForkNode extends HistogramNode
{

    private static final long serialVersionUID = -1L;
    private HistogramNode left;
    private HistogramNode right;
    private float splitValue;

    public HistogramForkNode(float f, HistogramNode histogramnode, HistogramNode histogramnode1)
    {
        left = null;
        right = null;
        splitValue = f;
        left = histogramnode;
        right = histogramnode1;
    }

    public HistogramNode addValue(AdaptiveHistogram adaptivehistogram, float f)
    {
        if(f > splitValue)
        {
            right = right.addValue(adaptivehistogram, f);
            return this;
        } else
        {
            left = left.addValue(adaptivehistogram, f);
            return this;
        }
    }

    public void apply(AdaptiveHistogram.ValueConversion valueconversion)
    {
        left.apply(valueconversion);
        right.apply(valueconversion);
        splitValue = valueconversion.convertValue(splitValue);
    }

    public long getAccumCount(float f)
    {
        long l = left.getAccumCount(f);
        if(f > splitValue)
        {
            l += right.getAccumCount(f);
        }
        return l;
    }

    public long getCount(float f)
    {
        if(f > splitValue)
        {
            return right.getCount(f);
        } else
        {
            return left.getCount(f);
        }
    }

    public Float getValueForAccumCount(long al[])
    {
        Float float1 = left.getValueForAccumCount(al);
        if(float1 == null)
        {
            float1 = right.getValueForAccumCount(al);
        }
        return float1;
    }

    public void reset()
    {
        if(left != null)
        {
            left.reset();
            left = null;
        }
        if(right != null)
        {
            right.reset();
            right = null;
        }
        splitValue = 0.0F;
    }

    public void show(int i)
    {
        left.show(i + 1);
        margin(i);
        System.out.println((new StringBuilder()).append("Fork at: ").append(splitValue).toString());
        right.show(i + 1);
    }

    public void toTable(ArrayList arraylist)
    {
        left.toTable(arraylist);
        right.toTable(arraylist);
    }
}

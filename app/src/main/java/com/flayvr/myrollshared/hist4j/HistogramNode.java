package com.flayvr.myrollshared.hist4j;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class HistogramNode
    implements Serializable
{

    private static final long serialVersionUID = -1L;

    public HistogramNode()
    {
    }

    public abstract HistogramNode addValue(AdaptiveHistogram adaptivehistogram, float f);

    public abstract void apply(AdaptiveHistogram.ValueConversion valueconversion);

    public abstract long getAccumCount(float f);

    public abstract long getCount(float f);

    public abstract Float getValueForAccumCount(long al[]);

    protected void margin(int i)
    {
        for(int j = 0; j < i; j++)
        {
            System.out.print("  ");
        }
    }

    public abstract void reset();

    public abstract void show(int i);

    public abstract void toTable(ArrayList arraylist);
}

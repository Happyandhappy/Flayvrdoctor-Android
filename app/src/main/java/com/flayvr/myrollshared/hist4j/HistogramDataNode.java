package com.flayvr.myrollshared.hist4j;

import java.io.PrintStream;
import java.util.ArrayList;

public class HistogramDataNode extends HistogramNode
{

    private static final long serialVersionUID = -1L;
    private Cell cell;

    public HistogramDataNode()
    {
        cell = new Cell();
        reset();
    }

    public HistogramDataNode(long l, float f, float f1)
    {
        cell = new Cell();
        reset();
        cell.count = l;
        cell.minValue = f;
        cell.maxValue = f1;
    }

    private float interpolate(float f, float f1, float f2, float f3, float f4)
    {
        return f1 + ((f4 - f) * (f3 - f1)) / (f2 - f);
    }

    public HistogramNode addValue(AdaptiveHistogram adaptivehistogram, float f)
    {
        boolean flag;
        long l2;
        long l3;
        if(f < cell.minValue || f > cell.maxValue) {
            if(cell.count < (long)adaptivehistogram.getCountPerNodeLimit())
            {
                Cell cell1 = cell;
                cell1.count = 1L + cell1.count;
                if(f < cell.minValue)
                {
                    cell.minValue = f;
                }
                if(f > cell.maxValue)
                {
                    cell.maxValue = f;
                    return this;
                }
            } else
            if(f < cell.minValue)
            {
                cell.minValue = Math.min(cell.minValue, (f + cell.maxValue) / 2.0F);
                return new HistogramForkNode(cell.minValue, new HistogramDataNode(1L, f, cell.minValue), this);
            } else
            {
                cell.maxValue = Math.max(cell.maxValue, (f + cell.minValue) / 2.0F);
                return new HistogramForkNode(cell.maxValue, this, new HistogramDataNode(1L, cell.maxValue, f));
            }
            return this;
        }
        if(cell.count >= (long)adaptivehistogram.getCountPerNodeLimit() && cell.minValue != cell.maxValue) {
            float f1 = (cell.minValue + cell.maxValue) / 2.0F;
            long l = cell.count / 2L;
            if(l + l < cell.count)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            if(f > f1)
            {
                l2 = 1L + l;
                int j;
                if(flag)
                {
                    j = 1;
                } else
                {
                    j = 0;
                }
                l3 = l + (long)j;
            } else
            {
                long l1 = 1L + l;
                int i;
                if(flag)
                {
                    i = 1;
                } else
                {
                    i = 0;
                }
                l2 = l + (long)i;
                l3 = l1;
            }
            return new HistogramForkNode(f1, new HistogramDataNode(l3, cell.minValue, f1), new HistogramDataNode(l2, f1, cell.maxValue));
        }
        Cell cell2 = cell;
        cell2.count = 1L + cell2.count;
        return this;
    }

    public void apply(AdaptiveHistogram.ValueConversion valueconversion)
    {
        cell.minValue = valueconversion.convertValue(cell.minValue);
        cell.maxValue = valueconversion.convertValue(cell.maxValue);
    }

    public long getAccumCount(float f)
    {
        long l = 0L;
        if(f >= cell.minValue)
        {
            l = cell.count;
        }
        return l;
    }

    public long getCount(float f)
    {
        long l = 0L;
        if(f >= cell.minValue && f <= cell.maxValue)
        {
            l = cell.count;
        }
        return l;
    }

    public Float getValueForAccumCount(long al[])
    {
        long l = al[0];
        long l1 = al[1];
        Float float1 = null;
        if(l <= l1)
        {
            float1 = null;
            if((l + cell.count) >= l1)
            {
                float1 = new Float(interpolate(l, cell.minValue, l + cell.count, cell.maxValue, l1));
            }
        }
        al[0] = al[0] + cell.count;
        return float1;
    }

    public void reset()
    {
        cell.count = 0L;
        cell.minValue = 3.402823E+038F;
        cell.maxValue = -3.402823E+038F;
    }

    public void show(int i)
    {
        margin(i);
        System.out.println((new StringBuilder()).append("Data: ").append(cell.count).append(" (").append(cell.minValue).append(",").append(cell.maxValue).append(")").toString());
    }

    public void toTable(ArrayList arraylist)
    {
        arraylist.add(cell);
    }
}

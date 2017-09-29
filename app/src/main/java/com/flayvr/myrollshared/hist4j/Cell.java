package com.flayvr.myrollshared.hist4j;

import java.io.Serializable;

public class Cell implements Serializable
{

    private static final long serialVersionUID = -1L;
    public long count;
    public float maxValue;
    public float minValue;

    public Cell()
    {
    }
}

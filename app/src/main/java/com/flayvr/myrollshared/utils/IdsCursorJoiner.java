package com.flayvr.myrollshared.utils;

import android.database.Cursor;
import java.util.Iterator;

public final class IdsCursorJoiner
    implements Iterable<IdsCursorJoiner.Result>, Iterator<IdsCursorJoiner.Result>
{

    static final boolean $assertionsDisabled;
    private int mColumnsLeft[];
    private int mColumnsRight[];
    private Result mCompareResult;
    private boolean mCompareResultIsValid;
    private Cursor mCursorLeft;
    private Cursor mCursorRight;
    private Long mValues[];

    public IdsCursorJoiner(Cursor cursor, String as[], Cursor cursor1, String as1[])
    {
        if(as.length != as1.length)
        {
            throw new IllegalArgumentException((new StringBuilder()).append("you must have the same number of columns on the left and right, ").append(as.length).append(" != ").append(as1.length).toString());
        } else {
            mCursorLeft = cursor;
            mCursorRight = cursor1;
            mCursorLeft.moveToFirst();
            mCursorRight.moveToFirst();
            mCompareResultIsValid = false;
            mColumnsLeft = buildColumnIndiciesArray(cursor, as);
            mColumnsRight = buildColumnIndiciesArray(cursor1, as1);
            mValues = new Long[2 * mColumnsLeft.length];
        }
    }

    private int[] buildColumnIndiciesArray(Cursor cursor, String as[])
    {
        int ai[] = new int[as.length];
        for(int i = 0; i < as.length; i++)
        {
            ai[i] = cursor.getColumnIndexOrThrow(as[i]);
        }

        return ai;
    }

    private static int compare(Long along[])
    {
        byte byte0;
        int i;
        byte0 = -1;
        if(along.length % 2 != 0)
            throw new IllegalArgumentException("you must specify an even number of values");
        i = 0;
        while(i < along.length)
        {
            if(along[i] != null) {
                if(along[i + 1] == null) {
                    return 1;
                }
                int j = along[i].compareTo(along[i + 1]);
                if(j != 0){
                    byte byte1;
                    if(j < 0)
                        byte1 = byte0;
                    else
                        byte1 = 1;
                    return byte1;
                }
            }else if(along[i + 1] != null)
                return byte0;
            i += 2;
        }
        return 0;
    }

    private void incrementCursors()
    {
        if(mCompareResultIsValid) {
            switch (mCompareResult) {
                default:
                    break;
                case BOTH:
                    mCursorLeft.moveToNext();
                    mCursorRight.moveToNext();
                    break;
                case LEFT:
                    mCursorLeft.moveToNext();
                    break;
                case RIGHT:
                    mCursorRight.moveToNext();
                    break;
            }
            mCompareResultIsValid = false;
        }
    }

    private static void populateValues(Long along[], Cursor cursor, int ai[], int i)
    {
        if(!$assertionsDisabled && i != 0 && i != 1)
        {
            throw new AssertionError();
        }
        for(int j = 0; j < ai.length; j++)
        {
            along[i + j * 2] = Long.valueOf(cursor.getLong(ai[j]));
        }

    }

    public boolean hasNext()
    {
        boolean flag1;
        if(!mCompareResultIsValid) {
            boolean flag;
            if(!mCursorLeft.isAfterLast())
                return true;
            flag = mCursorRight.isAfterLast();
            if(flag)
                return false;
            return true;
        }
        switch(mCompareResult){
            default:
                throw new IllegalStateException((new StringBuilder()).append("bad value for mCompareResult, ").append(mCompareResult).toString());
            case BOTH:
                if(mCursorLeft.isLast())
                {
                    boolean flag4 = mCursorRight.isLast();
                    if(flag4)
                        return false;
                }
                return true;
            case LEFT:
                boolean flag3;
                if(!mCursorLeft.isLast())
                    return true;
                flag3 = mCursorRight.isAfterLast();
                if(flag3)
                    return false;
                return true;
            case RIGHT:
                boolean flag2;
                if(!mCursorLeft.isAfterLast())
                    return true;
                flag2 = mCursorRight.isLast();
                if(flag2)
                    return false;
                return true;
        }
    }

    public Iterator iterator()
    {
        return this;
    }

    public Result next()
    {
        boolean flag;
        boolean flag1;
        if(!hasNext())
        {
            throw new IllegalStateException("you must only call next() when hasNext() is true");
        }
        incrementCursors();
        if(!$assertionsDisabled && !hasNext())
        {
            throw new AssertionError();
        }
        if(!mCursorLeft.isAfterLast())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if(!mCursorRight.isAfterLast())
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        if(!flag || !flag1) {
            if(flag)
            {
                mCompareResult = Result.LEFT;
            } else {
                if(!$assertionsDisabled && !flag1)
                    throw new AssertionError();
                mCompareResult = Result.RIGHT;
            }
        }else {
            populateValues(mValues, mCursorLeft, mColumnsLeft, 0);
            populateValues(mValues, mCursorRight, mColumnsRight, 1);
            switch (compare(mValues)) {
                default:
                    break;
                case -1:
                    mCompareResult = Result.LEFT;
                    break;
                case 0:
                    mCompareResult = Result.BOTH;
                    break;
                case 1:
                    mCompareResult = Result.RIGHT;
                    break;
            }
        }
        mCompareResultIsValid = true;
        return mCompareResult;
    }

    public void remove()
    {
        throw new UnsupportedOperationException("not implemented");
    }

    static 
    {
        boolean flag;
        if(!IdsCursorJoiner.class.desiredAssertionStatus())
        {
            flag = true;
        } else
        {
            flag = false;
        }
        $assertionsDisabled = flag;
    }

    public enum Result
    {
        RIGHT("RIGHT", 0),
        LEFT("LEFT", 1),
        BOTH("BOTH", 2);

        private Result(String s, int i)
        {
        }
    }

}

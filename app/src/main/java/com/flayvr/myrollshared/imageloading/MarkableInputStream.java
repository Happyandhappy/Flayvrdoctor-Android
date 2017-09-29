package com.flayvr.myrollshared.imageloading;

import java.io.*;

final class MarkableInputStream extends InputStream
{

    private long defaultMark;
    private final InputStream in;
    private long limit;
    private long offset;
    private long reset;

    public MarkableInputStream(InputStream inputstream)
    {
        defaultMark = -1L;
        if(!inputstream.markSupported())
        {
            inputstream = new BufferedInputStream(inputstream);
        }
        in = inputstream;
    }

    private void setLimit(long l)
    {
        try
        {
            if(reset >= offset || offset > limit)
            {
                reset = offset;
                in.mark((int)(l - offset));
            }else {
                in.reset();
                in.mark((int) (l - reset));
                skip(reset, offset);
            }
            limit = l;
        }
        catch(IOException ioexception)
        {
            throw new IllegalStateException((new StringBuilder()).append("Unable to mark: ").append(ioexception).toString());
        }
    }

    private void skip(long l, long l1)
    {
        try {
            while (l < l1) {
                l += in.skip(l1 - l);
            }
        }catch(Exception e){}
    }

    public int available()
    {
        try {
            return in.available();
        }catch(Exception e){
            return -1;
        }
    }

    public void close()
    {
        try {
            in.close();
        }catch(Exception e){
        }
    }

    public void mark(int i)
    {
        defaultMark = savePosition(i);
    }

    public boolean markSupported()
    {
        return in.markSupported();
    }

    public int read()
    {
        int i;
        try {
            i = in.read();
        }catch(Exception e){
            i = -1;
        }
        if(i != -1)
        {
            offset = 1L + offset;
        }
        return i;
    }

    public int read(byte abyte0[])
    {
        int i;
        try {
            i = in.read(abyte0);
        }catch(Exception e){
            i = -1;
        }
        if(i != -1)
            offset = offset + (long)i;
        return i;
    }

    public int read(byte abyte0[], int i, int j)
    {
        int k;
        try {
            k = in.read(abyte0, i, j);
        }catch (Exception e){
            k = -1;
        }
        if(k != -1)
            offset = offset + (long)k;
        return k;
    }

    public void reset()
    {
        reset(defaultMark);
    }

    public void reset(long l)
    {
        try {
            if (offset > limit || l < reset) {
                throw new IOException("Cannot reset");
            } else {
                in.reset();
                skip(reset, l);
                offset = l;
            }
        }catch(Exception e){}
    }

    public long savePosition(int i)
    {
        long l = offset + (long)i;
        if(limit < l)
        {
            setLimit(l);
        }
        return offset;
    }

    public long skip(long l)
    {
        try {
            long l1 = in.skip(l);
            offset = l1 + offset;
            return l1;
        }catch(Exception e){
            return -1;
        }
    }
}

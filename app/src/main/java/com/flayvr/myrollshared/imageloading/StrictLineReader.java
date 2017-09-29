package com.flayvr.myrollshared.imageloading;

import java.io.*;
import java.nio.charset.Charset;

class StrictLineReader
    implements Closeable
{

    private static final byte CR = 13;
    private static final byte LF = 10;
    private byte buf[];
    private final Charset charset;
    private int end;
    private final InputStream in;
    private int pos;

    public StrictLineReader(InputStream inputstream, int i, Charset charset1)
    {
        if(inputstream == null || charset1 == null)
            throw new NullPointerException();
        if(i < 0)
            throw new IllegalArgumentException("capacity <= 0");
        if(!charset1.equals(Util.US_ASCII))
        {
            throw new IllegalArgumentException("Unsupported encoding");
        } else {
            in = inputstream;
            charset = charset1;
            buf = new byte[i];
        }
    }

    public StrictLineReader(InputStream inputstream, Charset charset1)
    {
        this(inputstream, 8192, charset1);
    }

    private void fillBuf()
    {
        try {
            int i = in.read(buf, 0, buf.length);
            if (i == -1) {
                throw new EOFException();
            } else {
                pos = 0;
                end = i;
            }
        }catch(Exception e){}
    }

    public void close()
    {
        synchronized(in)
        {
            try {
                if (buf != null) {
                    buf = null;
                    in.close();
                }
            }catch(Exception e){}
        }
    }

    public String readLine() throws EOFException {
        _cls1 _lcls1;
        int j;
        String s;
        int k;
        String s1;
        InputStream inputstream = in;
        try {
            synchronized (inputstream) {
                if (buf == null)
                    throw new IOException("LineReader is closed");
                int i;
                if (pos >= end)
                    fillBuf();
                i = pos;
                while (true) {
                    if (i == end) {
                        _lcls1 = new _cls1(80 + (end - pos));
                        while (true) {
                            _lcls1.write(buf, pos, end - pos);
                            end = -1;
                            fillBuf();
                            j = pos;
                            while (true) {
                                if (j == end)
                                    break;
                                if (buf[j] == 10) {
                                    if (j != pos) {
                                        _lcls1.write(buf, pos, j - pos);
                                    }
                                    pos = j + 1;
                                    s = _lcls1.toString();
                                    return s;
                                }
                                j++;
                            }
                        }
                    }
                    if (buf[i] == 10) {
                        if (i != pos && buf[i - 1] == 13)
                            k = i - 1;
                        else
                            k = i;
                        s1 = new String(buf, pos, k - pos, charset.name());
                        pos = i + 1;
                        return s1;
                    }
                    i++;
                }
            }
        }catch(Exception e){
            throw new EOFException();
        }
    }


    private class _cls1 extends ByteArrayOutputStream
    {
        @Override
        public String toString()
        {
            int i;
            String s;
            if(count > 0 && buf[-1 + count] == 13)
            {
                i = -1 + count;
            } else
            {
                i = count;
            }
            try
            {
                s = new String(buf, 0, i, charset.name());
            }
            catch(UnsupportedEncodingException unsupportedencodingexception)
            {
                throw new AssertionError(unsupportedencodingexception);
            }
            return s;
        }

        _cls1(int i)
        {
            super(i);
        }
    }

}

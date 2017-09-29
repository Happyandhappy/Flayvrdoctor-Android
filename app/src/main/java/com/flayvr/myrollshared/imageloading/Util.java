package com.flayvr.myrollshared.imageloading;

import java.io.*;
import java.nio.charset.Charset;

public final class Util
{

    static final Charset US_ASCII = Charset.forName("US-ASCII");
    static final Charset UTF_8 = Charset.forName("UTF-8");

    private Util()
    {
    }

    static void closeQuietly(Closeable closeable)
    {
        try {
            if (closeable != null)
                closeable.close();
        }catch(Exception e){}
    }

    static void deleteContents(File file)
    {
        try {
            File afile[] = file.listFiles();
            if (afile == null) {
                throw new IOException((new StringBuilder()).append("not a readable directory: ").append(file).toString());
            }
            int i = afile.length;
            for (int j = 0; j < i; j++) {
                File file1 = afile[j];
                if (file1.isDirectory()) {
                    deleteContents(file1);
                }
                if (!file1.delete()) {
                    throw new IOException((new StringBuilder()).append("failed to delete file: ").append(file1).toString());
                }
            }
        }catch(Exception e){
        }
    }

    static String readFully(Reader reader)
    {
        StringWriter stringwriter;
        char ac[];
        stringwriter = new StringWriter();
        ac = new char[1024];
        try {
            while (true) {
                int i = reader.read(ac);
                if (i == -1) {
                    break;
                }
                stringwriter.write(ac, 0, i);
            }
            String s = stringwriter.toString();
            reader.close();
            return s;
        }catch(Exception e){
            return "";
        }
    }

}

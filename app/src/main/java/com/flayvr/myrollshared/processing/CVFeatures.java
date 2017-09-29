package com.flayvr.myrollshared.processing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.imageloading.AndroidImagesUtils;
import com.flayvr.myrollshared.imageloading.ImagesDiskCache;
import com.flayvr.myrollshared.oldclasses.FaceData;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class CVFeatures
{

    private static final String TAG = CVFeatures.class.getSimpleName();
    private static CascadeClassifier faceDetector;
    private static CVFeatures instance;
    private boolean cvInitFailed;

    private CVFeatures()
    {
        if(!OpenCVLoader.initDebug())
        {
            Log.d(TAG, "failed init opencv");
            cvInitFailed = true;
            return;
        }
        try
        {
            opencvStaticInit();
            return;
        }
        catch(Exception exception)
        {
            Log.d(TAG, "failed init opencv");
        }
        cvInitFailed = true;
        return;
    }

    public static CVFeatures getInstance()
    {
        if(instance == null)
        {
            instance = new CVFeatures();
        }
        return instance;
    }

    public static Mat loadMatFromStream(InputStream inputstream)
    {
        try {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(inputstream.available());
            byte abyte0[] = new byte[4096];
            while(true) {
                int i = inputstream.read(abyte0);
                if (i == -1)
                    break;
                bytearrayoutputstream.write(abyte0, 0, i);
            }
            inputstream.close();
            Mat mat = new Mat(1, bytearrayoutputstream.size(), 0);
            mat.put(0, 0, bytearrayoutputstream.toByteArray());
            bytearrayoutputstream.close();
            Mat mat1 = Imgcodecs.imdecode(mat, -1);
            mat.release();
            return mat1;
        }catch(Exception e){
            return null;
        }
    }

    public static void opencvStaticInit()
    {
        InputStream inputstream;
        File file;
        FileOutputStream fileoutputstream;
        byte abyte0[];
        try
        {
            inputstream = FlayvrApplication.getAppContext().getResources().openRawResource(R.raw.lbpcascade_frontalface);
            file = new File(FlayvrApplication.getAppContext().getDir("cascade", 0), "lpcascade_frontalface_alt.xml");
            fileoutputstream = new FileOutputStream(file);
            abyte0 = new byte[4096];
            while(true) {
                int i = inputstream.read(abyte0);
                if(i == -1)
                    break;
                fileoutputstream.write(abyte0, 0, i);
            }
            inputstream.close();
            fileoutputstream.close();
            faceDetector = new CascadeClassifier(file.getAbsolutePath());
        } catch(IOException ioexception) {
            Log.e(TAG, (new StringBuilder()).append("Failed to load cascade. Exception thrown: ").append(ioexception).toString());
        }
    }

    private int safeLongToInt(long l)
    {
        if(l < 0xffffffff80000000L || l > 0x7fffffffL)
        {
            throw new IllegalArgumentException((new StringBuilder()).append(l).append(" cannot be cast to int without changing its value.").toString());
        } else
        {
            return (int)l;
        }
    }

    public Double getBlurry(Mat mat)
    {
        Mat mat1;
        double d;
        Double double1;
        mat1 = new Mat();
        if(mat.channels() >= 3)
        {
            Imgproc.cvtColor(mat, mat1, 6);
            mat = mat1;
        }
        try
        {
            Mat mat2 = new Mat();
            Imgproc.Laplacian(mat, mat2, 0);
            MatOfDouble matofdouble = new MatOfDouble();
            MatOfDouble matofdouble1 = new MatOfDouble();
            Core.meanStdDev(mat2, matofdouble, matofdouble1);
            mat.release();
            mat2.release();
            d = matofdouble1.get(0, 0)[0];
            matofdouble.release();
            matofdouble1.release();
        }
        catch(Throwable throwable)
        {
            Log.e(TAG, throwable.getMessage(), throwable);
            return null;
        }
        if(d >= 15D)
        {
            if(d <= 35D)
            {
                double1 = Double.valueOf((d - 12.5D) / 25D);
                return double1;
            }
            return Double.valueOf(Math.min(0.90000000000000002D + 0.10000000000000001D * ((d - 35D) / 15D), 1.0D));
        }
        return Double.valueOf(0.10000000000000001D * (d / 15D));
    }

    public Double getColor(Mat mat)
    {
        MatOfInt matofint;
        MatOfInt matofint1;
        MatOfFloat matoffloat;
        Mat mat1;
        matofint = new MatOfInt(new int[] {
            0, 1
        });
        matofint1 = new MatOfInt(new int[] {
            4, 4
        });
        matoffloat = new MatOfFloat(new float[] {
            0.0F, 256F, 0.0F, 256F
        });
        mat1 = new Mat();
        int i;
        i = mat.rows() * mat.cols();
        Imgproc.calcHist(Arrays.asList(new Mat[] {
            mat
        }), matofint, new Mat(), mat1, matofint1, matoffloat);
        double d;
        int j;
        d = 0.0D;
        j = 0;
        try {
            while (j < mat1.rows()) {
                int k = 0;
                while (k < mat1.rows()) {
                    d += Math.pow(mat1.get(j, k)[0] - (double) (i / 16), 2D);
                    k++;
                }
                j++;
            }
            double d1 = 1.0D - Math.pow(d, 0.5D) / (double) i;
            if (d1 >= 0.5D) {
                double d2 = 2.25D * (d1 - 0.40000000000000002D);
                Double double2 = Double.valueOf(Math.min(d2, 1.0D));
                matofint.release();
                matofint1.release();
                matoffloat.release();
                mat1.release();
                return double2;
            }
            Double double1 = Double.valueOf(d1 / 5D);
            matofint.release();
            matofint1.release();
            matoffloat.release();
            mat1.release();
            return double1;
        }catch (Exception exception){
            matofint.release();
            matofint1.release();
            matoffloat.release();
            mat1.release();
            throw exception;
        }catch(Throwable throwable){
            Log.e(TAG, throwable.getMessage(), throwable);
            matofint.release();
            matofint1.release();
            matoffloat.release();
            mat1.release();
            return null;
        }
    }

    public Double getDark(Mat mat)
    {
        MatOfInt matofint;
        MatOfInt matofint1;
        MatOfFloat matoffloat;
        Mat mat1;
        Mat mat2;
        matofint = new MatOfInt(new int[] {
            0
        });
        matofint1 = new MatOfInt(new int[] {
            256
        });
        matoffloat = new MatOfFloat(new float[] {
            0.0F, 256F
        });
        mat1 = new Mat();
        mat2 = new Mat();
        int i;
        try {
            Imgproc.cvtColor(mat, mat2, 6);
            i = mat.rows() * mat.cols();
            Imgproc.calcHist(Arrays.asList(new Mat[]{
                    mat
            }), matofint, new Mat(), mat1, matofint1, matoffloat);
            int j;
            int k;
            j = 0;
            k = 0;
            while (k < 30) {
                j = (int) ((double) j + mat1.get(k, 0)[0]);
                k++;
            }
            Double double1 = Double.valueOf(-1D * ((double) j / (1.0D * (double) i) - 1.0D));
            matofint.release();
            matofint1.release();
            matoffloat.release();
            mat1.release();
            mat2.release();
            return double1;
        }catch(Exception exception){
            matofint.release();
            matofint1.release();
            matoffloat.release();
            mat1.release();
            mat2.release();
            throw exception;
        }catch (Throwable throwable){
            Log.e(TAG, throwable.getMessage(), throwable);
            matofint.release();
            matofint1.release();
            matoffloat.release();
            mat1.release();
            mat2.release();
            return null;
        }
    }

    public double getSimilarityScore(Mat mat, Mat mat1)
    {
        int _tmp = mat.rows() * mat.cols();
        MatOfInt matofint = new MatOfInt(new int[] {
            0
        });
        MatOfInt matofint1 = new MatOfInt(new int[] {
            16
        });
        MatOfFloat matoffloat = new MatOfFloat(new float[] {
            0.0F, 256F
        });
        Mat mat2 = new Mat();
        Imgproc.calcHist(Arrays.asList(new Mat[] {
            mat
        }), matofint, new Mat(), mat2, matofint1, matoffloat);
        int _tmp1 = mat1.rows() * mat1.cols();
        MatOfInt matofint2 = new MatOfInt(new int[] {
            0
        });
        MatOfInt matofint3 = new MatOfInt(new int[] {
            16
        });
        MatOfFloat matoffloat1 = new MatOfFloat(new float[] {
            0.0F, 256F
        });
        Mat mat3 = new Mat();
        Imgproc.calcHist(Arrays.asList(new Mat[] {
            mat1
        }), matofint2, new Mat(), mat3, matofint3, matoffloat1);
        return Imgproc.compareHist(mat2, mat3, 0);
    }

    public boolean isCvInitFailed()
    {
        return cvInitFailed;
    }

    public ArrayList opencvFaceDetection(Mat mat)
    {
        Rect arect[];
        ArrayList arraylist1;
        Mat mat1 = new Mat();
        try {
            if (mat.channels() < 3) {
                Mat mat2 = mat.clone();
                mat1 = mat2;
            } else
                Imgproc.cvtColor(mat, mat1, 6);
            MatOfRect matofrect = new MatOfRect();
            double d = Math.round(0.10000000000000001D * (double) mat1.height());
            faceDetector.detectMultiScale(mat1, matofrect, 1.2D, 2, 2, new Size(d, d), mat1.size());
            arect = matofrect.toArray();
            arraylist1 = new ArrayList(arect.length);
            int i = 0;
            while (i < arect.length) {
                Rect rect = arect[i];
                PointF pointf = new PointF();
                pointf.x = ((float) rect.x + (float) rect.width / 2.0F) / (float) mat1.width();
                pointf.y = ((float) rect.y + (float) rect.height / 2.0F) / (float) mat1.height();
                arraylist1.add(new FaceData(pointf, (0.0F + (float) Math.max(rect.width, rect.height)) / (float) mat1.width(), 0.0F));
                i++;
            }
            mat1.release();
            return arraylist1;
        }catch(Exception exception){
            mat1.release();
            throw exception;
        }catch (Throwable throwable){
            ArrayList arraylist;
            Log.e(TAG, throwable.getMessage(), throwable);
            arraylist = new ArrayList(0);
            mat1.release();
            return arraylist;
        }
    }

    public Mat readImageMatrix(MediaItem mediaitem, boolean flag)
    {
        Mat mat;
        String s;
        FileInputStream fileinputstream;
        Bitmap bitmap1;
        Bitmap bitmap;
        Mat mat1;
        Object obj;
        InputStream inputstream1;
        ImagesDiskCache imagesdiskcache;
        com.flayvr.myrollshared.imageloading.ImageCacheBitmap.ThumbnailSize thumbnailsize;
        com.flayvr.myrollshared.imageloading.DiskLruCache.Snapshot snapshot;
        if(mediaitem.isGif())
            return null;
        try {
            if (!mediaitem.isCloudItem()) {
                s = mediaitem.getThumbnail();
                if (s == null) {
                    mat1 = null;
                    obj = null;
                } else {
                    fileinputstream = new FileInputStream(mediaitem.getThumbnail());
                    obj = fileinputstream;
                    mat1 = null;
                }
            } else {
                imagesdiskcache = FlayvrApplication.getDiskCache();
                thumbnailsize = com.flayvr.myrollshared.imageloading.ImageCacheBitmap.ThumbnailSize.Normal;
                snapshot = imagesdiskcache.get(mediaitem, thumbnailsize);
                if (snapshot == null && flag) {
                    imagesdiskcache.put(mediaitem, thumbnailsize);
                    snapshot = imagesdiskcache.get(mediaitem, thumbnailsize);
                }
                if (snapshot == null) {
                    inputstream1 = null;
                } else {
                    InputStream inputstream = snapshot.getInputStream(0);
                    inputstream1 = inputstream;
                }
                obj = inputstream1;
                mat1 = null;
            }
            if (obj != null) {
                Mat mat3 = loadMatFromStream((InputStream) obj);
                mat1 = mat3;
            }
            if (mat1 == null) {
                if (mediaitem.isCloudItem() && !flag)
                    return null;
                bitmap = AndroidImagesUtils.createBitmapForItem(FlayvrApplication.getAppContext().getContentResolver(), mediaitem, com.flayvr.myrollshared.imageloading.ImageCacheBitmap.ThumbnailSize.Normal);
                if (bitmap == null) {
                    Log.w(TAG, (new StringBuilder()).append(mediaitem.getPath()).append(" null bitmap").toString());
                    return null;
                }
                mat1 = new Mat();
                Utils.bitmapToMat(bitmap, mat1);
                bitmap.recycle();
            }
            if (mat1.empty())
                return null;
            Mat mat2 = new Mat();
            if (mediaitem.getOrientation().intValue() == 270) {
                Core.flip(mat1.t(), mat2, 0);
                mat1 = mat2;
            } else if (mediaitem.getOrientation().intValue() == 180) {
                Core.flip(mat1, mat2, -1);
                mat1 = mat2;
            } else if (mediaitem.getOrientation().intValue() == 90) {
                Core.flip(mat1.t(), mat2, 1);
                mat1 = mat2;
            } else if (mediaitem.getOrientation().intValue() != 0) {
                mat1 = mat2;
            }
            return mat1;
        }catch(Exception e){
            return null;
        }
    }

}

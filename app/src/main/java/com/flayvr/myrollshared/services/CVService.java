package com.flayvr.myrollshared.services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.PointF;
import android.util.Log;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.oldclasses.FaceData;
import com.flayvr.myrollshared.processing.CVFeatures;
import com.flayvr.myrollshared.utils.*;
import com.google.gson.Gson;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.*;
import java.util.*;
import org.opencv.core.Mat;

public abstract class CVService extends IntentService
{

    public static final int ITER_SIZE = 100;
    private static final Integer SCORE_VERSION = Integer.valueOf(2);
    private static final String TAG = CVService.class.getSimpleName();
    private final WhereCondition cvDidntRunCond;
    private CVFeatures cvFeatures;
    private final Gson gson = new Gson();
    private MediaItemDao mediaItemDao;

    public CVService()
    {
        super("CVService");
        cvFeatures = CVFeatures.getInstance();
        mediaItemDao = DBManager.getInstance().getDaoSession().getMediaItemDao();
        WhereCondition wherecondition = com.flayvr.myrollshared.data.MediaItemDao.Properties.CvRan.isNull();
        WhereCondition wherecondition1 = com.flayvr.myrollshared.data.MediaItemDao.Properties.CvRan.eq(Boolean.valueOf(false));
        cvDidntRunCond = mediaItemDao.queryBuilder().or(wherecondition, wherecondition1, new WhereCondition[0]);
    }

    private Double calcScore(MediaItem mediaitem)
    {
        double d = 0.0D;
        if(mediaitem.getBlurry() == null || mediaitem.getColor() == null || mediaitem.getDark() == null || mediaitem.getFacesCount() == null)
        {
            return Double.valueOf(-1D);
        }
        double d1;
        double d2;
        double d3;
        double d4;
        if(mediaitem.getBlurry().doubleValue() > 0.02D)
        {
            d1 = 0.10000000000000001D;
        } else
        {
            d1 = d;
        }
        d2 = d1 + 0.20000000000000001D * mediaitem.getBlurry().doubleValue() + 0.20000000000000001D * mediaitem.getDark().doubleValue();
        if(mediaitem.getColor().doubleValue() > 0.20000000000000001D)
        {
            d3 = 0.10000000000000001D;
        } else
        {
            d3 = d;
        }
        d4 = d3 + d2 + 0.29999999999999999D * mediaitem.getColor().doubleValue();
        if(mediaitem.getFacesCount().intValue() > 0)
        {
            d = 0.10000000000000001D + 0.10000000000000001D * (double)mediaitem.getFacesCount().intValue();
        }
        return Double.valueOf(d4 + d);
    }

    private boolean checkIfEnoughBattery(int i)
    {
        float f = GeneralUtils.getBatteryLevel(this);
        FlayvrApplication flayvrapplication = (FlayvrApplication)getApplication();
        if(f < 0.35F && flayvrapplication.isBatteryOptimizationOn())
        {
            if(f >= 0.1F)
            {
                return i < 5;
            } else
            {
                return false;
            }
        } else
        {
            return true;
        }
    }

    private void runOnPhoto(MediaItem mediaitem)
    {
        if(mediaitem.getCvRan() == null || !mediaitem.getCvRan().booleanValue())
        {
            Mat mat = cvFeatures.readImageMatrix(mediaitem, shouldDownloadRemoteItems());
            if(mat != null)
            {
                if(mediaitem.getBlurry() == null)
                {
                    mediaitem.setBlurry(cvFeatures.getBlurry(mat));
                }
                if(mediaitem.getColor() == null)
                {
                    mediaitem.setColor(cvFeatures.getColor(mat));
                }
                if(mediaitem.getDark() == null)
                {
                    mediaitem.setDark(cvFeatures.getDark(mat));
                }
                if(mediaitem.getFacesCount() == null)
                {
                    ArrayList arraylist = cvFeatures.opencvFaceDetection(mat);
                    mediaitem.setFacesCount(Integer.valueOf(arraylist.size()));
                    if(arraylist.size() > 0)
                    {
                        FaceData facedata = null;
                        Iterator iterator = arraylist.iterator();
                        while(iterator.hasNext())
                        {
                            FaceData facedata1 = (FaceData)iterator.next();
                            if(facedata != null && facedata.getEyeDistance() >= facedata1.getEyeDistance())
                            {
                                facedata1 = facedata;
                            }
                            facedata = facedata1;
                        }
                        mediaitem.setCenterX(Float.valueOf(facedata.getMidPoint().x));
                        mediaitem.setCenterY(Float.valueOf(facedata.getMidPoint().y));
                        mediaitem.setFacesJson(gson.toJson(arraylist));
                    }
                }
                mat.release();
                mediaitem.setScore(calcScore(mediaitem));
                mediaitem.setCvRan(Boolean.valueOf(true));
            }
        }
    }

    private void updateScore()
    {
        QueryBuilder querybuilder = DaoHelper.getAllPhotos();
        querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.CvRan.eq(Boolean.valueOf(true)), new WhereCondition[0]);
        Query query = querybuilder.limit(1000).build();
        long l = querybuilder.count();
        Log.i(TAG, (new StringBuilder()).append("Updating score for ").append(l).append(" photos").toString());
        for(int i = 0; (long)i < l; i += 1000)
        {
            List list = query.list();
            MediaItem mediaitem;
            for(Iterator iterator = list.iterator(); iterator.hasNext(); mediaitem.setScore(calcScore(mediaitem)))
            {
                mediaitem = (MediaItem)iterator.next();
            }

            mediaItemDao.updateInTx(list);
        }

        Log.i(TAG, (new StringBuilder()).append("Finished updating score for ").append(l).append(" photos").toString());
    }

    public void onCVFailed()
    {
    }

    public void onFinish()
    {
    }

    protected void onHandleIntent(Intent intent)
    {
        long l;
        int i;
        Query query;
        int j;
        if(CVFeatures.getInstance().isCvInitFailed())
        {
            onCVFailed();
            return;
        }
        Date date = new Date();
        Log.i(TAG, (new StringBuilder()).append("received intent ").append(intent.getAction()).toString());
        QueryBuilder querybuilder;
        long l1;
        long l2;
        try
        {
            onStart();
            if(shouldUpdateScore())
            {
                updateScore();
            }
            querybuilder = DaoHelper.getAllPhotos();
            QueryBuilder querybuilder1 = querybuilder.where(cvDidntRunCond, new WhereCondition[0]);
            Property aproperty[] = new Property[1];
            aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
            querybuilder1.orderDesc(aproperty);
            l = querybuilder.count();
        }
        catch(Throwable throwable)
        {
            Log.e(TAG, throwable.getMessage(), throwable);
            return;
        }
        i = 0;
        query = querybuilder.limit(1000).build();
        Log.i(TAG, (new StringBuilder()).append("Started running on ").append(l).append(" photos").toString());
        j = 0;
        while((long)j < l)
        {
            if(!checkIfEnoughBattery(i))
            {
                onFinish();
                Log.i(TAG, (new StringBuilder()).append("Finished running on ").append(i).append(" photos").toString());
                l1 = (new Date()).getTime() - date.getTime();
                Log.i(TAG, (new StringBuilder()).append("timing: done ").append(l1).toString());
                break;
            }
            List list;
            LinkedList linkedlist;
            list = query.list();
            Log.i(TAG, (new StringBuilder()).append("photos: ").append(list.size()).append(" offset: ").append(j).toString());
            linkedlist = new LinkedList();
            int k;
            int i1;
            k = i;
            i1 = 0;
            MediaItem mediaitem;
            while(i1 < list.size())
            {
                mediaitem = (MediaItem)list.get(i1);
                runOnPhoto(mediaitem);
                k++;
                linkedlist.add(mediaitem);
                if((i1 + 1) % 100 != 0)
                {
                    if(i1 != -1 + list.size())
                    {
                        i1++;
                        continue;
                    }
                }
                mediaItemDao.updateInTx(linkedlist);
                Log.i(TAG, (new StringBuilder()).append((float)(i1 + j) / (1.0F * (float)l)).append("").toString());
                onUpdate((float)(i1 + j) / (1.0F * (float)l));
                linkedlist.clear();
                i1++;
            }
            j += 1000;
            i = k;
        }
        onFinish();
        sendBroadcast(new Intent(IntentActions.ACTION_CV_FINISHED));
        Log.i(TAG, (new StringBuilder()).append("Finished running on ").append(l).append(" photos").toString());
        l2 = (new Date()).getTime() - date.getTime();
        Log.i(TAG, (new StringBuilder()).append("timing: done ").append(l2).toString());
    }

    public void onStart()
    {
    }

    public void onUpdate(float f)
    {
    }

    protected boolean shouldDownloadRemoteItems()
    {
        return false;
    }

    protected boolean shouldUpdateScore()
    {
        boolean flag;
        if(SharedPreferencesManager.getLastVersionOfScoreUpdate().intValue() < SCORE_VERSION.intValue())
            flag = true;
        else
            flag = false;
        if(flag)
            SharedPreferencesManager.setLastVersionOfScoreUpdate(SCORE_VERSION);
        return flag;
    }

}

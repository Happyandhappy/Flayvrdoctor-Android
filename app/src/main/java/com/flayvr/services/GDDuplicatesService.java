package com.flayvr.services;

import android.util.Log;
import com.flayvr.myrollshared.cloud.PicasaSessionManager;
import com.flayvr.myrollshared.managers.GalleryDoctorServicesProgress;
import com.flayvr.myrollshared.services.DuplicatesService;
import com.flayvr.util.GalleryDoctorAnalyticsSender;
import com.flayvr.util.GalleryDoctorStatsUtils;
import com.kissmetrics.sdk.KISSmetricsAPI;
import java.util.HashMap;
import java.util.concurrent.Future;

public class GDDuplicatesService extends DuplicatesService
{

    private static final Integer PHOTOS_BUCKETS[];
    private static final String TAG = GDDuplicatesService.class.getSimpleName();
    private GDServiceProgress serviceProgress;

    public GDDuplicatesService()
    {
        serviceProgress = new GDServiceProgress(this, getSource());
    }

    private int getSource()
    {
        return !PicasaSessionManager.getInstance().hasUser() ? 1 : 2;
    }

    public void onFinish()
    {
        HashMap hashmap;
        com.flayvr.util.GalleryDoctorStatsUtils.MediaItemStat mediaitemstat;
        if(!GalleryDoctorServicesProgress.didDuplicatesServiceFinish(getSource()))
        {
            hashmap = new HashMap();
            mediaitemstat = GalleryDoctorStatsUtils.getMediaItemStat(1);
            int i;
            com.flayvr.util.GalleryDoctorStatsUtils.DriveStat drivestat;
            long l;
            Integer ainteger[];
            int j;
            Future future = GalleryDoctorStatsUtils.getDriveStat(1);
            i = GalleryDoctorStatsUtils.getGalleryHealth(mediaitemstat, future);
            try
            {
                drivestat = (com.flayvr.util.GalleryDoctorStatsUtils.DriveStat)future.get();
                hashmap.put("gallery health", (new StringBuilder()).append(i).append("").toString());
                hashmap.put("space left on device", (new StringBuilder()).append(drivestat.getFreeSpace() / 0x100000L).append("").toString());
                hashmap.put("total bad photos", (new StringBuilder()).append(mediaitemstat.getBadPhotoCount()).append("").toString());
                hashmap.put("total similar photos", (new StringBuilder()).append(mediaitemstat.getDuplicatePhotoCount()).append("").toString());
                hashmap.put("total photos for review", (new StringBuilder()).append(mediaitemstat.getForReviewCount()).append("").toString());
                l = mediaitemstat.getTotalPhotos();
                hashmap.put("total photos", (new StringBuilder()).append(l).append("").toString());
                hashmap.put("total photos", (new StringBuilder()).append(l).append("").toString());
                ainteger = PHOTOS_BUCKETS;
                j = ainteger.length;
                int k = 0;
                while(k < j)
                {
                    Integer integer = ainteger[k];
                    if(l < (long)integer.intValue())
                    {
                        break; /* Loop/switch isn't completed */
                    }
                    hashmap.put((new StringBuilder()).append("has ").append(integer).append(" photos").toString(), "true");
                    k++;
                }
                hashmap.put("total videos", (new StringBuilder()).append(mediaitemstat.getTotalVideos()).append("").toString());
                hashmap.put("total space", (new StringBuilder()).append(drivestat.getTotalSpace() / 0x100000L).append("").toString());
                hashmap.put("total free space", (new StringBuilder()).append(drivestat.getFreeSpace() / 0x100000L).append("").toString());
                hashmap.put("total photos space", (new StringBuilder()).append(mediaitemstat.getSizeOfPhotos() / 0x100000L).append("").toString());
                hashmap.put("total videos space", (new StringBuilder()).append(mediaitemstat.getSizeOfVideos() / 0x100000L).append("").toString());
                hashmap.put("total bad photos space", (new StringBuilder()).append(mediaitemstat.getBadPhotoSize() / 0x100000L).append("").toString());
                hashmap.put("total similar photos space", (new StringBuilder()).append(mediaitemstat.getDuplicatePhotoSize() / 0x100000L).append("").toString());
                hashmap.put("total photos for review space", (new StringBuilder()).append(mediaitemstat.getForReviewSize() / 0x100000L).append("").toString());
                KISSmetricsAPI.sharedAPI().set(hashmap);
                GalleryDoctorAnalyticsSender.sendDoctorUserDataAsync(l, mediaitemstat.getTotalVideos(), drivestat.getTotalSpace(), drivestat.getFreeSpace(), mediaitemstat.getSizeOfPhotos(), mediaitemstat.getSizeOfVideos(), i, mediaitemstat.getBadPhotoCount(), mediaitemstat.getBadPhotoSize(), mediaitemstat.getDuplicatePhotoCount(), mediaitemstat.getDuplicatePhotoSize(), mediaitemstat.getForReviewCount(), mediaitemstat.getForReviewSize());
            }
            catch(Exception exception)
            {
                Log.e(TAG, exception.getMessage(), exception);
            }
        }
        GalleryDoctorServicesProgress.duplicatesServiceFinished(getSource(), true);
        serviceProgress.stopNotification();
    }

    public void onStart()
    {
        if(!GalleryDoctorServicesProgress.didDuplicatesServiceFinish(getSource()))
        {
            serviceProgress.startWithNotification(true);
        }
    }

    public void onUpdate(float f)
    {
        GalleryDoctorServicesProgress.setDuplicatesServiceProgress(getSource(), f);
        if(!GalleryDoctorServicesProgress.didDuplicatesServiceFinish(getSource()))
        {
            serviceProgress.updateProgress();
        }
    }

    protected boolean shouldDownloadRemoteItems()
    {
        return true;
    }

    public void waitIfNeeded()
    {
        if(serviceProgress.status == com.flayvr.myrollshared.services.ServiceProgress.STATUS.PAUSE)
        {
            synchronized(serviceProgress.status)
            {
                try {
                    serviceProgress.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static 
    {
        Integer ainteger[] = new Integer[13];
        ainteger[0] = Integer.valueOf(50);
        ainteger[1] = Integer.valueOf(100);
        ainteger[2] = Integer.valueOf(200);
        ainteger[3] = Integer.valueOf(300);
        ainteger[4] = Integer.valueOf(400);
        ainteger[5] = Integer.valueOf(500);
        ainteger[6] = Integer.valueOf(750);
        ainteger[7] = Integer.valueOf(1000);
        ainteger[8] = Integer.valueOf(1500);
        ainteger[9] = Integer.valueOf(2000);
        ainteger[10] = Integer.valueOf(3000);
        ainteger[11] = Integer.valueOf(4000);
        ainteger[12] = Integer.valueOf(5000);
        PHOTOS_BUCKETS = ainteger;
    }
}

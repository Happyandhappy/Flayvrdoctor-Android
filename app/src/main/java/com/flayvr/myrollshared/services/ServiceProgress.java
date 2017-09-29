package com.flayvr.myrollshared.services;

import android.app.*;
import android.content.*;
import android.util.Log;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.utils.AnalyticsUtils;

public abstract class ServiceProgress
{

    public static final String PAUSE_ACTION = "PAUSE_PROCESSING";
    public static final String RESUME_ACTION = "RESUME_PROCESSING";
    public static final String STOP_ACTION = "STOP_PROCESSING";
    private static final String TAG = ServiceProgress.class.getSimpleName();
    protected android.support.v4.app.NotificationCompat.Builder notification;
    protected int notificationId;
    public Object pauseLock;
    protected int progress;
    protected BroadcastReceiver receiver;
    protected Service service;
    public STATUS status;

    public ServiceProgress(IntentService intentservice, int i)
    {
        service = intentservice;
        notificationId = i;
        progress = 0;
        status = STATUS.WORKING;
        pauseLock = new Object();
    }

    public abstract Class destinationActivity();

    public abstract String getContentText();

    public abstract String getContentTitle();

    public abstract int getSmallIcon();

    public void initReciever()
    {
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("PAUSE_PROCESSING");
        intentfilter.addAction("RESUME_PROCESSING");
        intentfilter.addAction("STOP_PROCESSING");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if(intent.getAction().equals("PAUSE_PROCESSING")) {
                    Log.d(ServiceProgress.TAG, "pause service");
                    service.unregisterReceiver(receiver);
                    startWithNotification(false);
                    status = STATUS.PAUSE;
                    AnalyticsUtils.trackEventWithKISS("paused analysis");
                } else if(intent.getAction().equals("RESUME_PROCESSING")) {
                    Log.d(ServiceProgress.TAG, "resume service");
                    service.unregisterReceiver(receiver);
                    startWithNotification(true);
                    synchronized(pauseLock)
                    {
                        pauseLock.notify();
                    }
                    status = STATUS.WORKING;
                    AnalyticsUtils.trackEventWithKISS("resumed analysis");
                } else if(intent.getAction().equals("STOP_PROCESSING")) {
                    Log.d(ServiceProgress.TAG, "stop service");
                    service.stopForeground(true);
                    synchronized (pauseLock) {
                        pauseLock.notify();
                    }
                    status = STATUS.STOPPED;
                    AnalyticsUtils.trackEventWithKISS("stopped analysis");
                    onCancel(context);
                }
            }
        };
        service.registerReceiver(receiver, intentfilter);
    }

    public void onCancel(Context context)
    {
    }

    public void startWithNotification(boolean flag)
    {
        initReciever();
        Intent intent = new Intent(service, destinationActivity());
        PendingIntent pendingintent = PendingIntent.getActivity(service, 0, intent, 0);
        intent.setFlags(0x24000000);
        notification = new android.support.v4.app.NotificationCompat.Builder(service);
        notification.setContentIntent(pendingintent)
                .setSmallIcon(getSmallIcon())
                .setWhen(System.currentTimeMillis())
                .setPriority(2)
                .setContentTitle(getContentTitle())
                .setContentText(getContentText())
                .setProgress(100, progress, false);
        Intent intent2;
        PendingIntent pendingintent2;
        if(flag)
        {
            Intent intent1 = new Intent("PAUSE_PROCESSING");
            PendingIntent pendingintent1 = PendingIntent.getBroadcast(service, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.addAction(R.drawable.notif_pause, service.getString(R.string.gallery_doctor_pross_pause), pendingintent1);
        } else
        {
            Intent intent3 = new Intent("RESUME_PROCESSING");
            PendingIntent pendingintent3 = PendingIntent.getBroadcast(service, 0, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.addAction(R.drawable.notif_play, service.getString(R.string.gallery_doctor_pross_resume), pendingintent3);
        }
        intent2 = new Intent("STOP_PROCESSING");
        pendingintent2 = PendingIntent.getBroadcast(service, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.addAction(R.drawable.notif_cancel, service.getString(R.string.gallery_doctor_pross_stop), pendingintent2);
        service.startForeground(notificationId, notification.build());
    }

    public void stopNotification()
    {
        if(notification != null)
        {
            service.stopForeground(true);
            service.unregisterReceiver(receiver);
            notification = null;
        }
    }

    public void updateProgress(int i)
    {
        progress = i;
        Log.d(TAG, (new StringBuilder()).append("updateProgress: ").append(i).toString());
        if(status == STATUS.WORKING && notification != null)
        {
            notification.setProgress(100, i, false); //wrong
            ((NotificationManager)service.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationId, notification.build());
        }
    }

    public enum STATUS
    {
        WORKING("WORKING", 0),
        PAUSE("PAUSE", 1),
        STOPPED("STOPPED", 2);

        private STATUS(String s, int i)
        {
        }
    }
}

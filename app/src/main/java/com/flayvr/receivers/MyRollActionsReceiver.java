package com.flayvr.receivers;

import android.content.*;
import android.os.Bundle;
import android.util.Log;
import com.flayvr.events.MediaItemsChangedEvent;
import com.flayvr.myrollshared.services.*;
import com.flayvr.myrollshared.utils.IntentActions;
import com.flayvr.services.*;
import de.greenrobot.event.EventBus;
import java.util.*;

public class MyRollActionsReceiver extends BroadcastReceiver
{

    private static final String TAG = MyRollActionsReceiver.class.getSimpleName();

    public MyRollActionsReceiver()
    {
    }

    private void sendAppEvent(Intent intent)
    {
        int i = 0;
        if(intent.getAction() == IntentActions.ACTION_NEW_MEDIA)
        {
            HashSet hashset = new HashSet();
            Object aobj[] = (Object[])(Object[])intent.getExtras().get(IntentActions.EXTRA_ITEMS_ADDED);
            for(int j = 0; j < aobj.length; j++)
            {
                hashset.add((Long)aobj[j]);
            }

            HashSet hashset1 = new HashSet();
            for(Object aobj1[] = (Object[])(Object[])intent.getExtras().get(IntentActions.EXTRA_ITEMS_REMOVED); i < aobj1.length; i++)
            {
                hashset1.add((Long)aobj1[i]);
            }

            MediaItemsChangedEvent mediaitemschangedevent = new MediaItemsChangedEvent(hashset, hashset1);
            EventBus.getDefault().post(mediaitemschangedevent);
        }
    }

    public void onReceive(Context context, Intent intent)
    {
        Log.i(TAG, (new StringBuilder()).append("received intent ").append(intent.getAction()).toString());
        String s = intent.getAction();
        if(s != null)
        {
            sendAppEvent(intent);
            if(GalleryBuilderService.getIntentFilter().contains(s))
            {
                Intent intent1 = new Intent(context, GalleryBuilderService.class);
                intent1.fillIn(intent, Intent.FILL_IN_ACTION);
                context.startService(intent1);
            }
            if(GDCVService.getIntentFilter().contains(s))
            {
                Intent intent2 = new Intent(context, GDCVService.class);
                intent2.fillIn(intent, Intent.FILL_IN_ACTION);
                context.startService(intent2);
            }
            if(GDDuplicatesService.getIntentFilter().contains(s))
            {
                Intent intent3 = new Intent(context, GDDuplicatesService.class);
                intent3.fillIn(intent, Intent.FILL_IN_ACTION);
                context.startService(intent3);
            }
            if(GDPhotoClassifierService.getIntentFilter().contains(s))
            {
                Intent intent4 = new Intent(context, GDPhotoClassifierService.class);
                intent4.fillIn(intent, Intent.FILL_IN_ACTION);
                context.startService(intent4);
            }
            if(UserProfileService.getIntentFilter().contains(s))
            {
                Intent intent5 = new Intent(context, UserProfileService.class);
                intent5.fillIn(intent, Intent.FILL_IN_ACTION);
                context.startService(intent5);
            }
            if(GalleryDoctorService.getIntentFilter().contains(s))
            {
                Intent intent6 = new Intent(context, GalleryDoctorService.class);
                intent6.fillIn(intent, Intent.FILL_IN_ACTION);
                context.startService(intent6);
            }
            if(PicasaGalleryBuilderService.getIntentFilter().contains(s))
            {
                Intent intent7 = new Intent(context, PicasaGalleryBuilderService.class);
                intent7.fillIn(intent, Intent.FILL_IN_ACTION);
                context.startService(intent7);
                return;
            }
        }
    }

}

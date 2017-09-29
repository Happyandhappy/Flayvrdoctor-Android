package com.flayvr.myrollshared.receivers;

import android.content.*;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.flayvr.myrollshared.utils.IntentActions;

public class ContentRegisterReceiver extends BroadcastReceiver
{

    public ContentRegisterReceiver()
    {
    }

    public static void registerObserver(Context context)
    {
        _cls1 _lcls1 = new _cls1(new Handler(), context);
        ContentResolver contentresolver = context.getContentResolver();
        contentresolver.registerContentObserver(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, _lcls1);
        contentresolver.registerContentObserver(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, _lcls1);
        contentresolver.registerContentObserver(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI, false, _lcls1);
        contentresolver.registerContentObserver(android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI, false, _lcls1);
    }

    public void onReceive(Context context, Intent intent)
    {
        registerObserver(context);
    }

    public static class _cls1 extends ContentObserver
    {
        private Context context;

        public void onChange(boolean flag)
        {
            Intent intent = new Intent(IntentActions.ACTION_ANDROID_CONTENT_CHANGED);
            context.sendBroadcast(intent);
        }

        public void onChange(boolean flag, Uri uri)
        {
            Intent intent = new Intent(IntentActions.ACTION_ANDROID_CONTENT_CHANGED);
            intent.putExtra(IntentActions.EXTRA_URI, uri);
            context.sendBroadcast(intent);
        }

        _cls1(Handler handler, Context context1)
        {
            super(handler);
            context = context1;

        }
    }

}

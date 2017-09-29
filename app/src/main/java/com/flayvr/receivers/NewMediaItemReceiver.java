package com.flayvr.receivers;

import android.content.*;
import android.util.Log;
import com.flayvr.myrollshared.utils.IntentActions;

public class NewMediaItemReceiver extends BroadcastReceiver
{

    private static final String TAG = NewMediaItemReceiver.class.getSimpleName();

    public NewMediaItemReceiver()
    {
    }

    public void onReceive(Context context, Intent intent)
    {
        Log.i(TAG, (new StringBuilder()).append("new media item: ").append(intent).toString());
        Intent intent1 = new Intent(IntentActions.ACTION_ANDROID_CONTENT_CHANGED);
        intent1.putExtra(IntentActions.EXTRA_URI, intent.getData());
        context.sendBroadcast(intent1);
    }

}

package com.flayvr.myrollshared.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import com.flayvr.myrollshared.services.FocusWizardService;

public class SharedActivity extends ActionBarActivity
{

    private static final String TAG = SharedActivity.class.getSimpleName();
    private ServiceConnection mConnection;

    public SharedActivity()
    {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
    }

    protected void onStart()
    {
        super.onStart();
        bindService(new Intent(this, FocusWizardService.class), mConnection, BIND_AUTO_CREATE);
    }

    protected void onStop()
    {
        super.onStop();
        unbindService(mConnection);
    }
}

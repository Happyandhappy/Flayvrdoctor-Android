package com.flayvr.screens.settings;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.events.PicasaLoginEvent;
import com.flayvr.myrollshared.events.PicasaLogoutEvent;
import com.flayvr.screens.register.AbstractCloudSignInActivity;
import de.greenrobot.event.EventBus;

public class SettingsActivity extends AbstractCloudSignInActivity
    implements com.flayvr.screens.register.CloudSignInFragment.CloudSignInFragmentListener
{

    private SettingsFragment fragment;
    private ServiceConnection mConnection;

    public SettingsActivity()
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

    protected String getSource()
    {
        return "settings";
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.settings_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragment = new SettingsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, (Fragment)fragment).commit();
        EventBus.getDefault().register(this);
    }

    protected void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(PicasaLoginEvent picasaloginevent)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.picasaStateChange(true);
            }
        });
    }

    public void onEvent(PicasaLogoutEvent picasalogoutevent)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.picasaStateChange(false);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch(menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case android.R.id.home: 
            finish();
            break;
        }
        return true;
    }

    public void onStart()
    {
        super.onStart();
    }

    public void onStop()
    {
        super.onStop();
    }
}

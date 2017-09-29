package com.flayvr.screens.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.events.PicasaLoginEvent;
import de.greenrobot.event.EventBus;

public class CloudSignInActivity extends AbstractCloudSignInActivity
    implements CloudSignInFragment.CloudSignInFragmentListener
{

    public static final String SOURCE_KEY = "SIGNIN_SOURCE";
    private String source;

    public CloudSignInActivity()
    {
    }

    public void dropboxLogin()
    {
    }

    public String getSource()
    {
        return source;
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("SIGNIN_SOURCE"))
        {
            source = getIntent().getExtras().getString("SIGNIN_SOURCE");
        }
        setContentView(R.layout.empty_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0.0F);
        CloudSignInFragment cloudsigninfragment = CloudSignInFragment.newInstance(false, true);
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, cloudsigninfragment).commit();
        EventBus.getDefault().register(this);
    }

    protected void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(PicasaLoginEvent picasaloginevent)
    {
        finish();
    }

    public void skipRegister()
    {
    }
}

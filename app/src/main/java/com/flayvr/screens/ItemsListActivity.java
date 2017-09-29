package com.flayvr.screens;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import com.flayvr.doctor.R;
import com.flayvr.util.GalleryDoctorDefaultActivity;

public abstract class ItemsListActivity extends GalleryDoctorDefaultActivity
{

    public static final String ITEMS_SOURCE = "ITEMS_SOURCE";
    private SelectionPhotosFragment fragment;

    public ItemsListActivity()
    {
    }

    public void finish()
    {
        if(fragment.didUserMakeAction())
        {
            Intent intent = new Intent();
            intent.putExtra("USER_DID_ACTION_EXTRA", true);
            intent.putExtra("USER_SIZE_OF_PHOTOS_DELETED", fragment.getSizeOfPhotosCleaned());
            intent.putExtra("USER_NUMBER_OF_PHOTOS_DELETED", fragment.getNumberOfPhotosCleaned());
            intent.putExtra("USER_DID_ACTION_ANALYTICS_EXPLANATION_EXTRA", fragment.getFragmentType());
            if(getParent() == null)
            {
                setResult(-1, intent);
            } else
            {
                getParent().setResult(-1, intent);
            }
        }
        super.finish();
    }

    protected abstract SelectionPhotosFragment getFragment(int i);

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(getResources().getBoolean(R.bool.gd_dashboard_portrait_only))
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        int i;
        if(bundle == null)
        {
            bundle = getIntent().getExtras();
        }
        i = bundle.getInt("ITEMS_SOURCE");
        setContentView(R.layout.settings_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragment = getFragment(i);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }
}

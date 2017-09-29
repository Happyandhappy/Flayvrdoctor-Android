package com.flayvr.screens.fullscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.flayvr.doctor.R;
import com.flayvr.util.GalleryDoctorDefaultActivity;

public class GalleryDoctorSelectionFullScreenActivity extends GalleryDoctorDefaultActivity
{

    private GalleryDoctorSelectionFullscreenFragment fragment;

    public GalleryDoctorSelectionFullScreenActivity()
    {
    }

    public void finish()
    {
        onFinish();
        super.finish();
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.settings_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragment = new GalleryDoctorSelectionFullscreenFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    protected void onFinish()
    {
        Intent intent = new Intent();
        intent.putExtra("UNSELECTED_ITEMS", fragment.unselectedItems);
        if(getParent() == null)
        {
            setResult(-1, intent);
        } else {
            getParent().setResult(-1);
        }
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
}

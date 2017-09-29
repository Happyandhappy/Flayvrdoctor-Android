package com.flayvr.screens.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.flayvr.doctor.R;
import com.flayvr.util.GalleryDoctorDefaultActivity;

public class SettingsHelpActivity extends GalleryDoctorDefaultActivity
{

    public SettingsHelpActivity()
    {
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.preferences_help);
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

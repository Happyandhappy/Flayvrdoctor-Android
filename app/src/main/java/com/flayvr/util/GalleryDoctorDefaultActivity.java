package com.flayvr.util;

import android.app.NotificationManager;
import android.os.Bundle;
import android.view.MenuItem;
import com.flayvr.myrollshared.utils.SharedActivity;

public class GalleryDoctorDefaultActivity extends SharedActivity
{

    public GalleryDoctorDefaultActivity()
    {
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(100);
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

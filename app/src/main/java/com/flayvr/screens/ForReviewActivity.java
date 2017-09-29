package com.flayvr.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.myrollshared.services.PhotoClassifierService;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.flayvr.util.GalleryDoctorAnalyticsSender;
import com.flayvr.util.GalleryDoctorDefaultActivity;
import java.util.*;

public abstract class ForReviewActivity extends GalleryDoctorDefaultActivity
{

    public static final String ITEMS_SOURCE = "ITEMS_SOURCE";
    private ForReviewFragment fragment;

    public ForReviewActivity()
    {
    }

    public void finish()
    {
        if(fragment.getUserMadeAction())
        {
            Intent intent = new Intent();
            intent.putExtra("USER_DID_ACTION_EXTRA", true);
            intent.putExtra("USER_SIZE_OF_PHOTOS_DELETED", fragment.getSizeOfPhotosCleaned());
            intent.putExtra("USER_NUMBER_OF_PHOTOS_DELETED", fragment.getNumberOfPhotosCleaned());
            intent.putExtra("USER_DID_ACTION_ANALYTICS_EXPLANATION_EXTRA", fragment.getReason());
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

    protected abstract com.flayvr.util.GalleryDoctorAnalyticsSender.DoctorActionType getAction();

    protected abstract ForReviewFragment getForReviewFragment(int i);

    protected abstract String getName();

    public void onBackPressed()
    {
        HashMap hashmap = new HashMap();
        hashmap.put((new StringBuilder()).append("total ").append(getName()).append(" viewed").toString(), (new StringBuilder()).append(1 + fragment.getIndex()).append("").toString());
        hashmap.put((new StringBuilder()).append("total ").append(getName()).append(" remaining").toString(), (new StringBuilder()).append(1L + (fragment.getmMediaItemCount() - (long)fragment.getIndex())).append("").toString());
        AnalyticsUtils.trackEventWithKISS((new StringBuilder()).append("exited ").append(getName()).toString(), hashmap, true);
        super.onBackPressed();
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        int i;
        HashMap hashmap;
        if(bundle == null)
        {
            bundle = getIntent().getExtras();
        }
        i = bundle.getInt("ITEMS_SOURCE");
        setContentView(R.layout.settings_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragment = getForReviewFragment(i);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        hashmap = new HashMap();
        hashmap.put("number of photos showed", (new StringBuilder()).append(fragment.getmMediaItemCount()).append("").toString());
        AnalyticsUtils.trackEventWithKISS((new StringBuilder()).append("viewed ").append(getName()).toString(), hashmap, true);
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch(menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case android.R.id.home: 
            onBackPressed();
            break;
        }
        return true;
    }

    public void onPause()
    {
        super.onPause();
        fragment.deleteLastItem();
        fragment.animateUndoOut();
        List list = fragment.getReviewedItemsForAnalytics();
        LinkedList linkedlist = new LinkedList();
        Iterator iterator = list.iterator();
        long l = 0L;
        int i = 0;
        while(iterator.hasNext()) 
        {
            MediaItem mediaitem = (MediaItem)iterator.next();
            boolean flag;
            int j;
            if(mediaitem.getWasKeptByUser() == null || !mediaitem.getWasKeptByUser().booleanValue())
            {
                flag = true;
            } else
            {
                flag = false;
            }
            if(flag)
            {
                l += mediaitem.getFileSizeBytesSafe().longValue();
                j = i + 1;
            } else
            {
                linkedlist.add(mediaitem);
                j = i;
            }
            i = j;
        }
        GalleryDoctorAnalyticsSender.sendLabeledPhotosDataAsync(list, false, true);
        GalleryDoctorAnalyticsSender.sendDoctorUserActionAsync(getAction(), i, linkedlist.size(), l);
        if(i > 0)
        {
            double d = (double)l / 1048576D;
            HashMap hashmap1 = new HashMap();
            hashmap1.put((new StringBuilder()).append("deleting ").append(getName()).append(" total photos deleted").toString(), (new StringBuilder()).append(i).append("").toString());
            hashmap1.put("total photos deleted", (new StringBuilder()).append(i).append("").toString());
            hashmap1.put("total space saved", (new StringBuilder()).append(d).append("").toString());
            hashmap1.put((new StringBuilder()).append("total space saved for ").append(getName()).toString(), (new StringBuilder()).append(d).append("").toString());
            AnalyticsUtils.trackEventWithKISS((new StringBuilder()).append("chose to delete ").append(getName()).toString(), hashmap1, true);
        }
        if(linkedlist.size() > 0)
        {
            PhotoClassifierService.updateRulesForKeptPhotosAsync(linkedlist);
            HashMap hashmap = new HashMap();
            hashmap.put((new StringBuilder()).append("keeping ").append(getName()).append(" total photos kept").toString(), (new StringBuilder()).append(linkedlist.size()).append("").toString());
            AnalyticsUtils.trackEventWithKISS((new StringBuilder()).append("chose to keep ").append(getName()).toString(), hashmap, true);
        }
        fragment.resetReviewedItemsForAnalytics();
    }
}

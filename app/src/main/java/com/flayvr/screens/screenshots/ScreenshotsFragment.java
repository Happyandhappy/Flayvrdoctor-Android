package com.flayvr.screens.screenshots;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.services.GalleryBuilderService;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.flayvr.screens.SelectionPhotosFragment;
import com.flayvr.util.GalleryDoctorDBHelper;
import com.flayvr.util.SectionedGridRecyclerViewAdapter;
import java.util.*;

public class ScreenshotsFragment extends SelectionPhotosFragment
{

    private List mediaItems;

    public ScreenshotsFragment()
    {
    }

    public static ScreenshotsFragment newInstance(int i)
    {
        ScreenshotsFragment screenshotsfragment = new ScreenshotsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("SOURCE", i);
        screenshotsfragment.setArguments(bundle);
        return screenshotsfragment;
    }

    public void addItemsData(Intent intent, MediaItem mediaitem)
    {
        long al[] = new long[mediaItems.size()];
        Iterator iterator = mediaItems.iterator();
        for(int i = 0; iterator.hasNext(); i++)
        {
            al[i] = ((MediaItem)iterator.next()).getId().longValue();
        }

        intent.putExtra("ITEMS_SELECTED", al);
        intent.putExtra("ITEMS_REVERESED", true);
    }

    public ScreenshotsAdapter createAdapter()
    {
        return new ScreenshotsAdapter(grid, mediaItems);
    }

    public void deleteCollection(final Collection items)
    {
        Iterator iterator = items.iterator();
        long l;
        MediaItem mediaitem;
        for(l = 0L; iterator.hasNext(); l += mediaitem.getFileSizeBytesSafe().longValue())
        {
            mediaitem = (MediaItem)iterator.next();
            mediaitem.setWasDeletedByUser(Boolean.valueOf(true));
        }

        DBManager.getInstance().getDaoSession().getMediaItemDao().updateInTx(items);
        FlayvrApplication.runAction(new Runnable() {
            @Override
            public void run() {
                GalleryBuilderService.deleteItems(items);
            }
        });
        HashMap hashmap = new HashMap();
        int i = items.size();
        hashmap.put((new StringBuilder()).append("total ").append(fragmentType).append(" photos deleted").toString(), (new StringBuilder()).append(i).append("").toString());
        hashmap.put("total photos deleted", (new StringBuilder()).append(i).append("").toString());
        hashmap.put((new StringBuilder()).append("total ").append(fragmentType).append(" photos kept").toString(), (new StringBuilder()).append(((ScreenshotsAdapter)photoAdapter).getItemCount() - i).append("").toString());
        hashmap.put("total space saved", (new StringBuilder()).append(l / 0x100000L).append("").toString());
        hashmap.put((new StringBuilder()).append("total space saved for ").append(fragmentType).append(" photos").toString(), (new StringBuilder()).append(l / 0x100000L).append("").toString());
        AnalyticsUtils.trackEventWithKISS((new StringBuilder()).append("accepted deleting ").append(fragmentType).append(" photos").toString(), hashmap, true);
    }

    protected com.flayvr.util.GalleryDoctorAnalyticsSender.DoctorActionType getActionType()
    {
        return com.flayvr.util.GalleryDoctorAnalyticsSender.DoctorActionType.CLEAN_SCREENSHOTS;
    }

    protected String getFragmentName()
    {
        return "screenshots";
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        mediaItems = GalleryDoctorDBHelper.getScreenshots(source);
        HashMap hashmap = new HashMap();
        hashmap.put("total screenshots photos", (new StringBuilder()).append(mediaItems.size()).append("").toString());
        AnalyticsUtils.trackEventWithKISS("viewed screenshots photos", hashmap, true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        menuinflater.inflate(R.menu.menu_gallery_bad_photos, menu);
        super.onCreateOptionsMenu(menu, menuinflater);
    }

    protected void updateAdapterSelection(Collection collection)
    {
        for(Iterator iterator = mediaItems.iterator(); iterator.hasNext();)
        {
            MediaItem mediaitem = (MediaItem)iterator.next();
            if(collection.contains(mediaitem.getId()))
            {
                ((ScreenshotsAdapter)photoAdapter).addUnselectedItem(mediaitem);
            } else
            {
                ((ScreenshotsAdapter)photoAdapter).removeUnselectedItem(mediaitem);
            }
        }

        ((ScreenshotsAdapter)photoAdapter).notifyDataSetChanged();
    }

    public void updateDBItems()
    {
        HashSet hashset = ((ScreenshotsAdapter)photoAdapter).getUnselectedItems();
        for(Iterator iterator = hashset.iterator(); iterator.hasNext(); ((MediaItem)iterator.next()).setWasKeptByUser(Boolean.valueOf(true))) { }
        DBManager.getInstance().getDaoSession().getMediaItemDao().updateInTx(hashset);
    }
}

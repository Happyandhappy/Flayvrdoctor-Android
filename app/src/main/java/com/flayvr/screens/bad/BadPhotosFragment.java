package com.flayvr.screens.bad;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.services.GalleryBuilderService;
import com.flayvr.myrollshared.services.PhotoClassifierService;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.flayvr.screens.SelectionPhotosFragment;
import com.flayvr.util.*;
import java.util.*;

public class BadPhotosFragment extends SelectionPhotosFragment
{

    private List mediaItems;

    public BadPhotosFragment()
    {
    }

    public static BadPhotosFragment newInstance(int i)
    {
        BadPhotosFragment badphotosfragment = new BadPhotosFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("SOURCE", i);
        badphotosfragment.setArguments(bundle);
        return badphotosfragment;
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

    public GalleryDoctorBadPhotosAdapter createAdapter()
    {
        return new GalleryDoctorBadPhotosAdapter(grid, mediaItems);
    }

    public void deleteCollection(final Collection items)
    {
        Iterator iterator = items.iterator();
        long l;
        MediaItem mediaitem;
        for(l = 0L; iterator.hasNext(); l += mediaitem.getFileSizeBytesSafe().longValue())
        {
            mediaitem = (MediaItem)iterator.next();
            mediaitem.setIsBad(Boolean.valueOf(false));
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
        hashmap.put((new StringBuilder()).append("total ").append(fragmentType).append(" photos kept").toString(), (new StringBuilder()).append(((GalleryDoctorBadPhotosAdapter)photoAdapter).getItemCount() - i).append("").toString());
        hashmap.put("total space saved", (new StringBuilder()).append(l / 0x100000L).append("").toString());
        hashmap.put((new StringBuilder()).append("total space saved for ").append(fragmentType).append(" photos").toString(), (new StringBuilder()).append(l / 0x100000L).append("").toString());
        AnalyticsUtils.trackEventWithKISS((new StringBuilder()).append("accepted deleting ").append(fragmentType).append(" photos").toString(), hashmap, true);
        PhotoClassifierService.updateRulesForKeptPhotosAsync(items);
        GalleryDoctorAnalyticsSender.sendLabeledPhotosDataAsync(items, true, false);
        GalleryDoctorAnalyticsSender.sendDoctorUserActionAsync(com.flayvr.util.GalleryDoctorAnalyticsSender.DoctorActionType.CLEAN_BAD_PHOTOS, i, ((GalleryDoctorBadPhotosAdapter)photoAdapter).getUnselectedItems().size(), l);
    }

    protected com.flayvr.util.GalleryDoctorAnalyticsSender.DoctorActionType getActionType()
    {
        return com.flayvr.util.GalleryDoctorAnalyticsSender.DoctorActionType.CLEAN_BAD_PHOTOS;
    }

    protected String getFragmentName()
    {
        return "bad photos";
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        mediaItems = GalleryDoctorDBHelper.getBadPhotos(source);
        HashMap hashmap = new HashMap();
        hashmap.put("total bad photos", (new StringBuilder()).append(mediaItems.size()).append("").toString());
        AnalyticsUtils.trackEventWithKISS("viewed bad photos", hashmap, true);
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
                ((GalleryDoctorBadPhotosAdapter)photoAdapter).addUnselectedItem(mediaitem);
            } else
            {
                ((GalleryDoctorBadPhotosAdapter)photoAdapter).removeUnselectedItem(mediaitem);
            }
        }

        ((GalleryDoctorBadPhotosAdapter)photoAdapter).notifyDataSetChanged();
    }

    public void updateDBItems()
    {
        HashSet hashset = ((GalleryDoctorBadPhotosAdapter)photoAdapter).getUnselectedItems();
        MediaItem mediaitem;
        for(Iterator iterator = hashset.iterator(); iterator.hasNext(); mediaitem.setWasKeptByUser(Boolean.valueOf(true)))
        {
            mediaitem = (MediaItem)iterator.next();
            mediaitem.setIsBad(Boolean.valueOf(false));
        }

        DBManager.getInstance().getDaoSession().getMediaItemDao().updateInTx(hashset);
        PhotoClassifierService.updateRulesForKeptPhotosAsync(hashset);
        GalleryDoctorAnalyticsSender.sendLabeledPhotosDataAsync(hashset, true, false);
    }
}

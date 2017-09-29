package com.flayvr.screens.videos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import com.flayvr.application.PreferencesManager;
import com.flayvr.doctor.R;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.services.GalleryBuilderService;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.flayvr.myrollshared.views.itemview.EditMediaItemView;
import com.flayvr.screens.SelectionPhotosFragment;
import com.flayvr.screens.fullscreen.VideoActivity;
import com.flayvr.util.SectionedGridRecyclerViewAdapter;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.*;
import java.util.*;

public class LongVideosFragment extends SelectionPhotosFragment
{

    protected static final long BUCKETS[] = {
        10L, 5L, 3L, 2L, 1L, 0L
    };
    protected Map items;

    public LongVideosFragment()
    {
    }

    private Long findKey(MediaItem mediaitem)
    {
        long l = mediaitem.getDuration().longValue() / 60000L;
        for(int i = 0; i < BUCKETS.length; i++)
        {
            if(BUCKETS[i] <= l)
            {
                return Long.valueOf(BUCKETS[i]);
            }
        }

        return null;
    }

    public static LongVideosFragment newInstance(int i)
    {
        LongVideosFragment longvideosfragment = new LongVideosFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("SOURCE", i);
        longvideosfragment.setArguments(bundle);
        return longvideosfragment;
    }

    public void addItemsData(Intent intent, MediaItem mediaitem)
    {
    }

    public LongVideosAdapter createAdapter()
    {
        return new LongVideosAdapter(grid, items);
    }

    public void deleteCollection(final Collection items)
    {
        FlayvrApplication.runAction(new Runnable() {
            @Override
            public void run() {
                GalleryBuilderService.deleteItems(items);
            }
        });
    }

    protected com.flayvr.util.GalleryDoctorAnalyticsSender.DoctorActionType getActionType()
    {
        return com.flayvr.util.GalleryDoctorAnalyticsSender.DoctorActionType.CLEAN_VIDEOS;
    }

    protected String getFragmentName()
    {
        return "long videos";
    }

    protected boolean isPhotos()
    {
        return false;
    }

    protected void itemClick(EditMediaItemView editmediaitemview)
    {
        Intent intent = new Intent(getActivity(), VideoActivity.class);
        intent.putExtra("ITEM_URI", Uri.parse(editmediaitemview.getItem().getPath()));
        startActivity(intent);
        AnalyticsUtils.trackEventWithKISS((new StringBuilder()).append("viewed ").append(fragmentType).append(" video in fullscreen").toString());
    }

    public void loadItems()
    {
        java.util.Set set = PreferencesManager.getExcludeAllFolder();
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getMediaItemDao().queryBuilder();
        querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.Source.eq(Integer.valueOf(source)), new WhereCondition[0]);
        WhereCondition wherecondition = com.flayvr.myrollshared.data.MediaItemDao.Properties.Type.eq(Integer.valueOf(2));
        WhereCondition wherecondition1 = com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.isNull();
        WhereCondition awherecondition[] = new WhereCondition[1];
        awherecondition[0] = querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]);
        WhereCondition wherecondition2 = querybuilder.and(wherecondition, wherecondition1, awherecondition);
        WhereCondition awherecondition1[] = new WhereCondition[2];
        awherecondition1[0] = querybuilder.or(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.isNull(), com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.eq(Boolean.valueOf(false)), new WhereCondition[0]);
        awherecondition1[1] = com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.notIn(set);
        querybuilder.where(wherecondition2, awherecondition1);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Duration;
        querybuilder.orderDesc(aproperty);
        querybuilder.limit(1000).offset(0);
        Query query = querybuilder.build();
        items = new HashMap();
        int i = 0;
        int k;
        for(int j = 1000; j == '\u03E8'; j = k)
        {
            query.setOffset(i);
            List list = query.list();
            Iterator iterator = list.iterator();
            while(iterator.hasNext())
            {
                MediaItem mediaitem = (MediaItem)iterator.next();
                Long long1 = findKey(mediaitem);
                if(long1 != null)
                {
                    Object obj = (List)items.get(long1);
                    if(obj == null)
                    {
                        obj = new LinkedList();
                        items.put(long1, obj);
                    }
                    ((List) (obj)).add(mediaitem);
                }
            }
            k = list.size();
            i += 1000;
        }

    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        source = getArguments().getInt("SOURCE");
        loadItems();
        AnalyticsUtils.trackEventWithKISS("viewed long videos");
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        super.onCreateOptionsMenu(menu, menuinflater);
        menuinflater.inflate(R.menu.menu_gallery_bad_photos, menu);
    }

    protected void updateAdapterSelection(Collection collection)
    {
        for(Iterator iterator = items.values().iterator(); iterator.hasNext();)
        {
            Iterator iterator1 = ((List)iterator.next()).iterator();
            while(iterator1.hasNext()) 
            {
                MediaItem mediaitem = (MediaItem)iterator1.next();
                if(collection.contains(mediaitem.getId()))
                    ((LongVideosAdapter)photoAdapter).addUnselectedItem(mediaitem);
                else
                    ((LongVideosAdapter)photoAdapter).removeUnselectedItem(mediaitem);
            }
        }

        ((LongVideosAdapter)photoAdapter).notifyDataSetChanged();
    }

    public void updateDBItems()
    {
    }
}
